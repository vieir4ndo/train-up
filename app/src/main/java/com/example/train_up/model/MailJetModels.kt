package com.example.train_up.model

import com.google.gson.Gson

data class EmailMessage(
    val messages: List<Message>
){
    override fun toString(): String {
        val gson = Gson();

        return gson.toJson(this);
    }
}

data class Message(
    val from: Sender,
    val to: List<Recipient>,
    val subject: String,
    val textPart: String,
    val htmlPart: String,
)

data class Sender(
    val email: String,
    val name: String
)

data class Recipient(
    val email: String,
    val name: String
)
