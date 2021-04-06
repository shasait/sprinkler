/*
 * Copyright (C) 2021 by Sebastian Hasait (sebastian at hasait dot de)
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

package de.hasait.sprinkler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.boot.system.EmbeddedServerPortFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "de.hasait")
public class Application {

    public static final String TITLE = "Sprinkler";

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static final AtomicReference<ApplicationContext> applicationContextHolder = new AtomicReference<>();

    public static void exit(int exitCode) {
        SpringApplication.exit(applicationContextHolder.get(), () -> exitCode);
        System.exit(exitCode);
    }

    public static void main(String[] mainArgs) throws Exception {
        SpringApplication app = new SpringApplication(Application.class);
        app.addListeners(new ApplicationPidFileWriter());
        app.addListeners(new EmbeddedServerPortFileWriter());
        applicationContextHolder.set(app.run(mainArgs));

        LOG.info("Press ENTER to shut down!");
        String line = new BufferedReader(new InputStreamReader(System.in)).readLine();
        if (line != null) {
            LOG.info("Shutting down...");
            exit(1);
        }
    }

}
