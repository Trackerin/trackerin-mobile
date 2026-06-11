package com.example.trackerinmobile.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.core.TodoViewModel
import com.example.trackerinmobile.ui.components.CustomBottomNavigation
import com.example.trackerinmobile.ui.screens.auth.AuthViewModel
import com.example.trackerinmobile.ui.theme.*
import java.util.Locale

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.trackerinmobile.ui.screens.auth.AuthState

@Composable
fun ProfileScreen() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val viewModel: TodoViewModel = hiltViewModel()
    val backStack = LocalBackStack.current
    val scrollState = rememberScrollState()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    // Load dynamic learning stats
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    val completedRoadmapsCount by viewModel.completedRoadmapsCount.collectAsState()
    val totalHours by viewModel.totalHours.collectAsState()
    val currentStreak = remember { authViewModel.tokenManager.getCurrentStreak() }

    // Profile Data State
    val name = authViewModel.tokenManager.getUserName() ?: "User"
    val occupation = authViewModel.tokenManager.getOccupation() ?: "College Student, 4th Semester"
    val specialization = authViewModel.tokenManager.getSpecialization() ?: "Fullstack Developer"
    
    var showContactDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.ContactSuccess -> {
                Toast.makeText(context, "Your message has been sent successfully!", Toast.LENGTH_SHORT).show()
                showContactDialog = false
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                activeTab = 3,
                onTabSelected = { index ->
                    when (index) {
                        0 -> backStack.add(Routes.DashboardRoute)
                        1 -> backStack.add(Routes.ExploreRoute)
                        2 -> backStack.add(Routes.ProgressRoute)
                    }
                }
            )
        },
        containerColor = BackgroundApp
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Black
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Profile Picture
            Image(
                painter = painterResource(id = R.drawable.temporary_profile),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Transparent, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name & Info
            Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Black)
            Text(text = occupation, fontSize = 14.sp, color = TextGray)
            Text(text = specialization, fontSize = 14.sp, color = TextGray)

            Spacer(modifier = Modifier.height(20.dp))

            // Edit Profile Button
            Button(
                onClick = { backStack.add(Routes.EditProfileRoute) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text("Edit Profile", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Row (Redesigned: 3 smaller compact cards side-by-side)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.book_icon,
                    iconTint = PrimaryBlue,
                    value = "$completedRoadmapsCount",
                    label = "Completed"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.fire_icon,
                    iconTint = Color(0xFFFF7A00),
                    value = "$currentStreak",
                    label = "Streak"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.profile_icon, // clock placeholder
                    iconTint = Color(0xFF10B981),
                    value = String.format(Locale.US, "%.1f", totalHours) + "h",
                    label = "Hours"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Menu List
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                color = WhitePure,
                border = androidx.compose.foundation.BorderStroke(1.dp, ComponentGray.copy(alpha = 0.3f))
            ) {
                Column {
                    MenuItem(iconRes = R.drawable.book_icon, label = "My Notes") {
                        backStack.add(Routes.NotesRoute)
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = ComponentGray.copy(alpha = 0.2f))
                    MenuItem(iconRes = R.drawable.settings_icon, label = "Settings")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = ComponentGray.copy(alpha = 0.2f))
                    MenuItem(iconRes = R.drawable.bell_icon, label = "Notifications")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = ComponentGray.copy(alpha = 0.2f))
                    MenuItem(iconRes = R.drawable.privacy_icon, label = "Privacy")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = ComponentGray.copy(alpha = 0.2f))
                    MenuItem(iconRes = R.drawable.helpcenter_icon, label = "Help Center")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = ComponentGray.copy(alpha = 0.2f))
                    MenuItem(iconRes = R.drawable.plus_icon, label = "Contact Us") {
                        showContactDialog = true
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Logout Button
            Text(
                text = "Log Out",
                color = Color.Red,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Red.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .clickable {
                        authViewModel.logout()
                        backStack.clear()
                        backStack.add(Routes.LoginRoute)
                    }
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }


        if (showContactDialog) {
            var contactName by remember { mutableStateOf(name) }
            var contactEmail by remember { mutableStateOf("") }
            var contactSubject by remember { mutableStateOf("") }
            var contactMessage by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = { showContactDialog = false },
                title = { Text("Contact Us", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = contactName,
                            onValueChange = { contactName = it },
                            label = { Text("Your Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = contactEmail,
                            onValueChange = { contactEmail = it },
                            label = { Text("Your Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = contactSubject,
                            onValueChange = { contactSubject = it },
                            label = { Text("Subject") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = contactMessage,
                            onValueChange = { contactMessage = it },
                            label = { Text("Message") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                },
                confirmButton = {
                    val isLoading = authState is AuthState.Loading
                    Button(
                        onClick = {
                            if (contactName.isNotBlank() && contactEmail.isNotBlank() && contactSubject.isNotBlank() && contactMessage.isNotBlank()) {
                                authViewModel.sendContactMessage(
                                    name = contactName,
                                    email = contactEmail,
                                    subject = contactSubject,
                                    message = contactMessage
                                )
                            } else {
                                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = WhitePure, strokeWidth = 2.dp)
                        } else {
                            Text("Send", color = WhitePure)
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showContactDialog = false },
                        enabled = authState !is AuthState.Loading
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, iconRes: Int, iconTint: Color, value: String, label: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = WhitePure,
        border = androidx.compose.foundation.BorderStroke(1.dp, ComponentGray.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextGray,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun MenuItem(iconRes: Int, label: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Black)
    }
}
