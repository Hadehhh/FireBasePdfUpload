package com.example.firebasepdfupload.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasepdfupload.Activity.MainActivity
import com.example.firebasepdfupload.Activity.PdfDetailsActivity
import com.example.firebasepdfupload.Filters.FilterSearch
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.databinding.RowSearchBinding

class AdapterSearch : RecyclerView.Adapter<AdapterSearch.HolderSearch>, Filterable {
    //    context , get using constructor
    private  var context: Context
    //    arraylist to holdPdf,get using constructor
    public var pdfArrayList: ArrayList<ModelPdf>

    //    array list to hold filtered pdfs
    public var filterList:ArrayList<ModelPdf>

    private lateinit var binding: RowSearchBinding

    private var filter: FilterSearch?=null

    private lateinit var dataList: List<ModelPdf>


//    now we will create a filter class to enable searching

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList=pdfArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSearch {
        binding= RowSearchBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderSearch(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun getFilter(): Filter {
        if(filter==null)
        {
            filter= FilterSearch(filterList,this)
        }
        return filter as FilterSearch
    }

    override fun onBindViewHolder(holder: HolderSearch, position: Int) {
//        get data ,set data ,handle click

//        get data
        val model=pdfArrayList[position]
        val pdfId=model.id
        val categoryId=model.categoryId
        val title=model.title
        val description=model.description
        val uid=model.uid
        val url=model.url
        val timestamp=model.timestamp

//        convert time
        val date= MainActivity.formatTimeStamp(timestamp)

//        set data
        holder.titleTv.text=title
        holder.descriptionTv.text=description
        holder.dateTv.text=date

        MainActivity.loadCategory(categoryId, holder.categoryTv)
        MainActivity.loadPdfFromUrlSinglePage(url, title, holder.pdfView, holder.progressBar, null)
//        no need number of pages so pass null
        MainActivity.loadPdfSize(url, title, holder.sizeTv)

        holder.itemView.setOnClickListener {
//            pass pdfId in intent that will be used to get pdf info
            val intent= Intent(context, PdfDetailsActivity::class.java)
            intent.putExtra("pdfId",pdfId)
            context.startActivity(intent)
        }
    }

    //    ViewHolder class row _pdf_user
    inner class HolderSearch(itemView: View): RecyclerView.ViewHolder(itemView){
        //    init ui components of row_pdf_user.xml
        var pdfView=binding.pdfViewSearch
        var progressBar=binding.ProgressBar
        var titleTv=binding.tvTitle
        var descriptionTv=binding.descriptionTv
        var categoryTv=binding.tvCategory
        var sizeTv=binding.tvSize
        var dateTv=binding.tvDate
    }
}