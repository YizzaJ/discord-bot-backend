DROP DATABASE IF EXISTS NEWSBOTDB;

CREATE DATABASE NEWSBOTDB;
USE NEWSBOTDB;

CREATE TABLE Provider (
    nombre varchar(255) NOT NULL,
    link varchar(255) NOT NULL,

    PRIMARY KEY (link)
);
CREATE TABLE User(
	ID int NOT NULL auto_increment,
    username varchar(255) NOT NULL,
    provider varchar(255) NOT NULL,
    
    PRIMARY KEY (ID, username),
    FOREIGN KEY (provider) REFERENCES Provider(link)
);

CREATE TABLE Property (
	provider varchar(255) NOT NULL,
	uso varchar(255) NOT NULL,
    tipo varchar(255) NOT NULL,
    attributeName varchar(255),
    valor varchar(255) NOT NULL,

    PRIMARY KEY (provider, uso),
    FOREIGN KEY (provider) REFERENCES Provider(link)
);













