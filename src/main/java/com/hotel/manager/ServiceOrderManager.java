
package com.hotel.manager;

import com.hotel.model.Reservation;
import com.hotel.model.Service;
import com.hotel.model.ServiceOrder;
import com.hotel.model.ServiceOrderItem;
import com.hotel.repository.HibernateRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ServiceOrderManager {

    private final HibernateRepository repository = new HibernateRepository();

    public boolean addOrder(ServiceOrder order) {
        if (order == null) throw new IllegalArgumentException("Service order cannot be null");
        if (order.getOrderId() > 0 || order.getReservation() == null) return false;

        int reservationId = order.getReservation().getReservationId();
        Reservation reservation = repository.findById(Reservation.class, reservationId);

        if (reservation == null || findOrderByReservationId(reservationId) != null) return false;

        order.setReservation(reservation);
        repository.save(order);
        return true;
    }

    public ServiceOrder findOrderById(int orderId) {
        return orderId <= 0 ? null : repository.findById(ServiceOrder.class, orderId);
    }

    public List<ServiceOrder> findAllOrders() {
        return repository.findAll(ServiceOrder.class);
    }

    public ServiceOrder findOrderByReservationId(int reservationId) {
        if (reservationId <= 0) return null;
        for (ServiceOrder order : findAllOrders())
            if (order.getReservation().getReservationId() == reservationId) return order;
        return null;
    }

    public boolean addService(int orderId, int serviceId, int quantity, LocalDate date) {
        ServiceOrder order = findOrderById(orderId);
        Service service = findServiceById(serviceId);
        if (order == null || service == null) return false;

        order.addService(service, quantity, date);
        repository.update(order);
        return true;
    }

    public boolean removeService(int orderId, int serviceId) {
        ServiceOrder order = findOrderById(orderId);
        if (order == null) return false;

        Service service = order.getServices().stream()
                .filter(s -> s.getServiceId() == serviceId)
                .findFirst().orElse(null);

        if (service == null) return false;

        order.removeService(service);
        repository.update(order);
        return true;
    }

    public boolean updateServiceQuantity(int orderId, int serviceId, int quantity) {
        ServiceOrder order = findOrderById(orderId);
        if (order == null) return false;

        for (ServiceOrderItem item : order.getItems()) {
            if (item.getService().getServiceId() == serviceId) {
                item.setQuantity(quantity);
                repository.update(order);
                return true;
            }
        }
        return false;
    }

    public ServiceOrder updateOrder(ServiceOrder order) {
        if (order == null || order.getOrderId() <= 0)
            throw new IllegalArgumentException("A saved service order is required");
        return repository.update(order);
    }

    public List<Service> findAvailableServices() {
        return repository.findAll(Service.class).stream()
                .filter(s -> s.isAvailable() && s.getServiceDay() != null)
                .toList();
    }

    public Service findServiceById(int serviceId) {
        return serviceId <= 0 ? null : repository.findById(Service.class, serviceId);
    }

    public List<LocalDate> getAvailableDates(int reservationId, int serviceId) {
        Reservation reservation = repository.findById(Reservation.class, reservationId);
        Service service = findServiceById(serviceId);
        if (reservation == null || service == null) return List.of();
        return service.getAvailableDates(reservation);
    }

    public BigDecimal calculateServiceTotal(int serviceId, int quantity) {
        Service service = findServiceById(serviceId);
        if (service == null) throw new IllegalArgumentException("Service not found");
        return service.calculateTotal(quantity);
    }

    public boolean bookService(int reservationId, int serviceId, int quantity, LocalDate date) {
        Reservation reservation = repository.findById(Reservation.class, reservationId);
        Service service = findServiceById(serviceId);
        if (reservation == null || service == null || !service.canBookOn(reservation, date))
            return false;

        ServiceOrder order = findOrderByReservationId(reservationId);
        if (order == null) {
            order = new ServiceOrder(reservation);
            if (!addOrder(order)) return false;
        }

        return addService(order.getOrderId(), serviceId, quantity, date);
    }
}
