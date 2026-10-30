package com.hotel.model;
import com.hotel.enums.EventBookingStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventBookingManager {

    // Attributes
    private List<EventBooking> eventBookings;
    // Aggregation Relation

    // Constructor
    public EventBookingManager() {
        this.eventBookings = new ArrayList<>();
    }

    // Methods

    // التحقق مما إذا كانت القاعة متاحة خلال الفترة الزمنية المطلوبة
    public boolean isHallAvailable(EventHall hall, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        for (EventBooking booking : eventBookings) {
            // نتحقق أولاً أن الحجز ليس ملغى، وأنه لنفس القاعة
            if (booking.getStatus() != EventBookingStatus.CANCELLED &&
                    booking.getHall().getHallId() == hall.getHallId()) {

                LocalDateTime existingStart = booking.getStartDateTime();
                LocalDateTime existingEnd = booking.getEndDateTime();

                // المعادلة الرياضية للتحقق من تقاطع أو تداخل المواعيد
                // (بداية الحجز الجديد قبل نهاية القديم) AND (نهاية الحجز الجديد بعد بداية القديم)
                if (startDateTime.isBefore(existingEnd) && endDateTime.isAfter(existingStart)) {
                    return false; // يوجد تضارب، القاعة غير متاحة
                }
            }
        }
        return true; // القاعة متاحة ولا يوجد تضارب
    }

    // إضافة حجز جديد بعد التأكد من توفر القاعة
    public boolean addBooking(EventBooking booking) {
        if (booking == null || booking.getHall() == null) {
            return false;
        }

        // فحص توفر القاعة أولاً
        if (isHallAvailable(booking.getHall(), booking.getStartDateTime(), booking.getEndDateTime())) {
            eventBookings.add(booking);
            return true;
        }

        return false; // فشل الإضافة بسبب تضارب المواعيد
    }

    // البحث عن حجز باستخدام رقم الحجز (Booking ID)
    public EventBooking findBookingById(int bookingId) {
        for (EventBooking booking : eventBookings) {
            if (booking.getBookingId() == bookingId) {
                return booking;
            }
        }
        return null; // لم يتم العثور على الحجز
    }

    // إلغاء حجز معين وتخزين سبب الإلغاء
    public boolean cancelBooking(int bookingId, String reason) {
        EventBooking booking = findBookingById(bookingId);

        if (booking != null) {
            try {
                // استدعاء دالة الإلغاء (بما أنها void لن نكتب قبله return)
                booking.cancelBooking(reason);
                return true; // تمت الإلغاء بنجاح
            } catch (IllegalStateException | IllegalArgumentException e) {
                // في حال فشل الشروط وتم رمي Exception، نرجع false
                return false;
            }
        }

        return false; // الحجز غير موجود
    }
}