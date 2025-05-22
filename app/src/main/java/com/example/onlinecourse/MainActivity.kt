package com.example.onlinecourse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlinecourse.account.ChangeProfileData
import com.example.onlinecourse.account.PageView
import com.example.onlinecourse.administration.Administration
import com.example.onlinecourse.administration.RegistrationTeacher
import com.example.onlinecourse.administration.courseCategories.CourseCategoriesAdd
import com.example.onlinecourse.administration.courseCategories.CourseCategoriesEdit
import com.example.onlinecourse.administration.courseCategories.CourseCategoriesView
import com.example.onlinecourse.administration.topicsAppeals.AppealTopicEdit
import com.example.onlinecourse.administration.topicsAppeals.AppealTopicsAdd
import com.example.onlinecourse.administration.topicsAppeals.AppealTopicsView
import com.example.onlinecourse.appeal.AppealAdd
import com.example.onlinecourse.appeal.AppealView
import com.example.onlinecourse.appeal.AppealsView
import com.example.onlinecourse.certificate.ViewCertificate
import com.example.onlinecourse.certificate.ViewYourCertificates
import com.example.onlinecourse.entrance.Authorization
import com.example.onlinecourse.entrance.Entrance
import com.example.onlinecourse.entrance.Registration
import com.example.onlinecourse.estimation.AnswerForStepView
import com.example.onlinecourse.estimation.AnswersForStepView
import com.example.onlinecourse.estimation.Estimation
import com.example.onlinecourse.function.Loading
import com.example.onlinecourse.notification.NotificationView
import com.example.onlinecourse.notification.NotificationsView
import com.example.onlinecourse.setting.ChangingThePassword
import com.example.onlinecourse.setting.Settings
import com.example.onlinecourse.statistics.AppealStatisticsView
import com.example.onlinecourse.statistics.PlatformStatisticsView
import com.example.onlinecourse.statistics.Statistic
import com.example.onlinecourse.statistics.TeacherCoursesView
import com.example.onlinecourse.statistics.UserActivityStatsView
import com.example.onlinecourse.user.AllUsersView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }
}


