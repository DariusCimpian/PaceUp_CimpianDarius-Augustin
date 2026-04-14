package com.example.paceup.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.paceup.model.ChatMessage
import com.example.paceup.ui.theme.*
import com.example.paceup.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    chatPath: String,
    title: String,
    chatViewModel: ChatViewModel = viewModel()
) {
    var messageText by remember { mutableStateOf("") }
    val messages by chatViewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(chatPath) {
        chatViewModel.listenToChat(chatPath)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(PaceDark)
    ) {
        // Lista mesaje
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Niciun mesaj încă. Fii primul! 💬",
                            color = PaceGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            items(messages) { message ->
                ChatBubble(
                    message = message,
                    isOwn = message.uid == chatViewModel.currentUid
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        // Input bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PaceCardDark)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Scrie un mesaj...", color = PaceGray, fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PaceGreen,
                    unfocusedBorderColor = PaceGray,
                    focusedTextColor = PaceWhite,
                    unfocusedTextColor = PaceWhite
                ),
                maxLines = 3,
                singleLine = false
            )
            Button(
                onClick = {
                    if (messageText.isNotBlank()) {
                        chatViewModel.sendMessage(chatPath, messageText.trim())
                        messageText = ""
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PaceGreen),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text("▶", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isOwn: Boolean) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val time = timeFormat.format(Date(message.timestamp))

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
    ) {
        if (!isOwn) {
            Text(
                text = message.username,
                fontSize = 11.sp,
                color = PaceGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
            )
        }
        Box(
            modifier = Modifier
                .background(
                    if (isOwn) PaceGreen.copy(alpha = 0.3f) else PaceCardDark,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isOwn) 16.dp else 4.dp,
                        bottomEnd = if (isOwn) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                Text(text = message.message, fontSize = 15.sp, color = PaceWhite)
                Text(
                    text = time,
                    fontSize = 10.sp,
                    color = PaceGray,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}