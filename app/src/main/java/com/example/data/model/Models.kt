package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "channels")
data class Channel(
    @PrimaryKey val username: String, // e.g. "akharinkhabar" (without '@')
    val title: String,
    val description: String,
    val avatarUrl: String, // can be a reference or placeholder ID
    val isSubscribed: Boolean = false,
    val subscriberCount: String = "150K",
    val lastSyncedTimestamp: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey val id: String, // e.g., "akharinkhabar_1204"
    val channelUsername: String,
    val message: String,
    val date: Long, // timestamp
    val viewsCount: String = "12K",
    val mediaUrl: String? = null,
    val mediaType: String? = "text", // "text", "image", "alert"
    val isBookmarked: Boolean = false
) : Serializable

@Entity(tableName = "mirror_servers")
data class MirrorServer(
    @PrimaryKey val url: String, // e.g., "http://185.112.4.29", "https://mirror.polyvand.ir"
    val label: String,
    val isDefault: Boolean = false,
    val isActive: Boolean = true,
    val latencyMs: Int? = null,
    val lastChecked: Long = 0
) : Serializable
