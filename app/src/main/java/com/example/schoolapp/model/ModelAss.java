package com.example.schoolapp.model;

public class ModelAss {
    private String AssignmentSubject, AssignmentClass, AssignmentTopic, AssignmentDG, AssignmentDtoS, AssignmentTyped, AssignmentImage, AssignmentPDF, AssignmentID, AssignmentStatus;

    public ModelAss() {

    }

    public ModelAss(String assignmentSubject, String assignmentClass, String assignmentTopic, String assignmentDG, String assignmentDtoS, String assignmentTyped, String assignmentImage, String assignmentPDF, String assignmentID, String assignmentStatus) {

        this.AssignmentSubject = assignmentSubject;
        this.AssignmentClass = assignmentClass;
        this.AssignmentTopic = assignmentTopic;
        this.AssignmentDG = assignmentDG;
        this.AssignmentDtoS = assignmentDtoS;
        this.AssignmentTyped = assignmentTyped;
        this.AssignmentImage = assignmentImage;
        this.AssignmentPDF = assignmentPDF;
        this.AssignmentID = assignmentID;
        this.AssignmentStatus = assignmentStatus;
    }

    public String getAssignmentSubject() {
        return AssignmentSubject;
    }

    public void setAssignmentSubject(String assignmentSubject) {
        this.AssignmentSubject = assignmentSubject;
    }

    public String getAssignmentClass() {
        return AssignmentClass;
    }

    public void setAssignmentClass(String assignmentClass) {
        this.AssignmentClass = assignmentClass;
    }

    public String getAssignmentTopic() {
        return AssignmentTopic;
    }

    public void setAssignmentTopic(String assignmentTopic) {
        this.AssignmentTopic = assignmentTopic;
    }

    public String getAssignmentDG() {
        return AssignmentDG;
    }

    public void setAssignmentDG(String assignmentDG) {
        this.AssignmentDG = assignmentDG;
    }

    public String getAssignmentDtoS() {
        return AssignmentDtoS;
    }

    public void setAssignmentDtoS(String assignmentDtoS) {
        this.AssignmentDtoS = assignmentDtoS;
    }

    public String getAssignmentTyped() {
        return AssignmentTyped;
    }

    public void setAssignmentTyped(String assignmentTyped) {
        this.AssignmentTyped = assignmentTyped;
    }

    public String getAssignmentImage() {
        return AssignmentImage;
    }

    public void setAssignmentImage(String assignmentImage) {
        this.AssignmentImage = assignmentImage;
    }

    public String getAssignmentPDF() {
        return AssignmentPDF;
    }

    public void setAssignmentPDF(String assignmentPDF) {
        this.AssignmentPDF = assignmentPDF;
    }

    public String getAssignmentID() {
        return AssignmentID;
    }

    public void setAssignmentID(String assignmentID) {
        this.AssignmentID = assignmentID;
    }

    public String getAssignmentStatus() {
        return AssignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.AssignmentStatus = assignmentStatus;
    }
}
