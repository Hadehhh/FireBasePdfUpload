package com.example.firebasepdfupload.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.example.firebasepdfupload.Adapter.AdapterPdfAdmin
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.databinding.ActivityPdfListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfListAdminActivity : AppCompatActivity() {
    //    view binding
    private lateinit var binding: ActivityPdfListAdminBinding

    //    arrayList to hold Modul
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    //    adapter
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin


    //    category id,title
    private var categoryId=""
    private var category=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPdfListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        get from intent ,that we passes from adapter
        val intent=intent
        categoryId= intent.getStringExtra("categoryId")!!
        category= intent.getStringExtra("category")!!

        binding.backImage.setOnClickListener {
            val intent= Intent(this@PdfListAdminActivity, HomeAdmin::class.java)
            startActivity(intent)
            finish()
        }

//        set pdf category
        binding.subTitleTv.text=category

//        load pdf
        loadPdfList()

//        search
        binding.searchBar.addTextChangedListener(object : TextWatcher {
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
//        init arrayList
        pdfArrayList= ArrayList()
        val ref= FirebaseDatabase.getInstance().getReference("Modul")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

//                    clear list before start adding data into it
                    pdfArrayList.clear()
                    for(ds in snapshot.children)
                    {
//                        get data
                        val model=ds.getValue(ModelPdf::class.java)
//                        add to list
                        if (model != null) {
                            pdfArrayList.add(model)
                        }
                    }
//                    setup adapter
                    adapterPdfAdmin= AdapterPdfAdmin(this@PdfListAdminActivity,pdfArrayList)
                    binding.pdfsRv.adapter=adapterPdfAdmin
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}