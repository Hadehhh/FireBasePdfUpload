package com.example.firebasepdfupload.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.firebasepdfupload.Model.ModelCategory
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityPdfBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPdfBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mProgressDialog: Dialog
    //    arrayList to hold pdf Categories
    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    //    uri of picked pdf
    private  var pdfUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        init firebase Auth
        firebaseAuth= FirebaseAuth.getInstance()
        loadPdfCategories()

//      handle click
//        show category Pick Dialog
        binding.pdfCatTv.setOnClickListener {
            categoryPickDialog()
        }
//        pick pdf intent
        binding.attachFile.setOnClickListener {
            pdfPickIntent()
        }
//        start uploading pdf
        binding.uploadButton.setOnClickListener {
            validateData()
        }
//        handle go back btn
        binding.backImage.setOnClickListener {
            val intent= Intent(this@PdfActivity, HomeAdmin::class.java)
            startActivity(intent)
            finish()
        }

    }

    private var title=""
    private var description=""
    private var category=""

    private fun validateData() {
//        get data
        title=binding.pdfTitleEdt.text.toString().trim()
        description=binding.pdfDesEdt.text.toString().trim()
        category=binding.pdfCatTv.text.toString().trim()

//        validate data
        if(title.isEmpty())
        {
            Toast.makeText(this,"Judul harus di-isi..", Toast.LENGTH_SHORT).show()
        }
        else if(description.isEmpty())
        {
            Toast.makeText(this,"Deskripsi harus di-isi..", Toast.LENGTH_SHORT).show()
        }
        else if(category.isEmpty())
        {
            Toast.makeText(this,"Pilih Kategori..", Toast.LENGTH_SHORT).show()
        }
        else if(pdfUri==null)
        {
            Toast.makeText(this,"Pilih File...", Toast.LENGTH_SHORT).show()
        }
        else
        {
//            data validated--> begin upload
            uploadPdfToStorage()
        }
    }
    private fun uploadPdfToStorage() {
//        show progress dialog
        showProgressDialog("Mengunggah File..")
//        timestamp
        val timestamp=System.currentTimeMillis()

//        path of pdf in firebase storage
        val filePathAndName="Modul/$timestamp"

//        storage ref
        val storageReference= FirebaseStorage.getInstance().getReference(filePathAndName)

        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener {taskSnapshot->
//                set url of uploaded pdf-->step3
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl

                while (!uriTask.isSuccessful);
                val uploadedPdfUrl="${uriTask.result}"
                hideProgressDialog()
                uploadPdfInfoToDb(uploadedPdfUrl,timestamp)

            }
            .addOnFailureListener{
                hideProgressDialog()
                Toast.makeText(this,"Failed upload due to ${it.message} ", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {
//    Upload pdf info to firebase db
        showProgressDialog("Mengunggah pdf info..")

//        uid of current user
        val uid= firebaseAuth.uid

//        setup data to upload
        val hashMap:HashMap<String,Any> = HashMap()
        hashMap["uid"]="$uid"
        hashMap["id"]="$timestamp"
        hashMap["title"]="$title"
        hashMap["description"]="$description"
        hashMap["categoryId"]="$selectedCategoryId"
        hashMap["url"]="$uploadedPdfUrl"
        hashMap["timestamp"]=timestamp

//        db reference DB> Modul >pdfId >(PdfInfo)
        val ref= FirebaseDatabase.getInstance().getReference("Modul")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                hideProgressDialog()
                val intent= Intent(this,HomeAdmin::class.java)
                startActivity(intent)
                Toast.makeText(this,"Berhasil di-unggah..", Toast.LENGTH_SHORT).show()
                pdfUri=null

            }
            .addOnFailureListener{
                hideProgressDialog()
                Toast.makeText(this,"Failed upload due to ${it.message} ", Toast.LENGTH_SHORT).show()

            }


    }

    private fun loadPdfCategories() {
//        init arrayList
        categoryArrayList= ArrayList()
//        db reference to load categories DF >Categories

        val ref= FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                clear list before adding data
                categoryArrayList.clear()
                for(it in snapshot.children)
                {
//                    get data
                    val model=it.getValue(ModelCategory::class.java)
//                    add to array List
                    categoryArrayList.add(model!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private var selectedCategoryId=""
    private var selectedCategoryTitle=""

    private fun categoryPickDialog()
    {

//        get string array of Categories from arrayList
        val categoriesArray= arrayOfNulls<String>(categoryArrayList.size)
        for(i in categoryArrayList.indices)
        {
            categoriesArray[i]=categoryArrayList[i].category
        }

//        alert dialog
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Pilih Kategori")
            .setItems(categoriesArray){dialog,which->
//                handle item click
//                get clicked item
                selectedCategoryTitle=categoryArrayList[which].category
                selectedCategoryId=categoryArrayList[which].categoryId

//                set category to textview
                binding.pdfCatTv.text=selectedCategoryTitle
            }
            .show()
    }

    private fun pdfPickIntent(){
        val intent= Intent()
        intent.type="application/pdf"
        intent.action= Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    private val pdfActivityResultLauncher=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if(result.resultCode== RESULT_OK){
                pdfUri=result.data!!.data
            }
            else
            {
                Toast.makeText(this,"Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

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
}