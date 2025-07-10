package com.tech.hive.base.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.tech.hive.R
import com.tech.hive.data.api.Constants
import com.tech.hive.data.model.HomeRoomTData
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object BindingUtils {
    var latitude: Double? = null
    var longitude: Double? = null

    fun navigateWithSlide(navController: NavController, destinationId: Int, bundle: Bundle?) {
        val navOptions = NavOptions.Builder().build()
        navController.navigate(destinationId, bundle, navOptions)
    }


    inline fun <reified T> parseJson(json: String): T? {
        return try {
            val gson = Gson()
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    fun preventMultipleClick(view: View) {
        view.isEnabled = false
        view.postDelayed({
            try {
                view.isEnabled = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 1000)
    }


    fun hasPermissions(context: Context?, permissions: Array<String>?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context, permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }


    @BindingAdapter("setImageInt")
    @JvmStatic
    fun setImageInt(image: AppCompatImageView, url: Int?) {
        if (url != null) {
            image.setImageResource(url)
        }
    }

    @BindingAdapter("setImageFromUrl")
    @JvmStatic
    fun setImageFromUrl(image: ShapeableImageView, url: String?) {
        if (url != null) {
            if (url.isNotEmpty()) {
                Glide.with(image.context).load(Constants.BASE_URL_IMAGE + url)
                    .placeholder(R.drawable.progress_animation_small)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(image)
            }
        }
    }


    @BindingAdapter("setSingleData")
    @JvmStatic
    fun setSingleData(text: AppCompatTextView, date: HomeRoomTData?) {
        if (date != null) {
            val address = if (date.address?.isNotEmpty() == true) {
                "📍 ${date.address}"
            } else {
                "📍 No Address found"
            }

            val roomType = if (date.roomType?.isNotEmpty() == true) {
                "🛏 ${date.roomType}"
            } else {
                "🛏 Not found"
            }

            val roommates = if (date.roommates?.isNotEmpty() == true) {
                "\uD83D\uDC65 ${date.roommates.size} Roommates"
            } else {
                "\uD83D\uDC65 0 Roommates"
            }
            text.text = "$address $roomType $roommates"
        }

    }


    @BindingAdapter("setSmokingData")
    @JvmStatic
    fun setSmokingData(text: AppCompatTextView, date: HomeRoomTData?) {
        if (date != null) {
            val smokingText = if (date.smoke?.contains("yes", ignoreCase = true) == true) {
                "\uD83D\uDEAD Yes Smoking"
            } else {
                "\uD83D\uDEAD No Smoking"
            }

            val petsText = if (date.pets == true) {
                "\uD83D\uDC36 Pets Allowed"
            } else {
                "\uD83D\uDC36 No Pets"
            }


            text.text = "$smokingText $petsText"
        }
    }


    @BindingAdapter("setDate")
    @JvmStatic
    fun setDate(text: AppCompatTextView, date: String?) {
        if (date?.isNotEmpty() == true) {
            val utcTime = date
            val formattedTime = convertUtcToLocalTime(utcTime)
            text.text = formattedTime
        }
    }

    fun convertUtcToLocalTime(utcTime: String): String {
        val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        val date = utcFormat.parse(utcTime)

        val localFormat = SimpleDateFormat("hh:mm a", Locale.US)
        localFormat.timeZone = TimeZone.getDefault()  // Local time zone

        return localFormat.format(date!!)
    }

    @BindingAdapter("setProgress")
    @JvmStatic
    fun setProgress(guideline: Guideline, percentage: Int?) {
        if (percentage != null && percentage in 0..100) {
            val layoutParams = guideline.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.guidePercent = percentage / 100f
            guideline.layoutParams = layoutParams
        }
    }

    @BindingAdapter("changeColor")
    @JvmStatic
    fun changeColor(imageView: ImageView, status: Boolean) {
        if (status) {
            imageView.clearColorFilter()
        } else {
            imageView.setColorFilter(
                ContextCompat.getColor(
                    imageView.context, R.color.et_hint_color
                )
            )
        }
    }

    /** full screen status bar style change **/
    @SuppressLint("ObsoleteSdkInt")
    fun statusBarStyle(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }


    fun statusBarTextColor1(activity: Activity, isDark: Boolean = false) {
        // Allow content to draw behind status bar
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)

        WindowInsetsControllerCompat(
            activity.window, activity.window.decorView
        ).isAppearanceLightStatusBars = isDark
    }


    /** set status bar item color change **/
    fun statusBarTextColor(activity: Activity, isDark: Boolean = false) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        WindowInsetsControllerCompat(
            activity.window, activity.window.decorView
        ).isAppearanceLightStatusBars = isDark
    }


    fun styleSystemBars(activity: Activity, color: Int) {
        activity.window.navigationBarColor = color
    }

    fun statusBarStyleWhite(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun statusBarStyleBlack(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Ensures black text/icons
            activity.window.statusBarColor = Color.TRANSPARENT // Transparent status bar
        }
    }
}
