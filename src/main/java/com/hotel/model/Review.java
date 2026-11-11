package com.hotel.model;

import com.hotel.enums.ReservationStatus;
import com.hotel.repository.HibernateRepository;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private int reviewId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(name = "rating")
    private int rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "date")
    private LocalDate date;

    protected Review() {}

    private Review(Reservation reservation, int rating, String comment) {
        validate(reservation, rating);
        this.reservation = reservation;
        this.rating = rating;
        this.comment = normalize(comment);
        this.date = LocalDate.now();
    }

    public static Review create(Reservation reservation, int rating, String comment) {
        validate(reservation, rating);

        if (!reservation.isSaved())
            throw new IllegalStateException("Reservation is not saved in the database yet");

        HibernateRepository repository = new HibernateRepository();

        Reservation savedReservation = repository.findById(
                Reservation.class, reservation.getReservationId());

        if (savedReservation == null)
            throw new IllegalStateException("Reservation not found in database");

        if (savedReservation.getStatus() != ReservationStatus.CHECKED_OUT)
            throw new IllegalStateException("You can review only after checkout");

        if (findFor(savedReservation).isPresent())
            throw new IllegalStateException("This reservation already has a review");

        Review review = new Review(savedReservation, rating, comment);
        return repository.save(review);
    }

    public void editReview(int newRating, String newComment) {
        validate(reservation, newRating);

        if (reviewId <= 0)
            throw new IllegalStateException("Save the review before editing");

        HibernateRepository repository = new HibernateRepository();
        Review savedReview = repository.findById(Review.class, reviewId);

        if (savedReview == null)
            throw new IllegalStateException("Review not found in database");

        savedReview.rating = newRating;
        savedReview.comment = normalize(newComment);

        repository.update(savedReview);

        this.rating = savedReview.rating;
        this.comment = savedReview.comment;
    }

    public static Optional<Review> findFor(Reservation reservation) {
        if (reservation == null || !reservation.isSaved())
            return Optional.empty();

        HibernateRepository repository = new HibernateRepository();

        for (Review review : repository.findAll(Review.class)) {
            if (review.getReservation().getReservationId()
                    == reservation.getReservationId())
                return Optional.of(review);
        }

        return Optional.empty();
    }

    public static List<Review> findAllReviews() {
        HibernateRepository repository = new HibernateRepository();
        List<Review> reviews = new ArrayList<>(repository.findAll(Review.class));

        for (int i = 0; i < reviews.size() - 1; i++) {
            for (int j = i + 1; j < reviews.size(); j++) {
                Review first = reviews.get(i);
                Review second = reviews.get(j);

                if (first.getDate().isBefore(second.getDate())
                        || (first.getDate().equals(second.getDate())
                        && first.getReviewId() < second.getReviewId())) {
                    reviews.set(i, second);
                    reviews.set(j, first);
                }
            }
        }

        return reviews;
    }

    public static double getAverageRating() {
        List<Review> reviews = findAllReviews();

        if (reviews.isEmpty()) return 0.0;

        int total = 0;
        for (Review review : reviews)
            total += review.getRating();

        return (double) total / reviews.size();
    }

    public static long getTotalReviews() {
        HibernateRepository repository = new HibernateRepository();
        long count = 0;

        for (Review review : repository.findAll(Review.class))
            count++;

        return count;
    }

    public void displayReview() {
        System.out.println(
                "Review #" + reviewId +
                        " | Reservation #" + reservation.getReservationId() +
                        " | Guest: " + reservation.getGuest().getFirstName() +
                        " | " + date
        );

        System.out.println("Rating: " + rating + "/5");
        System.out.println("Comment: " + (comment == null ? "(none)" : comment));
    }

    private static void validate(Reservation reservation, int rating) {
        if (reservation == null)
            throw new IllegalArgumentException("Reservation is required");

        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 1 and 5");
    }

    private static String normalize(String comment) {
        if (comment == null || comment.isBlank())
            return null;
        return comment.trim();
    }

    public int getReviewId() {
        return reviewId;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDate getDate() {
        return date;
    }
}