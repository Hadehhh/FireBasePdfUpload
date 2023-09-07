package com.example.firebasepdfupload.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.example.firebasepdfupload.R
import com.example.firebasepdfupload.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    companion object
    {
        lateinit var auth: FirebaseAuth

    }
    private lateinit var binding:ActivityRegisterBinding
    private lateinit var mProgressDialog: Dialog
    //buttons
    private var bRegister: Button?=null
    //edit text
    private var etFirstName: EditText?=null
    private var etLastName: EditText?=null
    var etEmail: EditText?=null
    var etPassword: EditText?=null
    var etConfPassword: EditText?=null
    var isAllEditTextCheck=false
    var name=""
    var email=""
    var password=""

    private lateinit var textlogin : TextView
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN
        etEmail=binding.emailET
        etFirstName=binding.firstNameET
        etLastName=binding.lastNameET
        etPassword=binding.passwordET
        etConfPassword=binding.confirmPasswordET
        bRegister=binding.btnSignUp

        bRegister?.setOnClickListener { registerUser() }

        textlogin = findViewById(R.id.tvLogin)
        textlogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
    private fun registerUser()
    {
        email= etEmail?.text.toString().trim()
        password=etPassword?.text.toString().trim()
        name=etFirstName?.text.toString().trim()+" " +etLastName?.text.toString().trim()
        isAllEditTextCheck=checkAllEditText()
        if(isAllEditTextCheck){
            auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    updateUserInfo()
                }
                .addOnFailureListener{
                    hideProgressDialog()
                    Toast.makeText(this,"Gagal membuat akun", Toast.LENGTH_SHORT).show()
                }
        }
        else
        {
            Toast.makeText(this,"Pendaftaran Gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEmail(text: Editable?): Boolean {
        val email: CharSequence = text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun checkAllEditText(): Boolean {
        if(etFirstName?.length()  ==0)
        {
            etFirstName?.error = "Nama depan harus di-isi"
            return false;
        }
        if(etLastName?.length()==0)
        {
            etLastName?.error="Nama belakang harus di-isi"
            return false;
        }
        if(etEmail?.length()==0)
        {
            etEmail?.error="Alamat Email harus di-isi"
            return false;
        }
        if(!isEmail(etEmail?.editableText))
        {
            etEmail?.error="Alamat email tidak valid"
            return false
        }
        if (etPassword!!.length() == 0) {
            etPassword!!.error = "Kata sandi tidak boleh kosong"
            return false
        } else if (etPassword!!.length() < 8) {
            etPassword!!.error = "Kata sandi minimal 8 karakter"
            return false
        }
        if(etConfPassword!!.length()==0)
        {
            etConfPassword!!.error="Konfirmasi kata sandi anda"
            return false
        }
        if(!etConfPassword?.equals(etConfPassword)!!)
        {
            etConfPassword!!.error="Kata sandi tidak cocok"
            return false
        }
        return true
    }
    private fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
    private  fun updateUserInfo(){
//        save user info in firebase realtime database
//        showProgressDialog(resources.getString(R.string.Saving_User_Info))
        val timestamp=System.currentTimeMillis()
        val uid= auth.uid
        val hashMap:HashMap<String,Any?> = HashMap()
        hashMap["uid"]=uid
        hashMap["email"]=email
        hashMap["name"]=name
        hashMap["profileImage"]=" "//add empty,will do in profile edit
        hashMap["userType"]="user"
        hashMap["timestamp"]=timestamp
//        set data to db
        val ref= FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
//                user info saved dashboard open
                hideProgressDialog()
                Toast.makeText(this,"Akun berhasil dibuat", Toast.LENGTH_SHORT).show()
//                val intent= Intent(this@RegisterActivity, DashBoardUserActivity::class.java)
                startActivity(intent)
                finish()

            }
            .addOnFailureListener{
//                failed adding data to db
                hideProgressDialog()
                Toast.makeText(this,"Gagal menyimpan user info", Toast.LENGTH_SHORT).show()
            }

    }
}