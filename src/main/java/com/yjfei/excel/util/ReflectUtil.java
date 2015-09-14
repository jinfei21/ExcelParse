package com.yjfei.excel.util;

import static java.lang.reflect.Modifier.isStatic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

public class ReflectUtil {

    public static <T extends Annotation> void parseFieldAnnotation(Class<?> clazz, AnnotationCallBack<T> callback) {
        Field[] fields = getAllField(clazz);
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                T annotation = field.getAnnotation(callback.annotationClazz());
                if (annotation != null) {
                    callback.addAnnotation(field, annotation);
                }
            }
        }
    }

    public static Field[] getAllField(Class<?> clazz) {
        ArrayList<Field> fieldList = new ArrayList<Field>();
        Field[] dFields = clazz.getDeclaredFields();
        if (null != dFields && dFields.length > 0) {
            fieldList.addAll(Arrays.asList(dFields));
        }

        Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            Field[] superFields = getAllField(superClass);
            if (null != superFields && superFields.length > 0) {
                for (Field field : superFields) {
                    if (!isContain(fieldList, field)) {
                        fieldList.add(field);
                    }
                }
            }
        }
        Field[] result = new Field[fieldList.size()];
        fieldList.toArray(result);
        return result;
    }

    public static boolean isContain(ArrayList<Field> fieldList, Field field) {
        for (Field temp : fieldList) {
            if (temp.getName().equals(field.getName())) {
                return true;
            }
        }
        return false;
    }

    public static <T> T newInstance(Class<T> type, boolean accessible) {
        return newInstance(type, accessible, new Class[0], new Object[0]);
    }

    public static <T> T newInstance(Class<T> type, boolean accessible, Class<?>[] argumentTypes, Object[] arguments) {

        if (type.isMemberClass() && !isStatic(type.getModifiers())) {
            throw new RuntimeException(
                    "Creation of an instance of a non-static innerclass is not possible using reflection. The type "
                            + type.getSimpleName()
                            + " is only known in the context of an instance of the enclosing class "
                            + type.getEnclosingClass().getSimpleName()
                            + ". Declare the innerclass as static to make construction possible.");
        }
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(argumentTypes);
            if (accessible) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(arguments);

        } catch (Exception e) {
            throw new RuntimeException("Error while trying to create object of class " + type.getName(), e);
        }
    }

    public static interface AnnotationCallBack<T extends Annotation> {
        void addAnnotation(Field field, T annotation);

        Class<T> annotationClazz();
    }

}
