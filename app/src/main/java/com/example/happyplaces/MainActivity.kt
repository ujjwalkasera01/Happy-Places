package com.example.happyplaces

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnAddHappyPlace : FloatingActionButton = findViewById(R.id.fabAddHappyPlace)
        btnAddHappyPlace.setOnClickListener {
            val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }

    }
}