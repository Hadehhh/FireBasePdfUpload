package com.example.firebasepdfupload.Activity

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasepdfupload.MaxSize
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityPdfViewBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class PdfView : AppCompatActivity() {
    private lateinit var binding:ActivityPdfViewBinding

    //    TAG
    private companion object{
        const val TAG="PDF_VIEW_TAG"
    }

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
        Log.d(TAG,"loadPdfDetails: Get Pdf Url from db")
//        database ref to get pdf details
//        ex: get pdf url using pdf id
//        step 1 Get pdf url using Pdf Id
        val ref=FirebaseDatabase.getInstance().getReference("Pdfs")
        ref.child(pdfId)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
//                    get pdf url
                    val pdfUrl=snapshot.child("url").value
                    Log.d(TAG,"onDataChange:PDF_URL:$pdfUrl")
//                    step2 load pdf using url from firebase storage
                    loadPdfFromUrl("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }
    @SuppressLint("SetTextI18n")
    private fun loadPdfFromUrl(pdfUrl: String) {
        Log.d(TAG,"loadPdfFromUrl:Get Pdf from firebase storage Using Url")
        val reference=FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(MaxSize.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes->
                Log.d(TAG,"loadPdfFromUrl:Pdf got from Url")
                binding.pdfView.fromBytes(bytes)
                    .swipeHorizontal(false)  //set false to scroll vertical ,set true to scroll horizontal
                    .onPageChange{page,pageCount->
//                        set current page and total page in toolbar
                        val currentPage=page+1
                        binding.subTitleTv.text="$currentPage/$pageCount"
                        Log.d(TAG,"loadPdfFromUrl:$currentPage/$pageCount")
                    }
                    .onError{t->
                        Log.d(TAG,"loadPdfFromUrl: ${t.message}")
                    }
                    .onPageError { page, t ->
                        Log.d(TAG,"loadPdfFromUrl:${t.message}")
                    }.load()
                binding.progressBar.visibility=View.GONE

            }
            .addOnFailureListener{e->
                Log.d(TAG,"loadPdfFromUrl: Failed to get pdf due to ${e.message}")
                binding.progressBar.visibility=View.GONE
            }
    }
}