package com.agilefalcon.voicenotepad.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp


@Composable
fun TimerWithControls(
    seconds: Int,
    isRecording: Boolean,
    isHoldMode: Boolean,
    onToggleHoldMode: () -> Unit,
    onToggleRecording: () -> Unit,
    abortRecording: () -> Unit,
    placeholder: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .padding(bottom = 40.dp).fillMaxWidth()
            .drawBehind {

                drawLine(
                    color = Color.Gray,
                    strokeWidth = 4f,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f)
                )
            },




        contentAlignment = Alignment.BottomCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Sayaç Text
            Text(
                text = formatSeconds(seconds),
                style = MaterialTheme.typography.headlineLarge,
                color =  if (isRecording) Color(0xFFF44336) else Color(0xFF90A4AE),
                modifier = Modifier
                    .padding(top = 16.dp)
            )


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)

            ) {

                Button(
                    onClick ={
                        if(!isRecording){
                            onToggleHoldMode()
                        }
                        } ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isHoldMode) Color(0xFF4CAF50) else Color(0xFF90A4AE),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .height(48.dp)
                        .weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text("Hold", style = MaterialTheme.typography.bodyMedium)
                }


                Box(
                    modifier = Modifier
                        .scale(if (isRecording) scale else 1f)
                        .weight(1f)
                        .shadow(
                            elevation = if (isRecording) 10.dp else 2.dp,
                            shape = CircleShape
                        )

                        .size(100.dp)

                        .clip(CircleShape)
                        .background(if (isRecording) Color(0xFFF44336) else Color(0xFF4CAF50))
                        .then(
                            if (isHoldMode) Modifier.pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        onToggleRecording()
                                        val startTime = System.currentTimeMillis()
                                        tryAwaitRelease()
                                        val elapsedTime = System.currentTimeMillis() - startTime
                                        if (elapsedTime >= 1000) {
                                            onToggleRecording()
                                        }else{
                                            abortRecording()
                                        }
                                    }
                                )
                            } else Modifier.clickable {
                                onToggleRecording()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isRecording) "Recording.." else "Record",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(48.dp).padding(8.dp).weight(1f))
            }
        }
    }
}

fun formatSeconds(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
