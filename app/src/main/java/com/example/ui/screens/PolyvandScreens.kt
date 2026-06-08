package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.data.model.MirrorServer
import com.example.data.model.Post
import com.example.ui.viewmodel.PolyvandViewModel
import com.example.ui.theme.LimeActivePing
import com.example.ui.theme.TextGreenMuted
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolyvandMainScreen(
    viewModel: PolyvandViewModel,
    modifier: Modifier = Modifier
) {
    // Read state from ViewModel
    val selectedTab by viewModel.selectedTab.collectAsState()
    val textSizeMultiplier by viewModel.textSizeMultiplier.collectAsState()
    val saveDataMode by viewModel.saveDataMode.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Row for Title and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Accessibility controls and mesh active badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // High Density Mesh Active Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFFD3E3FD))
                                .border(1.dp, Color(0xFF001D35), RoundedCornerShape(50.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF2E7D32))
                            )
                            Text(
                                text = "شبکه محلی فعال",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001D35)
                            )
                        }

                        Spacer(modifier = Modifier.width(2.dp))

                        IconButton(
                            onClick = { viewModel.adjustTextSize(increase = true) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(17.dp))
                                .testTag("btn_zoom_in")
                        ) {
                            Text("A+", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { viewModel.adjustTextSize(increase = false) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(17.dp))
                                .testTag("btn_zoom_out")
                        ) {
                            Text("A-", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        IconButton(
                            onClick = { viewModel.toggleSaveDataMode() },
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (saveDataMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant, 
                                    RoundedCornerShape(17.dp)
                                )
                                .testTag("btn_save_data")
                        ) {
                            Icon(
                                imageVector = if (saveDataMode) Icons.Default.Lock else Icons.Default.Info, 
                                contentDescription = "صرفه‌جویی داده", 
                                modifier = Modifier.size(14.dp),
                                tint = if (saveDataMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Persian Application Header Title
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "پـلـیـونـد (آزادنت)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            ),
                            fontSize = 18.sp
                        )
                        Text(
                            text = "گذرگاه آفلاین محتوا",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Network Status Card with 3D status visual heights
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF001D35)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF1D3557))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "V 2.4.0",
                                    fontSize = (9 * textSizeMultiplier).sp,
                                    color = Color(0xFFD3E3FD),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "وضعیت اتصال مش (Mesh)",
                                fontSize = (11 * textSizeMultiplier).sp,
                                color = Color(0xFFD3E3FD).copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Signals graph indicator
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp),
                                verticalAlignment = Alignment.Bottom,
                                modifier = Modifier.height(28.dp)
                            ) {
                                Box(modifier = Modifier.width(3.dp).height(10.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF64B5F6)))
                                Box(modifier = Modifier.width(3.dp).height(16.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF64B5F6)))
                                Box(modifier = Modifier.width(3.dp).height(24.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF64B5F6)))
                                Box(modifier = Modifier.width(3.dp).height(14.dp).clip(RoundedCornerShape(1.5.dp)).background(Color(0xFF64B5F6)))
                            }

                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "۴۸۲ گره فعال",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Light,
                                        color = Color(0xFFD3E3FD)
                                    ),
                                    fontSize = (20 * textSizeMultiplier).sp
                                )
                                Text(
                                    text = "در محدوده ۱.۲ کیلومتری شما",
                                    fontSize = (9 * textSizeMultiplier).sp,
                                    color = Color(0xFFD3E3FD).copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Navigation Bar matching Material 3 specifications
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // We draw 4 tabs
                val tabs = listOf(
                    Triple("بقچه آفلاین", Icons.Default.Info, 3), // Bundle
                    Triple("مشترک (P2P)", Icons.Default.Share, 2), // P2P
                    Triple("آینه‌های ملی", Icons.Default.Refresh, 1), // Mirrors
                    Triple("کانال‌ها", Icons.Default.Home, 0) // Feed
                )

                tabs.forEach { (title, icon, index) ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(index) },
                        label = { 
                            Text(
                                title, 
                                fontSize = 11.sp, 
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ) 
                        },
                        icon = { Icon(icon, contentDescription = title, modifier = Modifier.size(20.dp)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Main content based on selection
            when (selectedTab) {
                0 -> FeedTabScreen(viewModel, textSizeMultiplier)
                1 -> MirrorsTabScreen(viewModel, textSizeMultiplier)
                2 -> P2pTabScreen(viewModel, textSizeMultiplier)
                3 -> BackupTabScreen(viewModel, textSizeMultiplier)
            }
        }
    }
}

