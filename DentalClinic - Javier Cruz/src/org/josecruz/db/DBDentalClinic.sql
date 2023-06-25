Drop database if exists DBDentalClinic;
Create database DBDentalClinic;
Use DBDentalClinic;

Create table Pacientes(
	codigoPaciente int not null,
    nombresPaciente varchar(50) not null,
    apellidosPaciente varchar(50) not null,
    sexo char(1) not null,
    fechaNacimiento date not null,
    direccionPaciente varchar(100) not null,
    telefonoPersonal varchar(8) not null,
    fechaPrimerVisita date,
    primary key PK_codigoPaciente (codigoPaciente)
);

Create table Especialidades(
	codigoEspecialidad int auto_increment not null,
    descripcion varchar(100) not null,
    primary key PK_codigoEspecialidad (codigoEspecialidad)
);

Create table Medicamentos(
	codigoMedicamento int auto_increment not null,
    nombreMedicamento varchar(100) not null,
    primary key PK_codigoMedicamento (codigoMedicamento)
);

Create table Doctores(
	numeroColegiado int not null,
	nombresDoctor varchar(50) not null,
	apellidosDoctor varchar(50) not null,
    telefonoContacto varchar(8) not null,
    codigoEspecialidad int not null,
    primary key PK_numeroColegiado (numeroColegiado),
	constraint FK_Doctores_Especialidades foreign key (codigoEspecialidad)
		references Especialidades(codigoEspecialidad)
);

Create table Recetas(
	codigoReceta int auto_increment not null,
    fechaReceta date not null,
    numeroColegiado int not null,
    primary key PK_codigoReceta (codigoReceta),
    constraint FK_Recetas_Doctores foreign key (numeroColegiado)
		references Doctores(numeroColegiado)
);

Create table Citas(
	codigoCita int auto_increment not null,
    fechaCita date not null,
    horaCita time not null,
    tratamiento varchar(150),
    descripCondActual varchar(255) not null,
    codigoPaciente int not null,
    numeroColegiado int not null,
    primary key PK_codigoCita (codigoCita),
    constraint FK_Citas_Pacientes foreign key (codigoPaciente)
		references Pacientes(codigoPaciente),
	constraint FK_Citas_Doctores foreign key (numeroColegiado)
		references Doctores(numeroColegiado)
);

Create table DetalleReceta(
	codigoDetalleReceta int auto_increment not null,
    dosis varchar(100) not null,
    codigoReceta int not null,
    codigoMedicamento int not null,
    primary key PK_codigoDetalleReceta (codigoDetalleReceta),
    constraint FK_DetalleReceta_Recetas foreign key (codigoReceta)
		references Recetas(codigoReceta),
	constraint FK_DetalleReceta_Medicamentos foreign key (codigoMedicamento)
		references Medicamentos(codigoMedicamento)
);

-- ------------------------------------------------------------------------------------------------
-- ----------------------------------PROCEDIMIETOS ALMACENADOS-------------------------------------
-- --------------------------------------AGREGAR PACIENTES-----------------------------------------
Delimiter $$
	Create procedure sp_AgregarPaciente (in codigoPaciente int, in nombresPaciente varchar(50), 
		in apellidosPaciente varchar(50), in sexo char, in fechaNacimiento date,
        in direccionPaciente varchar(100), in telefonoPersonal varchar(8), in fechaPrimerVisita date)
        Begin
			insert into Pacientes(codigoPaciente, nombresPaciente, apellidosPaciente, sexo,
				fechaNacimiento, direccionPaciente, telefonoPersonal, fechaPrimerVisita)
                values (codigoPaciente, nombresPaciente, apellidosPaciente, upper(sexo),
					fechaNacimiento, direccionPaciente, telefonoPersonal, fechaPrimerVisita);
		End$$
Delimiter ;

call sp_AgregarPaciente (1001, 'Francisco', 'Castillo', 'm', '1982-08-17', 'Zona 14 Capital', '48934570', now());
call sp_AgregarPaciente (1002, 'Cinthya', 'Gracia', 'f', '2005-06-24', 'Zona 12 Capital', '46738200', '2022-01-10');
call sp_AgregarPaciente (1003, 'Mateo', 'Cruz', 'm', '1992-09-21', 'Zona 12 Capital', '55346721', '2022-02-01');
call sp_AgregarPaciente (1004, 'Juan Diego', 'Santay', 'm', '2001-10-10', 'Zona 18 Capital', '78553590', '2022-03-22');
call sp_AgregarPaciente (1005, 'Miguel', 'Muñoz', 'm', '1990-02-20', 'Zona 10 Capital', '53049657', '2022-05-01');

