package com.example.onlinecourse.administration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.onlinecourse.R
import com.example.onlinecourse.function.AppBar
import com.example.onlinecourse.function.FeatureButton
import com.example.onlinecourse.ui.theme.OnlineCursesTheme

@Composable
fun Administration(navController: NavHostController, userId: String, role: String) {
    OnlineCursesTheme {
        AppBar(
            title = "Администрирование",
            showTopBar = true,
            showBottomBar = false,
            navController,
            userId = userId,
            role = role
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {

                FeatureButton(
                    text = "Темы обращений",
                    icon = R.drawable.person,
                    onClick = { navController.navigate("appealTopicsView/${userId}/${role}") }
                )

                FeatureButton(
                    text = "Категории курсов",
                    icon = R.drawable.book,
                    onClick = { navController.navigate("courseCategoriesView/${userId}/${role}") }
                )
            }
        }
    }
}