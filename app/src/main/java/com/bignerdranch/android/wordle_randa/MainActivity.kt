package com.bignerdranch.android.wordle_randa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bignerdranch.android.wordle_randa.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainAct", "Starting Main Activity")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Create a frament manager
        var ft = supportFragmentManager.beginTransaction()
        //add GameScreenFragment to the FrameLayout (left empty in xml)
        ft.replace(R.id.fragment, GameScreenFragment())
        ft.commit()
    }
}