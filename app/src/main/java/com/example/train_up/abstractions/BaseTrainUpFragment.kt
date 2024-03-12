package com.example.train_up.abstractions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.train_up.DBConnectionManager
import com.example.train_up.R
import com.example.train_up.helpers.Loader
import com.example.train_up.helpers.TrainUpDialogFactory
import com.example.train_up.helpers.Validator
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

abstract class BaseTrainUpFragment : Fragment() {
    protected var userEmail: String? = null
    protected var loader : Loader? = null
    protected var database : DBConnectionManager = DBConnectionManager()
    protected var trainUpDialogFactory: TrainUpDialogFactory = TrainUpDialogFactory()
    protected var validator : Validator = Validator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userEmail = it.getString("userEmail")
        }
        loader = Loader(requireContext())
    }

    protected fun showFrame(frame: Fragment) {
        val fragmentManager = getFragmentManager()
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.frameContainer, frame)
        fragmentTransaction.commit()
    }

    fun getBytes(imageView: ImageView): ByteArray {
        var bytesData: ByteArray
        try {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            bytesData = stream.toByteArray()
            stream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            bytesData = byteArrayOf()
        }
        return bytesData
    }
}