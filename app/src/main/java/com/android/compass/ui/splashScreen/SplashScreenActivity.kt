package com.android.compass.ui.splashScreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.compass.ui.compass.CompassActivity

/**
 * Created by Noble on 7/26/2019.
 */

class SplashScreenActivity : AppCompatActivity() {
    private var decisionWasMade = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navigateToCompassActivity()
    }

    private fun navigateToCompassActivity() {
        if (decisionWasMade.not()) {
            decisionWasMade = true
            startActivity(Intent(this, CompassActivity::class.java))
            finish()
        }
    }
}