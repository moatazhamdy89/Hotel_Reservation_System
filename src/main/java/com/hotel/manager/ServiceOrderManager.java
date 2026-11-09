
package com.hotel.manager;

import com.hotel.model.Reservation;
import com.hotel.model.Service;
import com.hotel.model.ServiceOrder;
import com.hotel.model.ServiceOrderItem;
import com.hotel.repository.HibernateRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ServiceOrderManager {

    private final HibernateRepository repository =
            new HibernateRepository();

    // Add a new service order
    public boolean addOrder(ServiceOrder order) {

        if (order == null) {
            throw new IllegalArgumentException(
                    "Service order cannot be null");
        }

        if (order.getOrderId() > 0) {
            return false;
        }

        if (order.getReservation() == null) {
            return false;
        }

        int reservationId =
                order.getReservation().getReservationId();

        Reservation reservation =
                repository.findById(
                        Reservation.class, reservationId);

        if (reservation == null) {
            return false;
        }

        // One service order per reservation
        if (findOrderByReservationId(reservationId) != null) {
            return false;
        }

        order.setReservation(reservation);

        if (order.getOrderTime() == null) {
            order.setOrderTime(LocalDateTime.now());
        }

        repository.save(order);
        return true;
    }

    // Find order by ID
    public ServiceOrder findOrderById(int orderId) {

        if (orderId <= 0) {
            return null;
        }

        return repository.findById(
                ServiceOrder.class, orderId);
    }

    // Find all orders
    public List<ServiceOrder> findAllOrders() {

        return repository.findAll(ServiceOrder.class);
    }

    // Find order by reservation ID
    public ServiceOrder findOrderByReservationId(
            int reservationId) {

        if (reservationId <= 0) {
            return null;
        }

        for (ServiceOrder order : findAllOrders()) {

            if (order.getReservation()
                    .getReservationId() == reservationId) {
                return order;
            }
        }

        return null;
    }

    // Add service with quantity
    public boolean addService(
            int orderId,
            int serviceId,
            int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        ServiceOrder order = findOrderById(orderId);

        Service service = repository.findById(
                Service.class, serviceId);

        if (order == null || service == null) {
            return false;
        }

        if (!service.isAvailable()) {
            return false;
        }

        order.addService(service, quantity);

        repository.update(order);
        return true;
    }

    // Remove a service from an order
    public boolean removeService(int orderId, int serviceId) {

        ServiceOrder order = findOrderById(orderId);

        if (order == null) {
            return false;
        }

        Service serviceToRemove = null;

        for (ServiceOrderItem item : order.getItems()) {
            if (item.getService().getServiceId() == serviceId) {
                serviceToRemove = item.getService();
                break;
            }
        }

        if (serviceToRemove == null) {
            return false;
        }

        order.removeService(serviceToRemove);
        repository.update(order);

        return true;
    }

    // Update service quantity
    public boolean updateServiceQuantity(
            int orderId,
            int serviceId,
            int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero");
        }

        ServiceOrder order = findOrderById(orderId);

        if (order == null) {
            return false;
        }

        for (ServiceOrderItem item : order.getItems()) {

            if (item.getService()
                    .getServiceId() == serviceId) {

                item.setQuantity(quantity);

                repository.update(order);
                return true;
            }
        }

        return false;
    }


    // Update an existing order
    public ServiceOrder updateOrder(ServiceOrder order) {

        if (order == null || order.getOrderId() <= 0) {
            throw new IllegalArgumentException(
                    "A saved service order is required");
        }

        return repository.update(order);
    }
}
