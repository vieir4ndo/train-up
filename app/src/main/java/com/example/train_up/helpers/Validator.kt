package com.example.train_up.helpers

import android.util.Patterns
import android.widget.EditText

class Validator {

     fun isEmpty(input: EditText, message: String) : Boolean{
         if (input.text.toString().isEmpty() || input.text.toString().isBlank()){
             input.error = message;
             return true;
         }
         return false;
     }

    fun hasMaxLength(input: EditText, maxLength: Int, message: String) : Boolean{
        if (input.text.toString().length > maxLength){
            input.error = message;
            return  false;
        }
        return true;
    }

    fun hasMinLength(input: EditText, minLength: Int, message: String) : Boolean{
        if (input.text.toString().length < minLength){
            input.error = message;
            return  false;
        }
        return true;
    }

    fun isValidEmail(input: EditText, message: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(input.text.toString()).matches()){
            input.error = message;
            return  false;
        }
        return true;
    }

    fun isValidSpanishPhoneNumber(input: EditText, message: String) : Boolean{
        val pattern = Regex("^(\\+34|34)?[6-9]\\d{8}\$")
        if (!pattern.matches(input.text.toString())){
            input.error = message;
            return false;
        }
        return true;
    }

    fun matchesRegex(input: EditText, message: String, regex: Regex) : Boolean{
        if (!regex.matches(input.text.toString())){
            input.error = message;
            return false;
        }
        return true;
    }
}