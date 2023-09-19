package com.example.firebasepdfupload.Activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.firebasepdfupload.MaxSize
import com.example.firebasepdfupload.databinding.ActivityPdfViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfView : AppCompatActivity() {
    private lateinit var binding:ActivityPdfViewBinding

    //    TAG
    var pdfId=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfId=intent.getStringExtra("pdfId")!!
        loadPdfDetails()

        binding.backBtn.setOnClickListener{
            onBackPressed()
        }
    }

    private fun loadPdfDetails() {
//        database ref to get pdf details
//        ex: get pdf url using pdf id
//        step 1 Get pdf url using Pdf Id
        val ref=FirebaseDatabase.getInstance().getReference("Modul")
        ref.child(pdfId)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
//                    get pdf url
                    val pdfUrl=snapshot.child("url").value
                    val pdfTitle=snapshot.child("title").value
//                    step2 load pdf using url from firebase storage
                    loadPdfFromUrl("$pdfUrl")
                    binding.toolbarTitleTv.text = pdfTitle.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }
    private fun loadPdfFromUrl(pdfUrl: String) {
        val reference=FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(MaxSize.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes->
                binding.pdfView.fromBytes(bytes)
                    .swipeHorizontal(false)  //set false to scroll vertical ,set true to scroll horizontal
                    .onPageChange{page,pageCount->
//                        set current page and total page in toolbar
                        val currentPage=page+1
                        binding.subTitleTv.text="$currentPage/$pageCount"
                    }
                    .onError{t->
                    }
                    .onPageError { page, t ->
                    }.load()
                binding.progressBar.visibility=View.GONE

            }
            .addOnFailureListener{e->
                binding.progressBar.visibility=View.GONE
            }
    }
}