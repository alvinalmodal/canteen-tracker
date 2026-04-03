package com.canteen.tracker.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.canteen.tracker.ui.home.HomeScreen

object Routes {
    const val HOME = "home"
    const val SCAN = "scan"
    const val OCR_REVIEW = "ocr_review"
    const val REPORT_EDITOR = "report_editor/{cutoffPeriodId}"
    const val HISTORY_DETAIL = "history/{cutoffPeriodId}"

    fun reportEditor(cutoffPeriodId: Long) = "report_editor/$cutoffPeriodId"
    fun historyDetail(cutoffPeriodId: Long) = "history/$cutoffPeriodId"
}

@Composable
fun CanteenTrackerNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToScan = { navController.navigate(Routes.SCAN) },
                onNavigateToHistory = { cutoffPeriodId ->
                    navController.navigate(Routes.historyDetail(cutoffPeriodId))
                }
            )
        }
        // Additional screens will be added in subsequent phases
    }
}
