package com.example.simplenote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.simplenote.data.remote.UserInfo
import com.example.simplenote.data.repo.Repository
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun SettingsScreen(nav: NavController) {
    val ctx = LocalContext.current
    val repo = remember { Repository(ctx) }
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<UserInfo?>(null) }
    var askLogout by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { runCatching { user = repo.userInfo() } }

    if (askLogout) {
        AlertDialog(
            onDismissRequest = { askLogout = false },
            title = { Text("Log Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out from the application?") },
            dismissButton = {
                OutlinedButton(onClick = { askLogout = false }) { Text("Cancel") }
            },
            confirmButton = {
                Button(onClick = {
                    askLogout = false
                    scope.launch {
                        repo.logout()
                        nav.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                }) { Text("Yes") }
            }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = { nav.popBackStack() }) { Text("Back") }
        Text("Settings", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))

        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(alpha = .2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = user?.username?.firstOrNull()?.uppercase() ?: "U", fontSize = 18.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(user?.let { "${(it.first_name ?: "").ifBlank { it.username }} ${(it.last_name ?: "")}".trim() }
                        ?: "User", fontWeight = FontWeight.SemiBold)
                    Text(user?.email ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("APP SETTINGS", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        ListItem(
            headlineContent = { Text("Change Password") },
            trailingContent = { Text(">") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { nav.navigate("change_password") }
        )
        Divider()
        Spacer(Modifier.height(12.dp))
        ListItem(
            headlineContent = { Text("Log Out", color = MaterialTheme.colorScheme.primary) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { askLogout = true }
        )
    }
}

@Composable
fun ChangePasswordScreen(nav: NavController) {
    val ctx = LocalContext.current
    val repo = remember { Repository(ctx) }
    val scope = rememberCoroutineScope()
    var current by remember { mutableStateOf("") }
    var new1 by remember { mutableStateOf("") }
    var new2 by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = { nav.popBackStack() }) { Text("Back") }
        Text("Change Password", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = current,
            onValueChange = { current = it },
            label = { Text("Current Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = new1,
            onValueChange = { new1 = it },
            label = { Text("New Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = new2,
            onValueChange = { new2 = it },
            label = { Text("Retype New Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))
        PrimaryButton(
            text = "Submit New Password",
            onClick = {
                if (new1 == new2 && new1.isNotBlank() && current.isNotBlank() && !busy) {
                    busy = true
                    scope.launch {
                        runCatching { repo.changePassword(current, new1) }
                            .onSuccess { nav.popBackStack() }
                            .onFailure { }
                        busy = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

