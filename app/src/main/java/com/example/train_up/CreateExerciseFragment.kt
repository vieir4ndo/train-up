package com.example.train_up

import android.app.Activity
import android.content.Intent
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
import com.example.train_up.model.Ejercicio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date

class CreateExerciseFragment : BaseTrainUpFragment() {
    private var trainingName: String? = null;
    private var name : EditText? = null
    private var description : EditText? = null
    private var duration : EditText? = null
    private lateinit var imageView: ImageView
    private var imageSelected : Boolean = false;
    private var exercise : Ejercicio? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
            trainingName = it.getString("trainingName")
        }
        loader = Loader(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonCreate = view.findViewById<Button>(R.id.save)

        name = view.findViewById(R.id.edit_text_name)
        description = view.findViewById(R.id.edit_text_description)
        duration = view.findViewById(R.id.edit_text_duration)
        imageView = view.findViewById(R.id.imageViewCreate)

        imageView.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        buttonCreate.setOnClickListener{
            if (!imageSelected){
                val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), "Imagen es obligatório")
                dialog.show()
            }
            else if (validate()){
                fetchData();
            }
        }
    }

    private fun validate() : Boolean{
        if (!validator.isEmpty(name!!, "Nombre es obligatorio"))
            validator.hasMaxLength(name!!, 25, "Nombre debe contener un máximo de 25 caracteres")

        if (!validator.isEmpty(description!!,  "Descripción es obligatorio" ))
            validator.hasMaxLength(description!!, 255, "Descripción debe contener un máximo de 255 caracteres")

        validator.isEmpty(duration!!,  "Duración es obligatorio" )

        return name!!.error == null && description!!.error == null && duration!!.error == null
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        fun newInstance(userEmail: String?, trainingName: String?): CreateExerciseFragment {
            val fragment = CreateExerciseFragment()
            val args = Bundle()
            args.putString("userEmail", userEmail)
            args.putString("trainingName", trainingName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data

            imageView.setImageURI(selectedImageUri)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageSelected = true
        }
    }

    private fun fetchData() {
        loader!!.show()

        val scope = CoroutineScope(Dispatchers.Main)

        exercise = Ejercicio(
            name!!.text.toString(),
            userEmail!!,
            description!!.text.toString(),
            duration!!.text.toString().toInt(),
            Date(),
            getBytes(imageView)//imageViewToByteArray(imageView)
        )

        val job = scope.async(Dispatchers.IO) {
            database.crearEjercicio(trainingName!!, exercise!!)
        }

        scope.launch {
            val result = job.await()

            handleResul(result)
        }
    }

    private fun handleResul(result: Pair<Boolean, String>){
        loader!!.cancel()
        if (result.first){
            val fragment = UpdateTrainingFragment.newInstance(userEmail!!, trainingName!!)
            val dialog = trainUpDialogFactory.createSuccessRedirectFrameDialog(requireContext(), "¡Ejercicio creado con exito!", fragment, ::showFrame)
            dialog.show()
        }
        else {
            val dialog = trainUpDialogFactory.createErrorDismissDialog(requireContext(), result.second)
            dialog.show()
        }
    }
}
