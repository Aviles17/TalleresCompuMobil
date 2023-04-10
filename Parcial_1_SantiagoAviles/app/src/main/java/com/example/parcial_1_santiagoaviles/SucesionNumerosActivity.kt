package com.example.parcial_1_santiagoaviles

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import com.example.parcial_1_santiagoaviles.databinding.ActivitySucesionNumerosBinding

class SucesionNumerosActivity : AppCompatActivity(), OnItemClickListener {
    private lateinit var binding: ActivitySucesionNumerosBinding
    private lateinit var arrayString:ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        arrayString =ArrayList<String>()
        super.onCreate(savedInstanceState)
        binding = ActivitySucesionNumerosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val Iterator = intent.getStringExtra("LimitFor")?.toInt()
        if (Iterator != null) {
            GenArrayList(Iterator)
        }
        val adapterString = ArrayAdapter<String>(this, R.layout.simple_list_item_single_choice, arrayString)
        binding.ListView.adapter = adapterString
        binding.ListView.choiceMode = AbsListView.CHOICE_MODE_SINGLE
        binding.ListView.onItemClickListener = this
    }
    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        val num = arrayString[p2]
        startActivity(Intent(baseContext,DisplayNumberActivity::class.java).apply{
            putExtra("NumDisplay", num)
        })
    }
    fun GenArrayList(Iterator:Int){
        for(i in 0..Iterator){
            val res = i*i*i
            arrayString.add(res.toString())
        }
    }
}