package com.example.firebasepdfupload.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.firebasepdfupload.Adapter.AdapterCategory
import com.example.firebasepdfupload.Model.ModelCategory
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityHomeAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeAdmin : AppCompatActivity() {
    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth

    //    arraylist to hold categories
    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    //    adapter
    private lateinit var adapterCategory: AdapterCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()
        loadUserInfo()
        val recyclerView = binding.categoriesRv
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadCategories()


//    handle click ,start add category page
        binding.categoryBtn.setOnClickListener {
            val intent= Intent(this@HomeAdmin, CategoryAddActivity::class.java)
            startActivity(intent)
        }
        binding.addPdfFab.setOnClickListener{
            intent= Intent(this@HomeAdmin, PdfActivity::class.java)
            startActivity(intent)
        }
        binding.userImageAdmin.setOnClickListener {
            intent=Intent(this@HomeAdmin,ProfileAdminActivity::class.java)
            startActivity(intent)
        }
        binding.btnAllpdf.setOnClickListener {
            intent=Intent(this@HomeAdmin, ListAllPdfAdmin::class.java)
            startActivity(intent)
        }
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                filter data
                try {
                    adapterCategory.filter!!.filter(s)
                }
                catch (e:Exception){
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun loadCategories() {
//        init array
        categoryArrayList= ArrayList()
//        getAll  Categories from firebase database .. Firebase DB >Categories
        val ref= FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                clear list before  starting adding data into it
                categoryArrayList.clear()
                for (ds in snapshot.children)
                {
//                    get data as model
                    val model= ds.getValue(ModelCategory::class.java)
//                    add to arraylist
                    categoryArrayList.add(model!!)

                }
//                setup adapter
                adapterCategory = AdapterCategory(this@HomeAdmin,categoryArrayList)
//                set Adapter to RecyclerView
                binding.categoriesRv.adapter=adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun loadUserInfo() {

//      db ref to load user info
        val ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
//                  Get user info
                    val profilePic="${snapshot.child("profileImage").value}"

//                  Set image
                    try{
                        Glide.with(this@HomeAdmin)
                            .load(profilePic)
                            .into(binding.userImageAdmin)
                    }
                    catch (e:Exception){
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}