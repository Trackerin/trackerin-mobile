package com.example.trackerinmobile.ui.screens.notes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.trackerinmobile.R
import com.example.trackerinmobile.core.LocalBackStack
import com.example.trackerinmobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(noteId: Int? = null) {
    val backStack = LocalBackStack.current
    val context = LocalContext.current
    val viewModel: NotesViewModel = hiltViewModel()

    val notes by viewModel.notes.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    // Initialize fields once the notes are loaded (if editing)
    LaunchedEffect(notes, noteId) {
        if (!isInitialized) {
            if (noteId != null) {
                val existingNote = notes.firstOrNull { it.id == noteId }
                if (existingNote != null) {
                    title = existingNote.title
                    content = existingNote.content
                    isInitialized = true
                }
            } else {
                isInitialized = true
            }
        }
    }

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = BackgroundApp
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    .graphicsLayer(rotationZ = 180f)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = if (noteId == null) "Add Note" else "Edit Note",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Black
                        )
                    }

                    // Delete button (only for existing notes)
                    if (noteId != null) {
                        TextButton(
                            onClick = {
                                viewModel.deleteNote(noteId) {
                                    Toast.makeText(context, "Note deleted successfully!", Toast.LENGTH_SHORT).show()
                                    backStack.removeLastOrNull()
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Delete", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (noteId != null && !isInitialized && isLoading) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                } else {
                    // Editor Section
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Title Editor
                        TextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("Note Title", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextGray.copy(alpha = 0.6f)) },
                            textStyle = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Black
                            ),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Divider line
                        HorizontalDivider(
                            color = ComponentGray.copy(alpha = 0.4f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Content Editor
                        TextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = { Text("Start writing your notes here...", fontSize = 16.sp, color = TextGray.copy(alpha = 0.6f)) },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = Black,
                                lineHeight = 24.sp
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 200.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(88.dp)) // Save button space
            }

            // Bottom Save Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            if (noteId == null) {
                                viewModel.createNote(title.trim(), content.trim()) {
                                    Toast.makeText(context, "Note saved successfully!", Toast.LENGTH_SHORT).show()
                                    backStack.removeLastOrNull()
                                }
                            } else {
                                viewModel.updateNote(noteId, title.trim(), content.trim()) {
                                    Toast.makeText(context, "Note updated successfully!", Toast.LENGTH_SHORT).show()
                                    backStack.removeLastOrNull()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Title and content cannot be empty!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = WhitePure, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = "Save Note",
                            color = WhitePure,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
