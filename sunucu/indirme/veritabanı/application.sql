-- phpMyAdmin SQL Dump
-- version 4.7.6
-- https://www.phpmyadmin.net/
--
-- Anamakine: localhost:3306
-- Üretim Zamanı: 01 Şub 2018, 20:46:13
-- Sunucu sürümü: 10.1.29-MariaDB
-- PHP Sürümü: 7.0.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Veritabanı: `id1285936_kutuphane`
--

-- --------------------------------------------------------

--
-- Tablo için tablo yapısı `application`
--

CREATE TABLE `application` (
  `id` int(11) NOT NULL,
  `versiyon` text COLLATE utf8_turkish_ci NOT NULL,
  `tur` text COLLATE utf8_turkish_ci NOT NULL,
  `boyut` int(11) NOT NULL,
  `link` text COLLATE utf8_turkish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Tablo döküm verisi `application`
--

INSERT INTO `application` (`id`, `versiyon`, `tur`, `boyut`, `link`) VALUES
(1, '0.2', 'apk', 2310000, '');

--
-- Dökümü yapılmış tablolar için indeksler
--

--
-- Tablo için indeksler `application`
--
ALTER TABLE `application`
  ADD PRIMARY KEY (`id`);

--
-- Dökümü yapılmış tablolar için AUTO_INCREMENT değeri
--

--
-- Tablo için AUTO_INCREMENT değeri `application`
--
ALTER TABLE `application`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
