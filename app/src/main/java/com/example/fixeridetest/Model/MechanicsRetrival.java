package com.example.fixeridetest.Model;

public class MechanicsRetrival {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String car;
    private String registrationNumber;

    public MechanicsRetrival() {
    }

    public MechanicsRetrival(String id,String name, String email, String phone, String car, String registrationNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.car = car;
        this.registrationNumber = registrationNumber;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }


    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
