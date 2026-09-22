package com.vivicarmonadev.appmovil_carpinteria.data.remote.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * Helper para Google Sign-In usando Credential Manager.
 *
 * Encapsula toda la lógica de Google para que el Repository no dependa
 * de APIs específicas de Google.
 */
class GoogleSignInHelper(
    private val context: Context,
    private val serverClientId: String
) {

    private val credentialManager = CredentialManager.create(context)

    /**
     * Lanza el flujo de Google Sign-In.
     * Devuelve el idToken si todo sale bien, o lanza una excepción.
     */
    suspend fun getGoogleIdToken(): String {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result: GetCredentialResponse = credentialManager.getCredential(
            context = context,
            request = request
        )

        val credential = result.credential

        // Verificar que sea una credencial de Google ID
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            return googleIdTokenCredential.idToken
        }

        throw Exception("Credencial de Google no válida")
    }
}