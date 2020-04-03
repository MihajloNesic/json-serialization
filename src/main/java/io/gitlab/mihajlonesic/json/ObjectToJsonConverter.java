package io.gitlab.mihajlonesic.json;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ObjectToJsonConverter {

    public static String convertToJson(Object object) throws JsonSerializationException {
        return convertToJson(object, 1);
    }

    private static String convertToJson(Object object, int level) throws JsonSerializationException {
        try {
            checkIfSerializable(object);
            initializeObject(object);
            return getJsonString(object, level);
        } catch (Exception e) {
            throw new JsonSerializationException(e.getMessage());
        }
    }

    private static void checkIfSerializable(Object object) {
        if (Objects.isNull(object)) {
            throw new JsonSerializationException("Can't serialize a null object");
        }

        Class<?> clazz = object.getClass();
        if (!clazz.isAnnotationPresent(JsonSerializable.class)) {
            throw new JsonSerializationException("The class " + clazz.getSimpleName() + " is not annotated with JsonSerializable");
        }
    }

    private static void initializeObject(Object object) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(JsonInit.class)) {
                method.setAccessible(true);
                method.invoke(object);
            }
        }
    }

    private static String getJsonString(Object object, int level) throws IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        LinkedHashMap<String, Object> jsonElementsMap = new LinkedHashMap<>();
        Set<String> objectFields = new HashSet<>();
        Set<String> arrayFields = new HashSet<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(JsonElement.class)) {
                // get field name and value
                String fieldName = getKey(field);
                Object fieldValue = field.get(object);

                // If field is not required and null, skip it
                if (!field.getAnnotation(JsonElement.class).required() && fieldValue == null) {
                    continue;
                }

                // handle array
                if (field.getType().isArray()) {
                    // bail array serialization if level two
                    if(level == 2) {
                        continue;
                    }
                    arrayFields.add(fieldName);
                    List<Object> fieldValueArray = Arrays.asList((Object[]) fieldValue);

                    String arrayValue = fieldValueArray.stream()
                            .map(item -> {
                                // if array items are Strings, surround them with double quotes
                                if (item.getClass().equals(String.class)) {
                                    return "\"" + item + "\"";
                                }
                                // if array items are json serializable objects, serialize the object
                                if (item.getClass().isAnnotationPresent(JsonSerializable.class)) {
                                    return convertToJson(item, 2);
                                }
                                // array items are (probably) numbers or boolean
                                return String.valueOf(item);
                            }).collect(Collectors.joining(","));

                    // array is surrounded with brackets
                    fieldValue = "[" + arrayValue + "]";
                }

                // handle json serializable object
                if (fieldValue != null && fieldValue.getClass().isAnnotationPresent(JsonSerializable.class)) {
                    // bail object serialization if level two
                    if (level == 2) {
                        continue;
                    }
                    objectFields.add(fieldName);
                    fieldValue = convertToJson(fieldValue, 2);
                }

                // add to json map
                jsonElementsMap.put(fieldName, fieldValue);
            }
        }

        String jsonString = jsonElementsMap.entrySet()
                .stream()
                .map(entry -> {
                    if (entry.getValue() == null) {
                        return "\"" + entry.getKey() + "\":null";
                    }
                    if (entry.getValue().getClass().equals(String.class) && !objectFields.contains(entry.getKey()) && !arrayFields.contains(entry.getKey())) {
                        return "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"";
                    }
                    return "\"" + entry.getKey() + "\":" + entry.getValue();
                })
                .collect(Collectors.joining(","));

        return "{" + jsonString + "}";
    }

    private static String getKey(Field field) {
        String value = field.getAnnotation(JsonElement.class).key();
        return value.isEmpty() ? field.getName() : value;
    }
}
