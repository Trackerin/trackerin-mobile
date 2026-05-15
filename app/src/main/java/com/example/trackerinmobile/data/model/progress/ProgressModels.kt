package com.example.trackerinmobile.data.model.progress

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodoRequest(
    val task: String,
    @SerialName("is_done")
    val isDone: Boolean
)

@Serializable
data class TodoResponse(
    val data: List<TodoApiModel>? = null,
    val message: String? = null
)

@Serializable
data class SingleTodoResponse(
    val data: TodoApiModel,
    val message: String? = null
)

@Serializable
data class TodoApiModel(
    val id: Int,
    val task: String,
    @SerialName("is_done")
    val isDone: Boolean
)

// For curriculums (to get total progress)
@Serializable
data class CurriculumsResponse(
    val data: List<CurriculumApiModel>? = null
)

@Serializable
data class CurriculumApiModel(
    val id: Int,
    val topic: String,
    @SerialName("total_progress")
    val totalProgress: Int? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

