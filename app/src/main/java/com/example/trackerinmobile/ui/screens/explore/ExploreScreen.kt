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

@Composable
fun ExploreScreen() {
    val backStack = LocalBackStack.current
    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember { mutableStateListOf("Python Crash Course", "Agile Methodologies", "Figma Prototyping") }
    var showNotFoundDialog by remember { mutableStateOf(false) }
    
    val smartSuggestions = listOf(
        SuggestionItem("Data Science Fundamentals", R.drawable.fire_icon),
        SuggestionItem("UI/UX Design Systems", R.drawable.plus_icon),
        SuggestionItem("Machine Learning Models", R.drawable.book_icon),
        SuggestionItem("Advanced React Patterns", R.drawable.search_icon),
    )

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
                            // Simulasi pencarian: Jika query tidak ada di suggestion atau recent, tampilkan not found
                            val allPossibleTopics = (recentSearches + smartSuggestions.map { it.text }).map { it.lowercase() }
                            
                            if (allPossibleTopics.any { it.contains(searchQuery.lowercase()) }) {
                                if (!recentSearches.contains(searchQuery)) {
                                    recentSearches.add(0, searchQuery)
                                }
                            } else {
                                showNotFoundDialog = true
                            }
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

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Recent Search",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                recentSearches.take(3).forEach { search ->
                    RecentSearchItem(text = search) {
                        searchQuery = search
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Smart Suggestions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SuggestionChip(smartSuggestions[0]) { searchQuery = smartSuggestions[0].text }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)) {
                    SuggestionChip(smartSuggestions[1]) { searchQuery = smartSuggestions[1].text }
                    SuggestionChip(smartSuggestions[2]) { searchQuery = smartSuggestions[2].text }
                }

                SuggestionChip(smartSuggestions[3]) { searchQuery = smartSuggestions[3].text }
            }
        }

        if (showNotFoundDialog) {
            AlertDialog(
                onDismissRequest = { showNotFoundDialog = false },
                title = { 
                    Text(
                        text = "Result Not Found",
                        fontWeight = FontWeight.Bold,
                        color = Black
                    )
                },
                text = {
                    Text(
                        text = "Sorry, we couldn't find any learning paths for \"$searchQuery\". Try searching for other topics like 'UI Design' or 'Kotlin'.",
                        color = TextGray
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { showNotFoundDialog = false }
                    ) {
                        Text("Try Again", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = WhitePure
            )
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

@Composable
fun SuggestionChip(item: SuggestionItem, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
        color = Color(0xFFF1F5F9), 
        modifier = Modifier.height(36.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = item.text, fontSize = 12.sp, color = Black, fontWeight = FontWeight.SemiBold)
        }
    }
}

data class SuggestionItem(val text: String, val iconRes: Int)
