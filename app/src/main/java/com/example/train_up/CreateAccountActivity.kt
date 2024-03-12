package com.example.train_up

import android.content.Intent
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

class CreateAccountActivity : AppCompatActivity() {

    private var database : DBConnectionManager = DBConnectionManager()
    private var surname : EditText? = null
    private var email : EditText? = null
    private var phoneNumber : EditText? = null
    private var password : EditText? = null
    private var name : EditText? = null
    private var validator : Validator = Validator()
    private var loader : Loader? = null
    private var trainUpDialogFactory: TrainUpDialogFactory = TrainUpDialogFactory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        loader = Loader(this)

        val buttonCreateUser = findViewById<Button>(R.id.create_account_button)

        name = findViewById(R.id.edit_text_name)
        surname = findViewById(R.id.edit_text_surname)
        email = findViewById(R.id.edit_text_email)
        phoneNumber = findViewById(R.id.edit_text_phone_number)
        password = findViewById(R.id.edit_text_password)

        buttonCreateUser.setOnClickListener {
            if (validate()) {
                fetchData()
            }
        }

        val buttonRedirectLogin = findViewById<Button>(R.id.existing_account_button)

        buttonRedirectLogin.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)
        }
    }

    private fun validate() : Boolean{
        if (!validator.isEmpty(name!!, "Nombre es obligatorio"))
            validator.hasMaxLength(name!!, 30, "Nombre debe contener un máximo de 30 caracteres")

        if (!validator.isEmpty(surname!!,  "Apellidos es obligatorio" ))
            validator.hasMaxLength(surname!!, 50, "Apellidos debe contener un máximo de 50 caracteres")

        if (!validator.isEmpty(email!!, "Correo electrónico es obligatorio"))
            if (validator.hasMaxLength(email!!, 50, "Correo electrónico debe contener un máximo de 50 caracteres"))
                validator.isValidEmail(email!!, "Correo electrónico inválido")

        if (!validator.isEmpty(phoneNumber!!,  "Teléfono es obligatorio" ))
            if (validator.hasMaxLength(phoneNumber!!, 20, "Teléfono debe contener un máximo de 20 caracteres"))
                validator.isValidSpanishPhoneNumber(phoneNumber!!, "Teléfono inválido")

        if (!validator.isEmpty(password!!,  "Contraseña es obligatorio" ))
            if (validator.hasMaxLength(password!!, 30, "Contraseña debe contener un máximo de 30 caracteres"))
                validator.hasMinLength(password!!, 8, "Contraseña debe contener un minimo de 8 caracteres")

        return name!!.error == null && surname!!.error == null && email!!.error == null &&
                phoneNumber!!.error == null && password!!.error == null
    }

    private fun fetchData() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.registrarUsuario(
                name!!.text.toString(),
                surname!!.text.toString(),
                email!!.text.toString(),
                phoneNumber!!.text.toString().replace("+", "").replace("34", ""),
                "+34",
                password!!.text.toString(),
                false
            )
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result : Pair<Boolean, String>) {
        loader!!.cancel()
        if (result.first){
            val dialog = trainUpDialogFactory.createSuccessRedirectDialog(this, "¡Usuario creado con exito!", MainActivity::class.java, "Acceder")
            dialog.show()
        }
        else {
            val dialog = trainUpDialogFactory.createErrorRedirectDialog(this, result.second, ForgotPasswordActivity::class.java,"Recuperar contraseña")
            dialog.show()
        }
    }
}