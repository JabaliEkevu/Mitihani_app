package com.example.mitihani_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mitihani_app.yourListOfQuizzes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "welcome") {
                composable("welcome") {
                    WelcomeScreen {
                        navController.navigate("quizSelection")
                    }
                }
                composable("quizSelection") {
                    QuizSelectionScreen(quizzes = yourListOfQuizzes) { selectedQuiz ->
                        navController.navigate("quiz/${selectedQuiz.title}")
                    }
                }
                composable(
                    route = "quiz/{quizTitle}",
                    arguments = listOf(navArgument("quizTitle") { type = NavType.StringType })
                ) { backStackEntry ->
                    val quizTitle = backStackEntry.arguments?.getString("quizTitle") ?: ""
                    val selectedQuiz = yourListOfQuizzes.find { it.title == quizTitle }
                    selectedQuiz?.let { quiz ->
                        QuizScreen(navController, quiz) { score, totalQuestions ->
                            // Handle score
                            navController.navigate("quizSelection")
                        }
                    }
                }
            }
        }
    }
}