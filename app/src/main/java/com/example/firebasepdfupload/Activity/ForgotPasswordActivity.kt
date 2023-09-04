package com.example.firebasepdfupload.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.example.firebasepdfupload.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    var bBack: ImageView?=null
    var edtEmail: EditText?=null
    private var bSubmit: Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding= ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_FULLSCREEN
        firebaseAuth= FirebaseAuth.getInstance()
        bBack=binding.btnBack
        bSubmit=binding.btnKirim
        edtEmail=binding.resetEmailET

        bBack!!.setOnClickListener {
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        bSubmit!!.setOnClickListener {
            val email= edtEmail!!.text.toString().trim()
            if(checkEmail())
            {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener{task->
                        if(task.isSuccessful)
                        {
                            Toast.makeText(this@ForgotPasswordActivity,"Email pemulihan kata sandi telah terkirim, silahkan cek email anda",
                                Toast.LENGTH_LONG).show()
                            finish()
                        }
                        else{
                            Toast.makeText(this@ForgotPasswordActivity,task.exception!!.message.toString(),
                                Toast.LENGTH_LONG).show()
                        }
                    }
            }

        }

    }
    private fun isEmail(text: Editable?): Boolean {
        val email: CharSequence = text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun checkEmail():Boolean{
        if(edtEmail?.length()==0)
        {
            edtEmail?.error="Email tidak boleh kosong"
            return false
        }
        if(!isEmail(edtEmail?.editableText))
        {
            edtEmail?.error="Email tidak valid"
            return false
        }
        return true
    }

}