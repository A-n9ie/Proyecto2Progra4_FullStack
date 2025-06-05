/* Creacion de la base de datos */
drop database Medicalappointment;
create database MedicalAppointment;

use MedicalAppointment;

/* Creacion de tablas */

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(20) UNIQUE NOT NULL,
    clave VARCHAR(255) NOT NULL, /* 255 -> puede estar encriptada*/
    rol ENUM('Medico', 'Paciente', 'Administrador') NOT NULL
);

CREATE TABLE medicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    cedula VARCHAR(20)  UNIQUE NOT NULL,
    nombre VARCHAR(30) NOT NULL,
	aprobado BOOLEAN DEFAULT FALSE,
    especialidad VARCHAR(30),
    costo_consulta DECIMAL(10, 2),
    lugar_atencion VARCHAR(80),
    foto_url VARCHAR(255),
    presentacion TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) 
);

CREATE TABLE horarios_medicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medico_id INT,
    dia ENUM('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO'),
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    frecuencia_minutos INT DEFAULT 30,
    FOREIGN KEY (medico_id) REFERENCES medicos(id)
);

CREATE TABLE pacientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    cedula VARCHAR(20)  UNIQUE NOT NULL,
    nombre VARCHAR(30) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    foto_url VARCHAR(255),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) 
);

CREATE TABLE citas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medico_id INT,
    paciente_id INT,
    fecha_cita DATE,
    hora_cita TIME,
    estado ENUM('Pendiente', 'Confirmada', 'Atendida', 'Cancelada') DEFAULT 'Pendiente',
    anotaciones TEXT,
    FOREIGN KEY (medico_id) REFERENCES medicos(id),
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id)
);

/* Administrador */
insert into usuarios (usuario, clave, rol) 
values ('Admin', 'root', 'Administrador');

/* usuarios medicos */
insert into usuarios (usuario, clave, rol) 
values ('BBanner', '111', 'Medico');
insert into usuarios (usuario, clave, rol) 
values ('JPerez', '222', 'Medico');
insert into usuarios (usuario, clave, rol) 
values ('LKjero', '333', 'Medico');



/* medicos */
insert into medicos (usuario_id, cedula, nombre, aprobado, especialidad, 
costo_consulta, lugar_atencion,
 foto_url, presentacion) values (2, '111111111', 'Bruce Branner', true, 'Dermatology', 50000, 'Cima Hospital, San Jose','BBanner.jpg', 'Médico altamente capacitado');
insert into medicos (usuario_id, cedula, nombre, aprobado, especialidad, 
costo_consulta, lugar_atencion,
 foto_url, presentacion) values (3, '222222222', 'Juan José Perez', true, 'Cardiology', 60800, 'Medical Center, Alajuela','JPerez.jpg', 'Médico altamente capacitado en cirugía cardiovascular');
insert into medicos (usuario_id, cedula, nombre, aprobado, especialidad, 
costo_consulta, lugar_atencion,
 foto_url, presentacion) values (4, '333333333', 'Luis Alfoncso Kajero', true, 'Cardiology', 70000, 'Heart Care Center, Alajuela','LKjero.jpg', 'Profesional dedicado a la atención');
 
 /* usuarios pacientes */
insert into usuarios (usuario, clave, rol) values ('GLucas', '444', 'Paciente');
insert into usuarios (usuario, clave, rol) values ('SLee', '555', 'Paciente');

 /*Pacientes*/
insert into pacientes (usuario_id, cedula, nombre, telefono, direccion, foto_url)
 values (5, '4444444444', 'Georges Lucas', '555-1234', 'Alajuela', 'GLucas.jpg');
insert into pacientes (usuario_id, cedula, nombre, telefono, direccion, foto_url)
 values (6, '5555555555', 'Stan Lee', '665-1245', 'San Jose', 'Slee.jpg');
 

insert into horarios_medicos (medico_id, dia, hora_inicio, hora_fin, frecuencia_minutos) 
values (1, 'LUNES', '08:00:00', '12:00:00', 60);
insert into horarios_medicos (medico_id, dia, hora_inicio, hora_fin, frecuencia_minutos) 
values (1, 'MARTES', '09:00:00', '16:00:00', 60);
insert into horarios_medicos (medico_id, dia, hora_inicio, hora_fin, frecuencia_minutos) 
values (1, 'MIERCOLES','08:00:00', '18:00:00', 30);
insert into horarios_medicos (medico_id, dia, hora_inicio, hora_fin, frecuencia_minutos) 
values (1, 'JUEVES', '10:00:00', '17:00:00', 30);
insert into horarios_medicos (medico_id, dia, hora_inicio, hora_fin, frecuencia_minutos) 
values (1, 'VIERNES', '08:00:00', '12:00:00', 30);

insert into horarios_medicos (medico_id, dia, hora_inicio, hora_fin, frecuencia_minutos) 
values (2, 'MARTES', '10:00:00', '14:00:00', 60);
insert into horarios_medicos (medico_id, dia, hora_inicio, hora_fin, frecuencia_minutos) 
values (2, 'JUEVES', '10:00:00', '14:00:00', 30);

insert into horarios_medicos (medico_id, dia, hora_inicio, hora_fin, frecuencia_minutos) 
values (3, 'SABADO', '09:00:00', '11:00:00', 120);
insert into horarios_medicos (medico_id, dia, hora_inicio, hora_fin, frecuencia_minutos) 
values (3, 'DOMINGO', '09:00:00', '11:00:00', 60);


UPDATE usuarios SET clave = '$2a$10$Rg4.yW64RMNp/iwzsToyceC5soZsAPkXZ2oQBNin60rfqh6a0N6Ei' WHERE usuario = 'Admin';
UPDATE usuarios SET clave = '$2a$10$qXBuV92uQ8HFZjDp.bAj3eJx5YCHOKBGYd97dgimjsjlL/llb4n/y' WHERE usuario = 'BBanner';
UPDATE usuarios SET clave = '$2a$10$DD6lzGOjyOpbGha2JLtA6.qcM2c9ZXdYhiw5cdtXP/TTI7PRkeun2' WHERE usuario = 'JPerez';
UPDATE usuarios SET clave = '$2a$10$DS6P0GShYYDmKY1yZgDq7.xzUMP1Jy4d5Myfr4HoMvERdCyXPciyC' WHERE usuario = 'LKjero';
UPDATE usuarios SET clave = '$2a$10$R6dAhvex7HjMboP5o.5Z/O.PxeCC1FojuYdsnTFEemyVsaz4mDjC2' WHERE usuario = 'GLucas';
UPDATE usuarios SET clave = '$2a$10$TrxOwOOjIqqK9NWLlrT1Uum5RHEvYOCLzebBa0L0sHEE/u9a24IeS' WHERE usuario = 'SLee';

select * from citas;