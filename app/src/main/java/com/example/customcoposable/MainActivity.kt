package com.example.customcoposable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.customcoposable.ui.theme.CustomCoposableTheme

// Data Model
data class Event(
    val id: Int,
    val title: String,
    val date: String,
    val location: String,
    val description: String,
    val isRegistered: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomCoposableTheme {
                EventDashboard()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDashboard() {
    var events by remember {
        mutableStateOf(
            listOf(
                Event(1, "Android Dev Summit", "Oct 24, 2024", "Mountain View, CA", "Join us for the latest Android updates."),
                Event(2, "Kotlin Conf", "May 15, 2024", "Copenhagen, Denmark", "A conference for Kotlin enthusiasts."),
                Event(3, "Google I/O", "May 10, 2024", "Shoreline Amphitheatre", "Google's annual developer conference."),
                Event(4, "Compose Camp", "Aug 12, 2024", "Online", "Learn Jetpack Compose with experts.")
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Event Dashboard", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(events, key = { it.id }) { event ->
                EventDetailCard(
                    event = event,
                    onStatusChange = { isRegistered ->
                        events = events.map { if (it.id == event.id) it.copy(isRegistered = isRegistered) else it }
                    }
                )
            }
        }
    }
}

@Composable
fun EventDetailCard(event: Event, onStatusChange: (Boolean) -> Unit) {
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    val statusColor by animateColorAsState(
        targetValue = if (event.isRegistered) Color(0xFF4CAF50) else Color(0xFFF44336),
        label = "StatusColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (event.isRegistered) showCancelDialog = true else showRegisterDialog = true
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(event.date, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor)
            ) {
                Text(
                    text = if (event.isRegistered) "Registered" else "Not Registered",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor
                )
            }
        }
    }

    // Two distinct dialog logic blocks kept directly inside the EventDetailCard function
    if (showRegisterDialog) {
        Dialog(onDismissRequest = { showRegisterDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Register for Event", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = event.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Date" to event.date, "Location" to event.location, "Description" to event.description).forEach { (label, value) ->
                            Column {
                                Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Text(text = value, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onStatusChange(true)
                            showRegisterDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("Confirm Registration")
                    }
                    
                    TextButton(onClick = { showRegisterDialog = false }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Go Back", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showCancelDialog) {
        Dialog(onDismissRequest = { showCancelDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Cancel Registration", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = event.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Date" to event.date, "Location" to event.location, "Description" to event.description).forEach { (label, value) ->
                            Column {
                                Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                Text(text = value, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onStatusChange(false)
                            showCancelDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                    ) {
                        Text("Cancel Registration")
                    }
                    
                    TextButton(onClick = { showCancelDialog = false }, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Go Back", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventDashboardPreview() {
    CustomCoposableTheme {
        EventDashboard()
    }
}
