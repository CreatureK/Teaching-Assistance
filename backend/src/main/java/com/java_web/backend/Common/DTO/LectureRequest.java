package com.java_web.backend.Common.DTO;

import java.util.List;

public class LectureRequest {
    private String courseId;
    private String courseTitle;
    private String unitTitle;
    private String knowledgeHour;
    private String knowledgePoint;
    private String request;
    private List<LectureSection> sections;

    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    public String getCourseTitle() {
        return courseTitle;
    }
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
    public String getUnitTitle() {
        return unitTitle;
    }
    public void setUnitTitle(String unitTitle) {
        this.unitTitle = unitTitle;
    }
    public String getKnowledgeHour() {
        return knowledgeHour;
    }
    public void setKnowledgeHour(String knowledgeHour) {
        this.knowledgeHour = knowledgeHour;
    }
    public String getKnowledgePoint() {
        return knowledgePoint;
    }
    public void setKnowledgePoint(String knowledgePoint) {
        this.knowledgePoint = knowledgePoint;
    }
    public String getRequest() {
        return request;
    }
    public void setRequest(String request) {
        this.request = request;
    }
    public List<LectureSection> getSections() {
        return sections;
    }
    public void setSections(List<LectureSection> sections) {
        this.sections = sections;
    }

    public static class LectureSection {
        private String topic;
        private String overview;
        private String coreContentBasic;
        private String coreContentEssential;
        private String coreContentAdvanced;
        private String exampleExercisesBasic;
        private String exampleExercisesEssential;
        private String exampleExercisesAdvanced;

        public String getTopic() { return topic; }
        public void setTopic(String topic) { this.topic = topic; }
        public String getOverview() { return overview; }
        public void setOverview(String overview) { this.overview = overview; }
        public String getCoreContentBasic() { return coreContentBasic; }
        public void setCoreContentBasic(String coreContentBasic) { this.coreContentBasic = coreContentBasic; }
        public String getCoreContentEssential() { return coreContentEssential; }
        public void setCoreContentEssential(String coreContentEssential) { this.coreContentEssential = coreContentEssential; }
        public String getCoreContentAdvanced() { return coreContentAdvanced; }
        public void setCoreContentAdvanced(String coreContentAdvanced) { this.coreContentAdvanced = coreContentAdvanced; }
        public String getExampleExercisesBasic() { return exampleExercisesBasic; }
        public void setExampleExercisesBasic(String exampleExercisesBasic) { this.exampleExercisesBasic = exampleExercisesBasic; }
        public String getExampleExercisesEssential() { return exampleExercisesEssential; }
        public void setExampleExercisesEssential(String exampleExercisesEssential) { this.exampleExercisesEssential = exampleExercisesEssential; }
        public String getExampleExercisesAdvanced() { return exampleExercisesAdvanced; }
        public void setExampleExercisesAdvanced(String exampleExercisesAdvanced) { this.exampleExercisesAdvanced = exampleExercisesAdvanced; }
    }
}
