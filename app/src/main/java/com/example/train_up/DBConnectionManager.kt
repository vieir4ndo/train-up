package com.example.train_up
import java.sql.*
import com.example.train_up.model.* //Cambiar para reflejar la ubicacion de las clases del modelo
import java.text.SimpleDateFormat
import java.util.Properties

class DBConnectionManager{
    private val FORMATTER = SimpleDateFormat("yyyy-MM-dd")
    private val FORMATTER_DATETIME = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

    private fun connectToDatabase(): Connection {
        val url = "jdbc:mysql://db4free.net:3306/vieir4ndo" //Cambiar a la base de datos que se vaya a utilizar

        val properties = Properties()
        properties["useSSL"] = "false"
        properties["user"] ="vieir4ndo"
        properties["password"] = "12345678"

        Class.forName("com.mysql.jdbc.Driver")

        return DriverManager.getConnection(url, properties)
    }

    fun registrarUsuario(nombre: String, apellidos: String, email: String, telefono: String, prefijoTlfn: String, password: String, premium: Boolean) : Pair<Boolean, String> {
        try {
            if (identificarUsuarioPorCorreo(email)){
                return  Pair(false, "¡Correo electrónico ya registrado!")
            }

            val connection = connectToDatabase()
            val query =
                "INSERT INTO usuario (nombre, apellidos, email, telefono, prefijo_tlfn, password, premium) VALUES (?, ?, ?, ?, ?, SHA2(?, 256), ?)"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, nombre)
            preparedStatement.setString(2, apellidos)
            preparedStatement.setString(3, email)
            preparedStatement.setString(4, telefono)
            preparedStatement.setString(5, prefijoTlfn)
            preparedStatement.setString(6, password)
            preparedStatement.setBoolean(7, premium)

            preparedStatement.executeUpdate()
            connection.close()

            return Pair(true, "")
        }
        catch (e : Error){
            e.printStackTrace()
            return  Pair(false, "¡Hubo un error al procesar tu solicitud!")
        }
    }

    fun identificarUsuarioPorCorreo(email: String): Boolean {
        try {
            val connection = connectToDatabase()
            val query = "SELECT count(*) FROM usuario WHERE email = ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, email)

            val resultSet: ResultSet = preparedStatement.executeQuery()
            val usuarioExiste = resultSet.next() && resultSet.getInt("count(*)") > 0

            connection.close()

            return usuarioExiste;
        }
        catch (e: Error){
            return false;
        }
    }

    fun identificarUsuario(email: String, password: String): Pair<Boolean, String> {
        try {
            if (!identificarUsuarioPorCorreo(email)){
                return  Pair(false, "¡Usuario no encontrado!")
            }

            val connection = connectToDatabase()
            val query =
                "SELECT count(*), nombre FROM usuario WHERE email = ? AND password = SHA2(?, 256)"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, email)
            preparedStatement.setString(2, password)

            val resultSet: ResultSet = preparedStatement.executeQuery()
            val usuarioExiste = resultSet.next() && resultSet.getInt("count(*)") > 0
            var message = resultSet.getString("nombre")

            connection.close()

            if (!usuarioExiste)
                message = "Usuario o contraseña inválido"

            return Pair(usuarioExiste, message)
        }
        catch (e : Error){
            e.printStackTrace()
            return  Pair(false, "¡Hubo un error al procesar tu solicitud!")
        }
    }

    fun cambiarUsuarioPlanPremium(email: String, premium: Boolean) : Boolean {
        return try {
            val connection = connectToDatabase()
            val query = "UPDATE usuario SET premium = ? WHERE email = ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setBoolean(1, premium)
            preparedStatement.setString(2, email)

            preparedStatement.executeUpdate()
            connection.close()
            true
        } catch (e : Error){
            false
        }
    }

    fun cambiarContrasena(email : String, newPassword: String) : Boolean{
        try {
            val connection = connectToDatabase()
            val query = "UPDATE usuario SET password = SHA2(?, 256) where email like ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, newPassword)
            preparedStatement.setString(2, email)

            preparedStatement.executeUpdate()
            connection.close()

            return true
        }
        catch (e : Error){
            return false
        }
    }

    fun buscarUsuario(email: String): Usuario? {
        try {
            val connection = connectToDatabase()
            val query = "SELECT * FROM usuario WHERE email like ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, email)
            val resultSet: ResultSet = preparedStatement.executeQuery()

            resultSet.next()

            val nombre = resultSet.getString("nombre")
            val apellidos = resultSet.getString("apellidos")
            val telefono = resultSet.getString("telefono")
            val prefijoTlfn = resultSet.getString("prefijo_tlfn")
            val password = resultSet.getString("password")
            val imagen = resultSet.getBytes("imagen")
            val premium = (resultSet.getBoolean("premium").compareTo(false) != 0)

            connection.close()

            return Usuario(
                "",
                nombre,
                apellidos,
                email,
                telefono,
                prefijoTlfn,
                password,
                premium,
                imagen
            )
        } catch (e : Error){
            return null
        }
    }

    fun editarUsuario(email: String, nombre: String, apellidos: String, telefono: String, prefijoTlfn: String, imagen : ByteArray) : Boolean {
        try {
            val connection = connectToDatabase()
            val query =
                "UPDATE usuario SET nombre = ?, apellidos = ?, telefono = ?, prefijo_tlfn = ?, imagen = ? where email like ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, nombre)
            preparedStatement.setString(2, apellidos)
            preparedStatement.setString(3, telefono)
            preparedStatement.setString(4, prefijoTlfn)
            preparedStatement.setBytes(5, imagen)
            preparedStatement.setString(6, email)

            preparedStatement.executeUpdate()

            connection.close()

            return true
        }
        catch (e: Error){
            return false
        }
    }

    fun mostrarEntrenamientos(): List<Entrenamiento> {
        val entrenamientos = mutableListOf<Entrenamiento>()
        try {
            val connection = connectToDatabase()
            val query = "SELECT * FROM entrenamiento"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)

            val resultSet: ResultSet = preparedStatement.executeQuery()


            while (resultSet.next()) {
                val nombre = resultSet.getString("nombre")
                val usuario = resultSet.getString("usuario")
                val descripcion = resultSet.getString("descripcion")
                val fechaCreacion = resultSet.getDate("fecha_creacion")
                val duracion = resultSet.getInt("duracion")
                val publico = (resultSet.getBoolean("publico").compareTo(false) != 0)
                val imagen = resultSet.getBytes("imagen")

                // Crear objeto com.example.train_up.model.Entrenamiento con los datos recuperados
                val entrenamiento = Entrenamiento(
                    nombre,
                    usuario,
                    descripcion,
                    duracion,
                    fechaCreacion,
                    publico,
                    imagen
                )
                entrenamiento.ejercicios = buscarEjercicios(nombre, usuario);
                entrenamiento.datosUsuario = buscarUsuario(usuario);
                entrenamientos.add(entrenamiento)
            }

            connection.close()
            return entrenamientos
        }
        catch (e: Error){
            return entrenamientos
        }
    }

    fun buscarEntrenamientos(): List<Entrenamiento>{
        val entrenamientos = mutableListOf<Entrenamiento>()
        try {
            val connection = connectToDatabase()
            val query = "SELECT * FROM entrenamiento where publico = true";

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)

            val resultSet: ResultSet = preparedStatement.executeQuery()

            while (resultSet.next()) {
                val nombre = resultSet.getString("nombre")
                val usuario = resultSet.getString("usuario")
                val descripcion = resultSet.getString("descripcion")
                val fechaCreacion = resultSet.getDate("fecha_creacion")
                val duracion = resultSet.getInt("duracion")
                val publico = (resultSet.getBoolean("publico").compareTo(false) != 0)
                val imagen = resultSet.getBytes("imagen")

                // Crear objeto com.example.train_up.model.Entrenamiento con los datos recuperados
                val entrenamiento = Entrenamiento(
                    nombre,
                    usuario,
                    descripcion,
                    duracion,
                    fechaCreacion,
                    publico,
                    imagen
                )
                entrenamiento.ejercicios = buscarEjercicios(nombre, usuario);
                entrenamiento.datosUsuario = buscarUsuario(usuario);
                entrenamientos.add(entrenamiento)
            }

            connection.close()
            return entrenamientos
        }
        catch (e: Error){
            return entrenamientos
        }
    }

    fun mostrarEntrenamientos(usuario: String): List<Entrenamiento> {
        val entrenamientos = mutableListOf<Entrenamiento>()

        try {
            val connection = connectToDatabase()
            val query = "SELECT * FROM entrenamiento where usuario = ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, usuario)

            val resultSet: ResultSet = preparedStatement.executeQuery()

            while (resultSet.next()) {
                val nombre = resultSet.getString("nombre")
                val usuarioSQL = resultSet.getString("usuario")
                val descripcion = resultSet.getString("descripcion")
                val fechaCreacion = resultSet.getDate("fecha_creacion")
                val duracion = resultSet.getInt("duracion")
                val publico = (resultSet.getBoolean("publico").compareTo(false) != 0)
                val imagen = resultSet.getBytes("imagen")

                // Crear objeto com.example.train_up.model.Entrenamiento con los datos recuperados
                val entrenamiento = Entrenamiento(
                    nombre,
                    usuarioSQL,
                    descripcion,
                    duracion,
                    fechaCreacion,
                    publico,
                    imagen
                )
                entrenamiento.ejercicios = buscarEjercicios(nombre, usuarioSQL);
                entrenamiento.datosUsuario = buscarUsuario(usuarioSQL);
                entrenamientos.add(entrenamiento)
            }

            connection.close()
            return entrenamientos
        }
        catch (e: Error){
            return entrenamientos
        }
    }

    fun mostrarDetallesEntrenamiento(nombreEntrenamiento: String, correoUsuario: String): Entrenamiento? {
        try {
            val connection = connectToDatabase()
            val query = "SELECT * FROM entrenamiento WHERE nombre = ? AND usuario = ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, nombreEntrenamiento)
            preparedStatement.setString(2, correoUsuario)

            val resultSet: ResultSet = preparedStatement.executeQuery()

            resultSet.next()

            val nombre = resultSet.getString("nombre")
            val usuario = resultSet.getString("usuario")
            val descripcion = resultSet.getString("descripcion")
            val fechaCreacion = resultSet.getDate("fecha_creacion")
            val duracion = resultSet.getInt("duracion")
            val publico = (resultSet.getBoolean("publico").compareTo(false) != 0)
            val imagen = resultSet.getBytes("imagen")

            // Crear objeto com.example.train_up.model.Entrenamiento con los datos recuperados
            val entrenamientoSQL = Entrenamiento(
                nombre,
                usuario,
                descripcion,
                duracion,
                fechaCreacion,
                publico,
                imagen
            )
            connection.close()

            entrenamientoSQL.ejercicios = buscarEjercicios(nombreEntrenamiento, correoUsuario);

            return entrenamientoSQL
        }
        catch (e: Error){
            return null
        }
    }

    private fun buscarEjercicios(nombreEntrenamiento: String, correoUsuario: String) : List<Ejercicio> {
        val ejercicios = mutableListOf<Ejercicio>()

        try {
            val connection = connectToDatabase()
            val query = ("SELECT e.* " +
                    "FROM ejercicio e " +
                    "JOIN lista_ejercicio le ON e.nombre = le.nombre AND e.usuario = le.usuario " +
                    "WHERE le.nombre_entrenamiento = ? AND le.usuario_entrenamiento = ?")

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, nombreEntrenamiento)
            preparedStatement.setString(2, correoUsuario)

            val resultSet: ResultSet = preparedStatement.executeQuery()

            while (resultSet.next()) {
                val nombre = resultSet.getString("nombre")
                val usuario = resultSet.getString("usuario")
                val descripcion = resultSet.getString("descripcion")
                val fechaCreacion = resultSet.getDate("fecha_creacion")
                val duracion = resultSet.getInt("duracion")
                val imagen = resultSet.getBytes("imagen")

                // Crear objeto com.example.train_up.model.Entrenamiento con los datos recuperados
                val ejercicioSQL =
                    Ejercicio(nombre, usuario, descripcion, duracion, fechaCreacion, imagen)
                ejercicios.add(ejercicioSQL)
            }
            connection.close()

            return ejercicios
        }
        catch (e: Error){
            return ejercicios
        }
    }

    fun crearEntrenamiento(entrenamiento: Entrenamiento) : Pair<Boolean, String> {
        try {

            if (identificarEntrenamientoPorCorreoYNombre(entrenamiento.usuario, entrenamiento.nombre)){
                return  Pair(false, "¡Ya existe un entrenamiento registrado con ese nombre!")
            }

            val connection = connectToDatabase()
            val query = "INSERT INTO entrenamiento VALUES (?, ?, ?, ?, ?, ?, ?)"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, entrenamiento.nombre)
            preparedStatement.setString(2, entrenamiento.usuario)
            preparedStatement.setString(3, entrenamiento.descripcion)
            preparedStatement.setString(4, FORMATTER.format(entrenamiento.fechaCreacion!!))
            preparedStatement.setInt(5, entrenamiento.duracion!!)
            preparedStatement.setBoolean(6, entrenamiento.publico!!)
            preparedStatement.setBytes(7, entrenamiento.imagen)

            preparedStatement.executeUpdate()
            connection.close()

            return Pair(true, "");
        }
        catch (e : Error){
            e.printStackTrace()
            return Pair(false, "¡Hubo un error al procesar tu solicitud!");
        }
    }

    fun identificarEntrenamientoPorCorreoYNombre(email: String, nombre: String): Boolean {
        try {
            val connection = connectToDatabase()
            val query = "SELECT count(*) FROM entrenamiento WHERE usuario = ? AND nombre = ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, email)
            preparedStatement.setString(2, nombre)

            val resultSet: ResultSet = preparedStatement.executeQuery()
            val existe = resultSet.next() && resultSet.getInt("count(*)") > 0

            connection.close()

            return existe
        }
        catch (e: Error){
            return false
        }
    }

    fun crearEjercicio(nombreEntrenamiento: String, ejercicio: Ejercicio) : Pair<Boolean, String> {
        try {

            val existe = buscarEjercicio(ejercicio.usuario, nombreEntrenamiento, ejercicio.nombre);

            if (existe != null) {
                return Pair(false, "¡Ya existe un ejercicio registrado con ese nombre!")
            }

            val connection = connectToDatabase()
            val query = "INSERT INTO ejercicio VALUES (?, ?, ?, ?, ?, ?)"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, ejercicio.nombre)
            preparedStatement.setString(2, ejercicio.usuario)
            preparedStatement.setString(3, ejercicio.descripcion)
            preparedStatement.setInt(4, ejercicio.duracion)
            preparedStatement.setString(5, FORMATTER_DATETIME.format(ejercicio.fechaCreacion))
            preparedStatement.setBytes(6, ejercicio.imagen)

            preparedStatement.executeUpdate()
            connection.close()

            asignarEjerciciosAEntrenamiento(nombreEntrenamiento, ejercicio.usuario, listOf(ejercicio))

            return Pair(true, "")
        }
        catch (e : SQLException){
            e.printStackTrace()
            return Pair(false, "¡Hubo un error al procesar tu solicitud!");
        }
    }

    private fun asignarEjerciciosAEntrenamiento(nombreEntrenamiento: String, usuarioEntrenamiento: String, ejercicios: List<Ejercicio>)  : Boolean{
        try {
            val connection = connectToDatabase()

            /* Eliminar relaciones existentes para este entrenamiento
            val deleteQuery = "DELETE FROM lista_ejercicio WHERE nombre_entrenamiento = ? AND usuario_entrenamiento = ?"
            val deleteStatement = connection.prepareStatement(deleteQuery)
            deleteStatement.setString(1, nombreEntrenamiento)
            deleteStatement.setString(2, usuarioEntrenamiento)
            deleteStatement.executeUpdate()*/

            // Insertar los nuevos ejercicios para el entrenamiento
            val insertQuery = "INSERT INTO lista_ejercicio (nombre_entrenamiento, usuario_entrenamiento, nombre, usuario) VALUES (?, ?, ?, ?)"
            val insertStatement = connection.prepareStatement(insertQuery)

            for (ejercicio in ejercicios) {
                insertStatement.setString(1, nombreEntrenamiento)
                insertStatement.setString(2, usuarioEntrenamiento)
                insertStatement.setString(3, ejercicio.nombre)
                insertStatement.setString(4, ejercicio.usuario)
                insertStatement.addBatch()
            }

            insertStatement.executeBatch()

            connection.close()

            return true;
        } catch (e: SQLException) {
            // Manejar excepciones
            e.printStackTrace()
            return false;
        }
    }

    fun publicarEntrenamiento(correoUsuario: String, nombre: String) : Boolean {
        try {
            // TODO: Hacer funcionar
            val connection = connectToDatabase()
            val query = "UPDATE entrenamiento SET publico = true WHERE nombre = ? AND usuario = ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, nombre)
            preparedStatement.setString(2, correoUsuario)

            preparedStatement.executeUpdate()
            connection.close()
            return true
        }
        catch (e : Error){
            return false
        }
    }

    fun editarEntrenamiento(nombreAntiguo: String, entrenamiento: Entrenamiento) : Pair<Boolean, String> {
        try {
            val connection = connectToDatabase()

            val query = "UPDATE entrenamiento SET descripcion=?, imagen=? WHERE nombre=? AND usuario=?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)

            preparedStatement.setString(1, entrenamiento.descripcion)
            preparedStatement.setBytes(2, entrenamiento.imagen)
            preparedStatement.setString(3, nombreAntiguo)
            preparedStatement.setString(4, entrenamiento.usuario)

            preparedStatement.executeUpdate()

            connection.close()
            return Pair(true, "");

        } catch (e: SQLException) {
            e.printStackTrace()
            return Pair(false, "¡Hubo un error al procesar tu solicitud!")
        }
    }

    fun buscarEjercicio(correoUsuario: String, nombreEntrenamiento: String, nombreEjercicio: String): Ejercicio? {
        try {
            val connection = connectToDatabase()
            val query = "SELECT e.* " +
                    "FROM ejercicio e " +
                    "JOIN lista_ejercicio le ON e.nombre = le.nombre " +
                    "WHERE le.nombre_entrenamiento = ? AND le.usuario_entrenamiento = ? AND le.nombre = ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, nombreEntrenamiento)
            preparedStatement.setString(2, correoUsuario)
            preparedStatement.setString(3, nombreEjercicio)

            val resultSet: ResultSet = preparedStatement.executeQuery()

            if (resultSet.next()) {
                val nombre = resultSet.getString("nombre")
                val usuario = resultSet.getString("usuario")
                val descripcion = resultSet.getString("descripcion")
                val fechaCreacion = resultSet.getDate("fecha_creacion")
                val duracion = resultSet.getInt("duracion")
                val imagen = resultSet.getBytes("imagen")

                val ejercicioSQL =
                    Ejercicio(nombre, usuario, descripcion, duracion, fechaCreacion, imagen)

                return ejercicioSQL
            }

            connection.close()

            return null
        }
        catch (e: Error){
            return null
        }
    }

    fun editarEjercicio(correoUsuario: String, nombreEjercicio: String, descripcion: String, duracion: Int, imagen: ByteArray?) : Boolean{
        try {
            val connection = connectToDatabase()
            val query =
                "UPDATE ejercicio SET descripcion = ?, duracion = ?, imagen = ? where nombre = ? and usuario = ?"

            val preparedStatement: PreparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, descripcion)
            preparedStatement.setInt(2, duracion)
            preparedStatement.setBytes(3, imagen)
            preparedStatement.setString(4, nombreEjercicio)
            preparedStatement.setString(5, correoUsuario)

            preparedStatement.executeUpdate()
            connection.close()

            return true
        }
        catch (e: Error){
            return false
        }
    }
}
