package com.example.attendease.common.network

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class FirebaseGoogleSignInContract :
    ActivityResultContract<Intent, GoogleSignInAccount?>() {

    override fun createIntent(context: android.content.Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): GoogleSignInAccount? {
        return if (resultCode == Activity.RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(intent).result
        } else null
    }
}
