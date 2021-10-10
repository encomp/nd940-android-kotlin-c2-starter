package com.udacity.asteroidradar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.airbnb.lottie.LottieAnimationView
import java.lang.Integer.max
import java.time.Clock
import java.time.Instant
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val lottieView = findViewById<LottieAnimationView>(R.id.animationView)
            lottieView.enableMergePathsForKitKatAndAbove(true)

            val splashScreenAnimationEndTime =
                Instant.ofEpochMilli(splashScreenView.iconAnimationStartMillis + splashScreenView.iconAnimationDurationMillis)

            val delay = Instant.now(Clock.systemUTC()).until(
                splashScreenAnimationEndTime,
                ChronoUnit.MILLIS
            )

            lottieView.postDelayed({
                splashScreenView.view.alpha = 0f
                splashScreenView.iconView.alpha = 0f
                lottieView!!.playAnimation()
            }, delay)

            lottieView.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    val contentView = findViewById<View>(android.R.id.content)
                    val fragmentContainer = findViewById<FrameLayout>(R.id.fragment_container)

                    val animator =
                        ViewAnimationUtils.createCircularReveal(
                            fragmentContainer,
                            contentView.width / 2,
                            contentView.height / 2,
                            0f,
                            max(contentView.width, contentView.height).toFloat()
                        ).setDuration(600)

                    fragmentContainer.visibility = View.VISIBLE
                    animator.start()
                }
            })
        }
    }
}
