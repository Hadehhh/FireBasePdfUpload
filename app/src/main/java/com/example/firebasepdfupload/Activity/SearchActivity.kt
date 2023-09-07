package com.example.firebasepdfupload.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasepdfupload.Adapter.AdapterSearch
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivitySearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private companion object{
        const val TAG="PDF_SEARCH_TAG"
    }
    private lateinit var binding: ActivitySearchBinding
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterSearch: AdapterSearch
    private lateinit var recyclerView: RecyclerView
    private var pdfId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recyclerView = binding.vpSearch
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadPdfList()

        binding.bottomNavigation.menu.findItem(R.id.search).setChecked(true)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.beranda -> {
                    // Respond to navigation item 1 click
                    val intent= Intent(this,HomeUser::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.bookmark -> {
                    // Respond to navigation item 2 click
                    val intent= Intent(this,BookmarkActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.search -> {
                    // Respond to navigation item 2 click
                    val intent= Intent(this,SearchActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.account -> {
                    // Respond to navigation item 2 click
                    val intent= Intent(this,ProfileActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
        binding.bottomNavigation.setOnItemReselectedListener { item ->
            when(item.itemId) {
                R.id.beranda -> {
                    // Respond to navigation item 1 reselection
                }
                R.id.bookmark -> {
                    // Respond to navigation item 2 reselection
                }
                R.id.search -> {
                    // Respond to navigation item 2 reselection
                }
                R.id.account -> {
                    // Respond to navigation item 2 reselection
                }
            }
        }

        //        search
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                filter data
                try {
                    adapterSearch.filter!!.filter(s)
                }
                catch (e:Exception){
                    Log.d(TAG,"onTextChanged:${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
    private fun loadPdfList() {
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Pdfs")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelPdf::class.java)
                        pdfArrayList.add(model!!)
                    }
                    //                    setup adapter
                    adapterSearch = AdapterSearch(this@SearchActivity,pdfArrayList)
                    binding.vpSearch.adapter = adapterSearch
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}