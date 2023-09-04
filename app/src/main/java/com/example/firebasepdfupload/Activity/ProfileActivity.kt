package com.example.firebasepdfupload.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
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
    private lateinit var mProgressDialog: Dialog

    //    firebase current user
    private lateinit var firebaseUser: FirebaseUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        reset to default values
//        binding.accountTypeTv.text="N/A"
        binding.memberDateTv.text="N/A"
        binding.favouritePdfscountTv.text="N/A"
//        binding.accountStatustv.text="N/A"

        firebaseAuth= FirebaseAuth.getInstance()
        firebaseUser=firebaseAuth.currentUser!!
        loadUserInfo()
        loadFavPdfs()

//        binding
//        binding.backBtn.setOnClickListener {
//            onBackPressed()
//        }
        binding.btnLogout.setOnClickListener{
            firebaseAuth.signOut()
            Toast.makeText(this,"Logout Successfully",Toast.LENGTH_SHORT).show()
            val intent=Intent(this@ProfileActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.profileEdit.setOnClickListener {
            val intent= Intent(this,ProfileEditActivity::class.java)
            startActivity(intent)
        }

//        handle click ,verify user if not
//        binding.accountStatustv.setOnClickListener {
//            if(firebaseUser.isEmailVerified)
//            {
//                Toast.makeText(this,"Already verified..", Toast.LENGTH_SHORT).show()
//            }
//            else
//            {
//                emailVerificationDialog()
//            }
//        }

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

    private fun emailVerificationDialog() {
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Verify Email")
            .setMessage("Are you sure you want to send email verification to your email ${firebaseUser.email}")
            .setPositiveButton("SEND"){d,e->
                sendEmailVerification()
            }
            .setNegativeButton("CANCEL"){d,e->
                d.dismiss()
            }.show()
    }

    private fun sendEmailVerification() {
        showProgressDialog("Sending email verification instructions to email ${firebaseUser.email}")
        firebaseUser.sendEmailVerification()
            .addOnSuccessListener {
                hideProgressDialog()
                Toast.makeText(this,"Instructions sent! check your email ${firebaseUser.email}",
                    Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                hideProgressDialog()
                Toast.makeText(this,"Failed to send ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    @SuppressLint("SetTextI18n")
    private fun loadUserInfo() {

//        check if user is verified or not

//        if(firebaseUser.isEmailVerified)
//        {
//            binding.accountStatustv.text="Verified"
//        }
//        else
//        {
//            binding.accountStatustv.text=" Not Verified"
//        }
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
                    val userType="${snapshot.child("userType").value}"

//                    convert timestamp to proper date format
                    val formattedDate=MainActivity.formatTimeStamp(timestamp.toLong())
//                    set data
                    binding.nameTv.text=name
                    binding.emailTv.text=email
                    binding.memberDateTv.text=formattedDate
//                    binding.accountTypeTv.text=userType

//                    set image
                    try{
                        Glide.with(this@ProfileActivity)
                            .load(profilePic)
                            .placeholder(R.drawable.logo)
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
        ref.child(firebaseAuth.uid!!).child("Favourites")
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
                    binding.favouritePdfscountTv.text="${pdfsArrayList.size}"
                    adapterPdfFav=AdapterPdfFav(this@ProfileActivity,pdfsArrayList)
//                    binding.favRv.adapter=adapterPdfFav
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    private fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.progress_indicator)

        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    private fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}