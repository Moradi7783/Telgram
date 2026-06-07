package com.example.data.database

import androidx.room.*
import com.example.data.model.Channel
import com.example.data.model.Post
import com.example.data.model.MirrorServer
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels ORDER BY title ASC")
    fun getAllChannels(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE isSubscribed = 1 ORDER BY title ASC")
    fun getSubscribedChannels(): Flow<List<Channel>>

    @Query("SELECT * FROM channels WHERE username = :username LIMIT 1")
    suspend fun getChannelByUsername(username: String): Channel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: Channel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<Channel>)

    @Query("UPDATE channels SET isSubscribed = :isSubscribed WHERE username = :username")
    suspend fun updateSubscription(username: String, isSubscribed: Int)

    @Delete
    suspend fun deleteChannel(channel: Channel)
}

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY date DESC")
    fun getAllPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE channelUsername = :channelUsername ORDER BY date DESC")
    fun getPostsByChannel(channelUsername: String): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE isBookmarked = 1 ORDER BY date DESC")
    fun getBookmarkedPosts(): Flow<List<Post>>

    @Query("SELECT * FROM posts WHERE message LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchPosts(query: String): Flow<List<Post>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<Post>)

    @Query("UPDATE posts SET isBookmarked = :isBookmarked WHERE id = :postId")
    suspend fun updateBookmark(postId: String, isBookmarked: Int)

    @Query("DELETE FROM posts")
    suspend fun clearAllPosts()
}

@Dao
interface MirrorServerDao {
    @Query("SELECT * FROM mirror_servers ORDER BY isDefault DESC, url ASC")
    fun getAllMirrors(): Flow<List<MirrorServer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMirror(mirror: MirrorServer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMirrors(mirrors: List<MirrorServer>)

    @Query("DELETE FROM mirror_servers WHERE url = :url")
    suspend fun deleteMirrorByUrl(url: String)

    @Query("UPDATE mirror_servers SET latencyMs = :latency, lastChecked = :lastChecked WHERE url = :url")
    suspend fun updateMirrorLatency(url: String, latency: Int, lastChecked: Long)
}
