package com.dimento.app.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.dimento.app.MainActivity
import com.dimento.app.R
import com.dimento.app.core.PermissionManager
import kotlinx.coroutines.delay

@Composable
fun DiMentoAppRoot() {
    var showSplash by remember { mutableStateOf(true) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? ComponentActivity

    LaunchedEffect(Unit) {
        delay(500)
        showSplash = false
        if (activity != null
            && !PermissionManager.isCompleted(context)
            && !PermissionManager.hasSkipped(context)
        ) {
            val needsNotifications = Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            if (needsNotifications) {
                showPermissionDialog = true
            }
        }
    }

    if (showSplash) {
        SplashScreen()
    } else {
        DiMentoAppContent()

        if (showPermissionDialog) {
            PermissionExplanationDialog(
                onOk = {
                    showPermissionDialog = false
                    PermissionManager.markCompleted(context)
                    (activity as? MainActivity)?.startPermissionFlow()
                },
                onSkip = {
                    showPermissionDialog = false
                    PermissionManager.markSkipped(context)
                }
            )
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp).align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(id = R.string.splash_tagline),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PermissionExplanationDialog(onOk: () -> Unit, onSkip: () -> Unit) {
    AlertDialog(
        onDismissRequest = onSkip,
        title = {
            Text(text = stringResource(R.string.permission_title))
        },
        text = {
            Text(
                text = buildAnnotatedString {
                    append("DiMento needs a couple of permissions to work properly:\n\n")
                    append("1. 🔔 ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Notifications") }
                    append(" — to remind you of events due today\n")
                    append("2. ⏰ ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Set alarms & reminders") }
                    append(" — to notify you at the exact event time")
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onOk) {
                Text(stringResource(R.string.permission_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip) {
                Text(stringResource(R.string.permission_skip))
            }
        }
    )
}
