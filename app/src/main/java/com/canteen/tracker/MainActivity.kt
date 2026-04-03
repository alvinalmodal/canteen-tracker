package com.canteen.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.canteen.tracker.ui.CanteenTrackerNavHost
import com.canteen.tracker.ui.theme.CanteenTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanteenTrackerTheme {
                CanteenTrackerNavHost()
            }
        }
    }
}
