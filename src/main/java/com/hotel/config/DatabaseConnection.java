package com.hotel.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hotel_reservation_system";

    private static final String USER = "root";

    public static Connection getConnection() throws SQLException {

        String password = System.getenv("HOTEL_DB_PASSWORD");

        return DriverManager.getConnection(URL, USER, password);
    }
}