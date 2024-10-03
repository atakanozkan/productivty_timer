package org.hyperskill.stopwatch

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import org.hyperskill.stopwatch.databinding.ActivityMainBinding
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private var seconds = 0
    private var running = false
    lateinit var binding: ActivityMainBinding
    private val handler = Handler()
    private var upperLimit = Int.MAX_VALUE
    private var isUpperLimit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNotificationChannel()
        setUpperLimitDialog()
        setButtonListeners()
    }

    private fun runTimer() {
        val timeView = binding.textView
        handler.post(object : Runnable {
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun run() {
                val minutes: Int = (seconds % 3600) / 60
                val secs: Int = seconds % 60
                val time = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    minutes, secs
                )
                val randomColor = getRandomColor()
                binding.progressBar.indeterminateTintList = ColorStateList.valueOf(randomColor)

                timeView.text = time
                if (running) {
                    seconds++
                }

                if (seconds > upperLimit && !isUpperLimit) {
                    isUpperLimit = true
                    timeView.setTextColor(Color.RED)
                    setAlarm()
                }

                binding.settingsButton.isEnabled = false
                handler.postDelayed(this, 1000)
            }
        })
    }

    fun setButtonListeners(){
        binding.startButton.setOnClickListener{
            if(!running){
                running = true
                binding.progressBar.visibility = View.VISIBLE
                runTimer()
            }
        }
        binding.resetButton.setOnClickListener {
            running = false
            isUpperLimit = false
            seconds = 0
            handler.removeCallbacksAndMessages(null)
            binding.progressBar.visibility = View.GONE
            binding.textView.setTextColor(Color.BLACK)
            binding.settingsButton.isEnabled = true
            binding.textView.text = getString(R.string.initialTimer)
        }
    }

    fun setNotificationChannel(){
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel("org.hyperskill","stopwatch", importance)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun setUpperLimitDialog(){
        binding.settingsButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.settings_dialog, null)
            val editText = dialogView.findViewById<EditText>(R.id.upperLimitEditText)
            builder.setView(dialogView)
                .setTitle("Set upper limit in seconds")
                .setPositiveButton("Ok") { dialog, _ ->
                    try{
                        val valueReceived = editText.text.toString().toInt()
                        upperLimit = if (valueReceived > 0) valueReceived else Int.MAX_VALUE
                    }
                    catch (e: Exception){
                        e.printStackTrace()
                    }

                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun setAlarm(){
        val intent = Intent(this, RemainderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(ALARM_SERVICE) as? AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager?.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100, pendingIntent)
        } else {
            alarmManager?.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100, pendingIntent)
        }
    }

    private fun getRandomColor(): Int {
        val random = java.util.Random()
        val color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
        return color
    }

}