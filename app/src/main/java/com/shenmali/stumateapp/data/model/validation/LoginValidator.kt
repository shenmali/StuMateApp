package com.shenmali.stumateapp.data.model.validation

import android.util.Patterns
import com.shenmali.stumateapp.data.model.credentials.LoginCredentials
import javax.inject.Inject

class LoginValidator @Inject constructor() : Validator<LoginCredentials> {

    override fun validate(args: LoginCredentials) {
        if(args.email.isEmpty()) {
            error("E-Mail cannot be empty")
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(args.email).matches()) {
            error("Invalid E-Mail")
        }
        if (!args.email.endsWith("@std.yildiz.edu.tr")) {
            error("E-mail must end with @std.yildiz.edu.tr")
        }
        if(args.password.isEmpty()) {
            error("Password Can Not be Null")
        }
    }

}