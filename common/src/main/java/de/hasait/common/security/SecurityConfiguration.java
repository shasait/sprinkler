/*
 * Copyright (C) 2026 by Sebastian Hasait (sebastian at hasait dot de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hasait.common.security;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import de.hasait.common.vaadin.LoginView;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfiguration.class);

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /**
         * Delegating the responsibility of general configuration
         * of HTTP security to the VaadinSecurityConfigurer.
         *
         * It's configuring the following:
         * - Vaadin's CSRF protection by ignoring internal framework requests,
         * - default request cache,
         * - ignoring public views annotated with @AnonymousAllowed,
         * - restricting access to other views/endpoints, and
         * - enabling ViewAccessChecker authorization.
         */

        // You can add any possible extra configurations of your own
        // here - the following is just an example:
        http.rememberMe(customizer -> customizer.alwaysRemember(false));

        // Configure your static resources with public access before calling
        // VaadinSecurityConfigurer.vaadin() as it adds final anyRequest matcher
        http.authorizeHttpRequests(auth -> {
            auth //
                    .requestMatchers("/admin-only/**").hasAnyRole("admin") //
                    .requestMatchers("/public/**").permitAll() //
                    .requestMatchers("/actuator/**").permitAll() //
            ;
        });

        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> {
            // This is important to register your login view to the
            // view access checker mechanism:
            configurer.loginView(LoginView.class);
        });

        return http.build();
    }

    @Bean
    public UserDetailsManager userDetailsService() {
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();

        File usersJsonFile = new File("users.json");
        if (!usersJsonFile.canRead()) {
            throw new RuntimeException("Users configuration does not exist or cannot be read: " + usersJsonFile);
        }

        log.info("Reading users from {}...", usersJsonFile);
        try (FileReader reader = new FileReader(usersJsonFile)) {
            JsonElement rootJsonElement = JsonParser.parseReader(reader);
            rootJsonElement.getAsJsonObject().getAsJsonArray("users").forEach(userJsonElement -> {
                JsonObject userJsonObject = userJsonElement.getAsJsonObject();
                String username = userJsonObject.get("username").getAsString();
                log.debug("Found user: {}", username);

                UserDetails user = User.withUsername(username)
                        .password(userJsonObject.get("password").getAsString())
                        .roles(userJsonObject.get("role").getAsString())
                        .build();
                userDetailsManager.createUser(user);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return userDetailsManager;
    }

}
