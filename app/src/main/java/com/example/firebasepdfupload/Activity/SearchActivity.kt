package com.example.firebasepdfupload.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.firebasepdfupload.Adapter.AdapterPdfAdmin
import com.example.firebasepdfupload.Adapter.AdapterPdfUser
import com.example.firebasepdfupload.Adapter.AdapterSearch
import com.example.firebasepdfupload.Model.ModelCategory
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.PdfFragment
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityMainBinding
import com.example.firebasepdfupload.databinding.ActivityPdfListAdminBinding
import com.example.firebasepdfupload.databinding.ActivitySearchBinding
import com.google.android.material.search.SearchBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private companion object {
        const val TAG = "PDF_SEARCH_USER_TAG"
    }

    private lateinit var binding: ActivitySearchBinding

    //    arrayList to hold pdfs
    private lateinit var pdfArrayList: ArrayList<ModelPdf>

    //    adapter
    private lateinit var adapterPdfUser: AdapterPdfUser
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var viewPagerAdapter: ViewPagerAdapter


    //    category id,title
    private var categoryId = ""
    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpWithViewPagerAdapter(binding.vpSearch)
        //        get from intent ,that we passes from adapter
        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!


        binding.bottomNavigation.menu.findItem(R.id.search).setChecked(true)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.beranda -> {
                    // Respond to navigation item 1 click
                    val intent = Intent(this, HomeUser::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }

                R.id.bookmark -> {
                    // Respond to navigation item 2 click
                    val intent = Intent(this, BookmarkActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }

                R.id.search -> {
                    // Respond to navigation item 2 click
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }

                R.id.account -> {
                    // Respond to navigation item 2 click
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }

                else -> return@setOnItemSelectedListener false
            }
        }
        binding.bottomNavigation.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.beranda -> {
                    // Respond to navigation item 1 reselection
                }

                R.id.bookmark -> {
                    // Respond to navigation item 2 reselection
                }

                R.id.search -> {
                    // Respond to navigation item 2 reselection
                }

                R.id.account -> {
                    // Respond to navigation item 2 reselection
                }
            }
        }
//        load pdf
//        loadPdfList()

//        search
        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                filter data
                try {
                    adapterPdfUser.filter!!.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged:${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    @SuppressLint("SuspiciousIndentation")
    private fun setUpWithViewPagerAdapter(viewPager: ViewPager) {
        viewPagerAdapter = ViewPagerAdapter(
            supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this
        )
//        init list
        categoryArrayList = ArrayList()
//        load categories from db
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                    clear list
                categoryArrayList.clear()
//                    load some static categories ex: all,most viewed , most downloaded
//                    add data to models
                val modelAll = ModelCategory("01", "All", 1, "")

//                    add to list
                categoryArrayList.add(modelAll)
//                    add to ViewPagerAdapter

                viewPagerAdapter.addFragment(
                    PdfFragment.newInstance(
                        "${modelAll.id}",
                        "${modelAll.category}",
                        "${modelAll.uid}"
                    ), modelAll.category
                )

                viewPagerAdapter.notifyDataSetChanged()
//                    now load from firebase db
                for (i in snapshot.children) {
//                        get data in model
                    val model = i.getValue(ModelCategory::class.java)
//                        add to list
                    categoryArrayList.add(model!!)
//                        add to viewPagerAdapter
                    viewPagerAdapter.addFragment(
                        PdfFragment.newInstance(
                            "${model.id}",
                            "${model.category}",
                            "${model.uid}"
                        ), model.category

                    )
//                        refresh List
                    viewPagerAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
//        setup adapter to viewPager
        viewPager.adapter = viewPagerAdapter
    }

    class ViewPagerAdapter(fm: FragmentManager, behavior: Int, context: Context) :
        FragmentPagerAdapter(fm, behavior) {
        //        holds list of fragments that is new instances of same fragment for each category
        private val fragmentList: ArrayList<PdfFragment> = ArrayList()

        //        list of titles of categories ,for tabs
        private val fragmentTitleList: ArrayList<String> = ArrayList()
        private val context: Context

        init {
            this.context = context
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList[position]
        }

        public fun addFragment(fragment: PdfFragment, title: String) {
//            add fragment that will be passed as parameter in fragmentList
            fragmentList.add(fragment)
//            add title that will be passed as parameter
            fragmentTitleList.add(title)
        }
    }
}
    /*@SuppressLint("SuspiciousIndentation")
    private fun setUpWithViewPagerAdapter(viewPager: ViewPager){
        viewPagerAdapter= ViewPagerAdapter(supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this)
//        init list
        categoryArrayList= ArrayList()
//        load categories from db
        val ref= FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                    clear list
                categoryArrayList.clear()
//                    load some static categories ex: all,most viewed , most downloaded
//                    add data to models
                val modelAll= ModelCategory("01","All",1,"")

//                    add to list
                categoryArrayList.add(modelAll)

//                    add to ViewPagerAdapter

                viewPagerAdapter.addFragment(
                    PdfFragment.newInstance(
                        "${modelAll.id}",
                        "${modelAll.category}",
                        "${modelAll.uid}"
                    ),modelAll.category
                )

//                    refresh list

                viewPagerAdapter.notifyDataSetChanged()
//                    now load from firebase db
                for( i in snapshot.children){
//                        get data in model
                    val model = i.getValue(ModelCategory::class.java)
//                        add to list
                    categoryArrayList.add(model!!)
//                        add to viewPagerAdapter
                    viewPagerAdapter.addFragment(
                        PdfFragment.newInstance(
                            "${model.id}",
                            "${model.category}",
                            "${model.uid}"
                        ),model.category

                    )
//                        refresh List
                    viewPagerAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
//        setup adapter to viewPager
        viewPager.adapter=viewPagerAdapter
    }
    class ViewPagerAdapter(fm: FragmentManager, behavior: Int, context: Context):
        FragmentPagerAdapter(fm,behavior){
        //        holds list of fragments that is new instances of same fragment for each category
        private val fragmentList:ArrayList<PdfFragment> = ArrayList()
        //        list of titles of categories ,for tabs
        private val fragmentTitleList:ArrayList<String> = ArrayList()
        private   val context: Context
        init {
            this.context=context
        }
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragmentTitleList[position]
        }

        public fun addFragment(fragment: PdfFragment, title:String){
//            add fragment that will be passed as parameter in fragmentList
            fragmentList.add(fragment)
//            add title that will be passed as parameter
            fragmentTitleList.add(title)
        }

    }
}
    /*private lateinit var binding: ActivitySearchBinding
    private lateinit var dataList: ArrayList<ModelPdf>
    private lateinit var adapter: AdapterSearch
    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gridLayoutManager = GridLayoutManager(this@SearchActivity, 1)
        binding.recyclerView.layoutManager = gridLayoutManager

        val builder = AlertDialog.Builder(this@SearchActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_indicator)
        val dialog = builder.create()
        dialog.show()

        dataList = ArrayList()
        adapter = AdapterSearch(this@SearchActivity, dataList)
        binding.recyclerView.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("Pdfs")
        dialog.show()

        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val dataClass = itemSnapshot.getValue(ModelPdf::class.java)
                    if (dataClass != null) {
                        dataList.add(dataClass)
                    }
                }
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                dialog.dismiss()
            }
        })
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchList(newText)
                return true
            }

        })
    }
    fun searchList(text:String){
        val searchList = ArrayList<ModelPdf>()
        for (dataClass in dataList){
            if (dataClass.title?.lowercase()?.contains(text.lowercase())==true){
                searchList.add(dataClass)
            }
        }
        adapter.searchDataList(searchList)
    }
}*/