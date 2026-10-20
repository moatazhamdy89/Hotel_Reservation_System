package com.hotel;

import com.hotel.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        String sql = "SELECT room_number, base_price FROM rooms";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {

                int roomNumber = result.getInt("room_number");
                double price = result.getDouble("base_price");

                System.out.println(
                        "Room: " + roomNumber + " | Price: " + price
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}