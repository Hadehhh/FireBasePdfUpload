package com.example.firebasepdfupload.Filters

import android.annotation.SuppressLint
import android.widget.Filter
import com.example.firebasepdfupload.Adapter.AdapterPdfAdmin
import com.example.firebasepdfupload.Model.ModelPdf

class FilterPdfAdmin: Filter {
    //    arrayList in which we want to search
    var filterList:ArrayList<ModelPdf>
    //    adapter in which filter need to be implemented
    var adapterPdfAdmin: AdapterPdfAdmin

    //    constructor
    constructor(filterList: ArrayList<ModelPdf>, adapterPdfAdmin: AdapterPdfAdmin) {
        this.filterList = filterList
        this.adapterPdfAdmin = adapterPdfAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence? = constraint  //value to search
        val results= FilterResults()
//        value should not be null and not empty
        if(constraint!=null && constraint.isNotEmpty())
        {
//            change to uppercase or lowercase to avoid case sensitivity
            constraint=constraint.toString().lowercase()
            var filterModels= ArrayList<ModelPdf>()
            for (i in filterList.indices)
            {
//                validate if match
                if(filterList[i].title.lowercase().contains(constraint))
                {
//                    searched value is similar to value in list,add to filtered List
                    filterModels.add(filterList[i])
                }
            }
            results.count=filterModels.size
            results.values=filterModels

        }
        else
        {
//            searched value is null or empty
            results.count=filterList.size
            results.values=filterList
        }
        return results

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
//        apply filter changes
        adapterPdfAdmin.pdfArrayList= results.values as ArrayList<ModelPdf>

//        notify changes
        adapterPdfAdmin.notifyDataSetChanged()
    }
}