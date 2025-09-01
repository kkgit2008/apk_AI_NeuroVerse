package com.dark.neuroverse.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotifListenerService : NotificationListenerService() {

    override fun onListenerConnected() {
        Log.d(TAG, "Notification listener connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val n = sbn.notification
        val extras = n.extras

        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""
        val pkg = sbn.packageName

        Log.d(TAG, "Posted from=$pkg title=$title text=$text")

        // Example: auto-handle only certain packages or titles/text
        if (shouldHandle(pkg, title, text)) {
            dismissIfClearable(sbn)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.d(TAG, "Removed ${sbn.packageName}")
    }

    private fun shouldHandle(pkg: String, title: String, text: String): Boolean {
        // TODO: your logic (e.g., only for messaging apps, or when keywords match)
        return true
    }

    /** Dismiss (clear) the notification if the app allows it. */
    private fun dismissIfClearable(sbn: StatusBarNotification) {
        if (!sbn.isClearable) return
        try {
            cancelNotification(sbn.key)   // modern API
            Log.d(TAG, "Dismissed ${sbn.packageName}")
        } catch (t: Throwable) {
            Log.w(TAG, "Failed to dismiss", t)
        }
    }

    companion object {
        private const val TAG = "NotifListener"
    }
}
