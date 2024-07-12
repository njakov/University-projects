DROP SCHEMA IF EXISTS `podsistem2_db` ;
CREATE SCHEMA `podsistem2_db`;
USE `podsistem2_db` ;

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
  `Naziv` varchar(45) NOT NULL,
  `Trajanje` int NOT NULL,
  `SifK` int NOT NULL,
  `DatumVreme` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`SifV`),
  UNIQUE KEY `SifV_UNIQUE` (`SifV`),
  KEY `SifK` (`SifK`),
  CONSTRAINT `korisnik_ibfk_1` FOREIGN KEY (`SifK`) REFERENCES `korisnik` (`SifK`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci AUTO_INCREMENT=1;

LOCK TABLES `video` WRITE;
INSERT INTO `video` (`Naziv`, `Trajanje`, `SifK`, `DatumVreme`) VALUES
('Molitva', 180, 1, '2007-07-27 00:00:00'),
('Lane Moje', 200, 2, '2004-01-01 00:00:00'),
('Nije Ljubav Stvar', 210, 3, '2012-01-01 00:00:00'),
('Oro', 220, 4, '2008-01-01 00:00:00'),
('Bitanga i princeza', 230, 5, '1979-03-06 00:00:00'),
('In Too Deep', 240, 6, '2017-01-01 00:00:00'),
('Beauty Never Lies', 250, 7, '2015-01-01 00:00:00'),
('Goodbye (Shelter)', 260, 8, '2016-01-01 00:00:00'),
('Nova Deca', 270, 9, '2018-01-01 00:00:00'),
('Kruna', 280, 10, '2019-03-20 00:00:00'),
('Bajaga i Instruktori - Moji drugovi', 290, 11, '1994-01-01 00:00:00'),
('Riblja Čorba - Amsterdam', 300, 12, '2023-08-23 00:00:00'),
('Jutro', 310, 13, '1980-01-01 00:00:00'),
('Pogledaj Dom Svoj, Anđele', 320, 14, '1985-03-27 00:00:00'),
('Molitva Za Magdalenu', 330, 15, '1978-01-01 00:00:00');

UNLOCK TABLES;

DROP TABLE IF EXISTS `kategorija`;
CREATE TABLE `kategorija`(
  `SifKat` int NOT NULL AUTO_INCREMENT,
  `Naziv` varchar(45) NOT NULL,
  PRIMARY KEY (`SifKat`),
  UNIQUE KEY `SifKat_UNIQUE` (`SifKat`)
) ENGINE=InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci AUTO_INCREMENT=1;

LOCK TABLES `kategorija` WRITE;

INSERT INTO `kategorija` (`Naziv`) VALUES
('Pop Ballad'), -- For 'Molitva', 'Lane Moje', 'Nije Ljubav Stvar', 'Oro'
('Rock'), -- For 'Bitanga i princeza', 'Bajaga i Instruktori - Moji drugovi', 'Riblja Čorba - Amsterdam'
('Pop Rock'), -- For 'In Too Deep', 'Goodbye (Shelter)'
('Ethno Pop'), -- For 'Nova Deca', 'Kruna'
('Classic Rock'), -- For 'Jutro', 'Pogledaj Dom Svoj, Anđele'
('Folk Ballad'); -- For 'Molitva Za Magdalenu'

UNLOCK TABLES;


DROP TABLE IF EXISTS `pripada`;

CREATE TABLE `pripada` (
	`SifP` INT PRIMARY KEY AUTO_INCREMENT,
    `SifV_id` INT NOT NULL,
    `SifKat_id` INT NOT NULL,
    FOREIGN KEY (`SifKat_id`) REFERENCES `kategorija` (`SifKat`)
		ON DELETE NO ACTION
        ON UPDATE NO ACTION, 
	FOREIGN KEY (`SifV_id`) REFERENCES `video` (`SifV`) 
		ON DELETE CASCADE
        ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
    
LOCK TABLES `pripada` WRITE;
INSERT INTO `pripada` (`SifV_id`, `SifKat_id`) VALUES
(1, 1), -- 'Molitva' as Pop Ballad
(2, 1), -- 'Lane Moje' as Pop Ballad
(3, 1), -- 'Nije Ljubav Stvar' as Pop Ballad
(4, 1), -- 'Oro' as Pop Ballad
(5, 2), -- 'Bitanga i princeza' as Rock
(6, 3), -- 'In Too Deep' as Pop Rock
(7, 3), -- 'Beauty Never Lies' as Pop Rock
(8, 3), -- 'Goodbye (Shelter)' as Pop Rock
(9, 4), -- 'Nova Deca' as Ethno Pop
(10, 4), -- 'Kruna' as Ethno Pop
(11, 2), -- 'Bajaga i Instruktori - Moji drugovi' as Rock
(12, 2), -- 'Riblja Čorba - Amsterdam' as Rock
(13, 5), -- 'Jutro' as Classic Rock
(14, 5), -- 'Pogledaj Dom Svoj, Anđele' as Classic Rock
(15, 6); -- 'Molitva Za Magdalenu' as Folk Ballad
UNLOCK TABLES;
