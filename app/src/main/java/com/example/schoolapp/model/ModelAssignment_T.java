package com.example.schoolapp.model;

public class ModelAssignment_T { private String TAGsubject, TAGClass, TAGTopic, TAGDOAG, TAGDOS, TAssId, TAStatus, TAto;

    public ModelAssignment_T(){

    }

    public ModelAssignment_T (String tagsubject, String tagClass, String tagTopic, String tagDOAG, String tagDOS, String tassId, String taStatus, String tato){


        this.TAGsubject = tagsubject;
        this.TAGClass = tagClass;
        this.TAGTopic = tagTopic;
        this.TAGDOAG = tagDOAG;
        this.TAGDOS= tagDOS;
        this.TAssId = tassId;
        this.TAStatus = taStatus;
        this.TAto = tato;


    }



    public String getTAGsubject() {
        return TAGsubject;
    }

    public void setTAGsubject(String TAGsubject) {
        this.TAGsubject = TAGsubject;
    }

    public String getTAGClass() {
        return TAGClass;
    }

    public void setTAGClass(String TAGClass) {
        this.TAGClass = TAGClass;
    }

    public String getTAGTopic() {
        return TAGTopic;
    }

    public void setTAGTopic(String TAGTopic) {
        this.TAGTopic = TAGTopic;
    }

    public String getTAGDOAG() {
        return TAGDOAG;
    }

    public void setTAGDOAG(String TAGDOAG) {
        this.TAGDOAG = TAGDOAG;
    }

    public String getTAGDOS() {
        return TAGDOS;
    }

    public void setTAGDOS(String TAGDOS) {
        this.TAGDOS = TAGDOS;
    }

    public String getTAssId() {
        return TAssId;
    }

    public void setTAssId(String TAssId) {
        this.TAssId = TAssId;
    }

    public String getTAStatus() {
        return TAStatus;
    }

    public void setTAStatus(String TAStatus) {
        this.TAStatus = TAStatus;
    }

    public String getTAto() {
        return TAto;
    }

    public void setTAto(String TAto) {
        this.TAto = TAto;
    }

}
