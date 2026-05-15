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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trackerinmobile.R
import com.example.trackerinmobile.TrackerinApplication
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.ui.components.CustomBottomNavigation
import com.example.trackerinmobile.ui.screens.auth.AuthViewModel
import com.example.trackerinmobile.ui.screens.auth.AuthViewModelFactory
import com.example.trackerinmobile.ui.theme.*

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as TrackerinApplication).container
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(appContainer.apiService, appContainer.tokenManager)
    )
    val backStack = LocalBackStack.current
    val scrollState = rememberScrollState()

    // Profile Data State
    val savedName = remember { appContainer.tokenManager.getUserName() ?: "User" }
    var name by remember { mutableStateOf(savedName) }
    var occupation by remember { mutableStateOf("College Student, 4th Semester") }
    var specialization by remember { mutableStateOf("Fullstack Developer") }
    
    var showEditDialog by remember { mutableStateOf(false) }

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
                onClick = { showEditDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(36.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                Text("Edit Profile", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.book_icon,
                    iconTint = PrimaryBlue,
                    value = "15",
                    label = "Course Completed"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.fire_icon,
                    iconTint = Color(0xFFFF7A00),
                    value = "20",
                    label = "Current Streak"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Learning Hours Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                color = WhitePure,
                border = androidx.compose.foundation.BorderStroke(1.dp, ComponentGray.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.search_icon), // Placeholder for clock
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Total Learning Hours", fontSize = 14.sp, color = TextGray)
                    Text(text = "400hrs", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Black)
                }
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
                    MenuItem(iconRes = R.drawable.settings_icon, label = "Settings")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = ComponentGray.copy(alpha = 0.2f))
                    MenuItem(iconRes = R.drawable.bell_icon, label = "Notifications")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = ComponentGray.copy(alpha = 0.2f))
                    MenuItem(iconRes = R.drawable.privacy_icon, label = "Privacy")
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = ComponentGray.copy(alpha = 0.2f))
                    MenuItem(iconRes = R.drawable.helpcenter_icon, label = "Help Center")
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

        if (showEditDialog) {
            var tempName by remember { mutableStateOf(name) }
            var tempOccupation by remember { mutableStateOf(occupation) }
            var tempSpecialization by remember { mutableStateOf(specialization) }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Edit Profile") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Name") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tempOccupation,
                            onValueChange = { tempOccupation = it },
                            label = { Text("Occupation") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = tempSpecialization,
                            onValueChange = { tempSpecialization = it },
                            label = { Text("Specialization") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        name = tempName
                        occupation = tempOccupation
                        specialization = tempSpecialization
                        appContainer.tokenManager.saveUserName(tempName) // Persist locally
                        showEditDialog = false
                    }) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
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
        shape = RoundedCornerShape(16.dp),
        color = WhitePure,
        border = androidx.compose.foundation.BorderStroke(1.dp, ComponentGray.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Black)
            Text(text = label, fontSize = 12.sp, color = TextGray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun MenuItem(iconRes: Int, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle click */ }
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
