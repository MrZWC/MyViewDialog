package com.zwc.myviewdialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zwc.myviewdialog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.opneBtn.setOnClickListener {
            val testDialog = TestDialog(this)
            testDialog.show()
        }
    }
}