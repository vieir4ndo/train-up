package com.example.train_up

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.train_up.helpers.Loader
import com.example.train_up.helpers.TrainUpDialogFactory
import com.example.train_up.helpers.Validator
import com.example.train_up.services.MailJetClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private var email : EditText? = null
    private var validator : Validator = Validator()
    private val mailJetClient : MailJetClient = MailJetClient()
    private var code : String? = null
    private var loader : Loader? = null
    private val database : DBConnectionManager = DBConnectionManager()
    private val trainUpDialogFactory: TrainUpDialogFactory = TrainUpDialogFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        loader = Loader(this)

        email = findViewById(R.id.edit_text_email)

        val okButton = findViewById<Button>(R.id.ok_button)
        okButton.setOnClickListener{
            if (validate()){
                getUser()
            }
        }
    }

    fun returnButton(view : View?) {
        super.onBackPressed()
    }

    private fun validate() : Boolean{
        if (!validator.isEmpty(email!!, "Correo electrónico es obligatorio"))
            if (validator.hasMaxLength(email!!, 50, "Correo electrónico debe contener un máximo de 50 caracteres"))
                validator.isValidEmail(email!!, "Correo electrónico inválido")

        return  email!!.error == null
    }

    private fun getUser(){
        loader!!.show()
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.identificarUsuarioPorCorreo(email!!.text.toString())
        }

        scope.launch {
            val result = job.await()

            handleResultGetUser(result)
        }
    }

    private fun handleResultGetUser(result : Boolean){
        if (result) {
            fetchData()
        }
        else {
            loader!!.cancel()

            val dialog = trainUpDialogFactory.createErrorDismissDialog(this, "¡Usuario no encontrado!")
            dialog.show()
        }
    }
    private fun fetchData() {
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            code = generateRandomCode()
            mailJetClient.sendResetPasswordMessage(code!!, email!!.text.toString())
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result: Boolean) {
        loader!!.cancel()
        if (result) {
            val openResetPasswordActivityIntent = Intent(this, ResetPasswordActivity::class.java)
            openResetPasswordActivityIntent.putExtra("code", code)
            openResetPasswordActivityIntent.putExtra("email", email!!.text.toString())
            startActivity(openResetPasswordActivityIntent)
        } else {
            val dialog = trainUpDialogFactory.createErrorDismissDialog(this, "¡Hubo un error al procesar tu solicitud!")
            dialog.show()
        }
    }

    private fun generateRandomCode(): String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..5)
            .map { charset.random() }
            .joinToString("")
    }
}