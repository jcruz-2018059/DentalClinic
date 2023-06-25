Drop database if exists DBWebApp;
Create database DBWebApp;

Use DBWebApp;

Create table Persona(
	codigoPersona int not null auto_increment,
    DPI varchar(13) not null,
    nombrePersona varchar(150) not null,
    primary key PK_codigoPersona (codigoPersona)
);

Insert into Persona (DPI, nombrePersona)
Values ('2418963390103', 'Javier Cruz');
Insert into Persona (DPI, nombrePersona)
Values ('2925353390103', 'Sofia Cruz');
Insert into Persona (DPI, nombrePersona)
Values ('1343423390103', 'Majo Cruz');

Select * from Persona;