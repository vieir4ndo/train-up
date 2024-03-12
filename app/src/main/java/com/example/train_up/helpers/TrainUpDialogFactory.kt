package com.example.train_up.helpers

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.train_up.CreateExerciseFragment
import com.example.train_up.DefaultExercisesFragment
import com.example.train_up.R

class TrainUpDialogFactory {
    fun <T> createErrorRedirectDialog(context: Context, message: String, redirectTo: Class<T>, okButtonMessage: String = "Ok") : Dialog {
        val dialog = Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.train_up_dialog_layout)

        var errorMessage = dialog.findViewById<TextView>(R.id.textView8);
        errorMessage.text = message;

        var returnButton = dialog.findViewById<Button>(R.id.return_button);
        returnButton.setOnClickListener{
            dialog.dismiss()
        }
        var okButton = dialog.findViewById<Button>(R.id.ok_button);
        okButton.text = okButtonMessage;

        okButton.setOnClickListener{
            val nextIntent = Intent(context, redirectTo);
            context.startActivity(nextIntent);
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(0));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.TOP);

        return dialog;
    }

    fun <T> createSuccessRedirectDialog(context: Context, message: String, redirectTo: Class<T>, okButtonMessage: String = "Ok") : Dialog {
        val dialog = Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.train_up_dialog_layout)

        var title = dialog.findViewById<TextView>(R.id.textView7);
        title.text = "Enhorabuena";

        var messageTextView = dialog.findViewById<TextView>(R.id.textView8);
        messageTextView.text = message;

        var returnButton = dialog.findViewById<Button>(R.id.return_button);
        returnButton.setOnClickListener{
            dialog.dismiss()
        }
        var okButton = dialog.findViewById<Button>(R.id.ok_button);
        okButton.text = okButtonMessage;

        okButton.setOnClickListener{
            val nextIntent = Intent(context, redirectTo);
            context.startActivity(nextIntent);
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(0));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.TOP);

        return dialog;
    }

    fun createErrorDismissDialog(context: Context, message: String, okButtonMessage: String = "Ok") : Dialog {
        val dialog = Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.train_up_dialog_layout)

        var errorMessage = dialog.findViewById<TextView>(R.id.textView8);
        errorMessage.text = message;

        var returnButton = dialog.findViewById<Button>(R.id.return_button);
        returnButton.setOnClickListener{
            dialog.dismiss()
        }
        var okButton = dialog.findViewById<Button>(R.id.ok_button);
        okButton.text = okButtonMessage;

        okButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(0));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.TOP);

        return dialog;
    }

    fun createSuccessDismissDialog(context: Context, message: String, okButtonMessage: String = "Ok") : Dialog {
        val dialog = Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.train_up_dialog_layout)

        var title = dialog.findViewById<TextView>(R.id.textView7);
        title.text = "Enhorabuena";

        var errorMessage = dialog.findViewById<TextView>(R.id.textView8);
        errorMessage.text = message;

        var returnButton = dialog.findViewById<Button>(R.id.return_button);
        returnButton.setOnClickListener{
            dialog.dismiss()
        }
        var okButton = dialog.findViewById<Button>(R.id.ok_button);
        okButton.text = okButtonMessage;

        okButton.setOnClickListener{
            dialog.dismiss()
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(0));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.TOP);

        return dialog;
    }

    fun createShareTrainingDialog(context: Context, function : (String) -> Unit, trainingName : String) : Dialog{
        val dialog = Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.train_up_dialog_layout)

        var title = dialog.findViewById<TextView>(R.id.textView7);
        title.text = "Atención";

        var errorMessage = dialog.findViewById<TextView>(R.id.textView8);
        errorMessage.text = "Ese entrenamiento se quedará público para todos los usuarios que tengan la cuenta premium de Train Up y es una acción irreversible";

        var returnButton = dialog.findViewById<Button>(R.id.return_button);
        returnButton.setOnClickListener{
            dialog.dismiss()
        }
        var okButton = dialog.findViewById<Button>(R.id.ok_button);
        okButton.text = "Confirmar";

        okButton.setOnClickListener{
            function(trainingName)
            dialog.dismiss()
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(0));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.TOP);

        return dialog;
    }

    fun createAddExerciseDialog(context: Context, showFrame : (Fragment) -> Unit, userEmail: String, trainingName: String) : Dialog{
        val dialog = Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_exercise_dialog_layout)

        var returnButton = dialog.findViewById<Button>(R.id.return_button);
        returnButton.setOnClickListener{
            dialog.dismiss()
        }

        var create = dialog.findViewById<Button>(R.id.button_create);
        create.setOnClickListener{
            dialog.dismiss()
            showFrame(CreateExerciseFragment.newInstance(userEmail, trainingName))
        }

        var pattern = dialog.findViewById<Button>(R.id.button_pattern);
        pattern.setOnClickListener{
            dialog.dismiss()
            showFrame(DefaultExercisesFragment.newInstance(userEmail, trainingName))
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(0));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.TOP);

        return dialog;
    }

    fun createSuccessRedirectFrameDialog(context: Context, message: String, redirectTo:Fragment, showFrame: (Fragment) -> kotlin.Unit, okButtonMessage: String = "Ok") : Dialog {
        val dialog = Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.train_up_dialog_layout)

        var title = dialog.findViewById<TextView>(R.id.textView7);
        title.text = "Enhorabuena";

        var messageTextView = dialog.findViewById<TextView>(R.id.textView8);
        messageTextView.text = message;

        var returnButton = dialog.findViewById<Button>(R.id.return_button);
        returnButton.setOnClickListener{
            dialog.dismiss()
        }
        var okButton = dialog.findViewById<Button>(R.id.ok_button);
        okButton.text = okButtonMessage;

        okButton.setOnClickListener{
            dialog.dismiss()
            showFrame(redirectTo)
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(0));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.TOP);

        return dialog;
    }

    fun createSuccessExecuteFuncDialog(context: Context, message: String, func: () -> Unit, okButtonMessage: String = "Ok") : Dialog {
        val dialog = Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.train_up_dialog_layout)

        var title = dialog.findViewById<TextView>(R.id.textView7);
        title.text = "Enhorabuena";

        var messageTextView = dialog.findViewById<TextView>(R.id.textView8);
        messageTextView.text = message;

        var returnButton = dialog.findViewById<Button>(R.id.return_button);
        returnButton.setOnClickListener{
            dialog.dismiss()
        }
        var okButton = dialog.findViewById<Button>(R.id.ok_button);
        okButton.text = okButtonMessage;

        okButton.setOnClickListener{
            dialog.dismiss()
            func()
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(0));
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
        dialog.window?.setGravity(Gravity.TOP);

        return dialog;
    }
}