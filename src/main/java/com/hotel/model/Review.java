
package com.hotel.model;

import com.hotel.enums.ReservationStatus;
import com.hotel.repository.HibernateRepository;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private int reviewId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reservation_id",
            nullable = false,
            unique = true
    )
    private Reservation reservation;

    @Column(name = "rating")
    private int rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "date")
    private LocalDate date;

    // Required by Hibernate
    protected Review() {
    }

    // Creates a NEW Review object
    private Review(Reservation reservation,
                   int rating,
                   String comment) {

        validate(reservation, rating);

        this.reservation = reservation;
        this.rating = rating;
        this.comment = normalize(comment);
        this.date = LocalDate.now();
    }

    // Create and save a review
    public static Review create(
            Reservation reservation,
            int rating,
            String comment) {

        validate(reservation, rating);

        if (!reservation.isSaved()) {
            throw new IllegalStateException(
                    "Reservation is not saved in the database yet");
        }

        HibernateRepository repository =
                new HibernateRepository();

        return repository.withTransaction(session -> {

            Reservation savedReservation =
                    session.find(
                            Reservation.class,
                            reservation.getReservationId()
                    );

            if (savedReservation == null) {
                throw new IllegalStateException(
                        "Reservation not found in database");
            }

            if (savedReservation.getStatus()
                    != ReservationStatus.CONFIRMED) {
                throw new IllegalStateException(
                        "Only a CONFIRMED reservation can be reviewed");
            }

            if (savedReservation.getCheckOutDate()
                    .isAfter(LocalDate.now())) {
                throw new IllegalStateException(
                        "You can review only after the stay ends");
            }

            Long count = session.createSelectionQuery(
                            "select count(r) from Review r " +
                                    "where r.reservation.reservationId = :id",
                            Long.class
                    )
                    .setParameter(
                            "id",
                            savedReservation.getReservationId()
                    )
                    .getSingleResult();

            if (count > 0) {
                throw new IllegalStateException(
                        "This reservation already has a review");
            }

            Review review = new Review(
                    savedReservation,
                    rating,
                    comment
            );

            session.persist(review);

            return review;
        });
    }

    // Edit an existing review
    public void editReview(
            int newRating,
            String newComment) {

        validate(reservation, newRating);

        if (reviewId <= 0) {
            throw new IllegalStateException(
                    "Save the review before editing");
        }

        String cleaned = normalize(newComment);

        HibernateRepository repository =
                new HibernateRepository();

        repository.withTransaction(session -> {

            Review savedReview =
                    session.find(Review.class, reviewId);

            if (savedReview == null) {
                throw new IllegalStateException(
                        "Review " + reviewId
                                + " not found in database");
            }

            savedReview.rating = newRating;
            savedReview.comment = cleaned;

            return null;
        });

        // Keep this Java object synchronized after commit
        this.rating = newRating;
        this.comment = cleaned;
    }

    // Find a review for a reservation
    public static Optional<Review> findFor(
            Reservation reservation) {

        if (reservation == null
                || !reservation.isSaved()) {
            return Optional.empty();
        }

        HibernateRepository repository =
                new HibernateRepository();

        return repository.withTransaction(session -> {

            Review review = session.createSelectionQuery(
                            "select r from Review r " +
                                    "join fetch r.reservation res " +
                                    "join fetch res.guest " +
                                    "where res.reservationId = :id",
                            Review.class
                    )
                    .setParameter(
                            "id",
                            reservation.getReservationId()
                    )
                    .uniqueResult();

            return Optional.ofNullable(review);
        });
    }

    public void displayReview() {
        System.out.println(
                "Review #" + reviewId
                        + " | Reservation #"
                        + reservation.getReservationId()
                        + " | Guest: "
                        + reservation.getGuest().getFirstName()
                        + " | " + date
        );

        System.out.println(
                "Rating: " + rating + "/5"
        );

        System.out.println(
                "Comment: "
                        + (comment == null ? "(none)" : comment)
        );
    }

    private static void validate(
            Reservation reservation,
            int rating) {

        if (reservation == null) {
            throw new IllegalArgumentException(
                    "Reservation is required");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5");
        }
    }

    private static String normalize(String c) {
        return (c == null || c.isBlank())
                ? null
                : c.trim();
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
