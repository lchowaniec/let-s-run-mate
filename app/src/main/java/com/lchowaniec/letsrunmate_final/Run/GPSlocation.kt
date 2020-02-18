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


            val NOTIFICATION_CHANNEL_ID = "com.example.simpleapp"
            val channelName = "My Background Service"
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
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
            startForeground(2, notification)

            manager2 = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            manager2.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                0f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        val intent = Intent("location_update")
                        intent.putExtra("coordinates", location)
                        println("service ciagle dziala2")
                        sendBroadcast(intent)
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderEnabled(provider: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onProviderDisabled(provider: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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










