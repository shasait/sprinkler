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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

public final class ReflectionUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class);

    private ReflectionUtil() {
    }

    private static final Cache<Class<?>, Map<String, PropertyDescriptor>> REFLECTION_CACHE = CacheBuilder.newBuilder().weakValues().build();

    private static final Map<String, Annotation> ANNOTATION_CACHE = new ConcurrentHashMap<>();

    public static <T> void addToProperty(final @Nonnull T object, final @Nonnull String propertyName,
                                         final @Nonnull Collection<?> valuesToAdd) {
        Objects.requireNonNull(object, "object");

        final Object propertyValue = getProperty(object, propertyName);
        AssertUtil.isTrue(propertyValue instanceof Collection, "Not many relation: {0}-{1}", object.getClass(), propertyName);
        ((Collection) propertyValue).addAll(valuesToAdd);
    }

    @Nonnull
    public static <T> Function<T, Object> findGetter(final @Nonnull Class<T> type, final @Nonnull String propertyName) {
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(type, propertyName);
        return getGetter(type, propertyDescriptor);
    }

    @Nonnull
    public static Method findRawGetter(final @Nonnull Class<?> type, final @Nonnull String propertyName) {
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(type, propertyName);
        return getRawGetter(type, propertyDescriptor);
    }

    @Nonnull
    public static Method findRawSetter(final @Nonnull Class<?> type, final @Nonnull String propertyName) {
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(type, propertyName);
        return getRawSetter(type, propertyDescriptor);
    }

    @Nonnull
    public static <T> BiConsumer<T, Object> findSetter(final @Nonnull Class<T> type, final @Nonnull String propertyName) {
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(type, propertyName);
        return getSetter(type, propertyDescriptor);
    }

    public static <T> Map<String, Object> getProperties(final @Nonnull T object) {
        Objects.requireNonNull(object, "object");

        final Class<T> type = (Class<T>) object.getClass();
        final Map<String, PropertyDescriptor> propertyDescriptors = getPropertyDescriptors(type);
        final Map<String, Object> result = new HashMap<>();
        propertyDescriptors.forEach((name, propertyDescriptor) -> result.put(name, getGetter(type, propertyDescriptor).apply(object)));
        return result;
    }

    public static <T> Object getProperty(final @Nonnull T object, final @Nonnull String propertyName) {
        Objects.requireNonNull(object, "object");

        final Class<T> type = (Class<T>) object.getClass();
        return findGetter(type, propertyName).apply(object);
    }

    public static Map<String, PropertyDescriptor> getPropertyDescriptors(final @Nonnull Class<?> type) {
        Objects.requireNonNull(type, "type");

        try {
            return REFLECTION_CACHE.get(type, () -> getPropertyDescriptorsInternal(type));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(final @Nonnull Class<T> type) {
        Objects.requireNonNull(type, "type");

        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> void setProperty(final @Nonnull T object, final @Nonnull String propertyName, final Object value) {
        Objects.requireNonNull(object, "object");

        final Class<T> type = (Class<T>) object.getClass();
        findSetter(type, propertyName).accept(object, value);
    }

    private static <T> Function<T, Object> getGetter(final @Nonnull Class<T> type, final @Nonnull PropertyDescriptor propertyDescriptor) {
        final Method getter = getRawGetter(type, propertyDescriptor);
        return object -> {
            try {
                return getter.invoke(object);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static PropertyDescriptor getPropertyDescriptor(final @Nonnull Class<?> type, final @Nonnull String propertyName) {
        Objects.requireNonNull(propertyName, "propertyName");

        final PropertyDescriptor propertyDescriptor = getPropertyDescriptors(type).get(propertyName);
        Objects.requireNonNull(propertyDescriptor, () -> MessageFormatUtil.format("No property found for type {0}", type));

        return propertyDescriptor;
    }

    private static Map<String, PropertyDescriptor> getPropertyDescriptorsInternal(final @Nonnull Class<?> type) {
        Objects.requireNonNull(type, "type");

        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(type);
            final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            final Map<String, PropertyDescriptor> map = new LinkedHashMap<>();
            for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                map.put(propertyDescriptor.getName(), propertyDescriptor);
            }
            return Collections.unmodifiableMap(map);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getRawGetter(final @Nonnull Class<?> type, final @Nonnull PropertyDescriptor propertyDescriptor) {
        final Method getter = propertyDescriptor.getReadMethod();
        return Objects.requireNonNull(getter, () -> MessageFormatUtil
                .format("No getter found for property {0} of type {1}", propertyDescriptor.getName(), type));
    }

    private static Method getRawSetter(final @Nonnull Class<?> type, final @Nonnull PropertyDescriptor propertyDescriptor) {
        final Method setter = propertyDescriptor.getWriteMethod();
        return Objects.requireNonNull(setter, () -> MessageFormatUtil
                .format("No setter found for property {0} of type {1}", propertyDescriptor.getName(), type));
    }

    private static <T> BiConsumer<T, Object> getSetter(final @Nonnull Class<?> type,
                                                       final @Nonnull PropertyDescriptor propertyDescriptor) {
        final Method setter = getRawSetter(type, propertyDescriptor);
        return (object, value) -> {
            try {
                setter.invoke(object, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> void forEachProperty(Class<T> beanClass, Function<PropertyDescriptor, Boolean> logic) {
        for (PropertyDescriptor propertyDescriptor : getPropertyDescriptors(beanClass).values()) {
            if (!logic.apply(propertyDescriptor)) {
                break;
            }
        }
    }

    public static <T> boolean isRequired(Class<T> beanClass, PropertyDescriptor propertyDescriptor) {
        if (propertyDescriptor.getPropertyType().isPrimitive()) {
            return true;
        }
        if (findAnnotation(beanClass, propertyDescriptor, NotNull.class) != null) {
            return true;
        }
        if (findAnnotation(beanClass, propertyDescriptor, Nonnull.class) != null) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A findAnnotation(Class<?> beanType, PropertyDescriptor propertyDescriptor, Class<A> annotationClass) {
        String key = beanType.getName() + "|" + propertyDescriptor.getName() + "|" + annotationClass.getName();
        return (A) ANNOTATION_CACHE.computeIfAbsent(key, ignored -> internalFindAnnotation(beanType, propertyDescriptor, annotationClass));
    }

    private static <A extends Annotation> A internalFindAnnotation(Class<?> beanType, PropertyDescriptor propertyDescriptor, Class<A> annotationClass) {
        Field field = findField(beanType, propertyDescriptor);
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

    public static Field findField(Class<?> beanType, PropertyDescriptor propertyDescriptor) {
        return findField(beanType, propertyDescriptor.getName(), propertyDescriptor.getPropertyType());
    }

    public static Field findField(Class<?> beanType, String name, Class<?> fieldType) {
        try {
            Field field = beanType.getDeclaredField(name);
            if (fieldType.isAssignableFrom(field.getType())) {
                return field;
            }
        } catch (NoSuchFieldException e) {
            //
        }
        Class<?> superclass = beanType.getSuperclass();
        return superclass != null ? findField(superclass, name, fieldType) : null;
    }

}
