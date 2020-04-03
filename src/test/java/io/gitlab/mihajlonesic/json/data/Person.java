package io.gitlab.mihajlonesic.json.data;

import io.gitlab.mihajlonesic.json.JsonElement;
import io.gitlab.mihajlonesic.json.JsonInit;
import io.gitlab.mihajlonesic.json.JsonSerializable;

@JsonSerializable
public class Person {

    @JsonElement
    private String firstName;
    @JsonElement
    private String lastName;

    private String middleName;

    @JsonElement(key = "personAge")
    private int age;

    @JsonElement
    private boolean isMarried;

    private Pet pet;

    @JsonElement(required = false)
    private Person spouse;

    @JsonElement(required = false)
    private Integer numberOfMarriages;

    @JsonElement(required = false)
    private Address address;

    @JsonElement(required = false)
    private String[] favoriteDrinks;

    @JsonElement(required = false)
    private Integer[] luckyNumbers;

    @JsonElement(required = false)
    private Phone[] phoneNumbers;

    @JsonElement(required = false)
    private Person[] children;

    public Person(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    @JsonInit
    private void capitalizeNames() {
        this.firstName = this.firstName.substring(0, 1).toUpperCase() + this.firstName.substring(1);
        this.lastName = this.lastName.substring(0, 1).toUpperCase() + this.lastName.substring(1);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isMarried() {
        return isMarried;
    }

    public void setMarried(boolean married) {
        isMarried = married;
    }

    public Integer getNumberOfMarriages() {
        return numberOfMarriages;
    }

    public void setNumberOfMarriages(Integer numberOfMarriages) {
        this.numberOfMarriages = numberOfMarriages;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String[] getFavoriteDrinks() {
        return favoriteDrinks;
    }

    public void setFavoriteDrinks(String[] favoriteDrinks) {
        this.favoriteDrinks = favoriteDrinks;
    }

    public Integer[] getLuckyNumbers() {
        return luckyNumbers;
    }

    public void setLuckyNumbers(Integer[] luckyNumbers) {
        this.luckyNumbers = luckyNumbers;
    }

    public Phone[] getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Phone[] phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Person[] getChildren() {
        return children;
    }

    public void setChildren(Person[] children) {
        this.children = children;
    }

    public Person getSpouse() {
        return spouse;
    }

    public void setSpouse(Person spouse) {
        this.spouse = spouse;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
