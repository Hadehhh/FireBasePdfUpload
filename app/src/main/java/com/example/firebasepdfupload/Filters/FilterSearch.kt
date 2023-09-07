package com.example.firebasepdfupload.Filters

import android.annotation.SuppressLint
import android.widget.Filter
import com.example.firebasepdfupload.Adapter.AdapterPdfUser
import com.example.firebasepdfupload.Adapter.AdapterSearch
import com.example.firebasepdfupload.Model.ModelPdf

class FilterSearch: Filter {
    //    arraylist which we want to search
    var filterList:ArrayList<ModelPdf>
    //    adapter in which filter need to be implemented
    var adapterSearch: AdapterSearch

    constructor(filterList: ArrayList<ModelPdf>, adapterSearch: AdapterSearch):super() {
        this.filterList = filterList
        this.adapterSearch = adapterSearch
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint: CharSequence?=constraint
        val results = FilterResults()
//        value to be searched should not be null and not empty
        if(constraint!=null && constraint.isNotEmpty())
        {
//              not null nor empty
//            change to uppercase ,or lower to remove case sensitive
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
//            return filtered list and size
            results.count=filterModels.size
            results.values=filterModels
        }
        else
        {
//            searched value is null or empty
//            return original list and size
            results.count=filterList.size
            results.values=filterList
        }
        return results

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun publishResults(constraint: CharSequence?, results: FilterResults){
//        apply filter changes
        adapterSearch.pdfArrayList= results.values as ArrayList<ModelPdf>

//        notify changes
        adapterSearch.notifyDataSetChanged()

    }
//    constructor
}