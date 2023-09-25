package com.example.firebasepdfupload.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasepdfupload.Adapter.AdapterPdfAdmin
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.databinding.ActivityListAllPdfAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListAllPdfAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityListAllPdfAdminBinding
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListAllPdfAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView = binding.rvAllPdf
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadPdfList()

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeAdmin::class.java)
            startActivity(intent)
            finish()
        }

        //        search
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                filter data
                try {
                    adapterPdfAdmin.filter!!.filter(s)
                }
                catch (e:Exception){
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
    private fun loadPdfList() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Modul")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pdfArrayList.clear()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelPdf::class.java)
                    pdfArrayList.add(model!!)
                }
                //                    setup adapter
                adapterPdfAdmin = AdapterPdfAdmin(this@ListAllPdfAdmin,pdfArrayList)
                binding.rvAllPdf.adapter = adapterPdfAdmin
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}