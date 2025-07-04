package com.tech.hive.ui.for_room_mate.home.cardstackview.internal;

import android.content.Context;

public final class DisplayUtil {

    private DisplayUtil() {}

    public static int dpToPx(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

}