-- ---------------------------------------LISTAR PACIENTES-----------------------------------------
Delimiter $$
	create procedure sp_ListarPacientes ()
		Begin
			Select
				P.codigoPaciente, 
                P.nombresPaciente, 
                P.apellidosPaciente, 
                P.sexo, 
                P.fechaNacimiento, 
                P.direccionPaciente, 
                P.telefonoPersonal, 
                P.fechaPrimerVisita
                from Pacientes P;
        End$$
Delimiter ;

call sp_ListarPacientes();

-- ---------------------------------------BUSCAR PACIENTES-----------------------------------------
Delimiter $$
	create procedure sp_BuscarPaciente(in codPaciente int)
		Begin
			select
				P.codigoPaciente, 
                P.nombresPaciente, 
                P.apellidosPaciente, 
                P.sexo, 
                P.fechaNacimiento, 
                P.direccionPaciente, 
                P.telefonoPersonal, 
                P.fechaPrimerVisita
                from Pacientes P where codigoPaciente = codPaciente;
		End$$
Delimiter ;

call sp_BuscarPaciente(1001);

-- --------------------------------------ELIMINAR PACIENTES----------------------------------------
Delimiter $$
	Create procedure sp_BorrarPaciente (in codPaciente int)
		Begin
			delete from Pacientes
				where codigoPaciente = codPaciente;
		End$$
Delimiter ;


-- ----------------------------------------EDITAR PACIENTES----------------------------------------
Delimiter $$
	Create procedure sp_EditarPaciente (in codPaciente int, in nomPaciente varchar(50), 
		in apPaciente varchar(50), in sx char, in fechaNac date, in dirPaciente varchar(100),
        in telPersonal varchar(8), in fechaPV date)
        Begin
			Update Pacientes P 
				set 
					P.nombresPaciente = nomPaciente, 
                    P.apellidosPaciente = apPaciente, 
                    P.sexo = sx, 
                    P.fechaNacimiento = fechaNac, 
                    P.direccionPaciente = dirPaciente, 
                    P.telefonoPersonal = telPersonal, 
                    P.fechaPrimerVisita = fechaPV
                    where codigoPaciente = codPaciente;
		End$$
Delimiter ;


-- ---------------------------PROCEDIMIENTOS ALMACENADOS ESPECIALIDADES----------------------------
-- ------------------------------------AGREGAR ESPECIALIDADES--------------------------------------
Delimiter $$
	Create procedure sp_AgregarEspecialidad(in descripcion varchar(100))
		Begin
			insert into Especialidades(descripcion)
            values (descripcion);
        End$$
Delimiter ;

call sp_AgregarEspecialidad('Odontologia General');
call sp_AgregarEspecialidad('Ortopediatria');
call sp_AgregarEspecialidad('Endodoncista');
call sp_AgregarEspecialidad('Prostodoncista');

-- -------------------------------------LISTAR ESPECIALIDADES--------------------------------------
Delimiter $$
	Create procedure sp_ListarEspecialidades()
		Begin
			Select 
				E.codigoEspecialidad, 
                E.descripcion
                from Especialidades E;
        End$$
Delimiter ;

call sp_ListarEspecialidades();

-- -------------------------------------BUSCAR ESPECIALIDADES--------------------------------------
Delimiter $$
	Create procedure sp_BuscarEspecialidad(in codEsp int)
		Begin
			Select 
				E.codigoEspecialidad, 
                E.descripcion
                from Especialidades E where codigoEspecialidad = codEsp;
        End$$
Delimiter ;

call sp_BuscarEspecialidad(1);

-- -------------------------------------BORRAR ESPECIALIDADES--------------------------------------
Delimiter $$
	Create procedure sp_BorrarEspecialidad(in codEsp int)
		Begin
			Delete from Especialidades
				where codigoEspecialidad = codEsp;
        End$$
Delimiter ;

-- -------------------------------------EDITAR ESPECIALIDADES--------------------------------------
Delimiter $$
	Create procedure sp_EditarEspecialidad(in codEsp int, in descrip varchar(100))
		Begin
			Update Especialidades E
				set E.descripcion = descrip
                where codigoEspecialidad = codEsp;
        End$$
Delimiter ;

call sp_EditarEspecialidad(1, 'Odontologia General');


-- ---------------------------PROCEDIMIENTOS ALMACENADOS MEDICAMENTOS------------------------------
-- ------------------------------------AGREGAR MEDICAMENTOS----------------------------------------
Delimiter $$
	Create procedure sp_AgregarMedicamento(in nombreMedicamento varchar(100))
		Begin
			insert into Medicamentos(nombreMedicamento)
				values (nombreMedicamento);
        End$$
