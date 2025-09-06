package com.leo.bns;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Objects;

public class MemberData {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Date of birth is required")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Date of birth must be in format DD/MM/YYYY")
    private String dateOfBirth;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Position is required")
    private String position;
    
    private String address;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    private String gender;
    private String skills;
    private String pictures;
    private String occupation;
    
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid emergency contact format")
    private String emergencyContact;
    
    @NotBlank(message = "Consent is required")
    private String consent;


    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public String getConsent() {
        return consent;
    }

    public void setConsent(String consent) {
        this.consent = consent;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
    
    // Constructor
    public MemberData() {}
    
    // Constructor with required fields
    public MemberData(String name, String dateOfBirth, String email, String position, String consent) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.position = position;
        this.consent = consent;
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MemberData that = (MemberData) obj;
        return Objects.equals(email, that.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
    
    // toString
    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
