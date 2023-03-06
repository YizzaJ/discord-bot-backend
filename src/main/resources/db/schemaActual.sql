DROP DATABASE IF EXISTS NEWSBOTDB;

CREATE DATABASE NEWSBOTDB;
USE NEWSBOTDB;

CREATE TABLE Provider (
    nombre varchar(255) NOT NULL,
    link varchar(255) NOT NULL,
	uso_articulo varchar(255) NOT NULL,
    tipo_articulo varchar(255) NOT NULL,
    attributeName_articulo varchar(255),
    valor_articulo varchar(255) NOT NULL,
	uso_parrafo varchar(255) NOT NULL,
    tipo_parrafo varchar(255) NOT NULL,
    attributeName_parrafo varchar(255),
    valor_parrafo varchar(255) NOT NULL,
	uso_topic varchar(255) NOT NULL,
    tipo_topic varchar(255) NOT NULL,
    attributeName_topic varchar(255),
    valor_topic varchar(255) NOT NULL,

    PRIMARY KEY (link)
);
CREATE TABLE User(
	ID int NOT NULL auto_increment,
    username varchar(255) NOT NULL,
    servername varchar(255) NOT NULL,
    provider varchar(255) NOT NULL,

    PRIMARY KEY (ID, username),
    FOREIGN KEY (provider) REFERENCES Provider(link),
    FOREIGN KEY (servername) REFERENCES Server(servername)
);

CREATE TABLE Server(
	ID int NOT NULL,
    servername varchar(255) NOT NULL,

    PRIMARY KEY (ID)
);














