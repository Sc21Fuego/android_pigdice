package com.example.pigdice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri

class AboutActivity : AppCompatActivity() {
//    About screen to display upon opening the app
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun imgViewOnClick(v: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun btnContactOnClick(v: View) {
        val uri: Uri = "https://sc21fuego.github.io/ai-profile-website/contact.html".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    fun btnAboutOnClick(v: View) {
        val uri: Uri = "https://sc21fuego.github.io/ai-profile-website/index.html".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    fun txtAttributionOnClick(v: View) {
        val uri: Uri =
            "https://wallpapers.com/wallpapers/android-fire-w2n8jx6tolx0pue6.html".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

}