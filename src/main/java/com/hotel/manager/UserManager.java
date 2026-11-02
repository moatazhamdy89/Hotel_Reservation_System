
package com.hotel.manager;

import com.hotel.model.Admin;
import com.hotel.model.Guest;
import com.hotel.model.Receptionist;
import com.hotel.model.User;
import com.hotel.repository.HibernateRepository;

import java.util.List;

public class UserManager {

    private final HibernateRepository repository =
            new HibernateRepository();

    // Register a new Guest
    public Guest registerGuest(
            int nationalID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String rawPassword,
            String nationality,
            String phone) {

        Guest guest = Guest.createForRegistration(
                nationalID,
                firstName,
                lastName,
                gender,
                age,
                email,
                rawPassword,
                nationality,
                phone
        );

        return repository.save(guest);
    }

    // Register a new Receptionist
    public Receptionist registerReceptionist(
            int nationalID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String rawPassword,
            String phone,
            String shift) {

        Receptionist receptionist =
                Receptionist.createForRegistration(
                        nationalID,
                        firstName,
                        lastName,
                        gender,
                        age,
                        email,
                        rawPassword,
                        phone,
                        shift
                );

        return repository.save(receptionist);
    }

    // Register a new Admin
    public Admin registerAdmin(
            int nationalID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String rawPassword) {

        Admin admin = Admin.createForRegistration(
                nationalID,
                firstName,
                lastName,
                gender,
                age,
                email,
                rawPassword
        );

        return repository.save(admin);
    }

    // Find a user by ID
    public User findUserById(int userId) {

        if (userId <= 0) {
            return null;
        }

        return repository.findById(User.class, userId);
    }

    // Find all users
    public List<User> findAllUsers() {
        return repository.findAll(User.class);
    }

    // Update an existing user
    public User updateUser(User user) {

        if (user == null || user.getUserID() <= 0) {
            throw new IllegalArgumentException(
                    "A saved user is required");
        }

        return repository.update(user);
    }

    // Delete a user by ID
    public boolean deleteUser(int userId) {

        if (userId <= 0) {
            return false;
        }

        return repository.deleteById(User.class, userId);
    }
}
