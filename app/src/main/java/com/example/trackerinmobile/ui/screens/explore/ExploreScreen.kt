package com.example.trackerinmobile.ui.screens.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.core.Routes
import com.example.trackerinmobile.ui.components.CustomBottomNavigation
import com.example.trackerinmobile.ui.theme.*

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.trackerinmobile.ui.screens.progress.CurriculumViewModel

@Composable
fun ExploreScreen() {
    val backStack = LocalBackStack.current
    val curriculumViewModel: CurriculumViewModel = hiltViewModel()
    val isLoading by curriculumViewModel.isLoading.collectAsState()
    val error by curriculumViewModel.error.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember {
        mutableStateListOf<String>().apply {
            addAll(curriculumViewModel.tokenManager.getRecentSearches())
        }
    }
    var hasSearchedTopic by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            curriculumViewModel.clearError()
        }
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                activeTab = 1,
                onTabSelected = { index ->
                    when (index) {
                        0 -> backStack.add(Routes.DashboardRoute)
                        2 -> backStack.add(Routes.ProgressRoute)
                        3 -> backStack.add(Routes.ProfileRoute)
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Explore Learning Paths",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, PrimaryBlue, RoundedCornerShape(12.dp))
                    .background(WhitePure, RoundedCornerShape(12.dp))
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search for 'UI Design', 'Machine Learning' ...", fontSize = 12.sp, color = TextGray) },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Button(
                    onClick = { 
                        if (searchQuery.isNotBlank()) {
                            hasSearchedTopic = searchQuery
                            curriculumViewModel.tokenManager.saveRecentSearch(searchQuery)
                            recentSearches.clear()
                            recentSearches.addAll(curriculumViewModel.tokenManager.getRecentSearches())
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.padding(4.dp).height(40.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text("Search", fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(painter = painterResource(id = R.drawable.search_icon), contentDescription = null, modifier = Modifier.size(14.dp), tint = WhitePure)
                }
            }

            // AI Roadmap Generator Card
            if (hasSearchedTopic != null && hasSearchedTopic!!.isNotBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PrimaryBlue.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WhitePure)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "AI Roadmap Generator",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ready to generate a personalized learning roadmap for:",
                            fontSize = 13.sp,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"${hasSearchedTopic}\"",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                val topic = hasSearchedTopic!!
                                curriculumViewModel.generateCurriculum(topic) { generatedId ->
                                    curriculumViewModel.tokenManager.saveRecentSearch(topic)
                                    recentSearches.clear()
                                    recentSearches.addAll(curriculumViewModel.tokenManager.getRecentSearches())
                                    hasSearchedTopic = null
                                    searchQuery = ""
                                    backStack.add(Routes.CurriculumDetailRoute(generatedId))
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = WhitePure,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating Roadmap...", fontSize = 14.sp)
                            } else {
                                Text("Generate Learning Path", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Search",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Black
                )
                if (recentSearches.isNotEmpty()) {
                    Text(
                        text = "Clear All",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                curriculumViewModel.tokenManager.clearRecentSearches()
                                recentSearches.clear()
                            }
                            .padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                recentSearches.take(3).forEach { search ->
                    RecentSearchItem(text = search) {
                        searchQuery = search
                        hasSearchedTopic = search
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun RecentSearchItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Since no history icon, using a generic indicator (dot or small circle) or just text
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .border(1.dp, TextGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(10.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 14.sp, color = Black, fontWeight = FontWeight.Medium)
    }
}


