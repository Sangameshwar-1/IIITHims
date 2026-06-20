package com.ims.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ims.app.ImsApplication
import com.ims.app.ui.screens.admission.AdmissionFormScreen
import com.ims.app.ui.screens.admission.AdmissionListScreen
import com.ims.app.ui.screens.admission.AdmissionViewModel
import com.ims.app.ui.screens.admission.StudentDetailScreen
import com.ims.app.ui.screens.dashboard.DashboardScreen
import com.ims.app.ui.screens.dashboard.DashboardViewModel
import com.ims.app.ui.screens.examination.*
import com.ims.app.ui.screens.settings.SettingsScreen

/**
 * Central navigation graph defining all routes and screen transitions.
 * Uses shared ViewModels for modules with related sub-screens.
 */
@Composable
fun ImsNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val app = context.applicationContext as ImsApplication

    // Shared ViewModels for each module
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(app)
    )
    val admissionViewModel: AdmissionViewModel = viewModel(
        factory = AdmissionViewModel.Factory(app)
    )
    val examinationViewModel: ExaminationViewModel = viewModel(
        factory = ExaminationViewModel.Factory(app)
    )

    NavHost(
        navController = navController,
        startDestination = if (com.ims.app.data.network.TokenManager.getInstance(context).getToken() != null) Screen.Dashboard.route else Screen.Login.route,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {
        // ─── Auth ───────────────────────────────────────────
        composable(Screen.Login.route) {
            val authViewModel = viewModel<com.ims.app.ui.screens.auth.AuthViewModel>(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        return com.ims.app.ui.screens.auth.AuthViewModel(
                            com.ims.app.data.network.TokenManager.getInstance(context)
                        ) as T
                    }
                }
            )
            com.ims.app.ui.screens.auth.LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        // ─── Dashboard ──────────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToAdmissions = {
                    navController.navigate(Screen.Admissions.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToExams = {
                    navController.navigate(Screen.Examinations.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToNewAdmission = {
                    navController.navigate(Screen.AdmissionForm.createRoute())
                },
                onNavigateToCreateExam = {
                    navController.navigate(Screen.CreateExam.createRoute())
                }
            )
        }

        // ─── Admission Module ───────────────────────────────
        composable(Screen.Admissions.route) {
            AdmissionListScreen(
                viewModel = admissionViewModel,
                onStudentClick = { studentId ->
                    navController.navigate(Screen.StudentDetail.createRoute(studentId))
                },
                onNewAdmission = {
                    navController.navigate(Screen.AdmissionForm.createRoute())
                }
            )
        }

        composable(
            route = Screen.AdmissionForm.route,
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getLong("studentId") ?: -1L
            AdmissionFormScreen(
                viewModel = admissionViewModel,
                studentId = studentId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StudentDetail.route,
            arguments = listOf(
                navArgument("studentId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getLong("studentId") ?: return@composable
            StudentDetailScreen(
                viewModel = admissionViewModel,
                studentId = studentId,
                onBack = { navController.popBackStack() },
                onEdit = { id ->
                    navController.navigate(Screen.AdmissionForm.createRoute(id))
                }
            )
        }

        // ─── Examination Module ─────────────────────────────
        composable(Screen.Examinations.route) {
            ExamListScreen(
                viewModel = examinationViewModel,
                onExamClick = { examId ->
                    navController.navigate(Screen.ExamResults.createRoute(examId))
                },
                onCreateExam = {
                    navController.navigate(Screen.CreateExam.createRoute())
                },
                onReportsClick = {
                    navController.navigate(Screen.ExamReports.route)
                }
            )
        }

        composable(
            route = Screen.CreateExam.route,
            arguments = listOf(
                navArgument("examId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: -1L
            CreateExamScreen(
                viewModel = examinationViewModel,
                examId = examId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ExamResults.route,
            arguments = listOf(
                navArgument("examId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val examId = backStackEntry.arguments?.getLong("examId") ?: return@composable
            ExamResultsScreen(
                viewModel = examinationViewModel,
                examId = examId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ExamReports.route) {
            ExamReportsScreen(
                viewModel = examinationViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // ─── Settings ───────────────────────────────────────
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
