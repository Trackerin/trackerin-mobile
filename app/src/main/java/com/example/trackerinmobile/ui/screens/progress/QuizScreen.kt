package com.example.trackerinmobile.ui.screens.progress

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.ui.theme.*

@Composable
fun QuizScreen(milestoneId: Int) {
    val viewModel: CurriculumViewModel = hiltViewModel()
    val quizState by viewModel.quiz.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val backStack = LocalBackStack.current
    val context = LocalContext.current

    // Local Quiz State
    var currentQuestionIndex by remember { mutableStateOf(0) }
    val selectedAnswers = remember { mutableStateMapOf<Int, String>() }
    var showResults by remember { mutableStateOf(false) }
    var apiError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(milestoneId) {
        apiError = null
        viewModel.generateQuiz(milestoneId)
    }

    LaunchedEffect(error) {
        error?.let {
            apiError = it
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearQuiz()
        }
    }

    Scaffold(
        containerColor = BackgroundApp,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhitePure)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = { backStack.removeLastOrNull() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.arrow_right),
                            contentDescription = "Back",
                            tint = Black,
                            modifier = Modifier.rotate(180f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (showResults) "Quiz Results" else "AI Milestone Quiz",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                }
            }
        }
    ) { paddingValues ->
        if (quizState == null && apiError == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = PrimaryBlue)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading your interactive quiz...",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            val quiz = quizState
            if (quiz == null || quiz.questions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "No Quiz Generated",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = apiError ?: "We couldn't generate a quiz for this milestone. Try again later.",
                            fontSize = 14.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { backStack.removeLastOrNull() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            } else {
                val questions = quiz.questions
                if (showResults) {
                    // Quiz Review and Scorecard Screen
                    val score = questions.filterIndexed { index, question ->
                        selectedAnswers[index] == question.answer
                    }.size

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
                    ) {
                        item {
                            // Summary Score Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, ComponentGray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = WhitePure)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Your Quiz Score",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextGray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "$score / ${questions.size}",
                                        fontSize = 40.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val percent = ((score.toFloat() / questions.size) * 100).toInt()
                                    Text(
                                        text = "$percent% Correct",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (percent >= 60) Color(0xFF10B981) else Color(0xFFEF4444)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = if (percent >= 80) "Excellent job! You've mastered this milestone!"
                                        else if (percent >= 60) "Good effort! Review the concepts and keep practicing."
                                        else "Keep learning! Give it another try to reinforce your understanding.",
                                        fontSize = 13.sp,
                                        color = BlackishBlue,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Questions Review",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // List out all questions with results
                        itemsIndexed(questions) { index, question ->
                            val userAnswer = selectedAnswers[index]
                            val isCorrect = userAnswer == question.answer

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .border(1.dp, ComponentGray.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = WhitePure)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Q${index + 1}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isCorrect) "✓" else "✕",
                                            color = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = question.question,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Black
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Display options with color coding
                                    question.options.forEach { option ->
                                        val isOptionCorrect = option == question.answer
                                        val isOptionUserSelected = option == userAnswer

                                        val (optBg, optBorder, optText) = when {
                                            isOptionCorrect -> Triple(Color(0xFFECFDF5), Color(0xFF10B981), Color(0xFF065F46))
                                            isOptionUserSelected && !isCorrect -> Triple(Color(0xFFFEF2F2), Color(0xFFEF4444), Color(0xFF991B1B))
                                            else -> Triple(Color(0xFFF9FAFB), Color(0xFFE5E7EB), BlackishBlue)
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .background(optBg, RoundedCornerShape(8.dp))
                                                .border(1.dp, optBorder, RoundedCornerShape(8.dp))
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = option,
                                                fontSize = 13.sp,
                                                color = optText,
                                                fontWeight = if (isOptionCorrect || isOptionUserSelected) FontWeight.Bold else FontWeight.Normal,
                                                modifier = Modifier.weight(1f)
                                            )
                                            if (isOptionCorrect) {
                                                Text(
                                                    text = "✓",
                                                    color = Color(0xFF10B981),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                            } else if (isOptionUserSelected) {
                                                Text(
                                                    text = "✕",
                                                    color = Color(0xFFEF4444),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { backStack.removeLastOrNull() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text("Back to Milestones", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    // Quiz Active Screen (Question by Question Card layout)
                    val currentQuestion = questions[currentQuestionIndex]

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(24.dp)
                    ) {
                        // Progress bar indication
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                            Text(
                                text = "${((currentQuestionIndex + 1) * 100) / questions.size}%",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth((currentQuestionIndex + 1).toFloat() / questions.size)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(PrimaryBlue)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Question & Options Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .border(1.dp, ComponentGray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = WhitePure)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = currentQuestion.question,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Black,
                                    lineHeight = 24.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Options List
                                currentQuestion.options.forEach { option ->
                                    val isSelected = selectedAnswers[currentQuestionIndex] == option
                                    val borderColor = if (isSelected) PrimaryBlue else ComponentGray.copy(alpha = 0.3f)
                                    val bgColor = if (isSelected) Color(0xFFF0F5FF) else Color.Transparent

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(bgColor)
                                            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                                            .clickable { selectedAnswers[currentQuestionIndex] = option }
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Custom Radio Indicator
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, if (isSelected) PrimaryBlue else ComponentGray, CircleShape)
                                                .background(if (isSelected) PrimaryBlue else Color.Transparent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(WhitePure)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = option,
                                            fontSize = 14.sp,
                                            color = Black,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Navigation Buttons: Back & Next/Submit
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                                enabled = currentQuestionIndex > 0,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE2E8F0),
                                    contentColor = BlackishBlue
                                )
                            ) {
                                Text("Previous", fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            val hasSelected = selectedAnswers.containsKey(currentQuestionIndex)
                            val isLast = currentQuestionIndex == questions.size - 1

                            Button(
                                onClick = {
                                    if (isLast) {
                                        viewModel.setQuizCompleted(milestoneId, true)
                                        showResults = true
                                    } else {
                                        currentQuestionIndex++
                                    }
                                },
                                enabled = hasSelected,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                            ) {
                                Text(
                                    text = if (isLast) "Submit Quiz" else "Next",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
