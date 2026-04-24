package com.example.trackerinmobile.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.ui.theme.BackgroundApp
import com.example.trackerinmobile.ui.theme.Black
import com.example.trackerinmobile.ui.theme.ComponentGray
import com.example.trackerinmobile.ui.theme.PrimaryBlue
import com.example.trackerinmobile.ui.theme.TextGray
import com.example.trackerinmobile.ui.theme.WhitePure

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val backStack = LocalBackStack.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.trackerin_logo),
            contentDescription = "Trackerin Logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Welcome Back Title
        Text(
            text = "Welcome Back!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Ready to smash today's learning targets ?",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Email field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Email", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = ComponentGray,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedContainerColor = WhitePure,
                    focusedContainerColor = WhitePure
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Password", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) R.drawable.eye_open else R.drawable.eye_slash
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = painterResource(id = image), contentDescription = "Toggle password visibility", modifier = Modifier.size(24.dp))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = ComponentGray,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedContainerColor = WhitePure,
                    focusedContainerColor = WhitePure
                ),
                singleLine = true
            )

            // Lupa password - Teks kecil di bawah form password
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "forgot your password?",
                fontSize = 12.sp,
                color = PrimaryBlue,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* Handle forgot password */ }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign In Button
        Button(
            onClick = {
                backStack.clear()
                backStack.add(Routes.DashboardRoute)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = WhitePure)
        ) {
            Text(text = "Sign in", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // OR Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.HorizontalDivider(modifier = Modifier.weight(1f), color = ComponentGray)
            Text(
                text = "OR",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outline
            )
            androidx.compose.material3.HorizontalDivider(modifier = Modifier.weight(1f), color = ComponentGray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign In with Google Button
        Button(
            onClick = { /* Handle google sign in */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .border(1.dp, ComponentGray, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WhitePure, contentColor = Black)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.google_icon),
                    contentDescription = "Google Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Sign in with Google", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Button / Text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "don't have an account yet? ",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "create now",
                fontSize = 14.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { backStack.add(Routes.RegisterRoute) }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
