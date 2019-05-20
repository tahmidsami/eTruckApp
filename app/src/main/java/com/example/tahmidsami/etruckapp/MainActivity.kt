package com.example.tahmidsami.etruckapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonMap.setOnClickListener{ //Google map button
            var i = Intent(this, MapsActivity::class.java)
            i.putExtra("latitude","23.77")
            i.putExtra("longitude","90.42")
            startActivity(i)
        }

    }
}
