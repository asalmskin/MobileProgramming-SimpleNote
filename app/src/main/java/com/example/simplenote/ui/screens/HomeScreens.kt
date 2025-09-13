package com.example.simplenote.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.simplenote.data.local.NoteEntity
import com.example.simplenote.data.repo.Repository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(nav: NavController) {
    val ctx = LocalContext.current
    val repo = remember { Repository(ctx) }
    var query by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val itemsPaging = repo.notesPaged(query).collectAsLazyPagingItems()
    LaunchedEffect(Unit) { repo.syncOnce() }
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Home", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { nav.navigate("settings") }) { Text("Settings") }
        }
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                scope.launch { repo.syncOnce(it) }
            },
            placeholder = { Text("Search") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f).padding(12.dp),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(itemsPaging.itemCount) { index ->
                val note = itemsPaging[index]
                if (note != null) NoteCard(note) { nav.navigate("detail/${note.id}") }
            }
        }
        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
            FloatingActionButton(onClick = { nav.navigate("create") }) { Text("+") }
        }
    }
}

@Composable
fun NoteCard(note: NoteEntity, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.padding(8.dp)) {
        Column(Modifier.padding(12.dp)) {
            Text(note.title, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(note.content.take(120), fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun NoteDetailScreen(nav: NavController, id: String) {
    val ctx = LocalContext.current
    val repo = remember { Repository(ctx) }
    val scope = rememberCoroutineScope()
    var note by remember { mutableStateOf<NoteEntity?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    LaunchedEffect(id) { note = repo.note(id) }
    val time = remember(note?.updatedAt) {
        note?.updatedAt?.let { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it)) } ?: ""
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    scope.launch {
                        repo.delete(id)
                        Toast.makeText(ctx, "Deleted", Toast.LENGTH_SHORT).show()
                        nav.popBackStack()
                    }
                }) { Text("Yes") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } },
            title = { Text("Delete Note") },
            text = { Text("Do you want to delete this note?") }
        )
    }
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { nav.popBackStack() }) { Text("Back") }
            Row {
                TextButton(onClick = { nav.navigate("edit/$id") }) { Text("Edit") }
            }
        }
        Text(
            note?.title.orEmpty(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(note?.content.orEmpty(), modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (time.isNotEmpty()) Text("Last edited at $time")
            ExtendedFloatingActionButton(
                onClick = { confirmDelete = true },
                containerColor = MaterialTheme.colorScheme.error
            ) { Text("Delete") }
        }
    }
}

@Composable
fun EditNoteScreen(nav: NavController, id: String?) {
    val ctx = LocalContext.current
    val repo = remember { Repository(ctx) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var askDelete by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(id) {
        if (id != null) {
            val n = repo.note(id)
            title = n?.title.orEmpty()
            content = n?.content.orEmpty()
        }
    }
    if (askDelete) {
        AlertDialog(
            onDismissRequest = { askDelete = false },
            confirmButton = {
                TextButton(onClick = {
                    askDelete = false
                    scope.launch {
                        repo.delete(id!!)
                        Toast.makeText(ctx, "Deleted", Toast.LENGTH_SHORT).show()
                        nav.navigate("home") { popUpTo("home") { inclusive = true } }
                    }
                }) { Text("Yes") }
            },
            dismissButton = { TextButton(onClick = { askDelete = false }) { Text("Cancel") } },
            title = { Text("Delete Note") },
            text = { Text("Do you want to delete this note?") }
        )
    }
    Column(Modifier.fillMaxSize()) {
        TextButton(onClick = { nav.popBackStack() }, modifier = Modifier.padding(16.dp)) { Text("Back") }
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text("Title") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            placeholder = { Text("Feel Free to Write Here...") },
            modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp)
        )
        Column(Modifier.padding(16.dp)) {
            PrimaryButton(
                text = if (id == null) "Submit" else "Save",
                onClick = {
                    scope.launch {
                        if (id == null) {
                            val nid = repo.create(title, content)
                            nav.navigate("detail/$nid") { popUpTo("home") { inclusive = false } }
                        } else {
                            repo.update(id, title, content)
                            nav.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (id != null) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { askDelete = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete", color = MaterialTheme.colorScheme.onError) }
            }
        }
    }
}
