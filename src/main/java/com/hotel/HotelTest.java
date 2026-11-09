
package com.hotel;

import com.hotel.utils.HibernateUtil;

public class HotelTest {

    public static void main(String[] args) {

        try {
            HibernateUtil.getSessionFactory();

            System.out.println(
                    "Hibernate connected successfully!"
            );

        } catch (Exception e) {
            System.out.println(
                    "Hibernate connection or mapping failed!"
            );

            e.printStackTrace();
        }
    }
}
