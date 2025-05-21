package com.example.onlinecourse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlinecourse.account.ChangeProfileData
import com.example.onlinecourse.account.PageView
import com.example.onlinecourse.appeal.AppealAdd
import com.example.onlinecourse.appeal.AppealView
import com.example.onlinecourse.appeal.AppealsView
import com.example.onlinecourse.certificate.ViewCertificate
import com.example.onlinecourse.certificate.ViewYourCertificates
import com.example.onlinecourse.entrance.Authorization
import com.example.onlinecourse.entrance.Entrance
import com.example.onlinecourse.entrance.Registration
import com.example.onlinecourse.function.Loading
import com.example.onlinecourse.notification.NotificationView
import com.example.onlinecourse.notification.NotificationsView
import com.example.onlinecourse.setting.ChangingThePassword
import com.example.onlinecourse.setting.Settings

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

        // пользователь

        // главная страница
        composable("main/{userId}/{role}") {backStackEntry ->
            userId = backStackEntry.arguments?.getString("userId") ?: "-1"
            role = backStackEntry.arguments?.getString("role") ?: "-1"
            MainPage(navController, userId, role)
        }
    }
}
