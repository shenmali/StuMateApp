package com.shenmali.stumateapp.ui.auth.resetpassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.shenmali.stumateapp.R
import com.shenmali.stumateapp.data.model.credentials.ResetPasswordCredentials
import com.shenmali.stumateapp.data.model.validation.ResetPasswordValidator
import com.shenmali.stumateapp.data.source.auth.AuthRepository
import com.shenmali.stumateapp.databinding.ActivityResetPasswordBinding
import com.shenmali.stumateapp.util.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {

    @Inject
    lateinit var validator: ResetPasswordValidator
    @Inject
    lateinit var authRepository: AuthRepository
    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonResetPassword.setOnClickListener {
            try {
                val email = binding.textInputEditTextEmail.text.toString()
                val credentials = ResetPasswordCredentials(email)
                validator.validate(credentials)
                sendVerificationCode(credentials)
            } catch (e: Exception) {
                snackbar(e.message.toString(), isError = true)
            }
        }
    }


    private fun sendVerificationCode(credentials: ResetPasswordCredentials) {
        lifecycleScope.launch {
            binding.apply {
                buttonResetPassword.isEnabled = false
                buttonResetPassword.text = ""
                progressBar.show()
            }
            try {
                authRepository.sendResetPasswordEmail(credentials.email)
                binding.buttonResetPassword.text = getString(R.string.send_verification_email)
                snackbar("Verification code sent. Please check your e-mail.")
            } catch (e: CancellationException) {
                // ignore
            } catch (e: Exception) {
                e.printStackTrace()
                binding.buttonResetPassword.text = getString(R.string.send_verification_email)
                snackbar(e.message.toString(), isError = true)
            }
            binding.apply {
                buttonResetPassword.isEnabled = true
                progressBar.hide()
            }
        }
    }

}