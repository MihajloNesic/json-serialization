package io.gitlab.mihajlonesic.json.data;

import io.gitlab.mihajlonesic.json.JsonElement;
import io.gitlab.mihajlonesic.json.JsonInit;
import io.gitlab.mihajlonesic.json.JsonSerializable;

import java.util.regex.Pattern;

@JsonSerializable
public class Phone {

    @JsonElement
    private String type;
    @JsonElement
    private String number;

    @JsonElement(required = false)
    private Person owner;

    public Phone(String type, String number) {
        this.type = type;
        this.number = number;
    }

    public Phone(String type, String number, Person owner) {
        this(type, number);
        this.owner = owner;
    }

    @JsonInit
    private void validateNumber() {
        if (!Pattern.matches("\\d{3}-\\d{3}-\\d{4}", this.number)) {
            throw new RuntimeException("The phone number is not valid");
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }
}
