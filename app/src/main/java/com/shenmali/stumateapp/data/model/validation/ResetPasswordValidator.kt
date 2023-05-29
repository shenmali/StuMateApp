package com.shenmali.stumateapp.data.model.validation

import android.util.Patterns
import com.shenmali.stumateapp.data.model.credentials.ResetPasswordCredentials
import javax.inject.Inject

class ResetPasswordValidator @Inject constructor() : Validator<ResetPasswordCredentials> {

    override fun validate(args: ResetPasswordCredentials) {
        if(args.email.isEmpty()) {
            error("E-Mail cannot be empty")
        }
        if (!args.email.endsWith("@std.yildiz.edu.tr")) {
            error("E-mail must end with @std.yildiz.edu.tr")
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(args.email).matches()) {
            error("Invalid E-Mail")
        }
    }

}