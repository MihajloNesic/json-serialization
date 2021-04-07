package io.gitlab.mihajlonesic.json.data;

import io.gitlab.mihajlonesic.json.JsonElement;
import io.gitlab.mihajlonesic.json.JsonSerializable;

@JsonSerializable
public class Grade {

    @JsonElement
    private Double points;
    @JsonElement
    private Long grade;

    public Grade() {
    }

    public Grade(Double points, Long grade) {
        this.points = points;
        this.grade = grade;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }
}