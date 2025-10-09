package com.ssu.assu.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ssu.assu.R
import com.ssu.assu.presentation.admin.AdminMainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val CHANNEL_ID = "fcm_default"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        android.util.Log.d("FCM", "새 FCM 토큰: $token")
        // TODO: 서버 전송 필요하면 여기서 처리
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("FCM", "메시지 수신: data=${message.data} notif=${message.notification}")

        ensureChannel()

        val title = message.data["title"] ?: message.notification?.title ?: "알림"
        val body  = message.data["body"]  ?: message.notification?.body  ?: "새 메시지"
        val type  = message.data["type"]  ?: ""   // 서버에서 내려주는 type 값

        // 주문 안내라면 TTS로 읽어주기
        if (type.equals("ORDER", ignoreCase = true) || title.contains("주문 안내")) {
            val script = if (title.isNotBlank()) "$title. $body" else body
            if (script.isNotBlank()) {
                Log.w("TTS", "➡️ try speak: $script")
                TtsManager.speak(this, script)
            }
        }

        // 원래 알림도 그대로 표시
        val intent = Intent(this, AdminMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_account_bell)

            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        NotificationManagerCompat.from(this)
            .notify((System.currentTimeMillis() % 100000).toInt(), builder.build())
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val ch = NotificationChannel(CHANNEL_ID, "Default Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            mgr.createNotificationChannel(ch)
        }
    }
}