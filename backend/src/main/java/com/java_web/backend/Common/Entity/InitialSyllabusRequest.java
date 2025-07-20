package com.java_web.backend.Common.Entity;

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
    public InitialSyllabusRequest() {
        // 设置默认值
        this.courseCode = "XXXX";
        this.teachingLanguage = "中文";
        this.responsibleCollege = "XXXX";
        this.courseCategory = "专业必修";
        this.principle = "X老师";
        this.verifier = "X老师";
        this.credit = "3";
        this.courseHour = "48";
        this.courseIntroduction = "本课程介绍的基本概念和方法。";
        this.evaluationMode = "考试";
        this.whetherTechnicalCourse = "否";
        this.assessmentType = "理论";
        this.gradeRecording = "百分制";
    }

    /**
     * 便捷构造函数，只接收必要参数，其他使用默认值
     */
    public InitialSyllabusRequest(String courseId, String courseTitle, String teachingLanguage, 
                                 String teachingTarget, String request) {
        this(); // 调用无参构造函数设置默认值
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.teachingLanguage = teachingLanguage;
        this.teachingTarget = teachingTarget;
        this.request = request;
        // 更新课程介绍
        this.courseIntroduction = "本课程介绍" + courseTitle + "的基本概念和方法。";
    }

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
        // 如果课程介绍还是默认值，则更新为包含课程标题的介绍
        if ("本课程介绍的基本概念和方法。".equals(this.courseIntroduction)) {
            this.courseIntroduction = "本课程介绍" + courseTitle + "的基本概念和方法。";
        }
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

    /**
     * 重置为默认值
     */
    public void resetToDefaults() {
        this.courseCode = "XXXX";
        this.responsibleCollege = "XXXX";
        this.courseCategory = "专业必修";
        this.principle = "X老师";
        this.verifier = "X老师";
        this.credit = "3";
        this.courseHour = "48";
        this.courseIntroduction = "本课程介绍" + (this.courseTitle != null ? this.courseTitle : "") + "的基本概念和方法。";
        this.evaluationMode = "考试";
        this.whetherTechnicalCourse = "否";
        this.assessmentType = "理论";
        this.gradeRecording = "百分制";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        InitialSyllabusRequest that = (InitialSyllabusRequest) o;
        
        if (courseId != null ? !courseId.equals(that.courseId) : that.courseId != null) return false;
        if (courseCode != null ? !courseCode.equals(that.courseCode) : that.courseCode != null) return false;
        if (courseTitle != null ? !courseTitle.equals(that.courseTitle) : that.courseTitle != null) return false;
        if (teachingLanguage != null ? !teachingLanguage.equals(that.teachingLanguage) : that.teachingLanguage != null) return false;
        if (responsibleCollege != null ? !responsibleCollege.equals(that.responsibleCollege) : that.responsibleCollege != null) return false;
        if (courseCategory != null ? !courseCategory.equals(that.courseCategory) : that.courseCategory != null) return false;
        if (principle != null ? !principle.equals(that.principle) : that.principle != null) return false;
        if (verifier != null ? !verifier.equals(that.verifier) : that.verifier != null) return false;
        if (credit != null ? !credit.equals(that.credit) : that.credit != null) return false;
        if (courseHour != null ? !courseHour.equals(that.courseHour) : that.courseHour != null) return false;
        if (courseIntroduction != null ? !courseIntroduction.equals(that.courseIntroduction) : that.courseIntroduction != null) return false;
        if (teachingTarget != null ? !teachingTarget.equals(that.teachingTarget) : that.teachingTarget != null) return false;
        if (evaluationMode != null ? !evaluationMode.equals(that.evaluationMode) : that.evaluationMode != null) return false;
        if (whetherTechnicalCourse != null ? !whetherTechnicalCourse.equals(that.whetherTechnicalCourse) : that.whetherTechnicalCourse != null) return false;
        if (assessmentType != null ? !assessmentType.equals(that.assessmentType) : that.assessmentType != null) return false;
        if (gradeRecording != null ? !gradeRecording.equals(that.gradeRecording) : that.gradeRecording != null) return false;
        return request != null ? request.equals(that.request) : that.request == null;
    }

    @Override
    public int hashCode() {
        int result = courseId != null ? courseId.hashCode() : 0;
        result = 31 * result + (courseCode != null ? courseCode.hashCode() : 0);
        result = 31 * result + (courseTitle != null ? courseTitle.hashCode() : 0);
        result = 31 * result + (teachingLanguage != null ? teachingLanguage.hashCode() : 0);
        result = 31 * result + (responsibleCollege != null ? responsibleCollege.hashCode() : 0);
        result = 31 * result + (courseCategory != null ? courseCategory.hashCode() : 0);
        result = 31 * result + (principle != null ? principle.hashCode() : 0);
        result = 31 * result + (verifier != null ? verifier.hashCode() : 0);
        result = 31 * result + (credit != null ? credit.hashCode() : 0);
        result = 31 * result + (courseHour != null ? courseHour.hashCode() : 0);
        result = 31 * result + (courseIntroduction != null ? courseIntroduction.hashCode() : 0);
        result = 31 * result + (teachingTarget != null ? teachingTarget.hashCode() : 0);
        result = 31 * result + (evaluationMode != null ? evaluationMode.hashCode() : 0);
        result = 31 * result + (whetherTechnicalCourse != null ? whetherTechnicalCourse.hashCode() : 0);
        result = 31 * result + (assessmentType != null ? assessmentType.hashCode() : 0);
        result = 31 * result + (gradeRecording != null ? gradeRecording.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        return result;
    }
} 