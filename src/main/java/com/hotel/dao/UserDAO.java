package com.hotel.dao;

import com.hotel.config.DatabaseConnection;

import java.sql.*;

public class UserDAO {
    /** Returns the stored password hash for this email, or null if no such user. */
    public String findPasswordHashByEmail(String email) throws SQLException {
        String sql = "SELECT password FROM users WHERE email = ?";   // parameterized: prevents SQL injection
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("password") : null;
            }
        }
    }
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    public int insertGuest(int nationalId, String firstName, String lastName, String gender, int age,
                           String email, String passwordHash, String nationality, String phone)
            throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int userId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO users (national_id, first_name, last_name, gender, age, email, password, role) "
                                + "VALUES (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, nationalId);
                    ps.setString(2, firstName);
                    ps.setString(3, lastName);
                    ps.setString(4, gender);
                    ps.setInt(5, age);
                    ps.setString(6, email);
                    ps.setString(7, passwordHash);
                    ps.setString(8, "GUEST");
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        userId = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO guests (user_id, nationality, phone) VALUES (?,?,?)")) {
                    ps.setInt(1, userId);
                    ps.setString(2, nationality);
                    ps.setString(3, phone);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO loyalty_programs (guest_id, points, discount, membership_level) "
                                + "VALUES (?,0,0.0,'BRONZE')")) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
                con.commit();
                return userId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
}