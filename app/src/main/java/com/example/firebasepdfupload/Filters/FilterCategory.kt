package com.example.firebasepdfupload.Filters

import android.widget.Filter
import com.example.firebasepdfupload.Adapter.AdapterCategory
import com.example.firebasepdfupload.Model.ModelCategory

class FilterCategory: Filter {

    //    arraylist in which we want to search
    private var filterList:ArrayList<ModelCategory>

    //    adapter in which filter need to be implemented
    private var adapterCategory: AdapterCategory

    //    constructor
    constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) : super() {
        this.filterList = filterList
        this.adapterCategory = adapterCategory

    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint=constraint
        val results= FilterResults()

//        value shouldn't be null and not empty
        if(constraint!=null && constraint.isNotEmpty())
        {
//            searched value is nor null not empty
//            change to uppercase or lowercase to avoid case sensitive
            constraint=constraint.toString().uppercase()
            val filteredModels:ArrayList<ModelCategory> = ArrayList()
            for(i in 0 until filterList.size)
            {
//                validate
                if(filterList[i].category.uppercase().contains(constraint))
                {
                    filteredModels.add(filterList[i])
                }
            }
            results.count=filteredModels.size
            results.values=filteredModels
        }
        else
        {
//            search value is either null or empty
            results.count=filterList.size;
            results.values=filterList
        }
//        don't mis it
        return results
    }
    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
//        apply filter changes
        adapterCategory.categoryArrayList=results.values as ArrayList<ModelCategory>
//        notify changes
        adapterCategory.notifyDataSetChanged()
    }

}