Delimiter ;

call sp_AgregarMedicamento('Acetaminofen');
call sp_AgregarMedicamento('Eritormicina');
call sp_AgregarMedicamento('Amoxicilina');
call sp_AgregarMedicamento('Tetraciclina');

-- -------------------------------------LISTAR MEDICAMENTOS----------------------------------------
Delimiter $$
	Create procedure sp_ListarMedicamentos()
		Begin
			Select 
				M.codigoMedicamento,
                M.nombreMedicamento
                from Medicamentos M;
        End$$
Delimiter ;

call sp_ListarMedicamentos();

-- -------------------------------------BUSCAR MEDICAMENTOS----------------------------------------
Delimiter $$
	Create procedure sp_BuscarMedicamento(in codMe int)
		Begin
			Select 
				M.codigoMedicamento,
                M.nombreMedicamento
                from Medicamentos M where codigoMedicamento = codMe; 
        End$$
Delimiter ;

call sp_BuscarMedicamento(1);

-- -------------------------------------BORRAR MEDICAMENTOS----------------------------------------
Delimiter $$
	Create procedure sp_BorrarMedicamento(in codMe int)
		Begin
			Delete from Medicamentos
				where codigoMedicamento = codMe;
        End$$
Delimiter ;



-- -------------------------------------EDITAR MEDICAMENTOS----------------------------------------
Delimiter $$
	Create procedure sp_EditarMedicamentos(in codMe int, in nomMe varchar(100))
		Begin
			Update Medicamentos M
				set M.nombreMedicamento = nomMe
                where codigoMedicamento = codMe;
        End$$
Delimiter ;


-- ---------------------------PROCEDIMIENTOS ALMACENADOS DOCTORES----------------------------------
-- ------------------------------------AGREGAR DOCTORES--------------------------------------------
Delimiter $$
	Create procedure sp_AgregarDoctor(in numeroColegiado int, in nombresDoctor varchar(50), in apellidosDoctor varchar(50), in telefonoContacto varchar(8), in codigoEspecialidad int)
		Begin
			Insert into Doctores(numeroColegiado, nombresDoctor, apellidosDoctor, telefonoContacto, codigoEspecialidad)
				values(numeroColegiado, nombresDoctor, apellidosDoctor, telefonoContacto, codigoEspecialidad);
		End$$
Delimiter ;

call sp_AgregarDoctor(1234, 'Giovanni Alejandro','Garcia Sajche', 45632789, 1);
call sp_AgregarDoctor(1235, 'Maria Jose','Cruz de la Cruz', 31456723, 3);
call sp_AgregarDoctor(1280, 'Hector Raul','Subuyuj Torres', 45659515, 3);
call sp_AgregarDoctor(1252, 'Lourdes Camila','Reyes Melgar', 77854980, 2);
call sp_AgregarDoctor(1233, 'Miguel Angel','Castillo Cruz', 42150536, 4);


-- ------------------------------------LISTAR DOCTORES---------------------------------------------
Delimiter $$
	Create procedure sp_ListarDoctores()
		Begin
			Select
				D.numeroColegiado, 
                D.nombresDoctor, 
                D.apellidosDoctor, 
                D.telefonoContacto, 
                D.codigoEspecialidad
				from Doctores D;
		End$$
Delimiter ;

call sp_ListarDoctores();


-- ------------------------------------BUSCAR DOCTORES---------------------------------------------
Delimiter $$
	Create procedure sp_BuscarDoctor(in numColegiado int)
		Begin 
			Select 
				D.numeroColegiado, 
                D.nombresDoctor, 
                D.apellidosDoctor, 
                D.telefonoContacto, 
                D.codigoEspecialidad
                from Doctores D where numeroColegiado = numColegiado;
		End$$
Delimiter ;

call sp_BuscarDoctor(1235);


-- ------------------------------------BORRAR DOCTORES---------------------------------------------
Delimiter $$
	Create procedure sp_BorrarDoctor(in numColegiado int)
		Begin
			Delete from Doctores
            where numeroColegiado = numColegiado;
		End$$
Delimiter ;


-- ------------------------------------EDITAR DOCTORES---------------------------------------------
Delimiter $$
	Create procedure sp_EditarDoctor(in numColegiado int, in nomDoctor varchar(50), in apeDoctor varchar(50), in telContacto varchar(8), in codEspecialidad int)
		Begin
			Update Doctores D 
				set 
					D.nombresDoctor = nomDoctor,
					D.apellidosDoctor = apeDoctor,
					D.telefonoContacto = telContacto,
					D.codigoEspecialidad = codEspecialidad
                    where numeroColegiado = numColegiado;
        End$$
