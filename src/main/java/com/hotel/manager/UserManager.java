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

    // Check if the current user is an Admin
    private boolean isAdmin(int currentUserId) {

        if (currentUserId <= 0) {
            return false;
        }

        User currentUser = findUserById(currentUserId);

        return currentUser instanceof Admin;
    }

    // Register a new Guest
    public Guest registerGuest(
            String nationalID,
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
        validateUniqueEmail(email);

        return repository.save(guest);
    }

    // Register a new Receptionist
    public Receptionist registerReceptionist(
            int currentUserId,
            String nationalID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String rawPassword,
            String phone,
            String shift) {

        if (!isAdmin(currentUserId)) {
            throw new SecurityException(
                    "Only Admin can register Receptionists");
        }

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
        validateUniqueEmail(email);

        return repository.save(receptionist);
    }

    // Register a new Admin
    public Admin registerAdmin(
            int currentUserId,
            String nationalID,
            String firstName,
            String lastName,
            String gender,
            int age,
            String email,
            String rawPassword) {

        if (!isAdmin(currentUserId)) {
            throw new SecurityException(
                    "Only Admin can register Admins");
        }

        Admin admin = Admin.createForRegistration(
                nationalID,
                firstName,
                lastName,
                gender,
                age,
                email,
                rawPassword
        );
        validateUniqueEmail(email);

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
    public boolean deleteUser(
            int currentUserId,
            int userId) {

        if (!isAdmin(currentUserId)) {
            throw new SecurityException(
                    "Only Admin can delete users");
        }

        if (userId <= 0) {
            return false;
        }

        // Prevent Admin from deleting own account
        if (currentUserId == userId) {
            return false;
        }

        return repository.deleteById(User.class, userId);
    }
    private void validateUniqueEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        for (User user : findAllUsers()) {
            if (user.getEmail() != null
                    && user.getEmail().equalsIgnoreCase(email.trim())) {

                throw new IllegalArgumentException(
                        "This email is already registered"
                );
            }
        }
    }
    public Guest findGuestById(int guestId) {
        if (guestId <= 0) return null;
        return repository.findById(Guest.class, guestId);
    }
}