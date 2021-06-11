package com.example.schoolapp.model;

public class ModelAssignment_P { private String PAGsubject, PAGClass, PAGTopic, PAGDOAG, PAGDOS, PAGTeacher, PAssId;

    public ModelAssignment_P(){

    }

    public ModelAssignment_P (String pagsubject, String pagClass, String pagTopic, String pagDOAG, String pagDOS, String pagTeacher, String passId){


        this.PAGsubject = pagsubject;
        this.PAGClass = pagClass;
        this.PAGTopic = pagTopic;
        this.PAGDOAG = pagDOAG;
        this.PAGDOS= pagDOS;
        this.PAssId = passId;
        this.PAGTeacher = pagTeacher;


    }

    public String getPAGsubject() {
        return PAGsubject;
    }

    public void setPAGsubject(String PAGsubject) {
        this.PAGsubject = PAGsubject;
    }

    public String getPAGClass() {
        return PAGClass;
    }

    public void setPAGClass(String PAGClass) {
        this.PAGClass = PAGClass;
    }

    public String getPAGTopic() {
        return PAGTopic;
    }

    public void setPAGTopic(String PAGTopic) {
        this.PAGTopic = PAGTopic;
    }

    public String getPAGDOAG() {
        return PAGDOAG;
    }

    public void setPAGDOAG(String PAGDOAG) {
        this.PAGDOAG = PAGDOAG;
    }

    public String getPAGDOS() {
        return PAGDOS;
    }

    public void setPAGDOS(String PAGDOS) {
        this.PAGDOS = PAGDOS;
    }

    public String getPAGTeacher() {
        return PAGTeacher;
    }

    public void setPAGTeacher(String PAGTeacher) {
        this.PAGTeacher = PAGTeacher;
    }

    public String getPAssId() {
        return PAssId;
    }

    public void setPAssId(String PAssId) {
        this.PAssId = PAssId;
    }
}
