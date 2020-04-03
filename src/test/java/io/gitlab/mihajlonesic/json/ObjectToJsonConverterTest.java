package io.gitlab.mihajlonesic.json;

import io.gitlab.mihajlonesic.json.data.Address;
import io.gitlab.mihajlonesic.json.data.Person;
import io.gitlab.mihajlonesic.json.data.Phone;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObjectToJsonConverterTest {

    Person person;

    @Before
    public void beforeEach() {
        person = new Person("john", "doe", 34);
    }

    @Test(expected = JsonSerializationException.class)
    public void whenGivenObjectIsNotAnnotatedThenExceptionIsThrown() throws JsonSerializationException {
        Object object = new Object();
        ObjectToJsonConverter.convertToJson(object);
    }

    @Test(expected = JsonSerializationException.class)
    public void whenGivenObjectIsNullThenExceptionIsThrown() throws JsonSerializationException {
        ObjectToJsonConverter.convertToJson(null);
    }

    @Test
    public void whenOnlyRequiredFieldsArePassedThenJsonIsReturned() throws JsonSerializationException {
        String jsonString = ObjectToJsonConverter.convertToJson(person);
        System.out.println(jsonString);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false}", jsonString);
    }

    @Test
    public void whenNonAnnotatedFieldIsPassedThenFieldIsNotPresentInJson() throws JsonSerializationException {
        person.setMiddleName("sullivan");
        String jsonString = ObjectToJsonConverter.convertToJson(person);
        System.out.println(jsonString);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false}", jsonString);
    }

    @Test
    public void whenANonRequiredFieldIsPassedThenFieldIsPresentInJson() throws JsonSerializationException {
        person.setNumberOfMarriages(0);
        String jsonString = ObjectToJsonConverter.convertToJson(person);
        System.out.println(jsonString);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"numberOfMarriages\":0}", jsonString);
    }

    @Test
    public void whenObjectIsPassedThenObjectIsPresentInJson() throws JsonSerializationException {
        person.setAddress(new Address("Test st.", "22A", "Bratislava", "Slovakia"));
        String jsonString = ObjectToJsonConverter.convertToJson(person);
        System.out.println(jsonString);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"address\":{\"street\":\"Test st.\",\"number\":\"22A\",\"city\":\"Bratislava\",\"country\":\"Slovakia\"}}", jsonString);
    }

    @Test
    public void whenNullObjectIsPassedThenObjectIsNotPresentInJson() throws JsonSerializationException {
        person.setAddress(null);
        String jsonString = ObjectToJsonConverter.convertToJson(person);
        System.out.println(jsonString);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false}", jsonString);
    }

    @Test
    public void whenArrayOfStringsIsPassedThenArrayIsPresentInJson() throws JsonSerializationException {
        person.setFavoriteDrinks(new String[]{"Pepsi", "Fanta", "Water"});
        String jsonString = ObjectToJsonConverter.convertToJson(person);
        System.out.println(jsonString);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"favoriteDrinks\":[\"Pepsi\",\"Fanta\",\"Water\"]}", jsonString);
    }

    @Test
    public void whenArrayOfNumbersIsPassedThenArrayIsPresentInJson() throws JsonSerializationException {
        person.setLuckyNumbers(new Integer[]{7, 17, 8});
        String jsonString = ObjectToJsonConverter.convertToJson(person);
        System.out.println(jsonString);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"luckyNumbers\":[7,17,8]}", jsonString);
    }

    @Test
    public void whenArrayOfObjectsIsPassedThenArrayIsPresentInJson() throws JsonSerializationException {
        person.setPhoneNumbers(new Phone[]{
                new Phone("home", "212-555-1234"),
                new Phone("office", "646-555-4567"),
                new Phone("mobile", "123-456-7890")
        });
        String jsonString = ObjectToJsonConverter.convertToJson(person);
        System.out.println(jsonString);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"phoneNumbers\":[{\"type\":\"home\",\"number\":\"212-555-1234\"},{\"type\":\"office\",\"number\":\"646-555-4567\"},{\"type\":\"mobile\",\"number\":\"123-456-7890\"}]}", jsonString);
    }

    @Test
    public void whenArrayOfObjectsHasAnObjectThenLevelTwoObjectIsNotPresentInJson() throws JsonSerializationException {
        Phone phone = new Phone("home", "212-555-1234", person);
        person.setPhoneNumbers(new Phone[]{
                phone,
                new Phone("office", "646-555-4567", person),
                new Phone("mobile", "123-456-7890", person)
        });
        String jsonStringPerson = ObjectToJsonConverter.convertToJson(person);
        String jsonStringPhone = ObjectToJsonConverter.convertToJson(phone);
        System.out.println(jsonStringPerson);
        System.out.println(jsonStringPhone);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false,\"phoneNumbers\":[{\"type\":\"home\",\"number\":\"212-555-1234\"},{\"type\":\"office\",\"number\":\"646-555-4567\"},{\"type\":\"mobile\",\"number\":\"123-456-7890\"}]}", jsonStringPerson);
        assertEquals("{\"type\":\"home\",\"number\":\"212-555-1234\",\"owner\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":false}}", jsonStringPhone);
    }

    @Test
    public void whenAllAnnotatedFieldsArePassedThenJsonIsReturned() throws JsonSerializationException {
        person.setMarried(true);
        person.setNumberOfMarriages(1);
        person.setAddress(new Address("Test st.", "22A", "Bratislava", "Slovakia"));
        person.setFavoriteDrinks(new String[]{"Pepsi", "Fanta", "Water"});
        person.setLuckyNumbers(new Integer[]{7, 17, 8});
        person.setPhoneNumbers(new Phone[]{
                new Phone("home", "212-555-1234"),
                new Phone("office", "646-555-4567"),
                new Phone("mobile", "123-456-7890")
        });
        person.setChildren(new Person[]{});

        Person spouse = new Person("Jane", "doe", 33);
        spouse.setMarried(true);
        person.setSpouse(spouse);

        String jsonString = ObjectToJsonConverter.convertToJson(person);
        System.out.println(jsonString);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":true,\"spouse\":{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"personAge\":33,\"isMarried\":true},\"numberOfMarriages\":1,\"address\":{\"street\":\"Test st.\",\"number\":\"22A\",\"city\":\"Bratislava\",\"country\":\"Slovakia\"},\"favoriteDrinks\":[\"Pepsi\",\"Fanta\",\"Water\"],\"luckyNumbers\":[7,17,8],\"phoneNumbers\":[{\"type\":\"home\",\"number\":\"212-555-1234\"},{\"type\":\"office\",\"number\":\"646-555-4567\"},{\"type\":\"mobile\",\"number\":\"123-456-7890\"}],\"children\":[]}", jsonString);
    }

    @Test
    public void whenObjectFieldHasAnObjectThenLevelTwoObjectIsNotPresent() throws JsonSerializationException {
        person.setMarried(true);

        Person spouse = new Person("Jane", "doe", 33);
        spouse.setMarried(true);

        person.setSpouse(spouse);
        spouse.setSpouse(person);

        String jsonStringPerson = ObjectToJsonConverter.convertToJson(person);
        String jsonStringSpouse = ObjectToJsonConverter.convertToJson(spouse);
        System.out.println(jsonStringPerson);
        System.out.println(jsonStringSpouse);
        assertEquals("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":true,\"spouse\":{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"personAge\":33,\"isMarried\":true}}", jsonStringPerson);
        assertEquals("{\"firstName\":\"Jane\",\"lastName\":\"Doe\",\"personAge\":33,\"isMarried\":true,\"spouse\":{\"firstName\":\"John\",\"lastName\":\"Doe\",\"personAge\":34,\"isMarried\":true}}", jsonStringSpouse);
    }
}