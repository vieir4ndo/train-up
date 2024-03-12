package com.example.train_up

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.train_up.helpers.Loader
import com.example.train_up.helpers.TrainUpDialogFactory
import com.example.train_up.helpers.Validator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val trainUpDialogFactory: TrainUpDialogFactory = TrainUpDialogFactory()
    private var email : EditText? = null
    private var password : EditText? = null
    private val validator : Validator = Validator()
    private var loader : Loader? = null
    private val database : DBConnectionManager = DBConnectionManager()
    private var sharedPreferences : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loader = Loader(this)

        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE)

        email = findViewById(R.id.edit_text_email)
        password = findViewById(R.id.edit_text_password)

        val loginButton = findViewById<Button>(R.id.login)
        loginButton.setOnClickListener{
            if (validate()) {
                fetchData()
            }
        }

    }

    fun redirectToCreateAccount(view: View?){
        val nextIntent = Intent(this, CreateAccountActivity::class.java)
        startActivity(nextIntent)
    }

    override fun onBackPressed() {
        return
    }

    fun functionalityNotImplemented(view: View?){
        val dialog = trainUpDialogFactory.createErrorDismissDialog(this, "¡Esta funcionalidad aún no está disponible!")
        dialog.show()
    }

    fun redirectToForgotPassword(view: View?){
        val nextIntent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(nextIntent)
    }

    private fun validate() : Boolean{
        if (!validator.isEmpty(email!!, "Correo electrónico es obligatorio"))
            if (validator.hasMaxLength(email!!, 50, "Correo electrónico debe contener un máximo de 50 caracteres"))
                validator.isValidEmail(email!!, "Correo electrónico inválido")

        if (!validator.isEmpty(password!!,  "Contraseña es obligatorio" ))
            validator.hasMaxLength(password!!, 30, "Contraseña debe contener un máximo de 30 caracteres")

        return email!!.error == null && password!!.error == null
    }


    private fun fetchData() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.identificarUsuario(email!!.text.toString(), password!!.text.toString())
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result : Pair<Boolean, String>) {
        loader!!.cancel()
        if (result.first){
            val editor = sharedPreferences!!.edit()
            editor.putString("email", email!!.text.toString())
            editor.apply()

            val nextIntent = Intent(this, LoginSuccessActivity::class.java)
            nextIntent.putExtra("name", result.second)
            startActivity(nextIntent)
        }
        else {
            val nextIntent = Intent(this, LoginErrorActivity::class.java)
            nextIntent.putExtra("message", result.second)
            startActivity(nextIntent)
        }
    }
}