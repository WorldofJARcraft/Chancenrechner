-- phpMyAdmin SQL Dump
-- version 4.1.12
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Erstellungszeit: 27. Feb 2016 um 18:23
-- Server Version: 5.6.16
-- PHP-Version: 5.5.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Datenbank: `chancenrechner`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `android_connect`
--

CREATE TABLE IF NOT EXISTS `android_connect` (
  `user` varchar(255) NOT NULL,
  `password` text NOT NULL,
  `aktiv` tinyint(1) NOT NULL DEFAULT '0',
  `Startzeit` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Gesamtzeit` int(11) DEFAULT NULL,
  `Empfehlungen` longtext,
  PRIMARY KEY (`user`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `android_connect`
--

INSERT INTO `android_connect` (`user`, `password`, `aktiv`, `Startzeit`, `Gesamtzeit`, `Empfehlungen`) VALUES
('admin', 'password', 0, '2016-02-27 13:39:38', NULL, 'Schießen Sie!'),
('Boss', 'bosshaft', 0, '2016-02-27 14:20:08', NULL, 'Nicht mehr nötig!'),
('EA_Master', 'Eric', 1, '2016-02-27 14:26:06', NULL, 'Nicht vorhanden.');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
