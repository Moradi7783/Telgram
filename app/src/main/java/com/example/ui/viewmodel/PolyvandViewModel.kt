package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Channel
import com.example.data.model.MirrorServer
import com.example.data.model.Post
import com.example.data.repository.PolyvandRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PolyvandViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PolyvandRepository

    // Base flows from database
    val allChannels: StateFlow<List<Channel>>
    val subscribedChannels: StateFlow<List<Channel>>
    val allMirrors: StateFlow<List<MirrorServer>>

    // UI state states
    private val _selectedTab = MutableStateFlow(0) // 0: Feed, 1: Mirrors, 2: P2P, 3: Bundle
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedChannelUsername = MutableStateFlow<String?>(null)
    val selectedChannelUsername: StateFlow<String?> = _selectedChannelUsername.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _newChannelInput = MutableStateFlow("")
    val newChannelInput: StateFlow<String> = _newChannelInput.asStateFlow()

    private val _syncingChannel = MutableStateFlow<String?>(null)
    val syncingChannel: StateFlow<String?> = _syncingChannel.asStateFlow()

    private val _syncStatusMessage = MutableStateFlow<String?>(null)
    val syncStatusMessage: StateFlow<String?> = _syncStatusMessage.asStateFlow()

    // Preferences & Accessibility
    private val _textSizeMultiplier = MutableStateFlow(1.0f) // 0.8 to 1.6
    val textSizeMultiplier: StateFlow<Float> = _textSizeMultiplier.asStateFlow()

    private val _saveDataMode = MutableStateFlow(true) // default to true inside intranets
    val saveDataMode: StateFlow<Boolean> = _saveDataMode.asStateFlow()

    // P2P State Simulation (LAN & Hotspot Local Swarm)
    private val _isP2pHosting = MutableStateFlow(false)
    val isP2pHosting: StateFlow<Boolean> = _isP2pHosting.asStateFlow()

    private val _isP2pReceiving = MutableStateFlow(false)
    val isP2pReceiving: StateFlow<Boolean> = _isP2pReceiving.asStateFlow()

    private val _p2pTargetIpInput = MutableStateFlow("192.168.43.1")
    val p2pTargetIpInput: StateFlow<String> = _p2pTargetIpInput.asStateFlow()

    private val _p2pLogs = MutableStateFlow<List<String>>(emptyList())
    val p2pLogs: StateFlow<List<String>> = _p2pLogs.asStateFlow()

    // Bundle Backups State
    private val _pastedJsonInput = MutableStateFlow("")
    val pastedJsonInput: StateFlow<String> = _pastedJsonInput.asStateFlow()

    private val _backupStatusMessage = MutableStateFlow<Pair<Boolean, String>?>(null) // Pair(isSuccess, message)
    val backupStatusMessage: StateFlow<Pair<Boolean, String>?> = _backupStatusMessage.asStateFlow()

    // Custom Mirror Server Input State
    private val _newMirrorUrlInput = MutableStateFlow("http://")
    val newMirrorUrlInput: StateFlow<String> = _newMirrorUrlInput.asStateFlow()

    private val _newMirrorLabelInput = MutableStateFlow("")
    val newMirrorLabelInput: StateFlow<String> = _newMirrorLabelInput.asStateFlow()

    private val _isTestingMirrors = MutableStateFlow(false)
    val isTestingMirrors: StateFlow<Boolean> = _isTestingMirrors.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PolyvandRepository(
            database.channelDao(),
            database.postDao(),
            database.mirrorServerDao()
        )

        allChannels = repository.allChannels.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        subscribedChannels = repository.subscribedChannels.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allMirrors = repository.allMirrors.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        // Seed initial mock data for instant offline readiness
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Filtered lists matching search, channel, and UI flags
    val filteredPosts: StateFlow<List<Post>> = combine(
        repository.allPosts,
        _selectedChannelUsername,
        _searchQuery
    ) { posts, selectedChannel, query ->
        var list = posts
        if (selectedChannel != null) {
            list = list.filter { it.channelUsername == selectedChannel }
        }
        if (query.isNotEmpty()) {
            list = list.filter { it.message.contains(query, ignoreCase = true) }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedPosts: StateFlow<List<Post>> = repository.getBookmarkedPosts().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // Actions
    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun selectChannel(username: String?) {
        _selectedChannelUsername.value = username
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setNewChannelInput(input: String) {
        _newChannelInput.value = input
    }

    fun setNewMirrorUrlInput(url: String) {
        _newMirrorUrlInput.value = url
    }

    fun setNewMirrorLabelInput(label: String) {
        _newMirrorLabelInput.value = label
    }

    fun setPastedJsonInput(json: String) {
        _pastedJsonInput.value = json
    }

    fun setP2pTargetIpInput(ip: String) {
        _p2pTargetIpInput.value = ip
    }

    fun adjustTextSize(increase: Boolean) {
        val current = _textSizeMultiplier.value
        if (increase && current < 1.6f) {
            _textSizeMultiplier.value = current + 0.15f
        } else if (!increase && current > 0.85f) {
            _textSizeMultiplier.value = current - 0.15f
        }
    }

    fun toggleSaveDataMode() {
        _saveDataMode.value = !_saveDataMode.value
    }

    fun toggleChannelSubscription(channel: Channel) {
        viewModelScope.launch {
            repository.toggleSubscription(channel.username, channel.isSubscribed)
        }
    }

    fun togglePostBookmark(post: Post) {
        viewModelScope.launch {
            repository.toggleBookmark(post.id, post.isBookmarked)
        }
    }

    // Simulates testing latency of all local domestic servers
    fun testAllMirrorsLatency() {
        viewModelScope.launch {
            _isTestingMirrors.value = true
            val mirrors = allMirrors.value
            for (mirror in mirrors) {
                kotlinx.coroutines.delay(600) // Simulate latency ping
                val simulatedLatency = if (mirror.url.startsWith("https")) {
                    (70..150).random()
                } else {
                    (110..220).random()
                }
                repository.updateMirrorLatency(mirror.url, simulatedLatency)
            }
            _isTestingMirrors.value = false
        }
    }

    // Add unique custom mirror Server
    fun addNewMirror() {
        val url = _newMirrorUrlInput.value.trim()
        val label = _newMirrorLabelInput.value.trim()
        if (url.startsWith("http") && label.isNotEmpty()) {
            viewModelScope.launch {
                repository.addMirror(
                    MirrorServer(
                        url = url,
                        label = label,
                        isDefault = false,
                        isActive = true,
                        latencyMs = null,
                        lastChecked = 0
                    )
                )
                _newMirrorUrlInput.value = "http://"
                _newMirrorLabelInput.value = ""
            }
        }
    }

    fun deleteMirror(url: String) {
        viewModelScope.launch {
            repository.deleteMirror(url)
        }
    }

    // Channel Sync Engine Trigger
    fun syncChannelViaActiveMirrors(targetUsername: String) {
        val username = targetUsername.replace("@", "").trim()
        if (username.isEmpty()) return

        // Immediately select the channel username to show existing local database cache at lightning-fast speed
        _selectedChannelUsername.value = username

        viewModelScope.launch {
            _syncingChannel.value = username
            _syncStatusMessage.value = "موتور در حال اتصال به سرور همتا..."
            kotlinx.coroutines.delay(400)

            // Select active mirror if any, otherwise default to "https://ir-mirror1.polyvand.ir"
            val activeMirror = allMirrors.value.firstOrNull { it.isActive }?.url ?: "https://ir-mirror1.polyvand.ir"
            _syncStatusMessage.value = "$activeMirror اتصال مقاوم با گره ملی..."
            kotlinx.coroutines.delay(500)

            _syncStatusMessage.value = "در حال بازخوانی هش‌ها و دور زدن فیلترینگ..."
            kotlinx.coroutines.delay(400)

            try {
                // Returns newly cached posts if any
                val posts = repository.syncWithMirror(activeMirror, username)
                if (posts.isNotEmpty()) {
                    _syncStatusMessage.value = "همگام‌سازی تکمیل شد! تعداد ${posts.size} پست جدید بازیابی شد."
                } else {
                    _syncStatusMessage.value = "آرشیو محلی کانال خوانده شد. مطالب به روز هستند."
                }
            } catch (e: Exception) {
                _syncStatusMessage.value = "خطا در اتصال به هاب: ${e.message}"
            }

            kotlinx.coroutines.delay(2000)
            _syncingChannel.value = null
            _syncStatusMessage.value = null
            _newChannelInput.value = ""
        }
    }

    // P2P Local Hotspot Share - Server Mode
    fun startP2pShareServer() {
        if (_isP2pHosting.value) {
            // Stop
            _isP2pHosting.value = false
            _p2pLogs.value = emptyList()
        } else {
            // Start
            _isP2pHosting.value = true
            _p2pLogs.value = listOf(
                "درگاه فرستنده همتا (P2P Server) با موفقیت روی پورت ۷۰۷۰ باز شد.",
                "در حال پاسخگویی در آدرس آی‌پی هات‌اسپات ۱۹۲.۱۶۸.۴۳.۱ ...",
                "منتظر اتصال لایه‌محلی همسایه‌ها..."
            )
            
            // Simulates periodic local node handshakes
            viewModelScope.launch {
                kotlinx.coroutines.delay(4000)
                if (!_isP2pHosting.value) return@launch
                val updatedLogs = _p2pLogs.value.toMutableList()
                updatedLogs.add(0, "✅ همتای جدید با موفقیت متصل شد (دستگاه Redmi Note 11)")
                updatedLogs.add(0, "⏱️ درخواست دریافت آدرس‌ها و لیست پست‌ها... ")
                _p2pLogs.value = updatedLogs

                kotlinx.coroutines.delay(2500)
                if (!_isP2pHosting.value) return@launch
                val updatedLogs2 = _p2pLogs.value.toMutableList()
                updatedLogs2.add(0, "📤 ارسال ۴ کانال و ۷ پست همگام‌سازی شده با فشرده‌سازی لایت")
                updatedLogs2.add(0, "🎉 انتقال بسته محتوایی به همتا کامل شد! حجم کل: ۱۲ کیلوبایت")
                _p2pLogs.value = updatedLogs2
            }
        }
    }

    // P2P Local Hotspot Share - Client Mode
    fun startP2pReceiveClient() {
        if (_isP2pReceiving.value) return

        viewModelScope.launch {
            _isP2pReceiving.value = true
            val updatedLogs = mutableListOf<String>()
            updatedLogs.add("در حال تلاش برای اتصال به فرستنده همتا در آدرس ${_p2pTargetIpInput.value}:7070...")
            _p2pLogs.value = updatedLogs

            kotlinx.coroutines.delay(1200)
            updatedLogs.add(0, "دست دادن امن با سرور همتا (Handshake)...")
            _p2pLogs.value = updatedLogs.toList()

            kotlinx.coroutines.delay(1000)
            updatedLogs.add(0, "اتصال موثر برقرار شد. پهنای باند محلی: ۳۸ مگابیت بر ثانیه")
            updatedLogs.add(0, "در حال خواندن بسته محتوایی همسایه...")
            _p2pLogs.value = updatedLogs.toList()

            kotlinx.coroutines.delay(1500)
            
            // Generate some random posts fetched via P2P
            val currentTime = System.currentTimeMillis()
            val p2pPosts = listOf(
                Post(
                    id = "p2p_post_1",
                    channelUsername = "web_mesh",
                    message = "🔥 **این پست کاملاً آفلاین دریافت شده است!**\n\nاین یک مطلب اشتراک‌گذاری شده توسط همسایگان شما در سیستم همتای پلیوند (Polyvand Mesh Swarm) است.\n\nبه دیتابیس لوکال Room اضافه گردید تا دیگر هم محله‌ای‌ها هم واکشی کنند.",
                    date = currentTime,
                    viewsCount = "P2P Net",
                    mediaType = "image"
                ),
                Post(
                    id = "p2p_post_2",
                    channelUsername = "akharinkhabar",
                    message = "🚨 **پست فوروارد شده همتا:**\n\nامروز ترافیک اینترنت در برخی بخش‌ها به وضعیت سفید در آمد اما فیلترینگ تلگرام همچنان قفل باقی مانده است. سرورهای لایه‌سیم‌پیچ داخلی کماکان بهترین پایداری را ارائه می‌دهند.",
                    date = currentTime - 100000,
                    viewsCount = "P2P Net",
                    mediaType = "text"
                )
            )
            
            repository.addChannel(
                Channel(
                    username = "web_mesh",
                    title = "راهنمای همتا به همتا (Mesh)",
                    description = "آموزش‌های کاربردی برای انتقال اطلاعات کاملاً آفلاین بدون اینترنت جهانی",
                    avatarUrl = "mesh",
                    isSubscribed = true
                )
            )
            repository.addChannel(
                Channel(
                    username = "akharinkhabar",
                    title = "آخرین خبر آفلاین",
                    description = "مجموعه اخبار سراسری، تکنولوژی و ورزشی همگام‌سازی شده با دیتابیس محلی",
                    avatarUrl = "news",
                    isSubscribed = true
                )
            )

            // Insert
            val db = AppDatabase.getDatabase(getApplication())
            db.postDao().insertPosts(p2pPosts)

            updatedLogs.add(0, "✅ دریافت با موفقیت پایان یافت! ۲ پست همتا به دیتابیس شما افزوده شد.")
            _p2pLogs.value = updatedLogs.toList()
            _isP2pReceiving.value = false
        }
    }

    // Backup Generation & Extraction (Copy / Paste manual bundles for extreme cases)
    fun triggerExportBackup(): String {
        return try {
            val root = JSONObject()
            root.put("app", "Polyvand")
            root.put("version", 1)
            root.put("exportedAt", System.currentTimeMillis())

            val channelsJson = JSONArray()
            val loadedChannels = allChannels.value
            for (c in loadedChannels) {
                val j = JSONObject()
                j.put("username", c.username)
                j.put("title", c.title)
                j.put("description", c.description)
                j.put("avatarUrl", c.avatarUrl)
                j.put("subscriberCount", c.subscriberCount)
                channelsJson.put(j)
            }
            root.put("channels", channelsJson)

            val postsJson = JSONArray()
            val loadedPosts = filteredPosts.value.take(20) // Take recent 20 posts for packing density
            for (p in loadedPosts) {
                val j = JSONObject()
                j.put("id", p.id)
                j.put("channelUsername", p.channelUsername)
                j.put("message", p.message)
                j.put("date", p.date)
                j.put("viewsCount", p.viewsCount)
                j.put("mediaType", p.mediaType)
                postsJson.put(j)
            }
            root.put("posts", postsJson)

            val resultStr = root.toString(2)
            _pastedJsonInput.value = resultStr
            _backupStatusMessage.value = Pair(true, "بسته محتوایی آفلاین شامل ${loadedChannels.size} کانال و ${loadedPosts.size} پست با موفقیت در کادر زیر تولید گردید! لطفا کپی کرده و برای دوستان ارسال کنید.")
            resultStr
        } catch (e: Exception) {
            _backupStatusMessage.value = Pair(false, "خطا در ایجاد بسته پشتیبان: ${e.message}")
            ""
        }
    }

    fun triggerImportBackup() {
        val json = _pastedJsonInput.value.trim()
        if (json.isEmpty()) {
            _backupStatusMessage.value = Pair(false, "لطفا متن فایل جیسون (JSON) دریافتی را در کادر پیست کنید.")
            return
        }

        viewModelScope.launch {
            val success = repository.importBackupJson(json)
            if (success) {
                _backupStatusMessage.value = Pair(true, "تبریک! بقچه پشتیبان به درستی همگام شد. محتوای کانال‌ها در بخش فید افزوده شدند.")
                _pastedJsonInput.value = ""
                _selectedTab.value = 0 // Auto switch to main feed!
            } else {
                _backupStatusMessage.value = Pair(false, "خطا در پارس یا ساختار فرمت بقچه آفلاین. مطمئن شوید متن کامل کپی شده است.")
            }
        }
    }

    fun clearBackupStatusMessage() {
        _backupStatusMessage.value = null
    }

    // Helper formatter
    fun formatTimestamp(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm - yyyy/MM/dd", Locale("fa"))
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            SimpleDateFormat("HH:mm - MM/dd", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
