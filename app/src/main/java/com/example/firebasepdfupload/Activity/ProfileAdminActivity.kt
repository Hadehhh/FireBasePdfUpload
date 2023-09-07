package com.example.firebasepdfupload.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.firebasepdfupload.Adapter.AdapterPdfFav
import com.example.firebasepdfupload.Model.ModelPdf
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityProfileAdminBinding
import com.example.firebasepdfupload.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileAdminBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var pdfsArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfFav: AdapterPdfFav

    //    firebase current user
    private lateinit var firebaseUser: FirebaseUser



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        reset to default values
        binding.memberDateTv.text = "N/A"

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!
        loadUserInfo()

        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            Toast.makeText(this, "Berhasil Keluar", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@ProfileAdminActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.profileEdit.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
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
                        Glide.with(this@ProfileAdminActivity)
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
}