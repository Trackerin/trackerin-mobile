package com.example.trackerinmobile.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.ui.screens.auth.AuthViewModel
import com.example.trackerinmobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val tokenManager = authViewModel.tokenManager
    val backStack = LocalBackStack.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Load initial values from TokenManager
    val initialName = remember { tokenManager.getUserName() ?: "" }
    val initialOccupation = remember { tokenManager.getOccupation() ?: "" }
    val initialSpecialization = remember { tokenManager.getSpecialization() ?: "" }
    val initialEmail = remember { tokenManager.getEmail() ?: "" }
    val initialPassword = remember { tokenManager.getPassword() ?: "" }

    var name by remember { mutableStateOf(initialName) }
    var occupation by remember { mutableStateOf(initialOccupation) }
    var specialization by remember { mutableStateOf(initialSpecialization) }
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf(initialPassword) }

    var passwordVisible by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BackgroundApp
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Row with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { backStack.removeLastOrNull() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(WhitePure, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_right),
                        contentDescription = "Back",
                        tint = Black,
                        modifier = Modifier
                            .size(18.dp)
                            .graphicsLayer(rotationZ = 180f) // Rotate arrow_right to face left
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Edit Profile",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Profile Image (Statically rendered but beautifully styled)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.temporary_profile),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(3.dp, PrimaryBlue, CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Edit Credentials Form Container
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = WhitePure,
                border = androidx.compose.foundation.BorderStroke(1.dp, ComponentGray.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        placeholder = { Text("Enter your name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ComponentGray.copy(alpha = 0.5f)
                        )
                    )

                    // Occupation Field
                    OutlinedTextField(
                        value = occupation,
                        onValueChange = { occupation = it },
                        label = { Text("Occupation") },
                        placeholder = { Text("Enter your occupation") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ComponentGray.copy(alpha = 0.5f)
                        )
                    )

                    // Specialization Field
                    OutlinedTextField(
                        value = specialization,
                        onValueChange = { specialization = it },
                        label = { Text("Specialization") },
                        placeholder = { Text("Enter your specialization") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ComponentGray.copy(alpha = 0.5f)
                        )
                    )

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        placeholder = { Text("Enter your email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ComponentGray.copy(alpha = 0.5f)
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        placeholder = { Text("Enter password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val iconId = if (passwordVisible) R.drawable.eye_open else R.drawable.eye_slash
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(id = iconId),
                                    contentDescription = "Toggle password visibility",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = ComponentGray.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        // Persist all data locally to SharedPreferences
                        tokenManager.saveUserName(name.trim())
                        tokenManager.saveOccupation(occupation.trim())
                        tokenManager.saveSpecialization(specialization.trim())
                        tokenManager.saveEmail(email.trim())
                        tokenManager.savePassword(password)

                        Toast.makeText(context, "Changes saved successfully!", Toast.LENGTH_SHORT).show()
                        backStack.removeLastOrNull()
                    } else {
                        Toast.makeText(context, "Name and Email cannot be empty!", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Save Changes",
                    color = WhitePure,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
