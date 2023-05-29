package com.shenmali.stumateapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.shenmali.stumateapp.data.source.auth.AuthRepository
import com.shenmali.stumateapp.databinding.ActivityMainBinding
import com.shenmali.stumateapp.ui.auth.login.LoginActivity
import com.shenmali.stumateapp.ui.auth.signup.SignupActivity
import com.shenmali.stumateapp.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authRepository: AuthRepository
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check if user is logged in
        if (authRepository.getCurrentStudentId() != null) {
            Intent(this, HomeActivity::class.java).apply {
                startActivity(this)
            }
            finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            Intent(this, LoginActivity::class.java).apply {
                startActivity(this)
            }
        }

        binding.buttonSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }


}