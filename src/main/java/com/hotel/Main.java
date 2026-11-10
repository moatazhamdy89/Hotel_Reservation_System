package com.hotel;
import com.hotel.enums.PaymentMethod;
import com.hotel.enums.ViewType;
import com.hotel.manager.DashboardManager;
import com.hotel.manager.InvoiceManager;
import com.hotel.manager.ReservationManager;
import com.hotel.manager.ServiceOrderManager;
import com.hotel.manager.EventBookingManager;
import com.hotel.manager.RoomManager;
import com.hotel.manager.UserManager;
import com.hotel.model.*;
import com.hotel.repository.HibernateRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserManager userManager = new UserManager();
    private static final RoomManager roomManager = new RoomManager();
    private static final ReservationManager reservationManager = new ReservationManager();
    private static final InvoiceManager invoiceManager = new InvoiceManager();
    private static final ServiceOrderManager serviceOrderManager = new ServiceOrderManager();
    private static final EventBookingManager eventBookingManager = new EventBookingManager();
    private static final DashboardManager dashboardManager = new DashboardManager();
    private static final HibernateRepository repository = new HibernateRepository();
    public static void main(String[] args) {
        while (true) {
            heading("HOTEL RESERVATION SYSTEM");
            System.out.println("1. Login");
            System.out.println("2. Register as Guest");
            System.out.println("0. Exit");
            String choice = input("Choose: ");
            try {
                switch (choice) {
                    case "1":
                        login();
                        break;
                    case "2":
                        register();
                        break;
                    case "0":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private static void login() {
        heading("LOGIN");
        String email = input("Email: ").trim();
        String password = input("Password: ");
        User matched = null;
        for (User user : userManager.findAllUsers()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                matched = user;
                break;
            }
        }
        if (matched == null || !matched.login(email, password)) {
            System.out.println("Invalid email or password.");
            return;
        }
        System.out.println("Welcome, " + matched.getFirstName() + "!");
        try {
            if (matched instanceof Guest) {
                guestMenu((Guest) matched);
            } else if (matched instanceof Admin) {
                adminMenu(matched);
            } else if (matched instanceof Receptionist) {
                receptionistMenu(matched);
            } else {
                System.out.println("Unsupported user role.");
            }
        } finally {
            matched.logout();
        }
    }
    private static void register() {
        heading("GUEST REGISTRATION");
        String nationalId = input("National ID: ");
        String firstName = input("First Name: ");
        String lastName = input("Last Name: ");
        String gender = input("Gender: ");
        int age = readInt("Age: ");
        String email = input("Email: ");
        String password = input("Password: ");
        String nationality = input("Nationality: ");
        String phone = input("Phone: ");
        Guest guest = userManager.registerGuest(
                nationalId, firstName, lastName,
                gender, age, email, password,
                nationality, phone
        );
        System.out.println("Registration Successful!");
        System.out.println("Your User ID: " + guest.getUserID());
        System.out.println("Please login to continue.");
    }
    private static void guestMenu(Guest guest) {
        while (true) {
            heading("GUEST MENU - " + guest.getFirstName());
            System.out.println("1. View All Rooms");
            System.out.println("2. Make New Reservation");
            System.out.println("3. View My Reservations");
            System.out.println("4. Pay Reservation Deposit");
            System.out.println("5. View My Invoices");
            System.out.println("6. Pay Invoice");
            System.out.println("7. View My Profile (Loyalty Program)");
            System.out.println("8. Book Service / Activity");
            System.out.println("9. Book Event Hall");
            System.out.println("10. Pay Event Deposit");
            System.out.println("11. Add Hotel Review");
            System.out.println("0. Logout");
            String choice = input("Choose: ");
            try {
                switch (choice) {
                    case "1":
                        viewRooms();
                        break;
                    case "2":
                        makeReservation(guest);
                        break;
                    case "3":
                        viewMyReservations(guest);
                        break;
                    case "4":
                        payDeposit(guest);
                        break;
                    case "5":
                        viewMyInvoices(guest);
                        break;
                    case "6":
                        payInvoice(guest);
                        break;
                    case "7":
                        Guest updatedGuest = repository.findById(Guest.class, guest.getUserID());
                        updatedGuest.displayProfile();
                        updatedGuest.viewPoints();
                        break;
                    case "8":
                        bookService(guest);
                        break;
                    case "9":
                        bookEvent(guest);
                        break;
                    case "10":
                        payEventDeposit(guest);
                        break;
                    case "11":
                        addHotelReview(guest);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Invalid Choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private static Reservation getGuestReservation(Guest guest, int reservationId) {
        Reservation reservation = reservationManager.findReservationById(reservationId);
        if (reservation == null || reservation.getGuest() == null
                || reservation.getGuest().getUserID() != guest.getUserID()) {
            throw new IllegalArgumentException("Reservation not found for this guest");
        }
        return reservation;
    }
    private static void bookService(Guest guest) {
        heading("BOOK SERVICE / ACTIVITY");
        viewMyReservations(guest);
        int reservationId = readInt("Reservation ID: ");
        Reservation reservation = getGuestReservation(guest, reservationId);
        List<Service> services = serviceOrderManager.findAvailableServices();
        if (services.isEmpty()) {
            System.out.println("No available weekly activities.");
            return;
        }
        for (Service service : services) {
            System.out.println("ID: " + service.getServiceId() + " | " + service.getServiceName()
                    + " | Price: " + service.getPrice() + " EGP | Day: " + service.getServiceDay()
                    + " | Time: " + service.getStartTime());
        }
        int serviceId = readInt("Service ID: ");
        Service selected = serviceOrderManager.findServiceById(serviceId);
        if (selected == null || !selected.isAvailable() || selected.getServiceDay() == null) {
            System.out.println("Service not available.");
            return;
        }
        int quantity = readInt("Quantity: ");
        LocalDate date = readDate("Activity Date (YYYY-MM-DD): ");
        if (quantity <= 0 || date.isBefore(LocalDate.now())
                || date.isBefore(reservation.getCheckInDate()) || !date.isBefore(reservation.getCheckOutDate())
                || date.getDayOfWeek() != selected.getServiceDay()) {
            System.out.println("Invalid quantity or activity date.");
            return;
        }
        BigDecimal total = selected.getPrice().multiply(BigDecimal.valueOf(quantity));
        System.out.println("Total: " + total + " EGP");
        if (!input("Confirm service booking? (Y/N): ").equalsIgnoreCase("Y")) return;
        ServiceOrder order = serviceOrderManager.findOrderByReservationId(reservationId);
        if (order == null) {
            order = new ServiceOrder(reservation);
            if (!serviceOrderManager.addOrder(order)) {
                System.out.println("Could not create service order.");
                return;
            }
        }
        if (serviceOrderManager.addService(order.getOrderId(), serviceId, quantity, date)) {
            System.out.println("Service booked successfully! Order ID: " + order.getOrderId());
        } else {
            System.out.println("Service booking failed.");
        }
    }
    private static void bookEvent(Guest guest) {
        heading("BOOK EVENT HALL");
        List<EventHall> halls = eventBookingManager.findAllHalls();
        if (halls.isEmpty()) {
            System.out.println("No event halls available.");
            return;
        }
        for (EventHall hall : halls) {
            System.out.println("Hall ID: " + hall.getHallId() + " | " + hall.getHallName()
                    + " | Location: " + hall.getLocation() + " | Capacity: " + hall.getMaxCapacity()
                    + " | Rental Price: " + hall.getRentalPrice() + " EGP");
        }
        int hallId = readInt("Hall ID: ");
        EventHall hall = eventBookingManager.findHallById(hallId);
        if (hall == null) {
            System.out.println("Hall not found.");
            return;
        }
        int attendees = readInt("Number of attendees: ");
        if (attendees <= 0 || attendees > hall.getMaxCapacity()) {
            System.out.println("Invalid number of attendees for this hall.");
            return;
        }
        LocalDateTime start = LocalDateTime.parse(input("Start (YYYY-MM-DDTHH:MM): "));
        LocalDateTime end = LocalDateTime.parse(input("End (YYYY-MM-DDTHH:MM): "));
        if (start.isBefore(LocalDateTime.now()) || !end.isAfter(start)) {
            System.out.println("Invalid event dates.");
            return;
        }
        if (!eventBookingManager.isHallAvailable(hall, start, end)) {
            System.out.println("Hall is not available during this period.");
            return;
        }
        System.out.println("Hall rental price: " + hall.getRentalPrice() + " EGP");
        BigDecimal deposit = new BigDecimal(input("Required Deposit Amount (EGP): "));
        if (deposit.signum() < 0 || deposit.compareTo(hall.getRentalPrice()) > 0) {
            System.out.println("Invalid deposit amount.");
            return;
        }
        EventBooking booking = new EventBooking(hall, guest, start, end, deposit);
        if (!input("Confirm event booking? (Y/N): ").equalsIgnoreCase("Y")) return;
        if (eventBookingManager.addBooking(booking)) {
            System.out.println("Event booked! Booking ID: " + booking.getBookingId());
            System.out.println("Status: " + booking.getStatus());
            System.out.println("Deposit to pay: " + booking.getDepositAmount() + " EGP");
        } else {
            System.out.println("Event hall is no longer available.");
        }
    }
    private static void payEventDeposit(Guest guest) {
        heading("PAY EVENT DEPOSIT");
        boolean found = false;
        for (EventBooking booking : eventBookingManager.findAllBookings()) {
            if (booking.getGuest() != null && booking.getGuest().getUserID() == guest.getUserID()) {
                found = true;
                System.out.println("Booking ID: " + booking.getBookingId()
                        + " | Hall: " + booking.getHall().getHallName()
                        + " | Status: " + booking.getStatus()
                        + " | Deposit: " + booking.getDepositAmount()
                        + " | Paid: " + booking.isDepositPaid());
            }
        }
        if (!found) {
            System.out.println("No event bookings found.");
            return;
        }
        int bookingId = readInt("Booking ID: ");
        EventBooking booking = eventBookingManager.findBookingById(bookingId);
        if (booking == null || booking.getGuest() == null
                || booking.getGuest().getUserID() != guest.getUserID()) {
            System.out.println("Booking not found for this guest.");
            return;
        }
        System.out.println("Deposit: " + booking.getDepositAmount() + " EGP");
        if (!input("Confirm deposit payment? (Y/N): ").equalsIgnoreCase("Y")) return;
        eventBookingManager.payDeposit(bookingId);
        if (eventBookingManager.confirmBooking(bookingId)) {
            System.out.println("Deposit recorded. Event booking CONFIRMED!");
        } else {
            System.out.println("Deposit recorded, but booking could not be confirmed.");
        }
    }
    private static void addHotelReview(Guest guest) {
        heading("ADD HOTEL REVIEW");
        viewMyReservations(guest);
        int reservationId = readInt("Reservation ID: ");
        Reservation reservation = getGuestReservation(guest, reservationId);
        int rating = readInt("Rating (1-5): ");
        String comment = input("Comment: ");
        Review review = Review.create(reservation, rating, comment);
        System.out.println("Review submitted successfully! Review ID: " + review.getReviewId());
    }

    private static void viewRooms() {
        heading("AVAILABLE ROOMS");
        boolean found = false;
        for (Room room : roomManager.findAllRooms()) {
            if (!room.isAvailable()) {
                continue;
            }
            found = true;
            System.out.println(
                    "Room ID: " + room.getRoomId()
                            + " | Type: " + room.getClass().getSimpleName()
                            + " | View: " + room.getViewType()
                            + " | Price: " + room.getBasePrice() + " EGP"
            );
        }
        if (!found) {
            System.out.println("No available rooms found.");
        }
    }

    private static void makeReservation(Guest guest) {
        heading("NEW RESERVATION");
        LocalDate checkIn = readDate("Check-in (YYYY-MM-DD): ");
        LocalDate checkOut = readDate("Check-out (YYYY-MM-DD): ");
        if (checkIn.isBefore(LocalDate.now())
                || !checkOut.isAfter(checkIn)) {
            System.out.println("Invalid reservation dates.");
            return;
        }
        System.out.println("\nChoose Room Type:");
        System.out.println("1. Single Room");
        System.out.println("2. Double Room");
        System.out.println("3. Suite Room");
        System.out.println("4. VIP Room");
        int typeChoice = readInt("Choose: ");
        Class<? extends Room> roomType;
        switch (typeChoice) {
            case 1:
                roomType = SingleRoom.class;
                break;
            case 2:
                roomType = DoubleRoom.class;
                break;
            case 3:
                roomType = SuiteRoom.class;
                break;
            case 4:
                roomType = VIPRoom.class;
                break;
            default:
                System.out.println("Invalid room type.");
                return;
        }
        List<Accommodation> accommodations = repository.findAll(Accommodation.class);
        if (accommodations.isEmpty()) {
            System.out.println("No accommodation options available.");
            return;
        }
        System.out.println("\nChoose Accommodation:");
        for (int i = 0; i < accommodations.size(); i++) {
            System.out.println(
                    (i + 1) + ". " + accommodations.get(i)
            );
        }
        int accommodationChoice = readInt("Choose: ");
        if (accommodationChoice < 1
                || accommodationChoice > accommodations.size()) {
            System.out.println("Invalid accommodation option.");
            return;
        }
        Accommodation accommodation = accommodations.get(accommodationChoice - 1);
        ViewType[] views = ViewType.values();
        System.out.println("\nChoose Room View:");
        System.out.println("0. Any View");
        for (int i = 0; i < views.length; i++) {
            System.out.println((i + 1) + ". " + views[i]);
        }
        int viewChoice = readInt("Choose: ");
        if (viewChoice < 0 || viewChoice > views.length) {
            System.out.println("Invalid room view.");
            return;
        }
        ViewType selectedView = viewChoice == 0 ? null : views[viewChoice - 1];
        int numberOfGuests = readInt("Number of Guests: ");
        BigDecimal minPrice = new BigDecimal(input("Minimum Room Price (EGP): "));
        BigDecimal maxPrice = new BigDecimal(input("Maximum Room Price (EGP): "));
        RoomSearchFilter filter = new RoomSearchFilter(
                roomType,
                selectedView,
                minPrice,
                maxPrice,
                numberOfGuests
        );
        System.out.println("\nSearching for the best available room...");
        Room matchedRoom = roomManager.findMatchingRoom(
                filter, checkIn, checkOut
        );
        if (matchedRoom == null) {
            System.out.println("No available room matches your preferences."
            );
            return;
        }
        Reservation reservation = new Reservation(
                checkIn,
                checkOut,
                accommodation,
                guest,
                matchedRoom
        );
        heading("RESERVATION SUMMARY");
        System.out.println("Matched Room ID: "
                + matchedRoom.getRoomId());
        System.out.println("Room Number: "
                + matchedRoom.getRoomNumber());
        System.out.println("Room Type: "
                + matchedRoom.getClass().getSimpleName());
        System.out.println("Room View: "
                + matchedRoom.getViewType());
        System.out.println("Room Price/Night: "
                + matchedRoom.getBasePrice() + " EGP");
        System.out.println("Accommodation: "
                + accommodation.getType());
        System.out.println("Guests: " + numberOfGuests);
        System.out.println("Check-in: " + checkIn);
        System.out.println("Check-out: " + checkOut);
        System.out.println("Nights: "
                + reservation.calculateNights());
        System.out.println("Total Accommodation Cost: "
                + reservation.calculateAccommodationCost() + " EGP");
        System.out.println("Required Deposit (20%): "
                + reservation.getDepositAmount() + " EGP");
        String confirm = input("\nConfirm Reservation? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Reservation not saved.");
            return;
        }
        boolean saved = reservationManager.addReservation(reservation);
        if (saved) {
            System.out.println("\nReservation created successfully!");
            System.out.println("Reservation ID: "
                    + reservation.getReservationId());
            System.out.println("Status: " + reservation.getStatus());
            System.out.println("Please pay the deposit to confirm your reservation."
            );
        } else {
            System.out.println("Room is no longer available for these dates."
            );
        }
    }
    private static void viewMyReservations(Guest guest) {
        heading("MY RESERVATIONS");
        int count = 0;
        for (Reservation reservation :
                reservationManager.findAllReservations()) {
            if (reservation.getGuest().getUserID()
                    != guest.getUserID()) {
                continue;
            }
            printReservation(reservation);
            count++;
        }
        if (count == 0) {
            System.out.println("No reservations found.");
        }
    }
    private static void payDeposit(Guest guest) {
        heading("PAY RESERVATION DEPOSIT");
        viewMyReservations(guest);
        int reservationId = readInt("Reservation ID: ");
        Reservation reservation = reservationManager.findReservationById(reservationId);
        if (reservation == null
                || reservation.getGuest().getUserID()
                != guest.getUserID()) {
            System.out.println("Reservation not found.");
            return;
        }
        System.out.println(
                "Deposit Amount: "
                        + reservation.getDepositAmount() + " EGP"
        );
        if (reservation.isDepositPaid()) {
            System.out.println("Deposit already paid.");
            return;
        }
        String confirm = input("Confirm deposit payment? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            return;
        }
        Reservation updated = reservationManager.payDeposit(reservationId);
        System.out.println("Deposit payment recorded!");
        System.out.println("Reservation Status: " + updated.getStatus());
    }
    private static void viewMyInvoices(Guest guest) {
        heading("MY INVOICES");
        int count = 0;
        for (Invoice invoice : invoiceManager.findAllInvoices()) {
            if (!(invoice instanceof RoomInvoice)) {
                continue;
            }
            RoomInvoice roomInvoice = (RoomInvoice) invoice;
            if (roomInvoice.getReservation().getGuest().getUserID()
                    != guest.getUserID()) {
                continue;
            }
            roomInvoice.displayInvoice();
            System.out.println("------------------------------");
            count++;
        }
        if (count == 0) {
            System.out.println("No room invoices found. Invoices are issued after check-in."
            );
        }
    }
    private static void payInvoice(Guest guest) {
        heading("PAY INVOICE");
        viewMyInvoices(guest);
        int invoiceId = readInt("Invoice ID: ");
        Invoice invoice = invoiceManager.findInvoiceById(invoiceId);
        if (!(invoice instanceof RoomInvoice)) {
            System.out.println("Room invoice not found.");
            return;
        }
        RoomInvoice roomInvoice = (RoomInvoice) invoice;
        if (roomInvoice.getReservation().getGuest().getUserID()
                != guest.getUserID()) {
            System.out.println("Invoice not found.");
            return;
        }
        if (invoice.isPaid()) {
            System.out.println("Invoice already paid.");
            return;
        }
        PaymentMethod[] methods = PaymentMethod.values();
        for (int i = 0; i < methods.length; i++) {
            System.out.println((i + 1) + ". " + methods[i]);
        }
        int option = readInt("Payment method: ");
        if (option < 1 || option > methods.length) {
            System.out.println("Invalid payment method.");
            return;
        }
        PaymentMethod method = methods[option - 1];
        System.out.println(
                "Remaining Amount: "
                        + invoice.getRemainingAmount() + " EGP"
        );
        String confirm = input("Confirm payment? (Y/N): ");
        if (!confirm.equalsIgnoreCase("Y")) {
            return;
        }
        Invoice paid = invoiceManager.payInvoice(invoiceId, method);
        System.out.println("Payment completed successfully!");
        paid.displayInvoice();
    }
    private static void adminMenu(User admin) {
        while (true) {
            heading("ADMIN MENU - " + admin.getFirstName());
            System.out.println("1. Hotel Dashboard");
            System.out.println("2. View All Rooms");
            System.out.println("3. View All Reservations");
            System.out.println("4. View All Invoices");
            System.out.println("5. View All Users");
            System.out.println("6. Register New Admin");
            System.out.println("7. Register New Receptionist");
            System.out.println("0. Logout");
            String choice = input("Choose: ");
            try {
                switch (choice) {
                    case "1":
                        dashboardManager.displayDashboard();
                        break;
                    case "2":
                        viewRooms();
                        break;
                    case "3":
                        viewAllReservations();
                        break;
                    case "4":
                        viewAllInvoices();
                        break;
                    case "5":
                        viewAllUsers();
                        break;
                    case "6":
                        registerNewAdmin(admin);
                        break;
                    case "7":
                        registerNewReceptionist(admin);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private static void registerNewAdmin(User currentAdmin) {
        heading("REGISTER NEW ADMIN");
        String nationalId = input("National ID: ");
        String firstName = input("First Name: ");
        String lastName = input("Last Name: ");
        String gender = input("Gender: ");
        int age = readInt("Age: ");
        String email = input("Email: ");
        String password = input("Password: ");
        Admin created = userManager.registerAdmin(
                currentAdmin.getUserID(), nationalId, firstName,
                lastName, gender, age, email, password
        );
        System.out.println("Admin registered successfully!");
        System.out.println("New Admin User ID: " + created.getUserID());
    }
    private static void registerNewReceptionist(User currentAdmin) {
        heading("REGISTER NEW RECEPTIONIST");
        String nationalId = input("National ID: ");
        String firstName = input("First Name: ");
        String lastName = input("Last Name: ");
        String gender = input("Gender: ");
        int age = readInt("Age: ");
        String email = input("Email: ");
        String password = input("Password: ");
        String phone = input("Phone: ");
        String shift = input("Shift: ");
        Receptionist created = userManager.registerReceptionist(
                currentAdmin.getUserID(), nationalId, firstName,
                lastName, gender, age, email, password, phone, shift
        );
        System.out.println("Receptionist registered successfully!");
        System.out.println("New Receptionist User ID: " + created.getUserID());
    }
    private static void receptionistMenu(User receptionist) {
        while (true) {
            heading("RECEPTIONIST MENU - " + receptionist.getFirstName());
            System.out.println("1. View Available Rooms");
            System.out.println("2. View All Reservations");
            System.out.println("3. View All Invoices");
            System.out.println("4. Check-in Guest");
            System.out.println("5. Check-out Guest");
            System.out.println("0. Logout");
            String choice = input("Choose: ");
            try {
                switch (choice) {
                    case "1": viewRooms(); break;
                    case "2": viewAllReservations(); break;
                    case "3": viewAllInvoices(); break;
                    case "4": checkInGuest(); break;
                    case "5": checkOutGuest(); break;
                    case "0": return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    private static void checkInGuest() {
        heading("GUEST CHECK-IN");
        int id = readInt("Reservation ID: ");
        Reservation reservation = reservationManager.findReservationById(id);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }
        reservation.checkIn();
        reservationManager.updateReservation(reservation);
        System.out.println("Check-in successful! Status: " + reservation.getStatus());
    }
    private static void checkOutGuest() {
        heading("GUEST CHECK-OUT");
        int id = readInt("Reservation ID: ");
        Reservation reservation = reservationManager.findReservationById(id);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }
        reservation.checkOut();
        reservationManager.updateReservation(reservation);
        System.out.println("Check-out successful! Status: " + reservation.getStatus());
    }
    private static void viewAllReservations() {
        heading("ALL RESERVATIONS");
        List<Reservation> reservations = reservationManager.findAllReservations();
        for (Reservation reservation : reservations) {
            printReservation(reservation);
        }
        System.out.println("------------------------------");
        System.out.println("Total: " + reservations.size());
    }
    private static void printReservation(Reservation reservation) {
        System.out.println("------------------------------");
        System.out.println(
                "Reservation ID: " + reservation.getReservationId()
        );
        System.out.println(
                "Guest: " + reservation.getGuest().getFirstName()
                        + " " + reservation.getGuest().getLastName()
        );
        System.out.println(
                "Room ID: " + reservation.getRoom().getRoomId()
        );
        System.out.println(
                "Dates: " + reservation.getCheckInDate()
                        + " to " + reservation.getCheckOutDate()
        );
        System.out.println("Status: " + reservation.getStatus());
        System.out.println(
                "Deposit Paid: " + reservation.isDepositPaid()
        );
    }
    private static void viewAllInvoices() {
        heading("ALL INVOICES");
        List<Invoice> invoices = invoiceManager.findAllInvoices();
        for (Invoice invoice : invoices) {
            invoice.displayInvoice();
            System.out.println("------------------------------");
        }
        System.out.println("Total: " + invoices.size());
    }
    private static void viewAllUsers() {
        heading("ALL USERS");
        List<User> users = userManager.findAllUsers();
        for (User user : users) {
            user.displayProfile();
            System.out.println("------------------------------");
        }
        System.out.println("Total: " + users.size());
    }

    private static void heading(String title) {
        System.out.println("\n========================================");
        System.out.println("           " + title);
        System.out.println("========================================");
    }
    private static String input(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        return Integer.parseInt(input(prompt));
    }
    private static LocalDate readDate(String prompt) {
        return LocalDate.parse(input(prompt));
    }
}
