package com.example.firebasepdfupload.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.core.view.WindowCompat
import com.example.firebasepdfupload.R

class SplashScreen : AppCompatActivity() {
    //Deklarasi variabel timer splash screennya
    private val SPLASH_TIME_OUT: Long = 1500 //delay 1.5 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //Aktifkan progress bar/loading bar dan menjalankan main screen setelah timer splash screen habis
        Handler().postDelayed({
            startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
            finish()
        }, SPLASH_TIME_OUT)
    }
}
//    private val imageResources = arrayOf(
//        R.drawable.a,
//        R.drawable.b,
//        R.drawable.c
//    )
//    private val SPLASH_TIME_OUT: Long = 1000 // Durasi tampilan gambar dalam milidetik
//    private lateinit var imageView: ImageView
//    private var currentImageIndex = 0
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        setContentView(R.layout.activity_splash_screen)
//
//        imageView = findViewById(R.id.imageView)
//
//        val handler = Handler()
//        val runnable = object : Runnable {
//            override fun run() {
//                if (currentImageIndex < imageResources.size) {
//                    imageView.setImageResource(imageResources[currentImageIndex])
//                    currentImageIndex++
//                    handler.postDelayed(this, SPLASH_TIME_OUT)
//                } else {
////                   Pindah ke activity berikutnya setelah semua gambar ditampilkan
//                     val intent = Intent(this@SplashScreen, LoginActivity::class.java)
//                     startActivity(intent)
//                     finish()
//                }
//            }
//        }
//        handler.post(runnable)
//    }
//}