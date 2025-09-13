
package com.example.simplenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simplenote.ui.screens.*
import com.example.simplenote.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val nav = rememberNavController()
            NavHost(navController = nav, startDestination = "onboarding") {
                composable("onboarding") { OnboardingScreen(onGetStarted = { nav.navigate("login") { popUpTo("onboarding"){inclusive=true} } }) }
                composable("login") { LoginScreen(nav) }
                composable("register") { RegisterScreen(nav) }
                composable("home") { HomeScreen(nav) }
                composable("create") { EditNoteScreen(nav, null) }
                composable("detail/{id}") { back ->
                    val id = back.arguments?.getString("id") ?: ""
                    NoteDetailScreen(nav, id)
                }
                composable("edit/{id}") { back ->
                    val id = back.arguments?.getString("id") ?: ""
                    EditNoteScreen(nav, id)
                }
                composable("settings") { SettingsScreen(nav) }
                composable("change_password") { ChangePasswordScreen(nav) }
                composable("create_new_password") { CreateNewPasswordScreen(nav) }
            }
        }
    }
}
