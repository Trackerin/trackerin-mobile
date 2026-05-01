package com.example.trackerinmobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.trackerinmobile.R
import com.example.trackerinmobile.ui.theme.BlackishBlue
import com.example.trackerinmobile.ui.theme.PrimaryBlue
import com.example.trackerinmobile.ui.theme.WhitePure

@Composable
fun CustomBottomNavigation(activeTab: Int = 0, onTabSelected: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(BlackishBlue)
                .padding(vertical = 12.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home
            TabIcon(
                isActive = activeTab == 0,
                iconId = R.drawable.home_icon,
                contentDescription = "Home",
                onClick = { onTabSelected(0) }
            )

            // Search
            TabIcon(
                isActive = activeTab == 1,
                iconId = R.drawable.search_icon,
                contentDescription = "Search",
                onClick = { onTabSelected(1) }
            )

            // Book/Curriculum -> Progress
            TabIcon(
                isActive = activeTab == 2,
                iconId = R.drawable.book_icon,
                contentDescription = "Curriculum",
                onClick = { onTabSelected(2) }
            )

            // Profile
            TabIcon(
                isActive = activeTab == 3,
                iconId = R.drawable.profile_icon,
                contentDescription = "Profile",
                onClick = { onTabSelected(3) }
            )
        }
    }
}

@Composable
private fun TabIcon(isActive: Boolean, iconId: Int, contentDescription: String, onClick: () -> Unit) {
    if (isActive) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = contentDescription,
                tint = WhitePure,
                modifier = Modifier.size(24.dp)
            )
        }
    } else {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = contentDescription,
                tint = WhitePure,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

