package com.hotel.model;

import com.hotel.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class HibernateTest {

    public static void main(String[] args) {

        try (Session session =
                     HibernateUtil.getSessionFactory().openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                // 1. Create a new EventHall object
                EventHall hall = new EventHall(
                        "Hibernate Test Hall",
                        "Floor 5",
                        12000,
                        100
                );


                // 2. Save using Hibernate
                session.persist(hall);
                System.out.println("BEFORE PERSIST:");
                System.out.println("Name: " + hall.getHallName());
                System.out.println("Location: " + hall.getLocation());
                System.out.println("Price: " + hall.getRentalPrice());
                System.out.println("Capacity: " + hall.getMaxCapacity());

                // 3. Send INSERT to MySQL
                session.flush();

                System.out.println("INSERT executed successfully!");
                System.out.println("Generated Hall ID: " + hall.getHallId());

                // 4. Undo changes
                tx.commit();

                System.out.println("ROLLBACK completed!");
                System.out.println("Test hall was not permanently saved.");

            } catch (Exception e) {

                if (tx.isActive()) {
                    tx.rollback();
                }

                e.printStackTrace();
            }

        } finally {
            HibernateUtil.getSessionFactory().close();
        }
    }
}