// ==================== TAB 0: FEED & CHANNELS SCREEN ====================
@Composable
fun FeedTabScreen(viewModel: PolyvandViewModel, multiplier: Float) {
    val focusManager = LocalFocusManager.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val newChannelInput by viewModel.newChannelInput.collectAsState()
    val syncingChannel by viewModel.syncingChannel.collectAsState()
    val syncStatusMessage by viewModel.syncStatusMessage.collectAsState()
    val allChannelsList by viewModel.allChannels.collectAsState()
    val selectedChannel by viewModel.selectedChannelUsername.collectAsState()
    val postsList by viewModel.filteredPosts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // 1. Unified Instant Channel Finder / Join Bar (Top Main Feature)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "عضویت آفلاین و نمایش کانال",
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = (14 * multiplier).sp,
                        textAlign = TextAlign.Right
                    )
                    Text(
                        text = "آیدی کانال تلگرام را بنویسید؛ با بالاترین سرعت مطالب آن بالا می‌آید",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (10 * multiplier).sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick Action search button
                        Button(
                            onClick = {
                                if (newChannelInput.isNotEmpty()) {
                                    viewModel.syncChannelViaActiveMirrors(newChannelInput)
                                    focusManager.clearFocus()
                                }
                            },
                            enabled = syncingChannel == null && newChannelInput.isNotBlank(),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("btn_sync_channel")
                        ) {
                            if (syncingChannel != null) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search, 
                                    contentDescription = "یافتن و بروزرسانی", 
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("نمایش", fontSize = (11 * multiplier).sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // RTL Text input field
                        OutlinedTextField(
                            value = newChannelInput,
                            onValueChange = { viewModel.setNewChannelInput(it) },
                            placeholder = { 
                                Text(
                                    "مثلاً VahidOnline یا akharinkhabar", 
                                    fontSize = 11.sp, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                ) 
                            },
                            singleLine = true,
                            maxLines = 1,
                            prefix = { Text("@", color = MaterialTheme.colorScheme.primary) },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    if (newChannelInput.isNotEmpty()) {
                                        viewModel.syncChannelViaActiveMirrors(newChannelInput)
                                        focusManager.clearFocus()
                                    }
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("input_channel_name"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            )
                        )
                    }

                    // Network Sync Step Alerts & Status Messages
                    AnimatedVisibility(visible = syncStatusMessage != null) {
                        syncStatusMessage?.let { msg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                                    .border(1.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = msg,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = (11 * multiplier).sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Right
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    strokeWidth = 1.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Telegram Member Card (Active Channel Profile)
        if (selectedChannel != null) {
            val selectedClean = selectedChannel!!.replace("@", "").trim()
            val channelInfo = allChannelsList.firstOrNull { it.username.lowercase() == selectedClean.lowercase() }
            
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        // Header Row with Channel Avatar & Badges
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Subscribed Indicators & Pulse Check
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFE8F5E9))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "عضو ارشد",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "مشترک فعال",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                                
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF001D35))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(RoundedCornerShape(2.5.dp))
                                            .background(Color(0xFF00E676))
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "اتصال ضد فیلتر",
                                        fontSize = 9.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Channel Profile detail
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = channelInfo?.title ?: "کانال @$selectedClean",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        fontSize = (14 * multiplier).sp
                                    )
                                    Text(
                                        text = "@$selectedClean",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = (10 * multiplier).sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Stylish Circle Avatar
                                val avatarInitials = if (selectedClean.length >= 2) selectedClean.take(2).uppercase() else selectedClean.take(1).uppercase()
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(Color(0xFFD3E3FD), Color(0xFFFDE293))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = avatarInitials,
                                        color = Color(0xFF001D35),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Description
                        Text(
                            text = channelInfo?.description ?: "تمامی محتویات این کانال بدون سانسور اینترنتی، از طریق همزمان با آینه‌های ضد فیلتر ملی و شبکه مش همتا به همتا فیزیکی بارگذاری می‌شوند.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = (10 * multiplier).sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Force Resync/Refresh Action Row (Highly resilient)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Leave button
                            OutlinedButton(
                                onClick = { viewModel.selectChannel(null) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Text("نمایش همه کانال‌ها", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // Dynamic high-speed refresh button
                            Button(
                                onClick = { viewModel.syncChannelViaActiveMirrors(selectedClean) },
                                shape = RoundedCornerShape(10.dp),
                                enabled = syncingChannel == null,
                                modifier = Modifier.weight(2.5f),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "به‌روزرسانی", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("به‌روزرسانی سریع (ضد فیلترینگ شدید)", fontSize = (9.5 * multiplier).sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. User Joined Channels Story Circles List (Only shown if no channel selected or as quick toggle bar)
        item {
            Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "کانال‌های نشان شده اخیراً (فوری):",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = (10 * multiplier).sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp, end = 4.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    reverseLayout = true
                ) {
                    item {
                        val isAllSelected = selectedChannel == null
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.selectChannel(null) }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                                    .border(2.dp, if (isAllSelected) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "همه",
                                    tint = if (isAllSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("همه", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    items(allChannelsList) { ch ->
                        val isChSelected = selectedChannel?.lowercase() == ch.username.lowercase()
                        val initials = if (ch.username.length >= 2) ch.username.take(2).uppercase() else ch.username.take(1).uppercase()
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { viewModel.selectChannel(ch.username) }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isChSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)
                                    .border(
                                        2.dp, 
                                        if (isChSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, 
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    color = if (isChSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = ch.title.take(10), 
                                fontSize = 9.sp, 
                                fontWeight = FontWeight.Bold,
                                color = if (isChSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 4. Offline Post Message Filter Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                leadingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "پاک کردن", modifier = Modifier.size(16.dp))
                        }
                    }
                },
                trailingIcon = { 
                    Icon(
                        imageVector = Icons.Default.Search, 
                        contentDescription = "جستجو", 
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    ) 
                },
                placeholder = { 
                    Text(
                        "جستجو در متن مطالب کانال...", 
                        fontSize = 11.sp, 
                        textAlign = TextAlign.Right, 
                        style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl),
                        modifier = Modifier.fillMaxWidth()
                    ) 
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                textStyle = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("input_search_posts"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        // Empty Feed Stream Handling
        if (postsList.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "خالی",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "هیچ مطلبی یافت نشد!",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = (13 * multiplier).sp
                    )
                    Text(
                        text = "با وارد کردن آیدی معتبر در نوار جستجوی بالا، کانال موازی را به صورت تضمینی همگام‌سازی کنید.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (10 * multiplier).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 4.dp)
                    )
                }
            }
        } else {
            // 5. High-Speed Rendered Local Streams
            items(postsList, key = { it.id }) { post ->
                PostItemCard(post, viewModel, multiplier)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun PostItemCard(post: Post, viewModel: PolyvandViewModel, multiplier: Float) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (post.mediaType == "alert") Color(0x60FF9100) else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("post_card_${post.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Header Row (Channel Tag, Post Meta Views & Bookmarks)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bookmarks star & view counts
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.togglePostBookmark(post) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (post.isBookmarked) Icons.Default.Star else Icons.Default.Star,
                            contentDescription = "ستاره",
                            tint = if (post.isBookmarked) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "👁️ ${post.viewsCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Channel Label badge with initials (High Density Avatar style)
                val initial = if (post.channelUsername.length >= 2) post.channelUsername.take(2).uppercase() else post.channelUsername.take(1).uppercase()
                val (badgeBg, badgeText) = remember(post.channelUsername) {
                    val hash = post.channelUsername.hashCode()
                    when {
                        hash % 3 == 0 -> Pair(Color(0xFFD3E3FD), Color(0xFF001D35)) // Light Cyan-blue
                        hash % 3 == 1 -> Pair(Color(0xFFFDE293), Color(0xFF5F4300)) // Light Yellow
                        else -> Pair(Color(0xFFE1E2EC), Color(0xFF44474F))          // Soft Slate gray
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "@${post.channelUsername}",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "همگام‌سازی مش",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (post.mediaType == "alert") Color(0xFFFF9100) else Color(0xFF2E7D32)
                                    )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(badgeBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            color = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )

            // Dynamic Farsi Post Messaging
            Text(
                text = post.message,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = (13 * multiplier).sp,
                lineHeight = (22 * multiplier).sp,
                textAlign = TextAlign.Right,
                style = LocalTextStyle.current.copy(
                    textDirection = TextDirection.Rtl,
                    fontFamily = FontFamily.SansSerif
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Rich Multi-Media Render Block (Images, Videos, Audios)
            if (!post.mediaUrl.isNullOrEmpty() || post.mediaType == "image" || post.mediaType == "video" || post.mediaType == "audio") {
                Spacer(modifier = Modifier.height(10.dp))
                when (post.mediaType) {
                    "image" -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = post.mediaUrl,
                                contentDescription = "قالب تصویری کانال",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = android.R.drawable.ic_menu_gallery)
                            )
                            
                            // High-speed local cache alert tag
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xE0001D35))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF00E676))
                                    )
                                    Text(
                                        text = "پیش‌نمایش کش شده",
                                        fontSize = 8.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    "video" -> {
                        var isPlaying by remember { mutableStateOf(false) }
                        var videoProgress by remember { mutableStateOf(0.35f) }
                        
                        LaunchedEffect(isPlaying) {
                            if (isPlaying) {
                                while (isPlaying) {
                                    kotlinx.coroutines.delay(100)
                                    videoProgress = (videoProgress + 0.005f)
                                    if (videoProgress >= 1f) {
                                        videoProgress = 0f
                                        isPlaying = false
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(190.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = post.mediaUrl,
                                contentDescription = "پیش‌نمایش ویدیو کانال",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = android.R.drawable.ic_media_play)
                            )
                            
                            // Mask layer
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = if (isPlaying) 0.15f else 0.45f))
                            )

                            // Play and Pause Trigger icon
                            IconButton(
                                onClick = { isPlaying = !isPlaying },
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Close else Icons.Default.PlayArrow,
                                    contentDescription = "پخش ویدیو",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            // Interactive Seekbar & progress bar overlay
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                        )
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val currentSecs = (videoProgress * 154).toInt()
                                    val min = currentSecs / 60
                                    val sec = currentSecs % 60
                                    Text(
                                        text = String.format("%02d:%02d / 02:34", min, sec),
                                        color = Color.White,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(5.dp)
                                                .clip(CircleShape)
                                                .background(if (isPlaying) Color.Green else Color.LightGray)
                                        )
                                        Text(
                                            text = if (isPlaying) "در حال تماشای ویدیو (پسیو)" else "فایل ویدیو (ذخیره دستگاه)",
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // Progress Track background
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(Color.White.copy(alpha = 0.3f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .fillMaxWidth(videoProgress)
                                            .background(MaterialTheme.colorScheme.primary)
                                    )
                                }
                            }
                        }
                    }
                    "audio" -> {
                        var isAudioPlaying by remember { mutableStateOf(false) }
                        var audioProgress by remember { mutableStateOf(0.18f) }
                        
                        LaunchedEffect(isAudioPlaying) {
                            if (isAudioPlaying) {
                                while (isAudioPlaying) {
                                    kotlinx.coroutines.delay(100)
                                    audioProgress = (audioProgress + 0.003f)
                                    if (audioProgress >= 1f) {
                                        audioProgress = 0f
                                        isAudioPlaying = false
                                    }
                                }
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Right Side: Sound graph visualizer animation rows
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.height(22.dp)
                                ) {
                                    val waveHeights = if (isAudioPlaying) {
                                        listOf(12, 22, 10, 16, 6, 20, 8, 14)
                                    } else {
                                        listOf(6, 6, 6, 6, 6, 6, 6, 6)
                                    }
                                    waveHeights.forEachIndexed { i, h ->
                                        val heightVal = if (isAudioPlaying) {
                                            remember(audioProgress) { (h + (Math.random() * 6 - 3)).toInt().coerceIn(3, 24) }
                                        } else h
                                        Box(
                                            modifier = Modifier
                                                .width(2.5.dp)
                                                .height(heightVal.dp)
                                                .clip(RoundedCornerShape(1.25.dp))
                                                .background(if (isAudioPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                                        )
                                    }
                                }

                                // Central Track details
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "فایل صوتی / پادکست کانال • Mp3",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Right
                                    )
                                    
                                    Spacer(modifier = Modifier.height(3.dp))
                                    
                                    // Custom visual tracker progress
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(3.dp)
                                            .clip(RoundedCornerShape(1.5.dp))
                                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(audioProgress)
                                                .background(MaterialTheme.colorScheme.primary)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(2.dp))
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val audioSecs = (audioProgress * 258).toInt()
                                        val curM = audioSecs / 60
                                        val curS = audioSecs % 60
                                        Text(
                                            text = String.format("%02d:%02d", curM, curS),
                                            fontSize = 8.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "04:18 • آهنگ همگام",
                                            fontSize = 8.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Interactive local control trigger
                                IconButton(
                                    onClick = { isAudioPlaying = !isAudioPlaying },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(
                                        imageVector = if (isAudioPlaying) Icons.Filled.Close else Icons.Filled.PlayArrow,
                                        contentDescription = "کنترل صوتی",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )

            // Dynamic Action Bar & Clipboard instant Text Copier
            val clipboardManager = LocalClipboardManager.current
            var isTextCopied by remember { mutableStateOf(false) }
            LaunchedEffect(isTextCopied) {
                if (isTextCopied) {
                    kotlinx.coroutines.delay(1500)
                    isTextCopied = false
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side: Status date footer metrics
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check, 
                        contentDescription = "ذخیره لوکال",
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "محلی پایدار • ${viewModel.formatTimestamp(post.date)}",
                        fontSize = (9 * multiplier).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Right Side: Clipboard and offline bookmarks
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Modern styled Copy Button with state transition
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isTextCopied) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isTextCopied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                clipboardManager.setText(AnnotatedString(post.message))
                                isTextCopied = true
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isTextCopied) "کپی شد!" else "کپی پیام",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isTextCopied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Icon(
                            imageVector = if (isTextCopied) Icons.Default.Check else Icons.Default.Share,
                            contentDescription = "کپی پیام",
                            tint = if (isTextCopied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}


// ==================== TAB 1: DOMESTIC MIRRORS SCREEN ====================
@Composable
fun MirrorsTabScreen(viewModel: PolyvandViewModel, multiplier: Float) {
    val mirrorsList by viewModel.allMirrors.collectAsState()
    val isTesting by viewModel.isTestingMirrors.collectAsState()
    val newUrl by viewModel.newMirrorUrlInput.collectAsState()
    val newLabel by viewModel.newMirrorLabelInput.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Intro to Mirror Concept Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "روش آینه بومی (Direct IP Bypass)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = (13 * multiplier).sp
                        )
                        Icon(Icons.Default.Lock, contentDescription = "امنیت", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "در شدیدترین حالت فیلترینگ که ارتباط دامنه قطع می‌شود، سرورهای بومی مستقر در دیتاسنترهای داخلی ایران (آسیاتک، پارس آنلاین و...) که آدرس مستقیم IP دارند همچنان کار می‌کنند. این سرورها پابلیک تلگرام را امن همگام کرده و پلیوند کش را از آنها بازیابی می‌کند.",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = (11 * multiplier).sp,
                        lineHeight = (18 * multiplier).sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.padding(top = 4.dp),
                        style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                    )
                }
            }
        }

        // Ping Button
        item {
            Button(
                onClick = { viewModel.testAllMirrorsLatency() },
                enabled = !isTesting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_test_mirrors"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isTesting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("در حال سنجش پینگ آینه‌ها...")
                } else {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "تست پینگ")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("آزمون اتصال و بررسی تأخیر دیواره‌ها", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Mirror Servers List
        items(mirrorsList, key = { it.url }) { mirror ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Side: Speed rating & Delete button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!mirror.isDefault) {
                            IconButton(
                                onClick = { viewModel.deleteMirror(mirror.url) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                            }
                        }

                        // Latency state chip
                        if (mirror.latencyMs != null) {
                            val latency = mirror.latencyMs
                            val (color, word) = when {
                                latency < 100 -> Pair(Color(0xFF00E676), "ايده‌آل")
                                latency < 170 -> Pair(Color(0xFFAEEA00), "پایدار")
                                else -> Pair(Color(0xFFFF9100), "کُند")
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(color.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "$latency ms ($word)",
                                    color = color,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = "تست نشده",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Right Side: IP & Info
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (mirror.isDefault) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("پیش‌فرض بومی", fontSize = 8.sp, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = mirror.label,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Right
                            )
                        }
                        Text(
                            text = mirror.url,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        // Custom Mirror addition Form
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "افزودن آدرس آینه اختصاصی",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = (12 * multiplier).sp
                    )
                    Text(
                        text = "آدرس آی‌پی مستقیم یا دامنه آزاد .ir سرور پشتیبان را وارد کنید.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (10 * multiplier).sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = newLabel,
                        onValueChange = { viewModel.setNewMirrorLabelInput(it) },
                        placeholder = { Text("مثلاً آینه دانشگاه فردوسی", fontSize = 11.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        textStyle = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .testTag("input_mirror_label"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    OutlinedTextField(
                        value = newUrl,
                        onValueChange = { viewModel.setNewMirrorUrlInput(it) },
                        placeholder = { Text("http://192.168...", fontSize = 11.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Left) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("input_mirror_url"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    Button(
                        onClick = { viewModel.addNewMirror() },
                        enabled = newUrl.startsWith("http") && newUrl.length > 10 && newLabel.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .align(Alignment.Start)
                            .testTag("btn_add_mirror")
                    ) {
                        Text("ذخیره سرور", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}


// ==================== TAB 2: P2P OFFLINE SHARE SCREEN ====================
@Composable
fun P2pTabScreen(viewModel: PolyvandViewModel, multiplier: Float) {
    val isHosting by viewModel.isP2pHosting.collectAsState()
    val isReceiving by viewModel.isP2pReceiving.collectAsState()
    val targetIp by viewModel.p2pTargetIpInput.collectAsState()
    val logs by viewModel.p2pLogs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Intro message
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "شبکه ابری همسایگان (Polyvand Mesh Swarm)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = (13 * multiplier).sp
                    )
                    Text(
                        text = "وقتی هیچ اینترنتی در محله فعال نیست، کافیست یک نفر مطالب کانال‌ها را از طریق بقچه متنی یا از حاشیه شهر همگام کرده باشد. او فرستنده همتا می‌شود و دکمه اشتراک را روشن می‌کند. شما با روشن کردن وای‌فای و بدون اینترنت، مخزن را مستقیما از او رد و بدل می‌کنید.",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = (11 * multiplier).sp,
                        lineHeight = (18 * multiplier).sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.padding(top = 4.dp),
                        style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                    )
                }
            }
        }

        // Split Panel: Send / Receive Mode selection
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // SENDER MODE (میزبان ارسال)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp, 
                            if (isHosting) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, 
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.startP2pShareServer() },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isHosting) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share, 
                            contentDescription = "فرستنده", 
                            tint = if (isHosting) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "فرستادن محتوا (Host)",
                            fontWeight = FontWeight.Bold,
                            fontSize = (11 * multiplier).sp,
                            color = if (isHosting) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHosting) "میزبانی فعال است" else "توزیع آفلاین با وای‌فای",
                            fontSize = 8.sp,
                            color = if (isHosting) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // RECEIVER MODE (گیرنده همتا)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            1.dp, 
                            if (isReceiving) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, 
                            RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home, 
                            contentDescription = "گیرنده", 
                            tint = if (isReceiving) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "دریافت محتوا (Client)",
                            fontWeight = FontWeight.Bold,
                            fontSize = (11 * multiplier).sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "واکشی از فرستنده محلی",
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Hosting Interactive Panel
        if (isHosting) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("درگاه توزیع همتا (P2P Broadcaster) فعال است", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(LimeActivePing))
                        }
                        Text(
                            text = "راهنمایی اتصال برای همسایگان:\n۱. هات‌اسپات Wi-Fi گوشی خود را روشن کنید تا دیگران به شما وصل شوند.\n۲. از وای‌فای مشترک منزل یا دفتر استفاده کنید.\n۳. به دیگران بگویند آی‌پی زیر را در بخش دریافت کپی کنند:",
                            fontSize = (10 * multiplier).sp,
                            lineHeight = 16.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                        )

                        // Highlight box containing server IP
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("آی‌پی سرور توزیع: 192.168.43.1:7070", fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Client Input IP Form
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text("اتصال به دوست همتا (Sync via Peer)", fontWeight = FontWeight.Bold, fontSize = (12 * multiplier).sp)
                        Text(
                            text = "بخش هات‌اسپات فرستنده را وصل شوید، سپس آی‌پی ادمین هموار‌ساز او را وارد کنید.",
                            fontSize = (10 * multiplier).sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.startP2pReceiveClient() },
                                enabled = !isReceiving && targetIp.isNotBlank(),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(44.dp)
                                    .testTag("btn_p2p_receive")
                            ) {
                                Text("اتصال و دریافت", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = targetIp,
                                onValueChange = { viewModel.setP2pTargetIpInput(it) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("input_p2p_ip"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // Live logs terminal - Cosmic terminal design styling
        if (logs.isNotEmpty()) {
            item {
                Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth()) {
                    Text("لاگ فعالیت رادیویی همتا:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF030D05))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            logs.forEach { log ->
                                Text(
                                    text = "> $log",
                                    color = if (log.contains("✅") || log.contains("تکمیل")) Color(0xFF00E676) else TextGreenMuted,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    textAlign = TextAlign.Left
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}


// ==================== TAB 3: BUNDLE BACKUPS SCREEN (SNEAKERNET) ====================
@Composable
fun BackupTabScreen(viewModel: PolyvandViewModel, multiplier: Float) {
    val pastedJson by viewModel.pastedJsonInput.collectAsState()
    val backupMessage by viewModel.backupStatusMessage.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Information banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "روش بقچه فیزیکی (Physical Sneakernet Bundle)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = (13 * multiplier).sp
                    )
                    Text(
                        text = "بسته‌های محلی، ترفند انتقال اطلاعات به صورت متنی (JSON Base) است. شما می‌توانید دیتابیس کانال‌ها و پیام‌های فعلی خود را تبدیل به متن فشرده کرده، آن را به عنوان فایل متنی ذخیره کرده یا مستقیماً در برنامه‌های پیام‌رسان بومی یا بلوتوث ارسال کنید. همکار شما با کپی کردن پیام و واردکردن در این کادر، فوراً دیتابیس خود را همگام می‌کند.",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = (11 * multiplier).sp,
                        lineHeight = (18 * multiplier).sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.padding(top = 4.dp),
                        style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                    )
                }
            }
        }

        // Export Actions / Import Action
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.triggerImportBackup() },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_import_bundle")
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "بارگذاری بقچه")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("بارگذاری بقچه", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.triggerExportBackup() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("btn_export_bundle")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "ایجاد بقچه")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تولید بقچه متنی", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Display Success/Error notifications beautifully
        backupMessage?.let { (isSuccess, text) ->
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccess) Color(0x1500E676) else Color(0x15FF1744)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (isSuccess) Color(0x5000E676) else Color(0x50FF1744),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.clearBackupStatusMessage() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "بستن", modifier = Modifier.size(14.dp))
                        }
                        Text(
                            text = text,
                            color = if (isSuccess) Color(0xFF00E676) else Color(0xFFFF1744),
                            fontSize = (11 * multiplier).sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 6.dp),
                            style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                        )
                    }
                }
            }
        }

        // Clipboard Copy Helper Button (if JSON exists in کادر)
        if (pastedJson.isNotEmpty()) {
            item {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(pastedJson))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("btn_copy_clipboard")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "کپی", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("کپی کل کد بقچه در حافظه گوشی (Clipboard)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        // Primary Large Paste/Code Editor Text Field
        item {
            OutlinedTextField(
                value = pastedJson,
                onValueChange = { viewModel.setPastedJsonInput(it) },
                placeholder = { 
                    Text(
                        "متن بقچه آفلاین (کد پیچیده JSON) را اینجا پیست کنید یا کدهای تولیدی را برای انتقال دستی از اینجا کپی کنید...", 
                        fontSize = 11.sp, 
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        style = LocalTextStyle.current.copy(textDirection = TextDirection.Rtl)
                    ) 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .testTag("textarea_bundle_json"),
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}
