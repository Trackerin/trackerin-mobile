package com.example.trackerinmobile.data.model.notification

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationApiModel(
    val id: Int,
    @SerialName("user_id")
    val userId: Int,
    val title: String,
    val message: String,
    @SerialName("is_read")
    val isRead: Boolean,
    @SerialName("sent_at")
    val sentAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class NotificationListResponse(
    val data: List<NotificationApiModel>? = null,
    val message: String? = null
)

@Serializable
data class NotificationItemResponse(
    val data: NotificationApiModel,
    val message: String? = null
)
