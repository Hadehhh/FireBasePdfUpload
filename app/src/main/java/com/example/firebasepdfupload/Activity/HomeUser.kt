package com.example.firebasepdfupload.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.firebasepdfupload.Model.ModelCategory
import com.example.firebasepdfupload.PdfFragment
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityHomeUserBinding
import com.google.android.material.navigation.NavigationBarMenu
import com.google.android.material.navigation.NavigationBarView
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
    @SuppressLint("SuspiciousIndentation")
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
                val modelMostViewed= ModelCategory("01","Most Viewed",1,"")
                val modelMostDownload= ModelCategory("01","Most Downloaded",1,"")

//                    add to list
                categoryArrayList.add(modelAll)
                categoryArrayList.add(modelMostViewed)
                categoryArrayList.add(modelMostDownload)
//                    add to ViewPagerAdapter

                viewPagerAdapter.addFragment(
                    PdfFragment.newInstance(
                        "${modelAll.id}",
                        "${modelAll.category}",
                        "${modelAll.uid}"
                    ),modelAll.category
                )

                viewPagerAdapter.addFragment(
                    PdfFragment.newInstance(
                        "${modelMostViewed.id}",
                        "${modelMostViewed.category}",
                        "${modelMostViewed.uid}"
                    ),modelMostViewed.category
                )

                viewPagerAdapter.addFragment(
                    PdfFragment.newInstance(
                        "${modelMostDownload.id}",
                        "${modelMostDownload.category}",
                        "${modelMostDownload.uid}"
                    ),modelMostDownload.category
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
    /*@SuppressLint("SetTextI18n")

//    this activity can be opened with or without login ,so hide logout and profile btn

    private fun checkUser()
    {
        val firebaseUser=firebaseAuth.currentUser
        if(firebaseUser==null)
        {
//            not logged in  user can stay in user dashboard page  without login to
            binding.subTitleTv.text="Not Logged In"
//            hide profile and logout btn
            binding.profileBtn.visibility= View.GONE
            binding.powerImg.visibility=View.GONE


        }
        else{
//            logged in and show user info
            val email=firebaseUser.email
            binding.subTitleTv.text=email

            binding.profileBtn.visibility= View.VISIBLE
            binding.powerImg.visibility=View.VISIBLE


        }
    }*/
    private fun loadUserInfo() {

//        db ref to load user info
        val ref=FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
//                    get user info
                    val name="${snapshot.child("name").value}"
                    val profilePic="${snapshot.child("profileImage").value}"

//                    set data
                    binding.nameTv.text=name

//                    set image
                    try{
                        Glide.with(this@HomeUser)
                            .load(profilePic)
                            .placeholder(R.drawable.logo)
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