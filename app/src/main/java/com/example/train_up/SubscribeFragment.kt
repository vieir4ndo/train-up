package com.example.train_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.train_up.abstractions.BaseTrainUpFragment
import com.example.train_up.helpers.Validator
import com.example.train_up.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SubscribeFragment : BaseTrainUpFragment() {
    private var cardNumber : EditText? = null
    private var validThrow : EditText? = null
    private var code : EditText? = null
    private var name : EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscribe_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUser(view)
    }

    private fun getUser(view: View){
        loader!!.show()
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.buscarUsuario(userEmail!!)
        }

        scope.launch {
            val result = job.await()

            handleResultGetUser(view, result!!)
        }
    }

    private fun handleResultGetUser(view: View, result : Usuario) {
        loader!!.cancel()

        if (!result.premium) {
            val payButton = view.findViewById<Button>(R.id.pay_button)

            val date = view.findViewById<TextView>(R.id.date)
            date.text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

            val renew = view.findViewById<TextView>(R.id.renew)
            renew.text =
                LocalDate.now().plusDays(30).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))

            cardNumber = view.findViewById(R.id.edit_text_card_number)
            validThrow = view.findViewById(R.id.edit_text_valid_throw)
            code = view.findViewById(R.id.edit_text_code)
            name = view.findViewById(R.id.edit_text_name)

            payButton.setOnClickListener {
                if (validate()) {
                    //TODO : Add connection to payment gateway
                    if (cardNumber!!.text.toString() == "123456789") {
                        fetchData()
                    } else {
                        showFrame(CheckOutErrorFragment())
                    }
                }
            }
        }
        else {
            showFrame(ManageSubscriptionFragment.newInstance(userEmail))
        }
    }

    private fun validate() : Boolean{
        if (!validator.isEmpty(cardNumber!!, "Número de la tarjeta es obligatorio"))
            validator.hasMaxLength(cardNumber!!, 30, "Número de la tarjeta debe contener un máximo de 30 caracteres")

        if (!validator.isEmpty(validThrow!!,  "Caducidad es obligatorio" ))
            if (validator.hasMaxLength(validThrow!!, 7, "Caducidad debe contener un máximo de 7 caracteres"))
                validator.matchesRegex(validThrow!!, "Caducidad inválida", Regex("""^\d{2}/\d{4}$"""))

        if (!validator.isEmpty(code!!,  "CVV es obligatorio" ))
            if (validator.hasMaxLength(code!!, 3, "CVV debe contener un máximo de 3 caracteres"))
                validator.hasMinLength(code!!, 3, "CVV debe contener un mínimo de 3 caracteres")


        if (!validator.isEmpty(name!!, "Nombre es obligatorio"))
            validator.hasMaxLength(name!!, 30, "Nombre debe contener un máximo de 30 caracteres")

        return cardNumber!!.error == null && validThrow!!.error == null && code!!.error == null && name!!.error == null
    }


    private fun fetchData() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.cambiarUsuarioPlanPremium(userEmail!!, true)
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result : Boolean) {
        loader!!.cancel()
        if (result){
            showFrame(CheckOutSuccessFragment())
        }
        else {
            showFrame(CheckOutErrorFragment())
        }
    }

    companion object {
        fun newInstance(userEmail: String?): SubscribeFragment {
            val fragment = SubscribeFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            fragment.arguments = args
            return fragment
        }
    }
}