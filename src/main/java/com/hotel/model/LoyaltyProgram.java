package com.hotel.model;

import com.hotel.enums.MembershipLevel;

public class LoyaltyProgram {
    private int points;
    private double discount;                 // a rate such as 0.05, not a money amount
    private MembershipLevel membershipLevel;

    public LoyaltyProgram(int points) {
        if (points < 0) throw new IllegalArgumentException("Points cannot be negative");
        this.points = points;
        updateMembershipLevel();             // level and discount are always derived from points
    }

    public void addPoints(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Points to add must be positive");
        points += amount;
        updateMembershipLevel();
    }

    public void updateMembershipLevel() {
        if (points >= 300)      { membershipLevel = MembershipLevel.GOLD;   discount = 0.10; } // 10 %
        else if (points >= 100) { membershipLevel = MembershipLevel.SILVER; discount = 0.05; } // 5 %
        else                    { membershipLevel = MembershipLevel.BRONZE; discount = 0.0;  } // 0 %
    }

    public double calculateDiscount()          { return discount; }
    public int getPoints()                     { return points; }
    public MembershipLevel getMembershipLevel(){ return membershipLevel; }
}
