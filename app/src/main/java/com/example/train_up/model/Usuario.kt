package com.example.train_up.model

class Usuario(
    var username: String,
    var nombre: String,
    var apellidos: String,
    var email: String,
    var telefono: String,
    var prefijoTlfn: String,
    var password: String,
    var premium: Boolean,
    var imagen: ByteArray?
)