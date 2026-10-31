package com.hotel.model;

import com.hotel.enums.MembershipLevel;
import jakarta.persistence.*;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "loyalty_programs")
public class LoyaltyProgram {

    @Id
    @Column(name = "guest_id")
    private int guestId;


    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @Column(name = "points")
    private int points;

    @JdbcTypeCode(SqlTypes.DECIMAL)
    @Column(name = "discount", precision = 10, scale = 2)
    private double discount;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_level")
    private MembershipLevel membershipLevel;

    // Required by Hibernate
    protected LoyaltyProgram() {
    }

    public LoyaltyProgram(int points) {
        if (points < 0) {
            throw new IllegalArgumentException(
                    "Points cannot be negative");
        }

        this.points = points;
        updateMembershipLevel();
    }

    // Connect the loyalty program to its Guest
    void setGuest(Guest guest) {
        if (guest == null) {
            throw new IllegalArgumentException(
                    "Guest is required");
        }

        this.guest = guest;
    }

    public void addPoints(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Points to add must be positive");
        }

        points += amount;
        updateMembershipLevel();
    }

    public void updateMembershipLevel() {
        if (points >= 300) {
            membershipLevel = MembershipLevel.GOLD;
            discount = 0.10;
        } else if (points >= 100) {
            membershipLevel = MembershipLevel.SILVER;
            discount = 0.05;
        } else {
            membershipLevel = MembershipLevel.BRONZE;
            discount = 0.0;
        }
    }

    public double calculateDiscount() {
        return discount;
    }

    public int getPoints() {
        return points;
    }

    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }
}