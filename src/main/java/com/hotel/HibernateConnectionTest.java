package com.hotel;

import com.hotel.utils.HibernateUtil;
import org.hibernate.Session;

public class HibernateConnectionTest {

    public static void main(String[] args) {

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            session.doWork(connection -> {
                System.out.println(
                        "Database: " + connection.getCatalog()
                );

                System.out.println(
                        "Connection is valid: "
                                + connection.isValid(5)
                );
            });

            System.out.println("Hibernate connected successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}