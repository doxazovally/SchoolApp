package com.example.schoolapp.model;

public class ModelTKid {
    private String KidId, KidName, KidClass, KidSchool, KidImage, KidTeacherName, timestamp, uid, Gender, ParentName;

    public ModelTKid() {

    }

    public ModelTKid(String kidId, String kidName, String kidClass, String kidSchool, String kidTeacherName,
                     String timestamp, String kidImage, String uid, String Gender, String ParentName) {



        this.KidId = kidId;
        this.KidName = kidName;
        this.KidClass = kidClass;
        this.KidSchool = kidSchool;
        this.KidTeacherName = kidTeacherName;
        this.timestamp = timestamp;
        this.KidImage = kidImage;
        this.uid = uid;
        this.Gender = Gender;
        this.ParentName = ParentName;

    }

    public String getKidId() {
        return KidId;
    }

    public void setKidId(String kidId) {

        this.KidId = kidId;
    }


    public String getKidName() {

        return KidName;
    }

    public void setKidName(String kidName) {

        this.KidName = kidName;
    }


    public String getKidClass() {
        return KidClass;
    }

    public void setKidClass(String kidClass) {

        this.KidClass = kidClass;
    }


    public String getKidSchool() {
        return KidSchool;
    }

    public void setKidSchool(String kidSchool) {

        this.KidSchool = kidSchool;
    }


    public String getKidImage() {

        return KidImage;
    }

    public void setKidImage(String kidImage) {

        this.KidImage = kidImage;
    }


    public String getKidTeacherName() {

        return KidTeacherName;
    }

    public void setKidTeacherName(String kidTeacherName) {

        this.KidTeacherName = kidTeacherName;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {

        this.Gender = Gender;
    }


    public String getParentName() {
        return ParentName;
    }

    public void setParentName(String ParentName) {

        this.ParentName = ParentName;
    }

}
