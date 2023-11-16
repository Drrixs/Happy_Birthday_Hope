//   [RU]
//   ——————————————————————————————
//   🌟 Приложение Разработано для: t.me/@Albesus
//   🛠️ Создатель Приложения: t.me/@Drrixs
//   ——————————————————————————————
//   ⚠️ Внимание: Контакты могут стать неактуальными или недействительными со временем.
//   📬 Для долгосрочной связи, вот мои почтовые адреса:
//      Почта 1 - [адрес]
//      Почта 2 - [адрес]
//      Почта 3 - [адрес]
//   👉 Рекомендуется писать на все сразу для уверенности в доставке.
//   ——————————————————————————————
//   [EN]
//   ——————————————————————————————
//   🌟 App Developed for: t.me/@Albesus
//   🛠️ App Creator: t.me/@Drrixs
//   ——————————————————————————————
//   ⚠️ Attention: Contacts may become outdated or invalid over time.
//   📬 For long-term communication, here are my email addresses:
//      Email 1 - [address]
//      Email 2 - [address]
//      Email 3 - [address]
//   👉 It's recommended to write to all simultaneously to ensure delivery.
//   ——————————————————————————————


package com.drrixs.happybirthdaynadezhda

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.apache.commons.net.ntp.NTPUDPClient
import java.io.IOException
import java.util.Calendar
import java.net.InetAddress




class MainActivity : AppCompatActivity() {





    private lateinit var mediaPlayer: MediaPlayer



    private fun checkTimeManipulation() {
        val lastTimeUsed = prefs.getLong("LAST_TIME_USED", System.currentTimeMillis())
        val currentTime = System.currentTimeMillis()

        if (currentTime < lastTimeUsed) {
            Toast.makeText(this, "Обнаружено изменение системного времени!", Toast.LENGTH_LONG).show()
        }

        prefs.edit().putLong("LAST_TIME_USED", currentTime).apply()
    }

    private lateinit var countdownTextView: TextView
    private val prefs by lazy { getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE) }
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            val timeRemaining = (prefs.getLong("END_TIME", 0) - System.currentTimeMillis()) / 1000
            if (timeRemaining > 0) {
                countdownTextView.text = timeRemaining.toString()
                countdownTextView.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.fade_in))
                handler.postDelayed(this, 1000)
            } else {
                navigateToCountActivity()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        countdownTextView = findViewById(R.id.countdownTextView)
        val endTime = prefs.getLong("END_TIME", 0)
        val currentTime = System.currentTimeMillis()
        if (endTime > currentTime) {
            Log.d("MainActivity", "Setting alarm with existing end time")
            setAlarm(endTime)
        } else if (endTime == 0L) {
            Log.d("MainActivity", "Setting alarm with new end time")
            val targetDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, 2023)
                set(Calendar.MONTH, Calendar.NOVEMBER)
                set(Calendar.DAY_OF_MONTH, 21)
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 0)
            }
            setAlarm(targetDate.timeInMillis)
        } else {
            Log.d("MainActivity", "Navigating to CountActivity")
            navigateToCountActivity()
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.billie_eilish)


        mediaPlayer.start()

        checkTimeManipulation()




    }

    private fun setAlarm(targetTime: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Разреши будильники и напоминания в настройках, это важно!!!!", Toast.LENGTH_LONG).show()

                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }

        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntentFlags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or 0
        } else {
            0
        }
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, pendingIntentFlags)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, targetTime, pendingIntent)
        prefs.edit().putLong("END_TIME", targetTime).apply()
        val timeRemaining = (targetTime - System.currentTimeMillis()) / 1000
        countdownTextView.text = timeRemaining.toString()
        handler.post(updateRunnable)
    }

    class BootCompletedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                val prefs = context.getSharedPreferences("APP_PREFERENCES", Context.MODE_PRIVATE)
                val endTime = prefs.getLong("END_TIME", 0)
                val currentTime = System.currentTimeMillis()
                if (endTime > currentTime) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent = Intent(context, AlarmReceiver::class.java)
                    val pendingIntentFlags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        PendingIntent.FLAG_IMMUTABLE or 0
                    } else {
                        0
                    }
                    val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, pendingIntentFlags)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endTime, pendingIntent)
                }
            }
        }
    }

    class AlarmReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val newIntent = Intent(context, CountActivity::class.java)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(newIntent)
        }
    }

    private fun navigateToCountActivity() {
        val currentTime = System.currentTimeMillis()
        val endTime = prefs.getLong("END_TIME", 0)
        if (currentTime >= endTime) {
            val intent = Intent(this@MainActivity, CountActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            startActivity(intent)
            finish()
        } else {
        }
    }
}
