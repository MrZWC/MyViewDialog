package com.zwc.myviewdialog

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.zwc.myviewdialog.databinding.ActivityMainBinding
import com.zwc.myviewdialog.databinding.ViewTestLayoutBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.opneBtn.setOnClickListener {
             val testDialog = TestDialog(this)
             testDialog.show()
            //addView()
        }
    }

    private fun addView() {
        val inflate = ViewTestLayoutBinding.inflate(layoutInflater)
        val testView = inflate.root
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200)
        binding.root.addView(testView,layoutParams)
    }
}