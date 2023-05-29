package com.shenmali.stumateapp.data.model.validation

import android.util.Patterns
import com.shenmali.stumateapp.data.model.credentials.SignupCredentials
import javax.inject.Inject

class SignupValidator @Inject constructor() : Validator<SignupCredentials> {

    override fun validate(args: SignupCredentials) {
        if (args.firstName.length < 2) {
            error("Name must be at least 2 characters")
        }
        if (args.lastName.length < 2) {
            error("Surname must be at least 2 characters")
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(args.email).matches()) {
            error("Invalid E-mail")
        }
        if (!args.email.endsWith("@std.yildiz.edu.tr")) {
            error("E-mail must end with @std.yildiz.edu.tr")
        }
        if (args.password.length < 8) {
            error("Password must be at least 6 characters")
        }
        if (!args.password.contains(Regex("[0-9]"))) {
            error("Password must contain at least 1 digit")
        }
        if (!args.password.contains(Regex("[a-z]"))) {
            error("Password must contain at least 1 lowercase letter")
        }
        if (!args.password.contains(Regex("[A-Z]"))) {
            error("The password should contain at least 1 uppercase character")
        }
    }

}