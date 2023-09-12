package com.example.firebasepdfupload.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasepdfupload.Activity.MainActivity
import com.example.firebasepdfupload.Activity.PdfDetailsActivity
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.databinding.RowPdfFavBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdapterPdfFav  : RecyclerView.Adapter<AdapterPdfFav.HolderPdfFav>{
    private val context: Context
    private var pdfsArrayList:ArrayList<ModelPdf>
    private lateinit var binding: RowPdfFavBinding

    constructor(context: Context, pdfsArrayList: ArrayList<ModelPdf>) {
        this.context = context
        this.pdfsArrayList = pdfsArrayList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPdfFav.HolderPdfFav {
//   bind  && inflate layout row_xml
        binding= RowPdfFavBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderPdfFav(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfsArrayList.size
    }

    override fun onBindViewHolder(holder: AdapterPdfFav.HolderPdfFav, position: Int) {
//        get data, set data ,handle clicks

//        get data
        val model=pdfsArrayList[position]
        loadPdfDetails(model,holder)

        //handle click , open pdf details page ,pass pdf id to load details
        holder.itemView.setOnClickListener {
            val intent= Intent(context, PdfDetailsActivity::class.java)
            intent.putExtra("pdfId",model.id)
            context.startActivity(intent)
        }
        holder.removeFavBtn.setOnClickListener {
            MainActivity.removeFromFav(context, model.id)
        }


    }

    private fun loadPdfDetails(model: ModelPdf, holder: AdapterPdfFav.HolderPdfFav) {
        val pdfId=model.id
        val ref= FirebaseDatabase.getInstance().getReference("Pdfs")
        ref.child(pdfId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId="${snapshot.child("categoryId").value}"
                    val description="${snapshot.child("description").value}"
                    val timestamp="${snapshot.child("timestamp").value}"
                    val title="${snapshot.child("title").value}"
                    val uid="${snapshot.child("uid").value}"
                    val url="${snapshot.child("url").value}"

//                        set data to model
                    model.isBookmark=true
                    model.title=title
                    model.description=description
                    model.categoryId=categoryId
                    model.timestamp=timestamp.toLong()
                    model.uid=uid
                    model.url=url

                    val formattedDate=MainActivity.formatTimeStamp(timestamp.toLong())


//        load further details like category ,pdf from Url,pdf size
                    MainActivity.loadCategory(categoryId, holder.categoryTv)

//        we don't need page number here,pass null for page number \\ load pdf thumbnails
                    MainActivity.loadPdfFromUrlSinglePage(
                        url,
                        title,
                        holder.pdfView,
                        holder.progressBar,
                        null
                    )

//        load pdf size
                    MainActivity.loadPdfSize(url, title, holder.sizeTv)

                    //        set data

                    holder.titleTv.text=title
                    holder.descriptionTv.text=description
                    holder.dateTv.text=formattedDate

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    //    viewHolder Class for row_pdf_fav
    inner class HolderPdfFav(itemView: View): RecyclerView.ViewHolder(itemView){
        //        ui views of row_pdf_admin
        val pdfView=binding.pdfView
        val progressBar=binding.ProgressBar
        val titleTv=binding.titleTv
        val descriptionTv=binding.descriptionTv
        val categoryTv=binding.categoryTv
        val sizeTv=binding.sizeTv
        val dateTv=binding.dateTv
        val removeFavBtn=binding.favoriteBtn
    }


}