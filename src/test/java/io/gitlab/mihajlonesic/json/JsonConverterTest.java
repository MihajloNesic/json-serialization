package io.gitlab.mihajlonesic.json;

import io.gitlab.mihajlonesic.json.data.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonConverterTest {

    Person person;

    @Before
    public void beforeEach() {
        person = new Person("john", "doe", 34);
    }

    @Test(expected = JsonSerializationException.class)
    public void whenGivenObjectIsNotAnnotatedThenExceptionIsThrown() throws JsonSerializationException {
        Object object = new Object();
        JsonConverter.convertToJson(object);
    }

    @Test(expected = JsonSerializationException.class)
    public void whenGivenObjectIsNullThenExceptionIsThrown() throws JsonSerializationException {
        JsonConverter.convertToJson(null);
    }

    @Test
    public void whenOnlyRequiredFieldsArePassedThenJsonIsReturned() throws JsonSerializationException {
        String jsonString = JsonConverter.convertToJson(person);
        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false}", jsonString);
    }

    @Test
    public void whenNonAnnotatedFieldIsPassedThenFieldIsNotPresentInJson() throws JsonSerializationException {
        person.setMiddleName("sullivan");
        String jsonString = JsonConverter.convertToJson(person);
        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false}", jsonString);
    }

    @Test
    public void whenANonRequiredFieldIsPassedThenFieldIsPresentInJson() throws JsonSerializationException {
        person.setNumberOfMarriages(0);
        String jsonString = JsonConverter.convertToJson(person);
        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"numberOfMarriages\":0}", jsonString);
    }

    @Test
    public void whenObjectIsPassedThenObjectIsPresentInJson() throws JsonSerializationException {
        person.setAddress(new Address("Test st.", "22A", "Bratislava", "Slovakia"));
        String jsonString = JsonConverter.convertToJson(person);
        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"address\":{\"street\":\"Test st.\",\"number\":\"22A\",\"city\":\"Bratislava\",\"country\":\"Slovakia\"}}", jsonString);
    }

    @Test
    public void whenNullObjectIsPassedThenObjectIsNotPresentInJson() throws JsonSerializationException {
        person.setAddress(null);
        String jsonString = JsonConverter.convertToJson(person);
        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false}", jsonString);
    }

    @Test
    public void whenArrayOfStringsIsPassedThenArrayIsPresentInJson() throws JsonSerializationException {
        person.setFavoriteDrinks(new String[]{"Pepsi", "Fanta", "Water"});
        String jsonString = JsonConverter.convertToJson(person);
        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"favoriteDrinks\":[\"Pepsi\",\"Fanta\",\"Water\"]}", jsonString);
    }

    @Test
    public void whenArrayOfNumbersIsPassedThenArrayIsPresentInJson() throws JsonSerializationException {
        person.setLuckyNumbers(new Integer[]{7, 17, 8});
        String jsonString = JsonConverter.convertToJson(person);
        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"luckyNumbers\":[7,17,8]}", jsonString);
    }

    @Test
    public void whenArrayOfObjectsIsPassedThenArrayIsPresentInJson() throws JsonSerializationException {
        person.setPhoneNumbers(new Phone[]{
                new Phone(PhoneType.HOME, "212-555-1234"),
                new Phone(PhoneType.OFFICE, "646-555-4567"),
                new Phone(PhoneType.MOBILE, "123-456-7890")
        });
        String jsonString = JsonConverter.convertToJson(person);
        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"phoneNumbers\":[{\"type\":\"HOME\",\"number\":\"212-555-1234\"},{\"type\":\"OFFICE\",\"number\":\"646-555-4567\"},{\"type\":\"MOBILE\",\"number\":\"123-456-7890\"}]}", jsonString);
    }

    @Test
    public void whenArrayOfObjectsHasAnObjectThenLevelTwoObjectIsNotPresentInJson() throws JsonSerializationException {
        Phone phone = new Phone(PhoneType.HOME, "212-555-1234", person);
        person.setPhoneNumbers(new Phone[]{
                phone,
                new Phone(PhoneType.OFFICE, "646-555-4567", person),
                new Phone(PhoneType.MOBILE, "123-456-7890", person)
        });
        String jsonStringPerson = JsonConverter.convertToJson(person);
        String jsonStringPhone = JsonConverter.convertToJson(phone);
        System.out.println(jsonStringPerson);
        System.out.println(JsonConverter.pretty(jsonStringPerson));
        System.out.println(jsonStringPhone);
        System.out.println(JsonConverter.pretty(jsonStringPhone));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"phoneNumbers\":[{\"type\":\"HOME\",\"number\":\"212-555-1234\"},{\"type\":\"OFFICE\",\"number\":\"646-555-4567\"},{\"type\":\"MOBILE\",\"number\":\"123-456-7890\"}]}", jsonStringPerson);
        assertEquals("{\"type\":\"HOME\",\"number\":\"212-555-1234\",\"owner\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false}}", jsonStringPhone);
    }

    @Test
    public void whenAllAnnotatedFieldsArePassedThenJsonIsReturned() throws JsonSerializationException {
        person.setMarried(true);
        person.setNumberOfMarriages(1);
        person.setAddress(new Address("Test st.", "22A", "Bratislava", "Slovakia"));
        person.setFavoriteDrinks(new String[]{"Pepsi", "Fanta", "Water"});
        person.setLuckyNumbers(new Integer[]{7, 17, 8});
        person.setPhoneNumbers(new Phone[]{
                new Phone(PhoneType.HOME, "212-555-1234"),
                new Phone(PhoneType.OFFICE, "646-555-4567"),
                new Phone(PhoneType.MOBILE, "123-456-7890")
        });
        person.setChildren(new Person[]{});

        Person spouse = new Person("Jane", "doe", 33);
        spouse.setMarried(true);
        person.setSpouse(spouse);

        String jsonString = JsonConverter.convertToJson(person);
        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":true,\"spouse\":{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"personAge\":33,\"isMarried\":true},\"numberOfMarriages\":1,\"address\":{\"street\":\"Test st.\",\"number\":\"22A\",\"city\":\"Bratislava\",\"country\":\"Slovakia\"},\"favoriteDrinks\":[\"Pepsi\",\"Fanta\",\"Water\"],\"luckyNumbers\":[7,17,8],\"phoneNumbers\":[{\"type\":\"HOME\",\"number\":\"212-555-1234\"},{\"type\":\"OFFICE\",\"number\":\"646-555-4567\"},{\"type\":\"MOBILE\",\"number\":\"123-456-7890\"}],\"children\":[]}", jsonString);
    }

    @Test
    public void whenObjectFieldHasAnObjectThenLevelTwoObjectIsNotPresent() throws JsonSerializationException {
        person.setMarried(true);

        Person spouse = new Person("Jane", "doe", 33);
        spouse.setMarried(true);

        person.setSpouse(spouse);
        spouse.setSpouse(person);

        String jsonStringPerson = JsonConverter.convertToJson(person);
        String jsonStringSpouse = JsonConverter.convertToJson(spouse);
        System.out.println(jsonStringPerson);
        System.out.println(JsonConverter.pretty(jsonStringPerson));
        System.out.println(jsonStringSpouse);
        System.out.println(JsonConverter.pretty(jsonStringSpouse));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":true,\"spouse\":{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"personAge\":33,\"isMarried\":true}}", jsonStringPerson);
        assertEquals("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"personAge\":33,\"isMarried\":true,\"spouse\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":true}}", jsonStringSpouse);
    }

    @Test
    public void whenObjectHasAnObjectNotAnnotatedAsSerializableThenThatObjectIsNotPresent() {
        Pet pet = new Pet("Bucky", 6);
        person.setPet(pet);

        String jsonString = JsonConverter.convertToJson(person);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false}", jsonString);
    }

    @Test
    public void whenObjectHasAListObjectThenThatListIsPresent() {
        List<Profession> professions = Arrays.asList(Profession.TEACHER, Profession.ARTIST);
        person.setProfessions(professions);

        String jsonString = JsonConverter.convertToJson(person);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"professions\":[TEACHER, ARTIST]}", jsonString);
    }

    @Test
    public void whenObjectHasASetObjectThenThatSetIsPresentAsArrayWithoutDuplicates() {
        List<Profession> dreamProfessions = Arrays.asList(Profession.PILOT, Profession.FIREFIGHTER, Profession.PILOT, Profession.ARTIST);
        person.setDreamProfessions(new LinkedHashSet<>(dreamProfessions));

        String jsonString = JsonConverter.convertToJson(person);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"dreamProfessions\":[PILOT, FIREFIGHTER, ARTIST]}", jsonString);
    }

    @Test
    public void whenObjectHasANullAttributeThenThatNullIsPresent() {
        Student student = new Student("Jane", "Doe", 1005L);

        String jsonString = JsonConverter.convertToJson(student);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"idNumber\":1005,\"attendance\":[],\"diplomaIdNumber\":null}", jsonString);
        assertTrue(jsonString.contains("\"diplomaIdNumber\":null"));
    }

    @Test
    public void whenObjectHasAnEmptyMapObjectThenThatMapIsPresentAsEmptyList() {
        Map<String, Boolean> attendance = new HashMap<>();

        Student student = new Student("Jane", "Doe", 1005L);
        student.setAttendance(attendance);

        String jsonString = JsonConverter.convertToJson(student);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"idNumber\":1005,\"attendance\":[],\"diplomaIdNumber\":null}", jsonString);
        assertTrue(jsonString.contains("\"attendance\":[]"));
    }

    @Test
    public void whenObjectHasVariousValuesInsideMapObjectThenThoseValuesAreAppropriatelyReturned() {
        Map<String, Object> map = new HashMap<>();
        map.put("null", null);
        map.put("integer", 5);
        map.put("float", 5.12f);
        map.put("char", 'm');
        map.put("string", "This is a string");
        map.put("list", Arrays.asList(1, 2, 3, 4));
        map.put("list2", Arrays.asList("a", "b", "c"));
        map.put("array", new Boolean[]{false, true, true});
        map.put("enum", JsonConverter.IndentationCharacter.HYPHEN);

        String jsonString = JsonConverter.convertToJson(map);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("[\"null\":null,\"string\":\"This is a string\",\"array\":[false,true,true],\"list2\":[\"a\",\"b\",\"c\"],\"char\":\"m\",\"integer\":5,\"float\":5.12,\"list\":[1,2,3,4],\"enum\":\"HYPHEN\"]", jsonString);
    }


    @Test
    public void whenObjectHasANullValueInsideMapObjectThenThatNullIsPresent() {
        Map<String, Boolean> attendance = new HashMap<>();
        attendance.put("TE111", false);
        attendance.put("TE222", null);

        Student student = new Student("Jane", "Doe", 1005L);
        student.setAttendance(attendance);

        String jsonString = JsonConverter.convertToJson(student);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"idNumber\":1005,\"attendance\":[\"TE222\":null,\"TE111\":false],\"diplomaIdNumber\":null}", jsonString);
        assertTrue(jsonString.contains("\"TE222\":null"));
    }

    @Test
    public void whenObjectHasAMapObjectWithWrapperClassAsValueThenThatMapIsPresentAsListOfObjects() {
        Map<String, Boolean> attendance = new HashMap<>();
        attendance.put("TE111", false);
        attendance.put("TE222", true);

        Student student = new Student("Jane", "Doe", 1005L);
        student.setAttendance(attendance);

        String jsonString = JsonConverter.convertToJson(student);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"idNumber\":1005,\"attendance\":[\"TE222\":true,\"TE111\":false],\"diplomaIdNumber\":null}", jsonString);
        assertTrue(jsonString.contains("\"attendance\":[\"TE222\":true,\"TE111\":false]"));
    }

    @Test
    public void whenObjectHasAMapObjectThenThatMapIsPresentAsListOfObjects() {
        Map<String, Grade> grades = new HashMap<>();

        grades.put("TE111", new Grade(87.43, 9L));
        grades.put("TE222", new Grade(75.32, 8L));

        Student student = new Student("Jane", "Doe", 1005L);
        student.setGrades(grades);

        String jsonString = JsonConverter.convertToJson(student);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"idNumber\":1005,\"grades\":[\"TE222\":{\"points\":75.32,\"grade\":8},\"TE111\":{\"points\":87.43,\"grade\":9}],\"attendance\":[],\"diplomaIdNumber\":null}", jsonString);
        assertTrue(jsonString.contains("\"grades\":[\"TE222\":{\"points\":75.32,\"grade\":8},\"TE111\":{\"points\":87.43,\"grade\":9}]"));
    }

    @Test
    public void whenObjectHasAMapOfStringsThenThatMapIsPresentAsListOfObjectWithStrings() {
        Map<String, String> codes = new TreeMap<>();

        codes.put("locker", "4213ABC");
        codes.put("desk", "156");
        codes.put("drawer", "7777");

        Student student = new Student("Jane", "Doe", 1005L);
        student.setCodes(codes);

        String jsonString = JsonConverter.convertToJson(student);

        System.out.println(jsonString);
        System.out.println(JsonConverter.pretty(jsonString));

        assertEquals("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"idNumber\":1005,\"attendance\":[],\"codes\":[\"desk\":\"156\",\"drawer\":\"7777\",\"locker\":\"4213ABC\"],\"diplomaIdNumber\":null}", jsonString);
        assertTrue(jsonString.contains("\"codes\":[\"desk\":\"156\",\"drawer\":\"7777\",\"locker\":\"4213ABC\"]"));
    }
}