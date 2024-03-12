package com.example.train_up.services

import com.example.train_up.model.EmailMessage
import com.example.train_up.model.Message
import com.example.train_up.model.Recipient
import com.example.train_up.model.Sender
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.nio.charset.StandardCharsets
import java.util.Base64

class MailJetClient {
    private val uri: String = "https://api.mailjet.com/v3.1/send";
    private val shouldLog = true;
    private val apiKey = "2697821a2ec645e0ce473653ee4f573c";
    private val apiSecret = "2c74ee7f08f76df270020dd0a09c70ef";

    fun sendResetPasswordMessage(code: String, userEmail: String): Boolean {
        try {

            val from = Sender("matheus.eu.mv@gmail.com", "Train Up")

            val toList = listOf(Recipient(userEmail, "Usuario Train Up"))

            val message = Message(
                from = from,
                to = toList,
                subject = "Código de Recuperación de Contraseña",
                textPart = "Saludos de  Train Up",
                htmlPart = "Estimado usuario,<br /> Tu código de recuperación de contraseña es $code.<br /> Para finalizar el reseteo de contraseña, vuelva a la aplicación de Train Up e informe el código y su nueva contraseña. <br /> Saludos cordiales, <br /> Equipo Train Up."
            )

            val messagesList = listOf(message)

            val body = EmailMessage(messagesList).toString();

            val credentials = "$apiKey:$apiSecret"
            val base64Credentials = Base64.getEncoder().encodeToString(credentials.toByteArray(
                StandardCharsets.UTF_8))

            val headers: Map<String, String> = mapOf(
                "Content-Type" to "application/json",
                "charset" to "utf-8",
                "Authorization" to "Basic $base64Credentials"
            )

            if (shouldLog) {
                println("Url: '$uri'");
                println("Body: '$body'");
                println("Headers: '$headers'");
            }

            val headersHttp =
                MediaType.parse(listOf("application/json", "charset=utf-8").joinToString(";"));

            val okHttpHeaders = Headers.of(headers)

            val requestBody = RequestBody.create(headersHttp, body);

            val client = OkHttpClient();

            val request =
                Request.Builder().url(uri).headers(okHttpHeaders).post(requestBody).build();

            val response = client.newCall(request).execute();

            if (shouldLog) {
                println("Status Code: '${response.code()}'");
            }

            if (response.code() != 200){
                throw Error();
            }

            return true;

        } catch (e: Error) {
            e.printStackTrace()
            return false;
        }
    }
}