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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.data.model.progress.MilestoneApiModel
import com.example.trackerinmobile.ui.theme.*

@Composable
fun CurriculumDetailScreen(curriculumId: Int) {
    val viewModel: CurriculumViewModel = hiltViewModel()
    val detail by viewModel.curriculumDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val backStack = LocalBackStack.current
    val context = LocalContext.current

    var generatingQuizForId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(curriculumId) {
        viewModel.loadCurriculumDetail(curriculumId)
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
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
                        text = "Curriculum Roadmap",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                }
            }
        }
    ) { paddingValues ->
        if (isLoading && detail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else {
            val curDetail = detail
            if (curDetail == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Curriculum not found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextGray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
                ) {
                    // Title and Description Card
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ComponentGray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = WhitePure)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = curDetail.topic,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = curDetail.description ?: "No description provided for this path.",
                                    fontSize = 14.sp,
                                    color = TextGray,
                                    lineHeight = 20.sp
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                val progressVal = curDetail.totalProgress ?: 0.0
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Path Progress",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = PrimaryBlue
                                    )
                                    Text(
                                        text = "${progressVal.toInt()}%",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Black
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                // Progress Bar Custom
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(Color(0xFFAAC4FF))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth((progressVal / 100.0).coerceIn(0.0, 1.0).toFloat())
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(5.dp))
                                            .background(PrimaryBlue)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Milestones Timeline",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Milestone Items
                    if (curDetail.milestones.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No milestones available for this curriculum.",
                                    color = TextGray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        itemsIndexed(curDetail.milestones) { index, milestone ->
                            MilestoneTimelineItem(
                                milestone = milestone,
                                stepNumber = index + 1,
                                isGeneratingQuiz = false,
                                onToggleComplete = { isCompleted ->
                                    val milestones = curDetail.milestones
                                    val currentIndex = milestones.indexOfFirst { it.id == milestone.id }
                                    if (isCompleted) {
                                        // 1. Check if previous milestone is completed
                                        val isPrevCompleted = currentIndex == 0 || milestones[currentIndex - 1].isCompleted
                                        if (isPrevCompleted) {
                                            // 2. Check if user completed the quiz for this milestone
                                            if (viewModel.isQuizCompleted(milestone.id)) {
                                                viewModel.toggleMilestoneComplete(milestone.id, true)
                                            } else {
                                                Toast.makeText(context, "Kerjakan dan selesaikan kuis untuk milestone ini terlebih dahulu!", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Selesaikan milestone sebelumnya terlebih dahulu!", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        // User wants to uncheck completion
                                        val canUncomplete = currentIndex == milestones.lastIndex || !milestones[currentIndex + 1].isCompleted
                                        if (canUncomplete) {
                                            viewModel.toggleMilestoneComplete(milestone.id, false)
                                        } else {
                                            Toast.makeText(context, "Batalkan milestone setelahnya terlebih dahulu!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onTakeQuiz = {
                                    backStack.add(Routes.QuizRoute(milestone.id))
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneTimelineItem(
    milestone: MilestoneApiModel,
    stepNumber: Int,
    isGeneratingQuiz: Boolean,
    onToggleComplete: (Boolean) -> Unit,
    onTakeQuiz: () -> Unit
) {
    val isCompleted = milestone.isCompleted
    val opacity = if (isCompleted) 0.6f else 1.0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ComponentGray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhitePure)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circle Step Number
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) Color(0xFFD1E3FF) else Color(0xFFF1F5F9))
                        .border(
                            1.dp,
                            if (isCompleted) PrimaryBlue else ComponentGray.copy(alpha = 0.5f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$stepNumber",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) PrimaryBlue else Black
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Text details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = milestone.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Black.copy(alpha = opacity),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    if (isCompleted && milestone.completedAt != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Completed",
                            fontSize = 11.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Completion Checkbox Button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isCompleted) PrimaryBlue else Color.Transparent)
                        .border(2.dp, PrimaryBlue, CircleShape)
                        .clickable { onToggleComplete(!isCompleted) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(WhitePure)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Take AI Quiz Button
            Button(
                onClick = onTakeQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) PrimaryBlue else Color(0xFFE2E8F0),
                    contentColor = if (isCompleted) WhitePure else BlackishBlue
                ),
                enabled = !isGeneratingQuiz
            ) {
                if (isGeneratingQuiz) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = if (isCompleted) WhitePure else PrimaryBlue,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generating Quiz...", fontSize = 13.sp)
                } else {
                    Text(
                        text = if (isCompleted) "Practice Quiz (Completed)" else "Practice Quiz with AI",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
