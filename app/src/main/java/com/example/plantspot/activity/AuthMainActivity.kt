package com.example.plantspot.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.plantspot.R
import com.example.plantspot.databinding.ActivityAuthBinding
import com.example.plantspot.databinding.ActivityUserMainBinding

class AuthMainActivity : AppCompatActivity() {
private lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)




    }
}