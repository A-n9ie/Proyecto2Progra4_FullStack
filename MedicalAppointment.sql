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
    horario_inicio TIME,
    horario_fin TIME,
    frecuencia_citas varchar (20),
    foto_url VARCHAR(255),
    presentacion TEXT,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) 
);

CREATE TABLE horarios_medicos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medico_id INT,
    dia ENUM('Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'),
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
costo_consulta, lugar_atencion, horario_inicio, horario_fin, frecuencia_citas,
 foto_url, presentacion) values (2, '111111111', 'Bruce Branner', true, 'Dermatology', 50000, 'Cima Hospital, San Jose',
 '08:00:00', '16:00:00', '30 minutos',  'BBanner.jpg', 'Médico altamente capacitado');
insert into medicos (usuario_id, cedula, nombre, aprobado, especialidad, 
costo_consulta, lugar_atencion, horario_inicio, horario_fin, frecuencia_citas,
 foto_url, presentacion) values (3, '222222222', 'Juan José Perez', true, 'Cardiology', 60800, 'Medical Center, Alajuela',
 '08:00:00', '16:00:00', '1 horas',  'JPerez.jpg', 'Médico altamente capacitado en cirugía cardiovascular');
insert into medicos (usuario_id, cedula, nombre, aprobado, especialidad, 
costo_consulta, lugar_atencion, horario_inicio, horario_fin, frecuencia_citas,
 foto_url, presentacion) values (4, '333333333', 'Luis Alfoncso Kajero', true, 'Cardiology', 70000, 'Heart Care Center, Alajuela',
 '11:00:00', '19:00:00', '1 horas',  'LKjero.jpg', 'Profesional dedicado a la atención');
 
 /* usuarios pacientes */
insert into usuarios (usuario, clave, rol) values ('GLucas', '444', 'Paciente');
insert into usuarios (usuario, clave, rol) values ('SLee', '555', 'Paciente');

 /*Pacientes*/
insert into pacientes (usuario_id, cedula, nombre, telefono, direccion, foto_url)
 values (5, '4444444444', 'Georges Lucas', '555-1234', 'Alajuela', 'GLucas.jpg');
insert into pacientes (usuario_id, cedula, nombre, telefono, direccion, foto_url)
 values (6, '5555555555', 'Stan Lee', '665-1245', 'San Jose', 'Slee.jpg');
 

insert into horarios_medicos (medico_id, dia) 
values (1, 'Lunes');
insert into horarios_medicos (medico_id, dia) 
values (1, 'Martes');
insert into horarios_medicos (medico_id, dia) 
values (1, 'Miercoles');
insert into horarios_medicos (medico_id, dia) 
values (1, 'Jueves');
insert into horarios_medicos (medico_id, dia) 
values (1, 'Viernes');

insert into horarios_medicos (medico_id, dia) 
values (2, 'Martes');
insert into horarios_medicos (medico_id, dia) 
values (2, 'Jueves');

insert into horarios_medicos (medico_id, dia) 
values (3, 'Sabado');
insert into horarios_medicos (medico_id, dia) 
values (3, 'Domingo');


UPDATE usuarios SET clave = '$2a$10$Rg4.yW64RMNp/iwzsToyceC5soZsAPkXZ2oQBNin60rfqh6a0N6Ei' WHERE usuario = 'Admin';
UPDATE usuarios SET clave = '$2a$10$qXBuV92uQ8HFZjDp.bAj3eJx5YCHOKBGYd97dgimjsjlL/llb4n/y' WHERE usuario = 'BBanner';
UPDATE usuarios SET clave = '$2a$10$DD6lzGOjyOpbGha2JLtA6.qcM2c9ZXdYhiw5cdtXP/TTI7PRkeun2' WHERE usuario = 'JPerez';
UPDATE usuarios SET clave = '$2a$10$DS6P0GShYYDmKY1yZgDq7.xzUMP1Jy4d5Myfr4HoMvERdCyXPciyC' WHERE usuario = 'LKjero';
UPDATE usuarios SET clave = '$2a$10$R6dAhvex7HjMboP5o.5Z/O.PxeCC1FojuYdsnTFEemyVsaz4mDjC2' WHERE usuario = 'GLucas';
UPDATE usuarios SET clave = '$2a$10$TrxOwOOjIqqK9NWLlrT1Uum5RHEvYOCLzebBa0L0sHEE/u9a24IeS' WHERE usuario = 'SLee';

select * from citas;