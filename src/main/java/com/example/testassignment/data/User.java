package com.example.testassignment.data;

import java.util.Date;

public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String birthDate;
    private String address; //optional
    private String phoneNumber; //optional

    public User(String email, String firstName, String lastName, String birthDate, String address, String phoneNumber){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public User updateFields(User user){
        if(!(user.email == null)){
            this.email = user.email;
        }
        if(!(user.firstName == null)){
            this.firstName = user.firstName;
        }
        if(!(user.lastName == null)){
            this.lastName = user.lastName;
        }
        if(!(user.birthDate == null)){
            this.birthDate = user.birthDate;
        }
        if(!(user.address == null)){
            this.address = user.address;
        }
        if(!(user.phoneNumber == null)){
            this.phoneNumber = user.phoneNumber;
        }
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
