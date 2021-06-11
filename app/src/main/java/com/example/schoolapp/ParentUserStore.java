package com.example.schoolapp;

public class ParentUserStore {

    public ParentUserStore() {

    }

    String FullName;
    String Username;
    String Email;
    String Password;
    String PhoneNumber;
    String countryPick;

    public ParentUserStore(String fullName, String username, String email, String password, String phoneNumber, String countryPick) {
        this.FullName = fullName;
        this.Username = username;
        this.Email = email;
        this.Password = password;
        this.PhoneNumber = phoneNumber;
        this.countryPick = countryPick;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        this.FullName = fullName;
    }

    public String getUsername() {

        return Username;
    }

    public void setUsername(String username) {

        this.Username = username;
    }

    public String getEmail()
    {
        return Email;
    }

    public void setEmail(String email) {

        this.Email = email;
    }

    public String getPassword() {

        return Password;
    }

    public void setPassword(String password) {

        this.Password = password;
    }

    public String getPhoneNumber()
    {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {

        this.PhoneNumber = phoneNumber;
    }

    public String getCountryPick() {

        return countryPick;
    }

    public void setCountryPick(String countryPick) {

        this.countryPick = countryPick;
    }
}
