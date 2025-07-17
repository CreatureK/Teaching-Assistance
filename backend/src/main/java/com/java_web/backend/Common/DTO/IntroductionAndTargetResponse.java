package com.java_web.backend.common.dto;

import java.util.Objects;

public class IntroductionAndTargetResponse {
    private String introduction;
    private String target;

    public IntroductionAndTargetResponse() {}

    public IntroductionAndTargetResponse(String introduction, String target) {
        this.introduction = introduction;
        this.target = target;
    }

    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntroductionAndTargetResponse that = (IntroductionAndTargetResponse) o;
        return Objects.equals(introduction, that.introduction) &&
                Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(introduction, target);
    }

    @Override
    public String toString() {
        return "IntroductionAndTargetResponse{" +
                "introduction='" + introduction + '\'' +
                ", target='" + target + '\'' +
                '}';
    }
} 