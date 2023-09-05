package com.example.firebasepdfupload.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasepdfupload.Adapter.AdapterPdfFav
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityBookmarkBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookmarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookmarkBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pdfsArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfFav: AdapterPdfFav

    //    firebase current user
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

        loadBookmark()

        binding.bottomNavigation.menu.findItem(R.id.bookmark).setChecked(true)
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
    }
private fun loadBookmark(){
//        init arraylist
        pdfsArrayList=ArrayList()
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Bookmarks")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfsArrayList.clear()
                    for(ds in snapshot.children){
                        val pdfId="${ds.child("pdfId").value}"
                        val modelPdf= ModelPdf()
                        modelPdf.id=pdfId

//                      Menambahkan model ke list
                        pdfsArrayList.add(modelPdf)
                    }
                    adapterPdfFav= AdapterPdfFav(this@BookmarkActivity,pdfsArrayList)
                    binding.favRv.adapter=adapterPdfFav
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}