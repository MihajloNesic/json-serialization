## JSON Serialization with Java Reflection API

Features:
* _@JsonSerializable_ - Class annotation to mark class as json serializable
* _@JsonElement_ - Field annotation to mark field as json serializable. Can have an alias and can be marked as required (only for Objects)
* _@JsonInit_ - Method annotation. The method will execute before the json is constructed

Can serialize:
* primitives and wrapper classes
* arrays
* objects
