package com.example.firebasepdfupload.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.firebasepdfupload.Adapter.AdapterPdfFav
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pdfsArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfFav: AdapterPdfFav

    //    firebase current user
    private lateinit var firebaseUser: FirebaseUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        reset to default values
        binding.memberDateTv.text="N/A"
        binding.bookmarkPdfscountTv.text="N/A"

        firebaseAuth= FirebaseAuth.getInstance()
        firebaseUser=firebaseAuth.currentUser!!
        loadUserInfo()
        loadFavPdfs()

        binding.btnLogout.setOnClickListener{
            firebaseAuth.signOut()
            Toast.makeText(this,"Berhasil Keluar",Toast.LENGTH_SHORT).show()
            val intent=Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
        binding.profileEdit.setOnClickListener {
            val intent= Intent(this,ProfileEditActivity::class.java)
            startActivity(intent)
        }

        binding.bottomNavigation.menu.findItem(R.id.account).isChecked = true
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
    }

    private fun loadUserInfo() {

//        db ref to load user info
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
//                    get user info
                    val email="${snapshot.child("email").value}"
                    val name="${snapshot.child("name").value}"
                    val profilePic="${snapshot.child("profileImage").value}"
                    val timestamp="${snapshot.child("timestamp").value}"
                    val uid="${snapshot.child("uid").value}"

//                    convert timestamp to proper date format
                    val formattedDate=MainActivity.formatTimeStamp(timestamp.toLong())
//                    set data
                    binding.nameTv.text=name
                    binding.emailTv.text=email
                    binding.memberDateTv.text=formattedDate

//                    set image
                    try{
                        Glide.with(this@ProfileActivity)
                            .load(profilePic)
                            .placeholder(R.drawable.person)
                            .into(binding.profileImage)
                    }
                    catch (e:Exception){
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    private fun loadFavPdfs(){
//        init arraylist
        pdfsArrayList=ArrayList()
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Bookmarks")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
//                    clear arraylist before adding
                    pdfsArrayList.clear()
                    for(ds in snapshot.children){
//                        get only id of the pdfs
//                        rest of info will load in adapter class
                        val pdfId="${ds.child("pdfId").value}"
//                        set to model
                        val modelPdf=ModelPdf()
                        modelPdf.id=pdfId

//                        add model to list
                        pdfsArrayList.add(modelPdf)
                    }
                    binding.bookmarkPdfscountTv.text="${pdfsArrayList.size}"
                    adapterPdfFav=AdapterPdfFav(this@ProfileActivity,pdfsArrayList)
//                    binding.favRv.adapter=adapterPdfFav
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}