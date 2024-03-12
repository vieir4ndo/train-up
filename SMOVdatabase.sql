drop database smov;
create database smov;
use smov;
create table usuario(
nombre varchar(30),
apellidos varchar(50),
email varchar(50),
telefono varchar(20),
prefijo_tlfn varchar(4) DEFAULT '+34',
password varchar(255),
premium bit DEFAULT 0,
imagen mediumblob,
primary key (email)
);

create table entrenamiento(
nombre varchar(25),
usuario varchar(50),
descripcion varchar(255),
fecha_creacion date,
duracion integer,
publico bit DEFAULT 0,
imagen mediumblob,
primary key (nombre, usuario),
foreign key (usuario) references usuario(email)
);

create table ejercicio(
    nombre varchar(25),
    usuario varchar(50),
    descripcion varchar(255),
    duracion integer,
    fecha_creacion datetime,
    imagen mediumblob,
    primary key (nombre, usuario, fecha_creacion),
    foreign key (usuario) references usuario(email)
);

create table lista_ejercicio(
nombre varchar(25),
usuario varchar(50),
nombre_entrenamiento varchar(25),
usuario_entrenamiento varchar(50),
primary key (nombre, usuario, nombre_entrenamiento, usuario_entrenamiento),
foreign key (nombre, usuario) references ejercicio(nombre, usuario),
foreign key (nombre_entrenamiento, usuario_entrenamiento) references entrenamiento(nombre, usuario)
);