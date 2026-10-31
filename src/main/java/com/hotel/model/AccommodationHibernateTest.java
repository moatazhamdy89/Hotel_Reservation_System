package com.hotel.model;

import com.hotel.repository.HibernateRepository;
import com.hotel.utils.HibernateUtil;
import com.hotel.enums.AccommodationType;
import org.hibernate.Session;

public class AccommodationHibernateTest {

    public static void main(String[] args) {

       EventHall eventHall = new EventHall("Nile City ", "New Cairo",
        50000, 300);
        HibernateRepository r = new HibernateRepository();
        r.save(eventHall);
    }
}