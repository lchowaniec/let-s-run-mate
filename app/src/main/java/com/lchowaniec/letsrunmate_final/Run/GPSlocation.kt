package com.lchowaniec.letsrunmate_final.Run


import android.R
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat


open class GPSlocation : Service() {

    lateinit var intent: Intent
    lateinit var manager2: LocationManager
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent!!.action == getString(com.lchowaniec.letsrunmate_final.R.string.STOP_SERVICE)){
            stopSelf()
            stopForeground(STOP_FOREGROUND_DETACH)
        }else{


            val NOTIFICATION_CHANNEL_ID = "Let's run mate"
            val channelName = "GPS background service"
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)

            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.btn_plus)
                .setContentTitle("App is tracking your run! ")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)

            manager2 = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            manager2.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        val mIntent = Intent("location_update")
                        mIntent.putExtra("coordinates", location)
                        sendBroadcast(mIntent)
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                        Toast.makeText(this@GPSlocation,"Status changed",Toast.LENGTH_LONG).show()

                    }

                    override fun onProviderEnabled(provider: String?) {
                        Toast.makeText(this@GPSlocation,"Gps tracking",Toast.LENGTH_LONG).show()

                    }

                    override fun onProviderDisabled(provider: String?) {
                        Toast.makeText(this@GPSlocation,"Please turn on GPS Location",Toast.LENGTH_LONG).show()
                    }
                })
        }

        return START_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        stopForeground(true)
    }

    override fun unregisterReceiver(receiver: BroadcastReceiver?) {
        super.unregisterReceiver(receiver)
        stopSelf()
        stopForeground(true)
    }
}