Delimiter ;

call sp_EditarDoctor(1235,'Miguel Angel','Castillo Cifuentes', 42150536, 4 );

-- ---------------------------PROCEDIMIENTOS ALMACENADOS RECETAS-----------------------------------
-- ------------------------------------AGREGAR RECETA----------------------------------------------
Delimiter $$
	Create procedure sp_AgregarReceta(in fechaReceta date, in numeroColegiado int)
		Begin
			Insert into Recetas(fechaReceta, numeroColegiado)
				values(fechaReceta, numeroColegiado);
        End$$
Delimiter ;

call sp_AgregarReceta('2022-05-10', 1280);
call sp_AgregarReceta('2022-02-11', 1235);
call sp_AgregarReceta('2022-01-05', 1252);
call sp_AgregarReceta('2021-12-09', 1234);
call sp_AgregarReceta('2022-03-23', 1235);

-- ------------------------------------LISTAR RECETAS----------------------------------------------
Delimiter $$
	Create procedure sp_ListarRecetas()
		Begin
			Select
				R.codigoReceta, 
                R.fechaReceta, 
                R.numeroColegiado
                from Recetas R;
		End$$
Delimiter ;

call sp_ListarRecetas();

-- ------------------------------------BUSCAR RECETA-----------------------------------------------
Delimiter $$
	Create procedure sp_BuscarReceta(in codReceta int)
		Begin
			Select
				R.codigoReceta, 
                R.fechaReceta, 
                R.numeroColegiado
                from Recetas R where codigoReceta = codReceta ;
        End$$
Delimiter ;

call sp_BuscarReceta(1);

-- ------------------------------------BORRAR RECETA-----------------------------------------------
Delimiter $$
	Create procedure sp_BorrarReceta(in codReceta int)
		Begin 
			Delete from Recetas
				where codigoReceta = codReceta;
		End$$
Delimiter ;

call sp_BorrarReceta(2);

-- ------------------------------------EDITAR RECETA-----------------------------------------------
Delimiter $$
	Create procedure sp_EditarReceta(in codReceta int, in fechaRec date, in numColegiado int)
		Begin
			Update Recetas R 
				set
					R.fechaReceta = fechaRec, 
                    R.numeroColegiado = numColegiado
                    where codigoReceta = codReceta;
		End$$
Delimiter ;


-- ---------------------------PROCEDIMIENTOS ALMACENADOS CITAS-------------------------------------
-- ------------------------------------AGREGAR CITA------------------------------------------------
Delimiter $$
	Create procedure sp_AgregarCita(in fechaCita date, in horaCita time, in tratamiento varchar(150),
						in descripCondActual varchar(225), in codigoPaciente int, in numeroColegiado int)
		Begin
			Insert into Citas(fechaCita, horaCita, tratamiento, descripCondActual, codigoPaciente, numeroColegiado)
				values(fechaCita, horaCita, tratamiento, descripCondActual, codigoPaciente, numeroColegiado);
		End$$
Delimiter ;

call sp_AgregarCita('2022-01-10', '13:30', 'Reposo durante 2 semanas', 'Estable', 1004, 1280);
call sp_AgregarCita('2022-04-23', '12:30', 'Reposo absoluto', 'Estable', 1002, 1252);
call sp_AgregarCita('2022-03-20', '08:00', 'Reposo durante 1 semanas', 'Estable', 1003, 1235);


-- -------------------------------------LISTAR CITA------------------------------------------------
Delimiter $$
	Create procedure sp_ListarCitas()
		Begin
			Select
				C.codigoCita, 
                C.fechaCita, 
                C.horaCita, 
                C.tratamiento, 
                C.descripCondActual, 
                C.codigoPaciente, 
                C.numeroColegiado
                from Citas C;
		End$$
Delimiter ;

call sp_ListarCitas();

-- -------------------------------------BUSCAR CITA------------------------------------------------
Delimiter $$
	Create procedure sp_BuscarCita(in codCita int)
		Begin
			Select
				C.codigoCita, 
                C.fechaCita, 
                C.horaCita, 
                C.tratamiento, 
                C.descripCondActual, 
                C.codigoPaciente, 
                C.numeroColegiado
                from Citas C where codigoCita = codCita;
        End$$
Delimiter ;

call sp_BuscarCita(1);

-- -------------------------------------BORRAR CITA------------------------------------------------
Delimiter $$
	Create procedure sp_BorrarCita(in codCita int)
		Begin
			Delete from Citas
				where codigoCita = codCita;
		End$$
