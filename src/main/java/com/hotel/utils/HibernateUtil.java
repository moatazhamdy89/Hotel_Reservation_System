package com.hotel.utils;

import com.hotel.model.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory =
            buildSessionFactory();

    private static SessionFactory buildSessionFactory() {

        String username = System.getenv("HOTEL_DB_USER");
        String password = System.getenv("HOTEL_DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "HOTEL_DB_PASSWORD is missing");
        }

        return new Configuration()
                .addAnnotatedClass(Accommodation.class)
                .addAnnotatedClass(Reservation.class)

                .addAnnotatedClass(Invoice.class)
                .addAnnotatedClass(RoomInvoice.class)
                .addAnnotatedClass(EventInvoice.class)

                .addAnnotatedClass(EventHall.class)
                .addAnnotatedClass(EventBooking.class)
                .addAnnotatedClass(Service.class)
                .addAnnotatedClass(ServiceOrder.class)

                .addAnnotatedClass(Room.class)
                .addAnnotatedClass(SingleRoom.class)
                .addAnnotatedClass(DoubleRoom.class)
                .addAnnotatedClass(VIPRoom.class)
                .addAnnotatedClass(SuiteRoom.class)

                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Guest.class)
                .addAnnotatedClass(LoyaltyProgram.class)
                .addAnnotatedClass(Receptionist.class)







                .setProperty(
                        "hibernate.connection.driver_class",
                        "com.mysql.cj.jdbc.Driver")

                .setProperty(
                        "hibernate.connection.url",
                        "jdbc:mysql://10.29.214.187:3306/hotel_reservation_system")

                .setProperty(
                        "hibernate.connection.username",
                        username)

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
