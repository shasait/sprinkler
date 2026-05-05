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

package de.hasait.common.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.annotation.AnnotationUtils;

public final class BeanProperty<B, P> {

    private static final Cache<Class<?>, BeanProperties<?>> BP_CACHE = CacheBuilder.newBuilder().weakValues().build();

    public static <B> BeanProperties<B> getBeanProperties(@Nonnull Class<B> beanClass) {
        Objects.requireNonNull(beanClass, "beanClass");

        try {
            return (BeanProperties) BP_CACHE.get(beanClass, () -> getBeanPropertiesInternal(beanClass));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <B> void forEachProperty(Class<B> beanClass, Function<BeanProperty<B, ?>, Boolean> logic) {
        for (BeanProperty<B, ?> beanProperty : getBeanProperties(beanClass).all()) {
            if (!logic.apply(beanProperty)) {
                break;
            }
        }
    }

    private static <B> BeanProperties<B> getBeanPropertiesInternal(@Nonnull Class<B> beanClass) {
        Objects.requireNonNull(beanClass, "beanClass");

        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            final BeanProperties<B> result = new BeanProperties<>();
            for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                result.add(createBeanProperty(beanClass, propertyDescriptor.getPropertyType(), propertyDescriptor));
            }
            return result;
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    private static <B, P> BeanProperty<B, P> createBeanProperty(@Nonnull Class<B> beanClass, @Nonnull Class<P> propertyClass, @Nonnull PropertyDescriptor propertyDescriptor) {
        Objects.requireNonNull(beanClass, "beanClass");
        Objects.requireNonNull(propertyClass, "propertyClass");
        Objects.requireNonNull(propertyDescriptor, "propertyDescriptor");

        Function<B, P> getter;
        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod == null) {
            getter = null;
        } else {
            getter = bean -> {
                try {
                    return (P) readMethod.invoke(bean);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        }
        BiConsumer<B, P> setter;
        Method writeMethod = propertyDescriptor.getWriteMethod();
        if (writeMethod == null) {
            setter = null;
        } else {
            setter = (bean, value) -> {
                try {
                    writeMethod.invoke(bean, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        }
        return new BeanProperty<>(beanClass, propertyClass, propertyDescriptor.getName(), getter, setter, propertyDescriptor);
    }

    private final Class<B> beanClass;
    private final Class<P> propertyClass;
    private final String name;
    private final Function<B, P> getter;
    private final BiConsumer<B, P> setter;
    private final PropertyDescriptor propertyDescriptor;

    private final Map<Class<?>, Annotation> annotationCache = new ConcurrentHashMap<>();

    public BeanProperty(@Nonnull Class<B> beanClass, @Nonnull Class<P> propertyClass, @Nonnull String name, @Nullable Function<B, P> getter, @Nullable BiConsumer<B, P> setter) {
        this(beanClass, propertyClass, name, getter, setter, null);
    }

    private BeanProperty(@Nonnull Class<B> beanClass, @Nonnull Class<P> propertyClass, @Nonnull String name, @Nullable Function<B, P> getter, @Nullable BiConsumer<B, P> setter, PropertyDescriptor propertyDescriptor) {
        this.beanClass = beanClass;
        this.propertyClass = propertyClass;
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.propertyDescriptor = propertyDescriptor;
    }

    public @Nonnull Class<B> getBeanClass() {
        return beanClass;
    }

    public @Nonnull Class<P> getPropertyClass() {
        return propertyClass;
    }

    public @Nonnull String getName() {
        return name;
    }

    public @Nullable Function<B, P> getGetter() {
        return getter;
    }

    public @Nullable BiConsumer<B, P> getSetter() {
        return setter;
    }

    public boolean isRequired() {
        if (propertyClass.isPrimitive()) {
            return true;
        }
        if (findAnnotation(NotNull.class) != null) {
            return true;
        }
        if (findAnnotation(Nonnull.class) != null) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation> A findAnnotation(Class<A> annotationClass) {
        return (A) annotationCache.computeIfAbsent(annotationClass, ignored -> internalFindAnnotation(annotationClass));
    }

    private <A extends Annotation> A internalFindAnnotation(Class<A> annotationClass) {
        // Skip Field although no PropertyDescriptor is needed for it
        // but a BeanProperty without a PropertyDescriptor is an artificial one and a field would be only a random unintended hit
        if (propertyDescriptor == null) {
            return null;
        }

        Field field = findField(beanClass, name, propertyClass);
        if (field != null) {
            A annotation = AnnotationUtils.findAnnotation(field, annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }

        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod != null) {
            A annotation = AnnotationUtils.findAnnotation(readMethod, annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }

        Method writeMethod = propertyDescriptor.getWriteMethod();
        if (writeMethod != null) {
            A annotation = AnnotationUtils.findAnnotation(writeMethod, annotationClass);
            return annotation;
        }

        return null;
    }

    private static Field findField(Class<?> beanClass, String name, Class<?> fieldType) {
        try {
            Field field = beanClass.getDeclaredField(name);
            if (fieldType.isAssignableFrom(field.getType())) {
                return field;
            }
        } catch (NoSuchFieldException e) {
            //
        }
        Class<?> superclass = beanClass.getSuperclass();
        return superclass != null ? findField(superclass, name, fieldType) : null;
    }

}