@Composable
fun MyApp() {
    val navController = rememberNavController()
    var subjectId by remember { mutableStateOf("-1") }
    var courseId by remember { mutableStateOf("-1") }
    var stepId by remember { mutableStateOf("-1") }
    var answerId by remember { mutableStateOf("-1") }
    var topicId by remember { mutableStateOf("-1") }
    var topicName by remember { mutableStateOf("-1") }
    var topicDescription by remember { mutableStateOf("-1") }
    var categoryId by remember { mutableStateOf("-1") }
    var categoryName by remember { mutableStateOf("-1") }
    var categoryDescription by remember { mutableStateOf("-1") }
    var viewId by remember { mutableStateOf("-1") }
    var appealId by remember { mutableStateOf("-1") }
    var sertificateId by remember { mutableStateOf("-1") }
    var notificationId by remember { mutableStateOf("-1") }
    var userId by remember { mutableStateOf("-1") }
    var role by remember { mutableStateOf("Не распознано") }

    NavHost(navController = navController, startDestination = "loading") {
        // загрузка
        composable("loading") { Loading(navController, userId, role) }

        // аккаунт
        composable("pageView/{userId}/{role}/{viewId}") { backStackEntry ->
            viewId = backStackEntry.arguments?.getString("viewId") ?: "-1"
            PageView(navController,userId,role,viewId)
        }
        composable("changeProfileData/{userId}/{role}/{viewId}") { backStackEntry ->
            viewId = backStackEntry.arguments?.getString("viewId") ?: "-1"
            ChangeProfileData(navController,userId,role, viewId)
        }

        // администрирование
        composable("administration/{userId}/{role}") { Administration(navController,userId,role) }
        composable("courseCategoriesView/{userId}/{role}") { CourseCategoriesView(navController,userId,role) }
        composable("courseCategoriesAdd/{userId}/{role}") { CourseCategoriesAdd(navController,userId,role) }
        composable("courseCategoriesEdit/{userId}/{role}/{categoryId}/{categoryName}/{categoryDescription}") { backStackEntry ->
            categoryId = backStackEntry.arguments?.getString("categoryId") ?: "-1"
            categoryName = backStackEntry.arguments?.getString("categoryName") ?: "-1"
            categoryDescription = backStackEntry.arguments?.getString("categoryDescription") ?: "-1"
            CourseCategoriesEdit(navController,userId,role,categoryId,categoryName,categoryDescription)
        }
        composable("appealTopicsView/{userId}/{role}") { AppealTopicsView(navController,userId,role) }
        composable("appealTopicsAdd/{userId}/{role}") { AppealTopicsAdd(navController,userId,role) }
        composable("appealTopicsEdit/{userId}/{role}/{topicId}/{topicName}/{topicDescription}") { backStackEntry ->
            topicId = backStackEntry.arguments?.getString("topicId") ?: "-1"
            topicName = backStackEntry.arguments?.getString("topicName") ?: "-1"
            topicDescription = backStackEntry.arguments?.getString("topicDescription") ?: "-1"
            AppealTopicEdit(navController,userId,role, topicId, topicName, topicDescription)
        }
        composable("registrationTeacher/{userId}/{role}") { RegistrationTeacher(navController,userId,role) }

        // обращения
        composable("appealAdd/{userId}/{role}") { AppealAdd(navController,userId,role) }
        composable("appealsView/{userId}/{role}") { AppealsView(navController,userId,role) }
        composable("appealView/{userId}/{role}/{appealId}") { backStackEntry ->
            appealId = backStackEntry.arguments?.getString("appealId") ?: "-1"
            AppealView(navController,userId,role,appealId)
        }

        // сертификаты
        composable("viewYourCertificates/{userId}/{role}") { ViewYourCertificates(navController,userId,role) }
        composable("viewCertificate/{userId}/{role}/{sertificateId}") { backStackEntry ->
            sertificateId = backStackEntry.arguments?.getString("sertificateId") ?: "-1"
            ViewCertificate(navController,userId,role,sertificateId)
        }

        // курсы

        // первый вход
        composable("entrance") { Entrance(navController) }
        composable("authorization") { Authorization(navController) }
        composable("registration") { Registration(navController) }

        // оценка знаний
        composable("estimation/{userId}/{role}") { Estimation(navController,userId,role) }
        composable("answersForStepView/{userId}/{role}/{stepId}") { backStackEntry ->
            stepId = backStackEntry.arguments?.getString("stepId") ?: "-1"
            AnswersForStepView(navController,userId,role,stepId)
        }
        composable("answerForStepView/{userId}/{role}/{answerId}/{stepId}") { backStackEntry ->
            answerId = backStackEntry.arguments?.getString("answerId") ?: "-1"
            stepId = backStackEntry.arguments?.getString("stepId") ?: "-1"
            AnswerForStepView(navController,userId,role,answerId,stepId)
        }

        // уведомления
        composable("notificationsView/{userId}/{role}") { NotificationsView(navController,userId,role) }
        composable("notificationView/{userId}/{role}/{notificationId}") { backStackEntry ->
            notificationId = backStackEntry.arguments?.getString("notificationId") ?: "-1"
            NotificationView(navController,userId,role,notificationId)
        }

        // настройки
        composable("changingThePassword/{userId}/{role}") { ChangingThePassword(navController,userId,role) }
        composable("settings/{userId}/{role}") { Settings(navController,userId,role) }

        // статистика
        composable("statistic/{userId}/{role}") { Statistic(navController,userId,role) }
        composable("platformStatisticsView/{userId}/{role}") { PlatformStatisticsView(navController,userId,role) }
        composable("appealStatisticsView/{userId}/{role}") { AppealStatisticsView(navController,userId,role) }
        composable("userActivityStatsView/{userId}/{role}") { UserActivityStatsView(navController,userId,role) }
        composable("teacherCoursesView/{userId}/{role}") { TeacherCoursesView(navController,userId,role) }

        // пользователи
        composable("AllUsersView/{userId}/{role}") { AllUsersView(navController,userId,role) }

        // главная страница
        composable("main/{userId}/{role}") {backStackEntry ->
            userId = backStackEntry.arguments?.getString("userId") ?: "-1"
            role = backStackEntry.arguments?.getString("role") ?: "-1"
            MainPage(navController, userId, role)
        }
    }
}
