package com.example.firebasepdfupload.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasepdfupload.Activity.PdfListAdminActivity
import com.example.firebasepdfupload.Filters.FilterCategory
import com.example.firebasepdfupload.Model.ModelCategory
import com.example.firebasepdfupload.databinding.RowCategoriesBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory: RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable {
    private lateinit var binding: RowCategoriesBinding
    private val context: Context
    public var categoryArrayList:ArrayList<ModelCategory>
    private var filterList:ArrayList<ModelCategory>
    private  var filter: FilterCategory?=null


    //     Secondary constructor
    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList=categoryArrayList
    }



    //    ViewHolder class to hold /init UI views for rowCategory_rv
    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView){
        //        init ui views
        var categoryTv: TextView =binding.categoryTv
        var deleteBtn: ImageButton =binding.deleteBtn

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
//        inflate/bind row_category_xml
        binding= RowCategoriesBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderCategory(binding.root)
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
//        get data ,   Set Data , Handle Clicks

//        get Data
        val model=categoryArrayList[position]
        val categoryId=model.categoryId
        val category=model.category
        val uid=model.uid
        val timestamp=model.timestamp

//        set data

        holder.categoryTv.text=category

//        handle click ,delete category
        holder.deleteBtn.setOnClickListener{
//            confirm before delete
            val builder= AlertDialog.Builder(context)
            builder.setTitle("Hapus")
                .setMessage("Yakin ingin menghapus kategori ini?")
                .setPositiveButton("Ya"){a,d->
                    Toast.makeText(context,"Menghapus...", Toast.LENGTH_SHORT).show()
                    deleteCategory(model,holder)
                }
                .setNegativeButton("Tidak"){a,d->
                    a.dismiss()

                }
                .show()
        }

//        handle click ,start pdf list admin activity ,also pass pdf id ,title
        holder.itemView.setOnClickListener {
            val intent= Intent(context, PdfListAdminActivity::class.java)
            intent.putExtra("categoryId",categoryId)
            intent.putExtra("category",category)
            context.startActivity(intent)
        }
    }

    private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
//        get id of category to delete
        val categoryId=model.categoryId
//        Firebase DB  > Categories > categoryId
        val ref= FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(categoryId)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context,"Berhasil dihapus..", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(context,"Unable to delete due to ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    override fun getFilter(): Filter {
        if(filter==null)
        {
            filter= FilterCategory(filterList,this)

        }
        return filterList as FilterCategory
    }
}