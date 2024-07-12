DROP SCHEMA IF EXISTS `podsistem3_db` ;
CREATE SCHEMA `podsistem3_db`;
USE `podsistem3_db` ;

DROP TABLE IF EXISTS `korisnik`;
CREATE TABLE `korisnik` (
  `SifK` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`SifK`),
  UNIQUE KEY `SifK_UNIQUE` (`SifK`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci AUTO_INCREMENT=1;

LOCK TABLES `korisnik` WRITE;
INSERT INTO korisnik (SifK)
VALUES
  (1),
  (2),
  (3),
  (4),
  (5),
  (6),
  (7),
  (8),
  (9),
  (10),
  (11),
  (12),
  (13),
  (14),
  (15);

UNLOCK TABLES;



DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
  `SifV` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`SifV`),
  UNIQUE KEY `SifV_UNIQUE` (`SifV`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci AUTO_INCREMENT=1;

LOCK TABLES `video` WRITE;
INSERT INTO video (SifV)
VALUES
  (1),
  (2),
  (3),
  (4),
  (5),
  (6),
  (7),
  (8),
  (9),
  (10),
  (11),
  (12),
  (13),
  (14),
  (15);

UNLOCK TABLES;

DROP TABLE IF EXISTS `gledanje`;
CREATE TABLE `gledanje` (
  `SifG` int NOT NULL AUTO_INCREMENT,
  `SifK` int NOT NULL,
  `SifV` int NOT NULL,
  `DatumVreme` datetime NOT NULL,
  `Zapoceto` int NOT NULL,
  `Odgledano` int NOT NULL,
  PRIMARY KEY (`SifG`),
  UNIQUE KEY `SifG_UNIQUE` (`SifG`),
  KEY `SifK` (`SifK`),
  KEY `SifV` (`SifV`),
  CONSTRAINT `korisnik_ibfk_1` FOREIGN KEY (`SifK`) REFERENCES `korisnik` (`SifK`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `video_ibfk_2` FOREIGN KEY (`SifV`) REFERENCES `video` (`SifV`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci AUTO_INCREMENT=1;

LOCK TABLES `gledanje` WRITE;
INSERT INTO `gledanje` (`SifK`, `SifV`, `DatumVreme`, `Zapoceto`, `Odgledano`) VALUES
(1, 1, '2024-06-16 07:59:15', 0, 180),
(1, 2, '2024-06-15 08:02:00', 0, 200),
(2, 3, '2024-06-17 08:07:00', 0, 200),
(3, 4, '2024-06-14 08:11:33', 22, 198),
(4, 5, '2024-06-13 08:18:00', 0, 100),
(5, 1, '2024-06-17 08:22:00', 5, 160),
(6, 2, '2024-06-11 08:26:41', 80, 120),
(7, 13, '2024-06-08 09:30:03', 5, 300),
(8, 14, '2024-06-08 09:38:55', 2, 250),
(15, 15, '2024-06-07 09:41:20', 30, 300);

UNLOCK TABLES;

DROP TABLE IF EXISTS `ocena`;
CREATE TABLE `ocena`(
  `SifK` int NOT NULL,
  `SifV` int NOT NULL,
  `Ocena` int NOT NULL,
  `DatumVreme` datetime NOT NULL,
  PRIMARY KEY (`SifK`, `SifV`),
  KEY `FK_v_idx` (`SifV`),
  CONSTRAINT `FK_k` FOREIGN KEY (`SifK`) REFERENCES `korisnik` (`SifK`) ON UPDATE CASCADE,
  CONSTRAINT `FK_v` FOREIGN KEY (`SifV`) REFERENCES `video` (`SifV`) ON UPDATE CASCADE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

LOCK TABLES `ocena` WRITE;
INSERT INTO `ocena` (`SifK`, `SifV`, `Ocena`, `DatumVreme`) VALUES
(1, 1, 5, '2024-06-16 08:00:15'),
(1, 2, 4, '2024-06-15 08:05:03'),
(2, 3, 3, '2024-06-17 08:10:00'),
(3, 4, 2, '2024-06-14 08:15:33'),
(4, 5, 1, '2024-06-13 08:20:00'),
(5, 1, 5, '2024-06-17 08:25:00'),
(6, 2, 4, '2024-06-11 08:30:41'),
(7, 13, 3, '2024-06-08 09:35:00'),
(8, 14, 2, '2024-06-08 09:40:55'),
(15, 15, 1, '2024-06-07 09:45:00');
UNLOCK TABLES;


DROP TABLE IF EXISTS `paket`;

CREATE TABLE `paket` (
  `SifPak` int NOT NULL AUTO_INCREMENT,
  `Naziv` varchar(45),
  `Cena` int NOT NULL,
  PRIMARY KEY (`SifPak`),
  UNIQUE KEY `SifPak_UNIQUE` (`SifPak`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci AUTO_INCREMENT=1;

LOCK TABLES `paket` WRITE;
INSERT INTO `paket` (`Cena`, `Naziv`) VALUES (200, 'Osnovni'), (500, 'Premium');
UNLOCK TABLES;


DROP TABLE IF EXISTS `pretplata`;
CREATE TABLE `pretplata` (
  `SifPre` int NOT NULL AUTO_INCREMENT,
  `SifK` int NOT NULL,
  `SifPak` int NOT NULL,
  `DatumVreme` datetime NOT NULL,
  `Cena` int NOT NULL,
  PRIMARY KEY (`SifPre`),
  KEY `FK_korisnik_idx` (`SifK`),
  KEY `FK_paket_idx` (`SifPak`),
  CONSTRAINT `FK_korisnik` FOREIGN KEY (`SifK`) REFERENCES `korisnik` (`SifK`) ON UPDATE CASCADE,
  CONSTRAINT `FK_paket` FOREIGN KEY (`SifPak`) REFERENCES `paket` (`SifPak`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci AUTO_INCREMENT=1;

LOCK TABLES `pretplata` WRITE;
INSERT INTO `pretplata` (`SifK`, `SifPak`, `DatumVreme`, `Cena`) VALUES
(1, 1, '2024-06-15 08:00:00', 200),
(2, 2, '2024-06-11 09:00:00', 500),
(3, 1, '2024-06-10 10:00:00', 200),
(4, 2, '2024-06-09 11:00:00', 500),
(5, 1, '2024-06-01 12:00:00', 200),
(6, 2, '2024-05-17 13:00:00', 500),
(7, 1, '2024-06-03 14:00:00', 200),
(8, 2, '2024-06-07 15:00:00', 500),
(9, 1, '2024-06-10 16:00:00', 200),
(15, 2, '2024-06-01 17:00:00', 500);
UNLOCK TABLES;