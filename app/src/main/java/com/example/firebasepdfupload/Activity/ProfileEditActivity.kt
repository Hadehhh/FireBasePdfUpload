package com.example.firebasepdfupload.Activity

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityProfileEditBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

private lateinit var  binding:  ActivityProfileEditBinding
private lateinit var firebaseAuth: FirebaseAuth

private var imageUri: Uri?=null
private lateinit var mProgressDialog: Dialog

class ProfileEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()
        loadUserInfo()
//        goBack
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.profileTv.setOnClickListener {
            showImageAttachMenu()
        }
        binding.updateBtn.setOnClickListener {
            validateData()
        }


    }

    private var name=""
    private fun validateData() {
//        get data
        name=binding.nameEt.text.toString().trim()
        if(name.isEmpty()){
            Toast.makeText(this,"Masukkan nama", Toast.LENGTH_SHORT).show()
        }
        else
        {
            if(imageUri==null)
            {
//                update without image
                updateProfile("")
            }
            else
            {
//                need to update with image
                uploadImage()
            }
        }

    }

    private fun uploadImage() {
        showProgressDialog("Mengunggah foto profile")
//        image path and name , use uid to replace the previous name
        val filePathAndName="ProfileImages/"+ firebaseAuth.uid
//        storage ref
        val ref= FirebaseStorage.getInstance().getReference(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnSuccessListener {taskSnapshot->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl="${uriTask.result}"
                updateProfile(uploadedImageUrl)
                hideProgressDialog()

            }
            .addOnFailureListener{
                hideProgressDialog()
                Toast.makeText(this,"Failed to upload image due to${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun updateProfile(uploadedImageUrl: String) {
//        setup info to update to db
        val hashMap:HashMap<String,Any> =HashMap()
        hashMap["name"]= name
        if(imageUri!=null)
        {
            hashMap["profileImage"]=uploadedImageUrl
        }
//        update to db
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
//                profile updated
                Toast.makeText(this,"Profile berhasil diperbarui", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                hideProgressDialog()
                Toast.makeText(this,"Failed to update profile due to${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun loadUserInfo() {
        //        db ref to load user info
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
//                    get user info
                    val name="${snapshot.child("name").value}"
                    val profilePic="${snapshot.child("profileImage").value}"
                    val timestamp="${snapshot.child("timestamp").value}"
//                    set data
                    binding.nameEt.setText(name)
//                    set image
                    try{
                        Glide.with(this@ProfileEditActivity)
                            .load(profilePic)
                            .placeholder(R.drawable.person)
                            .into(binding.profileTv)
                    }
                    catch (e:Exception){
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    private  fun showImageAttachMenu(){
//        show popup menu with options Camera,Gallery to pick image
//        setup popup menu

        val popupMenu= PopupMenu(this, binding.profileTv)
        popupMenu.menu.add(Menu.NONE,0,0,"Camera")
        popupMenu.menu.add(Menu.NONE,1,1,"Gallery")
        popupMenu.show()

//        handle popup menu item click
        popupMenu.setOnMenuItemClickListener { item->
            val id=item.itemId
            if(id==0)
            {
//                camera clicked
                pickImageCamera()
            }
            else if(id==1)
            {
//                gallery clicked
                pickImageGallery()
            }
            true
        }
    }

    private fun  pickImageCamera(){
//        intent to pick image from camera
        val values= ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Description")

        imageUri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)

        val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)


    }

    private fun pickImageGallery() {
        val intent= Intent(Intent.ACTION_PICK)
        intent.type="image/*"
        galleryActivityResultLauncher.launch(intent)

    }

    //    used to handle result of camera intent(new way in  replacement of startactivityforresults)
    private val cameraActivityResultLauncher=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result->
//            get uri of pick image
            if(result.resultCode== Activity.RESULT_OK)
            {
                val data=result.data
//                imageUri=data!!.data   no need of image uri in camera case because it is already present
//                set to imageView
                binding.profileTv.setImageURI(imageUri)
            }
            else
            {
                Toast.makeText(this,"Dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }
    )
    private val galleryActivityResultLauncher=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult>{ result->
            if(result.resultCode== Activity.RESULT_OK)
            {
                val data=result.data
                imageUri=data!!.data
//                set to imageView
                binding.profileTv.setImageURI(imageUri)
            }
            else
            {
                Toast.makeText(this,"Dibatalkan", Toast.LENGTH_SHORT).show()
            }

        }
    )

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