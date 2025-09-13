package com.example.simplenote.ui.screens
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.simplenote.data.repo.Repository
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplenote.R

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6750A4)) // match the purple of your image
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Larger Image
        Image(
            painter = painterResource(id = R.drawable.idea_illustration),
            contentDescription = "Welcome illustration",
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f) // Keep square-ish proportions
                .padding(top = 48.dp)
        )

        // White Text
        Box(
            Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Jot Down anything you want to achieve, today or in the future",
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 18.sp
            )
        }

        // Button
        PrimaryButton(
            text = "Let's Get Started",
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun LoginScreen(nav: NavController) {
    val ctx = LocalContext.current
    val repo = remember { Repository(ctx) }
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(24.dp))
        Text("Let's Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        PrimaryButton(
            text = "Login",
            onClick = {
                scope.launch {
                    try {
                        repo.login(username, password)
                        repo.syncOnce()
                        nav.navigate("home") { popUpTo("login") { inclusive = true } }
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = { nav.navigate("register") }) { Text("Don’t have any account? Register here") }
    }
}

@Composable
fun RegisterScreen(nav: NavController) {
    val ctx = LocalContext.current
    val repo = remember { Repository(ctx) }
    val scope = rememberCoroutineScope()
    var first by remember { mutableStateOf("") }
    var last by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        TextButton(onClick = { nav.popBackStack() }) { Text("Back to Login") }
        Text("Register", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = first, onValueChange = { first = it }, label = { Text("First Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = last, onValueChange = { last = it }, label = { Text("Last Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, singleLine = true, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        PrimaryButton(
            text = "Register",
            onClick = {
                scope.launch {
                    try {
                        repo.register(first, last, username, email, password)
                        Toast.makeText(ctx, "Registered", Toast.LENGTH_SHORT).show()
                        nav.popBackStack()
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Register failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }

                }
            }
        )
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = { nav.popBackStack() }) { Text("Already have an account? Login here") }
    }
}

@Composable
fun CreateNewPasswordScreen(nav: NavController) {
    var pass by remember { mutableStateOf("") }
    var pass2 by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text("Create a New Password", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("Your new password should be different from the previous password")
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("New Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = pass2, onValueChange = { pass2 = it }, label = { Text("Retype New Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        }
        PrimaryButton(text = "Create Password", onClick = { nav.popBackStack() })
    }
}
