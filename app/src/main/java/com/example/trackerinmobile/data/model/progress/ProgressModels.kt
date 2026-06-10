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
    val totalProgress: Double? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class CurriculumDetailResponse(
    val data: CurriculumDetailApiModel
)

@Serializable
data class CurriculumDetailApiModel(
    val id: Int,
    val topic: String,
    val description: String? = null,
    @SerialName("total_progress")
    val totalProgress: Double? = null,
    @SerialName("is_completed")
    val isCompleted: Boolean = false,
    val milestones: List<MilestoneApiModel> = emptyList()
)

@Serializable
data class MilestoneApiModel(
    val id: Int,
    @SerialName("curriculum_id")
    val curriculumId: Int,
    val title: String,
    @SerialName("order_index")
    val orderIndex: Int,
    @SerialName("is_completed")
    val isCompleted: Boolean,
    @SerialName("completed_at")
    val completedAt: String? = null
)

@Serializable
data class GenerateCurriculumRequest(
    val topic: String
)

@Serializable
data class GenerateCurriculumResponse(
    val message: String? = null,
    val data: CurriculumDetailApiModel
)

@Serializable
data class CompleteMilestoneRequest(
    @SerialName("is_completed")
    val isCompleted: Boolean
)

@Serializable
data class CompleteMilestoneResponse(
    val message: String? = null,
    val data: MilestoneApiModel
)

@Serializable
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    @SerialName("correct_answer")
    val answer: String
)

@Serializable
data class QuizResponse(
    val message: String? = null,
    val data: QuizApiModel
)

@Serializable
data class QuizApiModel(
    val id: Int,
    @SerialName("milestone_id")
    val milestoneId: Int,
    val questions: List<QuizQuestion> = emptyList()
)

@Serializable
data class ContactRequest(
    val name: String,
    val email: String,
    val subject: String,
    val message: String
)

