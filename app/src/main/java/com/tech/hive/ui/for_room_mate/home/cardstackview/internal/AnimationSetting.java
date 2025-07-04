package com.tech.hive.ui.for_room_mate.home.cardstackview.internal;

import android.view.animation.Interpolator;

import com.tech.hive.ui.for_room_mate.home.cardstackview.Direction;


public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
