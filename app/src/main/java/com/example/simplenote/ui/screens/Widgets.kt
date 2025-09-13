
package com.example.simplenote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar(title:String, onBack:(()->Unit)?){
    Row(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        if(onBack!=null){ Text("< Back", color=MaterialTheme.colorScheme.primary, modifier=Modifier.padding(end=8.dp)) }
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PrimaryButton(text:String, onClick:()->Unit, modifier: Modifier=Modifier){
    Button(onClick=onClick, colors=ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.primary), modifier = modifier.height(48.dp).fillMaxWidth(), shape = CircleShape) {
        Text(text, color=Color.White)
    }
}
