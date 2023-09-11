package com.example.firebasepdfupload

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebasepdfupload.Adapter.AdapterPdfUser
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.databinding.FragmentPdfBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfFragment : Fragment {
    private lateinit var binding: FragmentPdfBinding

    public companion object{
        private const val TAG="PDFS_USER_TAG"
        //        receive  data from activity to load books ex: categoryId, category ,uid
        public fun newInstance(categoryId:String,category:String,uid:String):PdfFragment{
            val fragment = PdfFragment()
//    put data to bundle intent
            val args=Bundle()
            args.putString("categoryId",categoryId)
            args.putString("category",category)
            args.putString("uid",uid)
            fragment.arguments=args
            return fragment
        }
    }

    private var categoryId=""
    private var category=""
    private var uid=""

    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args=arguments
        if(args!=null)
        {
            categoryId=args.getString("categoryId")!!
            category=args.getString("category")!!
            uid=args.getString("uid")!!
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?, ): View? {
        // Inflate the layout for this fragment
        binding=FragmentPdfBinding.inflate(LayoutInflater.from(context),container,false)
//        load  pdf according to category , this fragment will have new instance to load each
//        category pdfs
        Log.d(TAG,"onCreateView: $category")
        if(category=="Lihat Semua")
        {
//            load all boo
            loadAllBooks()
        }
        else{
//            load selected category books
            loadCategorizedBooks()
        }
        return binding.root
    }

    private fun loadAllBooks() {
//        init list
        pdfArrayList= ArrayList()
        val ref= FirebaseDatabase.getInstance().getReference("Pdfs")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                clear list before starting adding data into it
                pdfArrayList.clear()
                for(i in snapshot.children){
//                    get data
                    val model=i.getValue(ModelPdf::class.java)
//                    add to list
                    pdfArrayList.add(model!!)
                }
//                setup adapter
                adapterPdfUser= AdapterPdfUser(context!!,pdfArrayList)
//                set adapter to recyclerView
                binding.pdfsRv.adapter=adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun loadCategorizedBooks() {
        //        init list
        pdfArrayList= ArrayList()
        val ref= FirebaseDatabase.getInstance().getReference("Pdfs")
        ref.orderByChild("categoryId").equalTo(categoryId)  // load most viewed or most downloaded books.   orderBy=""
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
//                clear list before starting adding data into it
                    pdfArrayList.clear()
                    for(i in snapshot.children){
//                    get data
                        val model=i.getValue(ModelPdf::class.java)
//                    add to list
                        pdfArrayList.add(model!!)
                    }
//                setup adapter
                    adapterPdfUser= AdapterPdfUser(context!!,pdfArrayList)
//                set adapter to recyclerView
                    binding.pdfsRv.adapter=adapterPdfUser
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}