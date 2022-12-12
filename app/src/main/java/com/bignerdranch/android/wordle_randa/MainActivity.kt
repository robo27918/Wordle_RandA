package com.bignerdranch.android.cs4750finaproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bignerdranch.android.cs4750finaproject.databinding.ActivityMainBinding

private lateinit var binding:ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}