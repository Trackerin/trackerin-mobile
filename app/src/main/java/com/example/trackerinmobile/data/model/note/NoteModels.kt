package com.example.trackerinmobile.data.model.note

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteApiModel(
    val id: Int,
    @SerialName("milestone_id")
    val milestoneId: Int? = null,
    val title: String,
    val content: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class NoteResponse(
    val data: List<NoteApiModel>? = null,
    val message: String? = null
)

@Serializable
data class SingleNoteResponse(
    val data: NoteApiModel,
    val message: String? = null
)

@Serializable
data class CreateNoteRequest(
    val title: String,
    val content: String,
    @SerialName("milestone_id")
    val milestoneId: Int? = null
)

@Serializable
data class UpdateNoteRequest(
    val title: String,
    val content: String
)
