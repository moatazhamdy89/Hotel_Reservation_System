
package com.hotel.manager;

import com.hotel.enums.ReservationStatus;
import com.hotel.model.Invoice;
import com.hotel.model.Reservation;
import com.hotel.model.Room;

import java.time.LocalDate;
import java.util.List;

public class DashboardManager {

    private LocalDate reportDate;

    private final ReservationManager reservationManager;
    private final InvoiceManager invoiceManager;
    private final RoomManager roomManager;

    public DashboardManager() {
        this(
                LocalDate.now(),
                new ReservationManager(),
                new InvoiceManager(),
                new RoomManager()
        );
    }

    public DashboardManager(
            LocalDate reportDate,
            ReservationManager reservationManager,
            InvoiceManager invoiceManager,
            RoomManager roomManager) {

        if (reportDate == null
                || reservationManager == null
                || invoiceManager == null
                || roomManager == null) {
            throw new IllegalArgumentException(
                    "Dashboard dependencies are required");
        }

        this.reportDate = reportDate;
        this.reservationManager = reservationManager;
        this.invoiceManager = invoiceManager;
        this.roomManager = roomManager;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        if (reportDate == null) {
            throw new IllegalArgumentException(
                    "Report date is required");
        }

        this.reportDate = reportDate;
    }

    // Scheduled check-ins on report date
    public long calculateCheckInsCount() {

        return reservationManager.findAllReservations()
                .stream()
                .filter(r -> r.getStatus()
                        == ReservationStatus.CONFIRMED)
                .filter(r -> reportDate.equals(
                        r.getCheckInDate()))
                .count();
    }

    // Scheduled check-outs on report date
    public long calculateCheckOutsCount() {

        return reservationManager.findAllReservations()
                .stream()
                .filter(r -> r.getStatus()
                        == ReservationStatus.CONFIRMED)
                .filter(r -> reportDate.equals(
                        r.getCheckOutDate()))
                .count();
    }

    // Reservations created on report date
    public long calculateNewBookingsCount() {

        return reservationManager.findAllReservations()
                .stream()
                .filter(r -> reportDate.equals(
                        r.getBookingDate()))
                .count();
    }

    // Reservations cancelled on report date
    public long calculateCancellationsCount() {

        return reservationManager.findAllReservations()
                .stream()
                .filter(r -> r.getStatus()
                        == ReservationStatus.CANCELLED)
                .filter(r -> reportDate.equals(
                        r.getCancellationDate()))
                .count();
    }

    // All reservations stored in the database
    public int getTotalReservations() {

        return reservationManager
                .findAllReservations()
                .size();
    }

    // Total amount of paid invoices
    public double calculateTotalRevenue() {

        double total = invoiceManager.findAllInvoices()
                .stream()
                .filter(Invoice::isPaid)
                .mapToDouble(Invoice::getTotalAmount)
                .sum();

        return Math.round(total * 100.0) / 100.0;
    }

    // Show projected room availability for 7 days
    public void displayWeeklyAvailability() {

        List<Room> rooms =
                roomManager.findAllRooms();

        List<Reservation> reservations =
                reservationManager.findAllReservations();

        System.out.println(
                "\n===== WEEKLY ROOM AVAILABILITY =====");

        for (int day = 0; day < 7; day++) {

            LocalDate date = reportDate.plusDays(day);
            int availableCount = 0;

            for (Room room : rooms) {

                boolean booked = false;

                for (Reservation reservation : reservations) {

                    if (reservation.getStatus()
                            == ReservationStatus.CANCELLED) {
                        continue;
                    }

                    if (reservation.getRoom().getRoomId()
                            != room.getRoomId()) {
                        continue;
                    }

                    boolean occupiesDate =
                            !date.isBefore(
                                    reservation.getCheckInDate())
                                    && date.isBefore(
                                    reservation.getCheckOutDate());

                    if (occupiesDate) {
                        booked = true;
                        break;
                    }
                }

                if (!booked) {
                    availableCount++;
                }
            }

            System.out.println(
                    date + " | Available: "
                            + availableCount + "/" + rooms.size()
            );
        }
    }

    // Display complete dashboard
    public void displayDashboard() {

        System.out.println(
                "\n========== HOTEL DASHBOARD ==========");

        System.out.println("Report Date: " + reportDate);

        System.out.println(
                "Scheduled Check-ins: "
                        + calculateCheckInsCount());

        System.out.println(
                "Scheduled Check-outs: "
                        + calculateCheckOutsCount());

        System.out.println(
                "New Bookings: "
                        + calculateNewBookingsCount());

        System.out.println(
                "Cancellations: "
                        + calculateCancellationsCount());

        System.out.println(
                "Total Reservations: "
                        + getTotalReservations());

        System.out.printf(
                "Total Paid Invoice Amount: %.2f EGP%n",
                calculateTotalRevenue());

        displayWeeklyAvailability();

        System.out.println(
                "=====================================\n");
    }
}
