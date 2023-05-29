package com.shenmali.stumateapp.data.model.validation

import android.util.Patterns
import com.shenmali.stumateapp.data.model.credentials.ResetPasswordCredentials
import javax.inject.Inject

class ResetPasswordValidator @Inject constructor() : Validator<ResetPasswordCredentials> {

    override fun validate(args: ResetPasswordCredentials) {
        if(args.email.isEmpty()) {
            error("E-Mail cannot be empty")
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(args.email).matches()) {
            error("Invalid E-Mail")
        }
    }

}