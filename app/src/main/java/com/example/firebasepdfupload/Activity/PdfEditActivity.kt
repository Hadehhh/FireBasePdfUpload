package com.example.firebasepdfupload.Activity

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityPdfEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfEditActivity : AppCompatActivity() {

    private lateinit var binding:ActivityPdfEditBinding

    private companion object{
        private const val TAG="PDF_EDIT_TAG"
    }

    //    pdf id get from intent started from AdapterPdfAdmin
    private var pdfId=""

    private lateinit var mProgressDialog: Dialog

    //    arrayList to hold category Titles
    private lateinit var categoryTitleArrayList:ArrayList<String>
    //    arrayList to hold Category Ids
    private lateinit var categoryIdArrayList:ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPdfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pdfId=intent.getStringExtra("pdfId")!!

        loadCategories()
        loadPdfInfo()

        binding.backImage.setOnClickListener {
            onBackPressed()
        }
//         handle click,pick Category
        binding.pdfCatTv.setOnClickListener {
            categoryDialog()

        }

//        handle click,begin update
        binding.updateButton.setOnClickListener {
            validateData()
        }



    }

    private fun loadPdfInfo() {
        Log.d(TAG,"loadPdfInfo:Loading pdf info")
        val ref= FirebaseDatabase.getInstance().getReference("Pdfs")
        ref.child(pdfId)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
//                    get pdf  info
                    selectedCategoryId=snapshot.child("categoryId").value.toString()
                    val description=snapshot.child("description").value.toString()
                    val title=snapshot.child("title").value.toString()

//                    set to views
                    binding.pdfTitleEdt.setText(title)
                    binding.pdfDesEdt.setText(description)
//                    load pdf category info using categoryId
                    Log.d(TAG,"onDataChange: Loading pdf Category info")
                    val refPdfCategory= FirebaseDatabase.getInstance().getReference("Categories")
                    refPdfCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
//                                get category
                                val category=snapshot.child("category").value
//                                set to textView
                                binding.pdfCatTv.text=category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    private var title=""
    private var description=""
    private fun validateData() {
//        get data
        title=binding.pdfTitleEdt.text.toString().trim()
        description=binding.pdfDesEdt.text.toString().trim()
//        validate data
        if(title.isEmpty())
        {
            Toast.makeText(this,"Enter title", Toast.LENGTH_SHORT).show()
        }
        else if(description.isEmpty())
        {
            Toast.makeText(this,"Enter description", Toast.LENGTH_SHORT).show()
        }
        else if(selectedCategoryId.isEmpty())
        {
            Toast.makeText(this,"Pick Category", Toast.LENGTH_SHORT).show()
        }
        else
        {
            updatePdf()
        }
    }

    private fun updatePdf() {
        Log.d(TAG,"updatePdf:Starting updating pdf info..")
        showProgressDialog("Updating Pdf info..")
//        setup data to update to db, spellings of keys must be same as in firebase
        val hashMap=HashMap<String,Any>()
        hashMap["title"]= title
        hashMap["description"]= description
        hashMap["categoryId"]=selectedCategoryId

//        start updating
        val ref= FirebaseDatabase.getInstance().getReference("Pdfs")
        ref.child(pdfId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                hideProgressDialog()
                Log.d(TAG,"updatePdf: Updated successfully..")
                Toast.makeText(this,"Updated successfully...", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{e->
                Log.d(TAG,"updatePdf: Failed to update due to ${e.message}")
                hideProgressDialog()
                Toast.makeText(this,"Failed to update due to ${e.message} ", Toast.LENGTH_SHORT).show()
            }
    }

    private var selectedCategoryId=""
    private var selectedCategoryTitle=""
    private fun categoryDialog() {
//        show dialog to pick the category of pdf we already  got the categories

//        make string array from arrayList of string
        val categoriesArray=arrayOfNulls<String>(categoryTitleArrayList.size)
        for(i in categoryTitleArrayList.indices)
        {
            categoriesArray[i]=categoryTitleArrayList[i]
        }
//        alert dialog
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Choose Category")
            .setItems(categoriesArray){ _, position->
//                handle click ,save clicked category id and title
                selectedCategoryId=categoryIdArrayList[position]
                selectedCategoryTitle=categoryTitleArrayList[position]
//                set to textView
                binding.pdfCatTv.text=selectedCategoryTitle

            }.show()


    }

    private fun loadCategories() {
        Log.d(TAG,"loadCategories: loading categories")
        categoryTitleArrayList= ArrayList()
        categoryIdArrayList=ArrayList()

        val ref= FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                clear list before starting adding data into them
                categoryIdArrayList.clear()
                categoryTitleArrayList.clear()

                for(ds in snapshot.children){
                    val id="${ds.child("id").value}"
                    val category="${ds.child("category").value}"

                    categoryIdArrayList.add(id)
                    categoryTitleArrayList.add(category)

                    Log.d(TAG,"OnDataChange:Category ID $id")
                    Log.d(TAG,"onDataChange:Category  $category")


                }


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
}