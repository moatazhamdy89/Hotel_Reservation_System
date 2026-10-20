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
}