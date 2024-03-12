package com.example.train_up

import android.os.Bundle
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

class ResetPasswordActivity : AppCompatActivity() {
    private var code : String? = null
    private var email: String? = null
    private var validator : Validator = Validator()
    private var editTextCode :EditText? = null
    private var editTextPassword : EditText? = null
    private val trainUpDialogFactory : TrainUpDialogFactory = TrainUpDialogFactory()
    private val database: DBConnectionManager = DBConnectionManager()
    private var loader: Loader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        loader = Loader(this)

        code = intent.getStringExtra("code")
        email =intent.getStringExtra("email")

        editTextCode = findViewById(R.id.edit_text_code)
        editTextPassword = findViewById(R.id.edit_text_password)

        val okButton = findViewById<Button>(R.id.ok_button)

        okButton.setOnClickListener {
            if (validate()){
                if (code!! == editTextCode!!.text.toString()){
                    fetchData()
                }
                else {
                    val dialog = trainUpDialogFactory.createErrorDismissDialog(this, "¡Código inválido!")
                    dialog.show()
                }
            }
        }
    }

    fun returnButton() {
        super.onBackPressed()
    }

    private fun validate() : Boolean{
        if (!validator.isEmpty(editTextCode!!, "Código es obligatorio"))
            validator.hasMaxLength(editTextCode!!, 5, "Código debe contener un máximo de 5 caracteres")

        if (!validator.isEmpty(editTextPassword!!,  "Contraseña es obligatorio" ))
            if (validator.hasMaxLength(editTextPassword!!, 30, "Contraseña debe contener un máximo de 30 caracteres"))
                validator.hasMinLength(editTextPassword!!, 8, "Contraseña debe contener un minimo de 8 caracteres")

        return editTextCode!!.error == null && editTextPassword!!.error == null
    }

    private fun fetchData() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.cambiarContrasena(email!!, editTextPassword!!.text.toString())
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result : Boolean) {
        loader!!.cancel()
        if (result){
            val dialog = trainUpDialogFactory.createSuccessRedirectDialog(this, "¡Contraseña actualizada con exito!", MainActivity::class.java, "Acceder")
            dialog.show()
        }
        else {
            val dialog = trainUpDialogFactory.createErrorDismissDialog(this, "¡Hubo un error al procesar tu solicitud!")
            dialog.show()
        }
    }
}