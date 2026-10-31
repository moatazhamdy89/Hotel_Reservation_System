package com.hotel.model;

import com.hotel.utils.PasswordHasher;
import com.hotel.utils.Validation;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Column(name = "national_id")
    protected int nationalID;

    @Id
    @Column(name = "user_id")
    protected int userID;

    @Column(name = "first_name")
    protected String firstName;

    @Column(name = "last_name")
    protected String lastName;

    @Column(name = "gender")
    protected String gender;

    @Column(name = "age")
    protected int age;

    @Column(name = "email")
    protected String email;

    @Column(name = "password")
    protected String password;

    @Column(name = "role")
    protected String role;

    @Transient
    private boolean loggedIn = false;

    // Required by Hibernate
    protected User() {
    }

    protected User(int nationalID, int userID, String firstName,
                   String lastName, String gender, int age,
                   String email, String password, String role) {

        if (isBlank(firstName) || isBlank(lastName))
            throw new IllegalArgumentException("Name cannot be empty");

        if (!Validation.isValidEmail(email))
            throw new IllegalArgumentException("Invalid email format");

        if (age <= 0)
            throw new IllegalArgumentException("Age must be positive");

        if (isBlank(password))
            throw new IllegalArgumentException("Password hash cannot be empty");

        if (isBlank(role))
            throw new IllegalArgumentException("Role cannot be empty");

        this.nationalID = nationalID;
        this.userID = userID;
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.gender = gender;
        this.age = age;
        this.email = email.trim().toLowerCase();
        this.password = password;
        this.role = role;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // Getters
    public int getNationalID() { return nationalID; }
    public int getUserID() { return userID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public int getAge() { return age; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public boolean isLoggedIn() { return loggedIn; }

    // Authentication
    public boolean login(String email, String rawPassword) {

        loggedIn = false;

        if (!Validation.isValidEmail(email)
                || rawPassword == null
                || rawPassword.isEmpty()) {
            return false;
        }

        String normalized = email.trim().toLowerCase();

        if (!this.email.equals(normalized)) {
            return false;
        }

        if (this.password == null) {
            return false;
        }

        loggedIn = PasswordHasher.verify(
                rawPassword, this.password
        );

        return loggedIn;
    }

    public void logout() {
        loggedIn = false;
    }

    // Display
    public void displayProfile() {
        System.out.println("ID: " + userID);
        System.out.println("Name: " + firstName + " " + lastName);
        System.out.println("Gender: " + gender + " | Age: " + age);
        System.out.println("Email: " + email);
        System.out.println("Role: " + role);
    }
}