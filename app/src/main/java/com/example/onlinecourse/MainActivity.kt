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
        composable("changeProfileData/{userId}/{role}") { ChangeProfileData(navController,userId,role) }

        // администрирование

        // обращения

        // сертификаты

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
