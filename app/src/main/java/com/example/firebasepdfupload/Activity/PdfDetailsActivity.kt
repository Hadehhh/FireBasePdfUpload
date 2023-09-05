package com.example.firebasepdfupload.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.firebasepdfupload.MaxSize
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityPdfDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class PdfDetailsActivity : AppCompatActivity() {
    internal companion object{
        const val TAG="PDF_DETAILS_TAG"

    }
    private lateinit var mProgressDialog: Dialog
    private lateinit var binding: ActivityPdfDetailsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private  var isMyFavourite=false
    private var pdfId=""
    //    get from firebase
    private var pdfTitle=""
    private var pdfUrl=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        pdfId = intent.getStringExtra("pdfId")!!

        if (firebaseAuth.currentUser != null) {
//            user is logged in ,check if pdf is in fav or not
            checkIsFav()
        }
        loadPdfDetails()


        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
//      handle click ,open pdfView Activity
        binding.readPdfBtn.setOnClickListener {
            val intent = Intent(this, PdfView::class.java)
            intent.putExtra("pdfId", pdfId)
            startActivity(intent)
        }
//        handle click ,download button
        binding.downloadBtn.setOnClickListener {
//            first check storage permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                Log.d(TAG, "onCreate : Storage permission is granted")
                downloadPdf()

            } else {
                Log.d(TAG, "OnCreate : storage permission is denied,Let's request it")
                requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
//        add to fav
        binding.favBtn.setOnClickListener {
            if (isMyFavourite) {
//                    already in fav
                removeFromFav()
            } else {
                addToFav()
            }
        }
    }

    private val requestStoragePermissionLauncher= registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted:Boolean->
        if(isGranted){
            Log.d(TAG,"onCreate :STORAGE permission is granted")
        }
        else
        {
            Log.d(TAG,"onCreate :STORAGE permission is denied")
            Toast.makeText(this,"permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    private fun downloadPdf(){
        Log.d(TAG,"Downloading Pdf")
//        progressBar
        showProgressDialog("Downloading Pdf")

//        lets download pdf from firebase storage
        val storageRef= FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        storageRef.getBytes(MaxSize.MAX_BYTES_PDF)
            .addOnSuccessListener {bytes->
                Log.d(TAG,"downloadPdf: Pdf download...")
                saveToDownloadsFolder(bytes)
            }
            .addOnFailureListener {e->
                hideProgressDialog()
                Log.d(TAG,"Failed to download Pdf due to ${e.message}")
                Toast.makeText(this,"Failed to download Pdf due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToDownloadsFolder(bytes: ByteArray?) {
        Log.d(TAG,"saveToDownloadFolder : saving download Pdf")
        val nameWithExtension="$pdfTitle.pdf"
        try {
            val downloadFolder= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadFolder.mkdirs()

            val filePath=downloadFolder.path +"/"+nameWithExtension
            val out= FileOutputStream(filePath)
            out.write(bytes)
            out.close()
            Toast.makeText(this,"Saved to Download Folder", Toast.LENGTH_SHORT).show()
            Log.d(TAG,"Saved to Download Folder")
            hideProgressDialog()

        }
        catch (e:Exception){
            Log.d(TAG,"saveToDownloadsFolder:failed to save due to ${e.message}")
            Toast.makeText(this,"failed to save due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPdfDetails() {
//        Pdfs>pdfId>details
        val ref= FirebaseDatabase.getInstance().getReference("Pdfs")
        ref.child(pdfId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
//                    get data
                    val categoryId="${snapshot.child("categoryId").value}"
                    val description="${snapshot.child("description").value}"
                    val timestamp="${snapshot.child("timestamp").value}"
                    pdfTitle="${snapshot.child("title").value}"
                    val uid="${snapshot.child("uid").value}"
                    pdfUrl="${snapshot.child("url").value}"

//                    formattedDate
                    val date= MainActivity.formatTimeStamp(timestamp.toLong())

                    MainActivity.loadCategory(categoryId, binding.categoryTv)
                    MainActivity.loadPdfFromUrlSinglePage(
                        "$pdfUrl",
                        "$pdfTitle",
                        binding.pdfViewer,
                        binding.progressBar,
                        binding.pagesTv
                    )
                    MainActivity.loadPdfSize("$pdfUrl", "$pdfTitle", binding.sizeTv)


//                    set data
                    binding.titleTv.text=pdfTitle
                    binding.descriptionTv.text=description
                    binding.date.text=date

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
    private fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.progress_indicator)

        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    private fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }


    @SuppressLint("SuspiciousIndentation")


    private fun checkIsFav(){
        Log.d(TAG,"checkIsFav: Checking if pdf is in fav or not")
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Bookmarks").child(pdfId)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    isMyFavourite=snapshot.exists()
                    if(isMyFavourite)
                    {
                        Log.d(TAG,"onDataChange: available in favourite")
//                        available in favourite,, set drawable icon
                        binding.favBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_bookmark_24,
                            0,0,0)
                        binding.favBtn.text="Hapus dari Markah"
                    }
                    else
                    {
                        Log.d(TAG,"onDataChange : not available in favourite")
//                        not available in fav
                        binding.favBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_bookmark_border_24,
                            0, 0,0)
                        binding.favBtn.text="Tambahkan ke Markah"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    @SuppressLint("SuspiciousIndentation")
    private fun addToFav(){
        Log.d(TAG,"addToFav: Adding to Bookmark")
        val timestamp=System.currentTimeMillis()
//        setup data to add in db
        val hashMap=HashMap<String,Any>()
        hashMap["pdfId"]=pdfId
        hashMap["timestamp"]=timestamp

//        save to db
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Bookmarks").child(pdfId)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG,"addToFav: Added to Bookmark")
                Toast.makeText(this,"Berhasil menambahkan ke markah", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Log.d(TAG,"addToFav: Failed to add to fav due to ${it.message}")
                Toast.makeText(this,"Failed to add to bookmark due to ${it.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun removeFromFav() {
        Log.d(TAG, "removeFromFav:Removing From Bookmark..")
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Bookmarks").child(pdfId)
            .removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "removeFromFavorite: Removed From Bookmark")
                Toast.makeText(this, "Removed from Favourite", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d(TAG, "removeFromFavourite: Failed to remove from bookmark due to ${it.message}")
                Toast.makeText(
                    this,
                    "Failed to remove from fav due to ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}