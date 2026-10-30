package com.hotel.config;

import com.hotel.model.Accommodation;
import com.hotel.model.EventHall;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory =
            buildSessionFactory();

    private static SessionFactory buildSessionFactory() {

        String password = System.getenv("HOTEL_DB_PASSWORD"); // Password SQL

        // Validation for password
        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "HOTEL_DB_PASSWORD is missing");
        }

        return new Configuration()
                .addAnnotatedClass(EventHall.class )
                .addAnnotatedClass(Accommodation.class)

                .setProperty(
                        "hibernate.connection.driver_class",
                        "com.mysql.cj.jdbc.Driver")

                .setProperty(
                        "hibernate.connection.url",
                        "jdbc:mysql://localhost:3306/hotel_reservation_system")

                .setProperty(
                        "hibernate.connection.username",
                        "root")

                .setProperty(
                        "hibernate.connection.password",
                        password)

                .setProperty(
                        "hibernate.hbm2ddl.auto",
                        "validate")

                .buildSessionFactory();
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}