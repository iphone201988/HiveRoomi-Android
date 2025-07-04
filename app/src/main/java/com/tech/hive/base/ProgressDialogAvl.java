package com.tech.hive.base;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.tech.hive.R;

import java.util.Objects;

public class ProgressDialogAvl {
    Dialog dialog;

    public ProgressDialogAvl(Context context) {
        View view = View.inflate(context, R.layout.dialog_progress_avl, null);
        dialog = new Dialog(context, R.style.CustomDialogProgress);
        /// binding.ivPlusImage.playAnimation()
        Objects.requireNonNull(dialog.getWindow()).clearFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(view);
        dialog.setCancelable(false);
    }

    public void isLoading(boolean isLoading) {
        if (isLoading) {
            if (!dialog.isShowing()) {
                try {
                    System.gc();
                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                System.gc();
                if (dialog.isShowing())
                    dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}