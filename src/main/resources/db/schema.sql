DROP DATABASE IF EXISTS NEWSBOTDB;

CREATE DATABASE NEWSBOTDB;
USE NEWSBOTDB;

CREATE TABLE Property (
    ID int NOT NULL,
	uso varchar(255) NOT NULL,
    tipo varchar(255) NOT NULL,
    attributeName varchar(255),
    valor varchar(255) NOT NULL,

    PRIMARY KEY (ID)
);

CREATE TABLE ScrapingProperties (
    ID int NOT NULL,
	article int NOT NULL,
    firstParagraph int NOT NULL,
    topic int NOT NULL,
    
    PRIMARY KEY (ID),
    FOREIGN KEY (article) REFERENCES Property(ID),
    FOREIGN KEY (firstParagraph) REFERENCES Property(ID),
    FOREIGN KEY (topic) REFERENCES Property(ID)
);

CREATE TABLE Provider (
    ID int NOT NULL,
    nombre varchar(255) NOT NULL,
    link varchar(80),
    properties int NOT NULL,
    
    PRIMARY KEY (ID),
    FOREIGN KEY (properties) REFERENCES ScrapingProperties(ID)
);

CREATE TABLE User(
    ID int NOT NULL,
    username varchar(255) NOT NULL,
    provider int NOT NULL,
    
    PRIMARY KEY (ID),
    FOREIGN KEY (provider) REFERENCES Provider(ID)
);