Delimiter ;


-- -------------------------------------EDITAR CITA------------------------------------------------
Delimiter $$
	Create procedure sp_EditarCita(in codCita int, in fechaCit date, in horaCit time, in trata varchar(150),
						in descCondActual varchar(225), in codPaciente int, in numColegiado int)
		Begin
			Update Citas C
				set
					C.fechaCita = fechaCit,
                    C.horaCita = horaCit,
                    C.tratamiento = trata,
                    C.descripCondActual = descCondActual,
                    C.codigoPaciente = codPaciente,
                    C.numeroColegiado = numColegiado
                    where codigoCita = codCita;
        End$$
Delimiter ;


-- ---------------------------PROCEDIMIENTOS DETALLE RECETA----------------------------------------
-- -----------------------------AGREGAR DETALLE RECETA---------------------------------------------
Delimiter $$
	Create procedure sp_AgregarDetalleReceta(in dosis varchar(100), in codigoReceta int, in codigoMedicamento int)
		Begin
			Insert into DetalleReceta(dosis, codigoReceta, codigoMedicamento)
				values(dosis, codigoReceta, codigoMedicamento);
        End$$
Delimiter ;

call sp_AgregarDetalleReceta('1 tableta c/8 hrs', 1, 2);
call sp_AgregarDetalleReceta('2 tableta c/12 hrs', 3, 1);
call sp_AgregarDetalleReceta('1 Sobre Antes de cada Comidad', 4, 3);
call sp_AgregarDetalleReceta('1/2 tableta c/8 hrs', 4, 3);


-- ------------------------------LISTAR DETALLE RECETA---------------------------------------------
Delimiter $$
	Create procedure sp_ListarDetalleRecetas()
		Begin
			Select 
				D.codigoDetalleReceta, 
                D.dosis, 
                D.codigoReceta, 
                D.codigoMedicamento
                from DetalleReceta D;
        End$$
Delimiter ;

call sp_ListarDetalleRecetas();
-- ------------------------------BUSCAR DETALLE RECETA---------------------------------------------
Delimiter $$
	Create procedure sp_BuscarDetalleReceta(in codDetalleReceta int)
		Begin
			Select 
				D.codigoDetalleReceta, 
                D.dosis, 
                D.codigoReceta, 
                D.codigoMedicamento
                from DetalleReceta D where codigoDetalleReceta = codDetalleReceta;
		End$$
Delimiter ;

-- ------------------------------BORRAR DETALLE RECETA---------------------------------------------
Delimiter $$
	Create procedure sp_BorrarDetalleReceta(in codDetalleReceta int)
		Begin
			Delete from DetalleReceta
				where codigoDetalleReceta = codDetalleReceta;
        End$$
Delimiter ;

-- ------------------------------EDITAR DETALLE RECETA---------------------------------------------
Delimiter $$
	Create procedure sp_EditarDetalleReceta(in codDetalleReceta int, in dos varchar(100), in codReceta int, in codMedicamento int)
		Begin
			Update DetalleReceta D
				set
					D.dosis = dos,
                    D.codigoReceta = codReceta,
                    D.codigoMedicamento = codMedicamento
                    where codigoDetalleReceta = codDetalleReceta;
        End$$
Delimiter ;

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '2018059';

Create table Usuario(
	codigoUsuario int not null auto_increment,
    nombreUsuario varchar(100) not null,
    apellidoUsuario varchar(100) not null,
    usuarioLogin varchar(50) not null,
    contrasena varchar(50) not null,
    primary key PK_codigoUsuario (codigoUsuario)
);

Delimiter $$
	Create procedure sp_AgregarUsuario(in nombreUsuario varchar(100), in apellidoUsuario varchar(100), in usuarioLogin varchar(50), in contrasena varchar(50))
		Begin
			Insert into Usuario(nombreUsuario, apellidoUsuario, usuarioLogin, contrasena)
				values(nombreUsuario, apellidoUsuario, usuarioLogin, contrasena);
        End$$
Delimiter ;

call sp_AgregarUsuario('Javier', 'Cruz', 'jcruz', '2018059');

Delimiter $$
	Create procedure sp_ListarUsuarios()
		Begin
			Select
				U.codigoUsuario,
                U.nombreUsuario,
                U.apellidoUsuario,
                U.usuarioLogin,
                U.contrasena
                from Usuario U;
        End$$
Delimiter ;

call sp_ListarUsuarios();

Create table Login(
	usuarioMaster varchar(50) not null,
    passwordLogin varchar(50) not null,
    primary key PK_usuarioMaster (usuarioMaster)
);

