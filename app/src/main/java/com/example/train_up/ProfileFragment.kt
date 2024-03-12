package com.example.train_up

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.train_up.abstractions.BaseTrainUpFragment
import com.example.train_up.helpers.Loader
import com.example.train_up.helpers.Validator
import com.example.train_up.model.Usuario
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ProfileFragment : BaseTrainUpFragment() {

    private var surname : EditText? = null
    private var email : EditText? = null
    private var phoneNumber : EditText? = null
    private var name : EditText? = null
    private lateinit var imageView: CircleImageView
    private var imageSelected : Boolean = false;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val updateProfile = view.findViewById<Button>(R.id.save_profile_button)

        name = view.findViewById(R.id.edit_text_name)
        surname = view.findViewById(R.id.edit_text_surname)
        email = view.findViewById(R.id.edit_text_email)
        phoneNumber = view.findViewById(R.id.edit_text_phone_number)
        imageView = view.findViewById(R.id.profile_picture)

        imageView.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        updateProfile.setOnClickListener {
            if (!imageSelected){
                val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), "Imagen es obligatório")
                dialog.show()
            }
            else if (validate()) {
                fetchData()
            }
        }

        val logoutButton = view.findViewById<Button>(R.id.logout_button)

        logoutButton.setOnClickListener{
            val nextIntent = Intent(requireContext(), MainActivity::class.java)
            startActivity(nextIntent)
        }

        getUser()
    }

    private fun validate() : Boolean{
        if (!validator.isEmpty(name!!, "Nombre es obligatorio"))
            validator.hasMaxLength(name!!, 30, "Nombre debe contener un máximo de 30 caracteres")

        if (!validator.isEmpty(surname!!,  "Apellidos es obligatorio" ))
            validator.hasMaxLength(surname!!, 50, "Apellidos debe contener un máximo de 50 caracteres")

        if (!validator.isEmpty(phoneNumber!!,  "Teléfono es obligatorio" ))
            if (validator.hasMaxLength(phoneNumber!!, 20, "Teléfono debe contener un máximo de 20 caracteres"))
                validator.isValidSpanishPhoneNumber(phoneNumber!!, "Teléfono inválido")

        return name!!.error == null && surname!!.error == null && email!!.error == null &&
                phoneNumber!!.error == null
    }


    private fun getUser(){
        loader!!.show()
        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.buscarUsuario(userEmail!!)
        }

        scope.launch {
            val result = job.await()

            handleResultGetUser(result!!)
        }
    }

    private fun handleResultGetUser(result : Usuario){
        loader!!.cancel()

        name!!.setText(result.nombre)
        surname!!.setText(result.apellidos)
        email!!.setText(result.email)
        email!!.isEnabled = false
        phoneNumber!!.setText(result.prefijoTlfn + result.telefono)

        if (result.imagen != null){
            val bitmap = BitmapFactory.decodeByteArray(result.imagen, 0, result.imagen!!.size)
            imageView.setImageBitmap(bitmap)
        }

    }

    private fun fetchData() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        val job = scope.async(Dispatchers.IO) {
            database.editarUsuario(
                email!!.text.toString(),
                name!!.text.toString(),
                surname!!.text.toString(),
                phoneNumber!!.text.toString().replace("+", "").replace("34", ""),
                "+34",
                getBytes(imageView)//circleImageViewToByteArray(imageView)
            )
        }

        scope.launch {
            val result = job.await()

            handleResult(result)
        }
    }

    private fun handleResult(result : Boolean) {
        loader!!.cancel()
        if (result){
            val dialog = trainUpDialogFactory.createSuccessDismissDialog(requireContext(), "¡Usuario actualizado con exito!")
            dialog.show()
        }
        else {
            val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), "¡Hubo un error al procesar tu solicitud!")
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data

            imageView.setImageURI(selectedImageUri)
            imageSelected = true
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1

        fun newInstance(userEmail: String?): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            fragment.arguments = args
            return fragment
        }
    }
}
