package com.example.firebasepdfupload.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebasepdfupload.Adapter.AdapterCategory
import com.example.firebasepdfupload.Model.ModelCategory
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

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()
        loadCategories()


//    handle click ,start add category page
        binding.categoryBtn.setOnClickListener {
            val intent= Intent(this@HomeAdmin, CategoryAddActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.addPdfFab.setOnClickListener{
            intent= Intent(this@HomeAdmin, PdfActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.userImageAdmin.setOnClickListener {
            intent=Intent(this@HomeAdmin,ProfileAdminActivity::class.java)
            startActivity(intent)
        }
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
}