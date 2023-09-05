package com.example.firebasepdfupload.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.firebasepdfupload.MaxSize
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
//    override fun onCreate() {
//        super.onCreate()
//    }
    companion object{

        fun formatTimeStamp(timestamp: Long): String {
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(cal.time).toString()
        }

        //        Fun to get PDF Size
        @SuppressLint("SetTextI18n")
        fun loadPdfSize(pdfUrl:String, pdfTitle:String, sizeTv: TextView){
            val TAG="PDF_SIZE_TAG"
//          Using url we can get file and its metadata from firebase storage
            val ref=FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener {
                    Log.d(TAG,"loadPdfSize: got metadata")
                    val bytes=it.sizeBytes.toDouble()
                    Log.d(TAG,"loadPdfSize:Size Bytes $bytes")
//                  Convert bytes to KB
                    val kb=bytes/1024
                    val mb=kb/1024
                    if(mb>=1)
                    {
                        sizeTv.text="${String.format("%.2f",mb)}MB"
                    }
                    else if(kb>=1)
                    {
                        sizeTv.text="${String.format("%.2f",kb)}KB"
                    }
                    else{
                        sizeTv.text="${String.format("%.2f,bytes")}bytes"
                    }
                }
                .addOnFailureListener{e->
//                  Failed to get metadata
                    Log.d(TAG,"loadPdfSize:Failed to get metadata due to ${e.message}")
                }


        }

//        Instead of making new function loadPdfPageCount() to just load pages count ,it  would be
//        more good to use some existing function to do that loadPdfFromSinglePage
//        we will add another parameter of type TextView ex-->PagesTv
//        Whenever we call that fun
//        1) if we require page numbers we will pass pageTv
//        2) if we don't require page number we will pass null
//        Add in function if PageTv(TextView) parameter is not null we will set the page-number count

        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTitle:String,
            pdfView: PDFView,
            progressBar: ProgressBar,
            pagesTv: TextView?
        ){
            val TAG="PDF_THUMBNAIL_TAG"
//          Using url we can get file and its metadata from firebase storage
            val ref=FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(MaxSize.MAX_BYTES_PDF)
                .addOnSuccessListener {bytes->
                    Log.d(TAG,"loadPdfFromUrlSinglePage:Size Bytes $bytes")

//                  Set to PDFView
                    pdfView.fromBytes(bytes)
                        .pages(0)  //show first page only
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError{t->
                            progressBar.visibility=View.INVISIBLE
                            Log.d(TAG,"loadPdfFromUrlSinglePage:${t.message}")
                        }
                        .onPageError { page, t ->
                            progressBar.visibility=View.INVISIBLE
                            Log.d(TAG,"loadPdfFromUrlSinglePage:${t.message}")
                        }
                        .onLoad{nbPages->
                            Log.d(TAG,"loadPdfUrlFromSinglePage:Pages:$nbPages")
//                          Pdf loaded ,we can set page count,pdf thumbnails
                            progressBar.visibility=View.INVISIBLE

//                          If pagesTv param is not null then set page numbers
                            if(pagesTv !=null)
                            {
                                pagesTv.text="$nbPages"
                            }
                        }
                        .load()
                }
                .addOnFailureListener{e->
//                  Failed to get metadata
                    Log.d(TAG,"loadPdfSize:Failed to get metadata due to ${e.message}")
                }
        }
        fun loadCategory(categoryId: String,categoryTv: TextView)
        {
//          Load category using category id from firebase
            val ref= FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
//                        get category
                        val category="${snapshot.child("category").value}"
//                        set category
                        categoryTv.text=category
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

        }

        fun deletePdf(context: Context, pdfId:String, pdfUrl:String, pdfTitle:String)
        {
//            parameter details
//            1 context,used when require ex: for progress dialog ,toast
//            2 pdfId,to delete pdf from db
//            3 pdfUrl ,delete pdf from firebase storage
//            4 pdfTitle ,show in dialog etc

            val TAG="DELETE_PDF_TAG"
            Log.d(TAG,"deletePdf:deleting..")
            val progressDialog= ProgressDialog(context)
            progressDialog.setTitle("please wait")
            progressDialog.setMessage("Deleting $pdfTitle...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Log.d(TAG,"deletePdf:Deleting from storage..")
            val storageRef=FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            storageRef.delete()
                .addOnSuccessListener {
                    Log.d(TAG,"deletePdf: Deleted from storage")
                    Log.d(TAG,"deletePdf: Deleting from db now..")
                    val ref= FirebaseDatabase.getInstance().getReference("Pdfs")
                    ref.child(pdfId)
                        .removeValue()
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(context,"Successfully Deleted..",Toast.LENGTH_SHORT).show()
                            Log.d(TAG,"deletePdf:Deleted from db too..")
                        }
                        .addOnFailureListener{e->
                            progressDialog.dismiss()
                            Log.d(TAG,"deletePdf: Failed to delete from db due to ${e.message}")
                            Toast.makeText(context,"Failed to delete  due to ${e.message}",Toast.LENGTH_SHORT).show()

                        }
                }
                .addOnFailureListener{e->
                    progressDialog.dismiss()
                    Log.d(TAG,"deletePdf: Failed to delete from storage due to ${e.message}")
                    Toast.makeText(context,"Failed to delete due to ${e.message}",Toast.LENGTH_SHORT).show()
                }


        }

        public fun removeFromFav(context: Context, bookId: String){
            val TAG ="REMOVE_FAV_TAG"
            Log.d(TAG,"removeFromFav:Removing From Fav..")
            val firebaseAuth= FirebaseAuth.getInstance()
            val ref= FirebaseDatabase.getInstance().getReference("Users")
            ref.child(firebaseAuth.uid!!).child("Bookmarks").child(bookId)
                .removeValue()
                .addOnSuccessListener {
                    Log.d(TAG,"removeFromFavorite: Removed From fav")
                    Toast.makeText(context,"Removed from Bookmark",Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Log.d(TAG,"removeFromFavourite: Failed to remove from fav due to ${it.message}")
                    Toast.makeText(context,"Failed to remove from fav due to ${it.message}",Toast.LENGTH_SHORT).show()
                }

        }
    }

}