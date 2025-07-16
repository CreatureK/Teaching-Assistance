package com.java_web.backend.Entity;

/**
 * 初始教学大纲生成请求实体类
 */
public class InitialSyllabusRequest {
    private String courseId;
    private String courseCode;
    private String courseTitle;
    private String teachingLanguage;
    private String responsibleCollege;
    private String courseCategory;
    private String principle;
    private String verifier;
    private String credit;
    private String courseHour;
    private String courseIntroduction;
    private String teachingTarget;
    private String evaluationMode;
    private String whetherTechnicalCourse;
    private String assessmentType;
    private String gradeRecording;
    private String request;

    // 构造函数
    public InitialSyllabusRequest() {}

    public InitialSyllabusRequest(String courseId, String courseCode, String courseTitle, 
                                 String teachingLanguage, String responsibleCollege, 
                                 String courseCategory, String principle, String verifier,
                                 String credit, String courseHour, String courseIntroduction,
                                 String teachingTarget, String evaluationMode, 
                                 String whetherTechnicalCourse, String assessmentType,
                                 String gradeRecording, String request) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.teachingLanguage = teachingLanguage;
        this.responsibleCollege = responsibleCollege;
        this.courseCategory = courseCategory;
        this.principle = principle;
        this.verifier = verifier;
        this.credit = credit;
        this.courseHour = courseHour;
        this.courseIntroduction = courseIntroduction;
        this.teachingTarget = teachingTarget;
        this.evaluationMode = evaluationMode;
        this.whetherTechnicalCourse = whetherTechnicalCourse;
        this.assessmentType = assessmentType;
        this.gradeRecording = gradeRecording;
        this.request = request;
    }

    // Getter和Setter方法
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getTeachingLanguage() {
        return teachingLanguage;
    }

    public void setTeachingLanguage(String teachingLanguage) {
        this.teachingLanguage = teachingLanguage;
    }

    public String getResponsibleCollege() {
        return responsibleCollege;
    }

    public void setResponsibleCollege(String responsibleCollege) {
        this.responsibleCollege = responsibleCollege;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public String getPrinciple() {
        return principle;
    }

    public void setPrinciple(String principle) {
        this.principle = principle;
    }

    public String getVerifier() {
        return verifier;
    }

    public void setVerifier(String verifier) {
        this.verifier = verifier;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getCourseHour() {
        return courseHour;
    }

    public void setCourseHour(String courseHour) {
        this.courseHour = courseHour;
    }

    public String getCourseIntroduction() {
        return courseIntroduction;
    }

    public void setCourseIntroduction(String courseIntroduction) {
        this.courseIntroduction = courseIntroduction;
    }

    public String getTeachingTarget() {
        return teachingTarget;
    }

    public void setTeachingTarget(String teachingTarget) {
        this.teachingTarget = teachingTarget;
    }

    public String getEvaluationMode() {
        return evaluationMode;
    }

    public void setEvaluationMode(String evaluationMode) {
        this.evaluationMode = evaluationMode;
    }

    public String getWhetherTechnicalCourse() {
        return whetherTechnicalCourse;
    }

    public void setWhetherTechnicalCourse(String whetherTechnicalCourse) {
        this.whetherTechnicalCourse = whetherTechnicalCourse;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) {
        this.assessmentType = assessmentType;
    }

    public String getGradeRecording() {
        return gradeRecording;
    }

    public void setGradeRecording(String gradeRecording) {
        this.gradeRecording = gradeRecording;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "InitialSyllabusRequest{" +
                "courseId='" + courseId + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", courseTitle='" + courseTitle + '\'' +
                ", teachingLanguage='" + teachingLanguage + '\'' +
                ", responsibleCollege='" + responsibleCollege + '\'' +
                ", courseCategory='" + courseCategory + '\'' +
                ", principle='" + principle + '\'' +
                ", verifier='" + verifier + '\'' +
                ", credit='" + credit + '\'' +
                ", courseHour='" + courseHour + '\'' +
                ", courseIntroduction='" + courseIntroduction + '\'' +
                ", teachingTarget='" + teachingTarget + '\'' +
                ", evaluationMode='" + evaluationMode + '\'' +
                ", whetherTechnicalCourse='" + whetherTechnicalCourse + '\'' +
                ", assessmentType='" + assessmentType + '\'' +
                ", gradeRecording='" + gradeRecording + '\'' +
                ", request='" + request + '\'' +
                '}';
    }
} 