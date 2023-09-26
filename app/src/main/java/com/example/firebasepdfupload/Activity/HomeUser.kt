package com.example.firebasepdfupload.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.firebasepdfupload.Model.ModelCategory
import com.example.firebasepdfupload.PdfFragment
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityHomeUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeUser : AppCompatActivity() {
    private lateinit var binding: ActivityHomeUserBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth= FirebaseAuth.getInstance()
        setUpWithViewPagerAdapter(binding.vpPdf)
        binding.tabLayout.setupWithViewPager(binding.vpPdf)
        loadUserInfo()

        binding.userImage.setOnClickListener {
            val intent=Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigation.menu.findItem(R.id.beranda).setChecked(true)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.beranda -> {
                    // Respond to navigation item 1 click
                    val intent= Intent(this,HomeUser::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.bookmark -> {
                    // Respond to navigation item 2 click
                    val intent= Intent(this,BookmarkActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.search -> {
                    // Respond to navigation item 2 click
                    val intent= Intent(this,SearchActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.account -> {
                    // Respond to navigation item 2 click
                    val intent= Intent(this,ProfileActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
        binding.bottomNavigation.setOnItemReselectedListener { item ->
            when(item.itemId) {
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
    }

    private fun setUpWithViewPagerAdapter(viewPager: ViewPager){
        viewPagerAdapter= ViewPagerAdapter(supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this)

        categoryArrayList= ArrayList()
//      Load categories from db
        val ref= FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//              Clear list
                categoryArrayList.clear()

                val modelAll= ModelCategory("01","Lihat Semua",1,"")

                categoryArrayList.add(modelAll)

//              Menambahkan ke ViewPagerAdapter

                viewPagerAdapter.addFragment(
                    PdfFragment.newInstance(
                        "${modelAll.categoryId}",
                        "${modelAll.category}",
                        "${modelAll.uid}"
                    ),modelAll.category
                )

                viewPagerAdapter.notifyDataSetChanged()
//              Load from firebase db
                for( i in snapshot.children){
//                  Get data in model
                    val model = i.getValue(ModelCategory::class.java)
//                  Add to list
                    categoryArrayList.add(model!!)
//                  Add to viewPagerAdapter
                    viewPagerAdapter.addFragment(
                        PdfFragment.newInstance(
                            "${model.categoryId}",
                            "${model.category}",
                            "${model.uid}"
                        ),model.category

                    )
                    viewPagerAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
//      Setup adapter to viewPager
        viewPager.adapter=viewPagerAdapter
    }
    class ViewPagerAdapter(fm: FragmentManager, behavior: Int, context: Context):
        FragmentPagerAdapter(fm,behavior){
        //Holds list of fragments that is new instances of same fragment for each category
        private val fragmentList:ArrayList<PdfFragment> = ArrayList()
        //List of titles of categories ,for tabs
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
    private fun loadUserInfo() {

//      db ref to load user info
        val ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
//                  Get user info
                    val name="${snapshot.child("name").value}"
                    val profilePic="${snapshot.child("profileImage").value}"

//                  Set data
                    binding.nameTv.text=name

//                  Set image
                    try{
                        Glide.with(this@HomeUser)
                            .load(profilePic)
                            .placeholder(R.drawable.person)
                            .into(binding.userImage)
                    }
                    catch (e:Exception){
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}