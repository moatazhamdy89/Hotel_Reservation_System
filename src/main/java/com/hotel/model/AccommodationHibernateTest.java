package com.hotel.model;

import com.hotel.config.HibernateUtil;
import com.hotel.enums.AccommodationType;
import org.hibernate.Session;

public class AccommodationHibernateTest {

    public static void main(String[] args) {

        try {
            try (Session session =
                         HibernateUtil.getSessionFactory().openSession()) {

                Accommodation accommodation =
                        session.find(
                                Accommodation.class,
                                AccommodationType.BREAKFAST
                        );

                if (accommodation != null) {
                    System.out.println("=== ACCOMMODATION FOUND ===");
                    System.out.println("Type: " +
                            accommodation.getType());
                    System.out.println("Price: " +
                            accommodation.getPricePerNight());
                } else {
                    System.out.println("Accommodation not found");
                }
            }

        } finally {
            HibernateUtil.getSessionFactory().close();
        }
    }
}