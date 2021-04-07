package io.gitlab.mihajlonesic.json;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mihajlo Nesic
 * https://mihajlonesic.gitlab.io/
 * https://github.com/MihajloNesic
 */
public class JsonConverter {

    private JsonConverter() {
    }

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

        if (clazz.isPrimitive() || clazz.isArray() || clazz.isEnum()
            || clazz.equals(Byte.class) || clazz.equals(Short.class)
            || clazz.equals(Integer.class) || clazz.equals(Long.class)
            || clazz.equals(Float.class) || clazz.equals(Double.class)
            || clazz.equals(Boolean.class) || clazz.equals(String.class)
            || clazz.equals(Character.class) || Collection.class.isAssignableFrom(clazz)
            || Map.class.isAssignableFrom(clazz) ) {
            return;
        }

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

        if (Collection.class.isAssignableFrom(object.getClass()) || object.getClass().isArray()) {
            Collection<?> objectList;
            if (object.getClass().isArray()) {
                Object[] objectArray = (Object[]) object;
                objectList = Arrays.asList(objectArray);
            }
            else {
                objectList = (Collection<?>) object;
            }

            String arrayValue = fieldValuesToJsonArray(objectList);

            // array is surrounded with brackets
            return "[" + arrayValue + "]";
        }

        if (Map.class.isAssignableFrom(object.getClass())) {
            Map<?, ?> fieldValueMap = (Map<?, ?>) object;
            StringJoiner mapValueBuilder = new StringJoiner(",");

            for (Map.Entry<?, ?> entry : fieldValueMap.entrySet()) {

                if (entry.getValue() == null) {
                    mapValueBuilder.add("\"" + entry.getKey() + "\":null");
                }
                else if (entry.getValue().getClass().equals(String.class) || entry.getValue().getClass().equals(Character.class) || entry.getValue().getClass().isEnum()) {
                    mapValueBuilder.add("\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"");
                }
                else if (entry.getValue().getClass().isAnnotationPresent(JsonSerializable.class) || entry.getValue().getClass().isArray() || Collection.class.isAssignableFrom(entry.getValue().getClass()) || Map.class.isAssignableFrom(entry.getValue().getClass())) {
                    String entryValue = convertToJson(entry.getValue(), 2);
                    mapValueBuilder.add("\"" + entry.getKey() + "\":" + entryValue);
                }
                else {
                    mapValueBuilder.add("\"" + entry.getKey() + "\":" + entry.getValue());
                }
            }
            return "[" + mapValueBuilder + "]";
        }

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(JsonElement.class)) {

                Class<?> fieldClass = field.getType();

                // ignore annotation check for Java wrapper classes
                if (!fieldClass.isAnnotationPresent(JsonSerializable.class)) {

                    if (!fieldClass.isPrimitive() && !fieldClass.isArray() && !fieldClass.isEnum()
                        && !fieldClass.equals(Byte.class) && !fieldClass.equals(Short.class)
                        && !fieldClass.equals(Integer.class) && !fieldClass.equals(Long.class)
                        && !fieldClass.equals(Float.class) && !fieldClass.equals(Double.class)
                        && !fieldClass.equals(Boolean.class) && !fieldClass.equals(String.class)
                        && !fieldClass.equals(Character.class) && !Collection.class.isAssignableFrom(fieldClass)
                        && !Map.class.isAssignableFrom(fieldClass) ) {
                        continue;
                    }
                }

                // get field name and value
                String fieldName = getKey(field);
                Object fieldValue = field.get(object);

                // If field is not required and null, skip it
                if (!field.getAnnotation(JsonElement.class).required()) {
                    if (fieldValue == null) {
                        continue;
                    }

                    if (fieldValue instanceof Collection) {
                        if (((Collection<?>) fieldValue).isEmpty()) {
                            continue;
                        }
                    }

                    if (fieldValue instanceof Map) {
                        if (((Map<?, ?>) fieldValue).isEmpty()) {
                            continue;
                        }
                    }
                }

                // handle array
                if (field.getType().isArray()) {
                    // bail array serialization if level two
                    if(level == 2) {
                        continue;
                    }
                    arrayFields.add(fieldName);
                    List<Object> fieldValueArray = Arrays.asList((Object[]) fieldValue);

                    String arrayValue = fieldValuesToJsonArray(fieldValueArray);

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

                // handle maps
                if (fieldValue instanceof Map) {
                    Map<?, ?> fieldValueMap = (Map<?, ?>) fieldValue;
                    StringJoiner mapValueBuilder = new StringJoiner(",");

                    for (Map.Entry<?, ?> entry : fieldValueMap.entrySet()) {

                        if (entry.getValue() == null) {
                            mapValueBuilder.add("\"" + entry.getKey() + "\":null");
                        }
                        else if (entry.getValue().getClass().equals(String.class) || entry.getValue().getClass().equals(Character.class) || entry.getValue().getClass().isEnum()) {
                            mapValueBuilder.add("\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"");
                        }
                        else if (entry.getValue().getClass().isAnnotationPresent(JsonSerializable.class) || entry.getValue().getClass().isArray() || Collection.class.isAssignableFrom(entry.getValue().getClass()) || Map.class.isAssignableFrom(entry.getValue().getClass())) {
                            String entryValue = convertToJson(entry.getValue(), 2);
                            mapValueBuilder.add("\"" + entry.getKey() + "\":" + entryValue);
                        }
                        else {
                            mapValueBuilder.add("\"" + entry.getKey() + "\":" + entry.getValue());
                        }
                    }
                    objectFields.add(fieldName);
                    fieldValue = "[" + mapValueBuilder + "]";
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
                    if ((entry.getValue().getClass().equals(String.class) || entry.getValue().getClass().isEnum()) && !objectFields.contains(entry.getKey()) && !arrayFields.contains(entry.getKey())) {
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

    private static String fieldValuesToJsonArray(Collection<?> objectList) {
        String arrayValue = objectList.stream()
                .map(item -> {
                    // if array items are Strings or Characters, surround them with double quotes
                    if (item.getClass().equals(String.class) || item.getClass().equals(Character.class) || item.getClass().isEnum()) {
                        return "\"" + item + "\"";
                    }
                    // if array items are json serializable objects, serialize the object
                    if (item.getClass().isAnnotationPresent(JsonSerializable.class)) {
                        return convertToJson(item, 2);
                    }
                    // array items are (probably) numbers or boolean
                    return String.valueOf(item);
                }).collect(Collectors.joining(","));
        return arrayValue;
    }

    /**
     * A simple implementation to pretty-print JSON file.
     * Author: asksw0rder
     *
     * @param unformattedJsonString a JSON object as String
     * @param indentationLevel a level of indentation ({@link IndentationLevel}
     * @param indentationCharacter an indentation character ({@link IndentationCharacter}
     * @param spaceAfterColon should there be a space after a colon
     * @return formatted JSON String
     */
    public static String pretty(String unformattedJsonString, IndentationLevel indentationLevel, IndentationCharacter indentationCharacter, boolean spaceAfterColon) {
        StringBuilder prettyJSONBuilder = new StringBuilder();
        int indentLevel = 0;
        boolean inQuote = false;
        char[] keyChar = unformattedJsonString.toCharArray();

        for(int i = 0; i < keyChar.length; i++) {
            char currentChar = keyChar[i];
            char nextChar = (i+1 > keyChar.length-1) ? '\0' : keyChar[i+1];
            char previousChar = (i-1 < 0) ? '\0' : keyChar[i-1];

            switch(currentChar) {
                case '"':
                    // switch the quoting status
                    inQuote = !inQuote;
                    prettyJSONBuilder.append(currentChar);
                    break;
                case ' ':
                    // For space: ignore the space if it is not being quoted.
                    if(inQuote) {
                        prettyJSONBuilder.append(currentChar);
                    }
                    break;
                case '{':
                case '[':
                    // Starting a new block: increase the indent level
                    prettyJSONBuilder.append(currentChar);
                    if (nextChar != ']' && nextChar != '}') {
                        indentLevel++;
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder, indentationLevel, indentationCharacter);
                    }
                    break;
                case '}':
                case ']':
                    // Ending a new block; decrease the indent level
                    if (previousChar != '[' && previousChar != '{') {
                        indentLevel--;
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder, indentationLevel, indentationCharacter);
                    }
                    prettyJSONBuilder.append(currentChar);
                    break;
                case ',':
                    // Ending a json item; create a new line after
                    prettyJSONBuilder.append(currentChar);
                    if(!inQuote) {
                        appendIndentedNewLine(indentLevel, prettyJSONBuilder, indentationLevel, indentationCharacter);
                    }
                    break;
                case ':':
                    // Add space after a colon
                    prettyJSONBuilder.append(currentChar);
                    if (spaceAfterColon) {
                        prettyJSONBuilder.append(" ");
                    }
                    break;
                default:
                    prettyJSONBuilder.append(currentChar);
            }
        }
        return prettyJSONBuilder.toString();
    }

    public static String pretty(String unformattedJsonString) {
        return pretty(unformattedJsonString, IndentationLevel.FOUR, IndentationCharacter.SPACE, true);
    }

    public static String pretty(String unformattedJsonString, IndentationLevel indentationLevel) {
        return pretty(unformattedJsonString, indentationLevel, IndentationCharacter.SPACE, true);
    }

    /**
     * Print a new line with indention at the beginning of the new line.
     */
    private static void appendIndentedNewLine(int indentLevel, StringBuilder stringBuilder, IndentationLevel indentationLevel, IndentationCharacter indentationCharacter) {
        stringBuilder.append("\n");
        for(int i = 0; i < indentLevel; i++) {
            if (indentationCharacter.equals(IndentationCharacter.TAB)) {
                stringBuilder.append(indentationCharacter.indentCharacter);
            }
            else {
                for (int level = 0; level < indentationLevel.indentLevel; level++) {
                    stringBuilder.append(indentationCharacter.indentCharacter);
                }
            }
        }
    }

    public enum IndentationLevel {
        TWO(2),
        THREE(3),
        FOUR(4),
        EIGHT(8);

        int indentLevel;

        IndentationLevel(int indentLevel) {
            this.indentLevel = indentLevel;
        }
    }

    public enum IndentationCharacter {
        SPACE(" "),
        TAB("\t"),
        PERIOD("."),
        HYPHEN("-"),
        UNDERSCORE("_"),
        BULLET("\u2022");

        String indentCharacter;

        IndentationCharacter(String indentCharacter) {
            this.indentCharacter = indentCharacter;
        }
    }
}
