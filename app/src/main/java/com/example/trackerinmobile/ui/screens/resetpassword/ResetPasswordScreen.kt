package com.example.trackerinmobile.ui.screens.resetpassword

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.data.model.auth.ResetPasswordRequest
import com.example.trackerinmobile.ui.screens.auth.AuthState
import com.example.trackerinmobile.ui.screens.auth.AuthViewModel
import com.example.trackerinmobile.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    email: String
) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    var otpValue by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    // Countdown Timer for Resend OTP
    var countdown by remember { mutableStateOf(60) }
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            delay(1000L)
            countdown -= 1
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.PasswordResetSuccess -> {
                Toast.makeText(context, "Password reset successful. Please login again.", Toast.LENGTH_LONG).show()
                authViewModel.resetState()
                backStack.clear()
                backStack.add(Routes.LoginRoute)
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            is AuthState.ForgotPasswordOtpSent -> {
                Toast.makeText(context, "Kode OTP berhasil dikirim ulang", Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
                countdown = 60
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Back Button Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { backStack.removeLastOrNull() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "← Back",
                fontSize = 14.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.trackerin_logo),
            contentDescription = "Trackerin Logo",
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = "Reset Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Enter the 6-digit OTP code sent to:",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = email,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Black
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "(Silakan cek folder Spam jika tidak ada di kotak masuk utama)",
            fontSize = 12.sp,
            color = TextGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        // 6-digit OTP fields utilizing hidden BasicTextField
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable { focusRequester.requestFocus() },
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = otpValue,
                onValueChange = {
                    if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                        otpValue = it
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .alpha(0f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(6) { index ->
                    val char = otpValue.getOrNull(index)?.toString() ?: ""
                    val isFocused = otpValue.length == index
                    
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) PrimaryBlue else ComponentGray.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(WhitePure, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // New Password Field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "New Password", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Confirm Password Field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Confirm New Password", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible) R.drawable.eye_open else R.drawable.eye_slash
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
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
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Password Button
        Button(
            onClick = {
                if (otpValue.length == 6 && password.isNotBlank() && confirmPassword.isNotBlank()) {
                    if (password == confirmPassword) {
                        authViewModel.resetPassword(
                            ResetPasswordRequest(
                                email = email,
                                otp = otpValue,
                                password = password,
                                passwordConfirmation = confirmPassword
                            )
                        )
                    } else {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill in all details and enter 6-digit OTP", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue, contentColor = WhitePure),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = WhitePure)
            } else {
                Text(text = "Reset Password", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Countdown Timer & Resend OTP button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (countdown > 0) {
                Text(
                    text = "Resend OTP in ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${countdown}s",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            } else {
                Text(
                    text = "Didn't receive code? ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Resend OTP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.clickable {
                        authViewModel.sendForgotPasswordOtp(email)
                    }
                )
            }
        }
    }
}
