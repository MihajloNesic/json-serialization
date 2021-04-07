## JSON Serialization with Java Reflection API

### Features
* _@JsonSerializable_ - Class annotation to mark class as json serializable
* _@JsonElement_ - Field annotation to mark field as json serializable. Can have an alias and can be marked as required (only for Objects)
* _@JsonInit_ - Method annotation. The method will execute before the json is constructed

### Can serialize
* primitives and wrapper classes
* arrays
* collections (lists, sets)
* objects
* maps

### Example

```java
@JsonSerializable
public class Student {

    @JsonElement
    private String firstName;

    @JsonElement
    private String lastName;

    @JsonElement(required = false)
    private String middleName;

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
```

```java
Student student = new Student("John", "Doe");
String jsonString = JsonConverter.convertToJson(student);
System.out.println(jsonString);
```

Output
```json
{"firstName": "John", "lastName": "Doe"}
```

---

See tests for full examples