package com.tech.hive.ui.for_room_mate.home.cardstackview;

public enum SwipeableMethod {
    AutomaticAndManual,
    Automatic,
    Manual,
    None;

    boolean canSwipe() {
        return canSwipeAutomatically() || canSwipeManually();
    }

    boolean canSwipeAutomatically() {
        return this == AutomaticAndManual || this == Automatic;
    }

    boolean canSwipeManually() {
        return this == AutomaticAndManual || this == Manual;
    }
}
