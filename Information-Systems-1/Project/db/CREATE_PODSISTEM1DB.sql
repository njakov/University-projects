DROP SCHEMA IF EXISTS `podsistem1_db` ;
CREATE SCHEMA `podsistem1_db`;
USE `podsistem1_db` ;

DROP TABLE IF EXISTS `mesto`;

CREATE TABLE `mesto` (
  `SifM` int NOT NULL AUTO_INCREMENT,
  `Naziv` varchar(45) NOT NULL,
  PRIMARY KEY (`SifM`),
  UNIQUE KEY `SifM_UNIQUE` (`SifM`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci AUTO_INCREMENT=16;

LOCK TABLES `mesto` WRITE;
INSERT INTO `mesto` VALUES (1,'Beograd'),(2,'Novi Sad'),(3,'Niš'),(4,'Kruševac'),(5,'Kragujevac'),(6,'Kraljevo'),(7,'Valjevo'),(8,'Zaječar'),(9,'Zrenjanin'),(10,'Subotica'),(11,'Čačak'),(12,'Leskovac'),(13,'Pančevo'),(14,'Užice'),(15,'Pirot');
UNLOCK TABLES;

DROP TABLE IF EXISTS `korisnik`;

CREATE TABLE `korisnik` (
  `SifK` int NOT NULL AUTO_INCREMENT,
  `Ime` varchar(45) NOT NULL,
  `Email` varchar(45) NOT NULL,
  `Godiste` int NOT NULL,
  `Pol` char(1) NOT NULL,
  `SifM` int NOT NULL,
  PRIMARY KEY (`SifK`),
  UNIQUE KEY `SifK_UNIQUE` (`SifK`),
  UNIQUE KEY `Email_UNIQUE` (`Email`),
  KEY `SifM` (`SifM`),
  CONSTRAINT `korisnik_ibfk_1` FOREIGN KEY (`SifM`) REFERENCES `mesto` (`SifM`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci AUTO_INCREMENT=1;

LOCK TABLES `korisnik` WRITE;
INSERT INTO korisnik (Ime, Email, Godiste, Pol, SifM)
VALUES
  ('Marko', 'marko@gmail.com', 1985, 'M', 1),
  ('Jelena', 'jelena@gmail.com', 1990, 'F', 2),
  ('Nikola', 'nikola@outlook.com', 1988, 'M', 3),
  ('Ana', 'ana@outlook.com', 1992, 'F', 4),
  ('Milos', 'milos@gmail.com', 1987, 'M', 5),
  ('Marija', 'marija@gmail.com', 1991, 'F', 6),
  ('Ivan', 'ivan@outlook.com', 1986, 'M', 7),
  ('Katarina', 'katarina@outlook.com', 1993, 'F', 8),
  ('Aleksandar', 'aleksandar@gmail.com', 1989, 'M', 9),
  ('Jovana', 'jovana@outlook.com', 1994, 'F', 10),
  ('Petar', 'petar@gmail.com', 1990, 'M', 11),
  ('Milica', 'milica@outlook.com', 1992, 'F', 12),
  ('Stefan', 'stefan@gmail.com', 1988, 'M', 13),
  ('Sara', 'sara@outlook.com', 1993, 'F', 14),
  ('Luka', 'luka@gmail.com', 1987, 'M', 15);

UNLOCK TABLES;
