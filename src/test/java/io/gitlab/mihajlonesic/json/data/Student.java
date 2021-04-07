package io.gitlab.mihajlonesic.json.data;

import io.gitlab.mihajlonesic.json.JsonElement;
import io.gitlab.mihajlonesic.json.JsonSerializable;

import java.util.HashMap;
import java.util.Map;

@JsonSerializable
public class Student {

    @JsonElement
    private String firstName;

    @JsonElement
    private String lastName;

    @JsonElement
    private Long idNumber;

    @JsonElement(required = false)
    private Map<String, Grade> grades = new HashMap<>();

    @JsonElement
    private Map<String, Boolean> attendance = new HashMap<>();

    @JsonElement(required = false)
    private Map<String, String> codes = new HashMap<>();

    @JsonElement
    private String diplomaIdNumber;

    public Student() {
    }

    public Student(String firstName, String lastName, Long idNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.idNumber = idNumber;
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

    public Long getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(Long idNumber) {
        this.idNumber = idNumber;
    }

    public Map<String, Grade> getGrades() {
        return grades;
    }

    public void setGrades(Map<String, Grade> grades) {
        this.grades = grades;
    }

    public Map<String, Boolean> getAttendance() {
        return attendance;
    }

    public void setAttendance(Map<String, Boolean> attendance) {
        this.attendance = attendance;
    }

    public Map<String, String> getCodes() {
        return codes;
    }

    public void setCodes(Map<String, String> codes) {
        this.codes = codes;
    }

    public String getDiplomaIdNumber() {
        return diplomaIdNumber;
    }

    public void setDiplomaIdNumber(String diplomaIdNumber) {
        this.diplomaIdNumber = diplomaIdNumber;
    }
}
