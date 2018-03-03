-- phpMyAdmin SQL Dump
-- version 4.7.6
-- https://www.phpmyadmin.net/
--
-- Anamakine: localhost:3306
-- Üretim Zamanı: 01 Şub 2018, 20:46:25
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
-- Tablo için tablo yapısı `kutuphane`
--

CREATE TABLE `kutuphane` (
  `id` int(11) NOT NULL,
  `isim` text COLLATE utf8_turkish_ci NOT NULL,
  `link` text COLLATE utf8_turkish_ci NOT NULL,
  `tur` text COLLATE utf8_turkish_ci NOT NULL,
  `boyut` int(11) NOT NULL,
  `indirme` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_turkish_ci;

--
-- Tablo döküm verisi `kutuphane`
--

INSERT INTO `kutuphane` (`id`, `isim`, `link`, `tur`, `boyut`, `indirme`) VALUES
(1, 'Çaka Bey', 'Caka Bey - Yavuz Bahadiroglu.epub', '.epub', 243017, 10),
(2, 'Efendi İle Uşağı', 'Efendi ile Usagi - Lev Tolstoy.epub', '.epub', 163727, 0),
(3, 'Kaşağı', 'Kasagi - Omer Seyfettin.epub', '.epub', 126346, 0),
(4, 'İnsan Ne İle Yaşar', 'Lev Nikolayevic Tolstoy - Insan Ne Ile Yasar.epub', '.epub', 2121411, 3),
(5, 'Tarihi Değiştiren Konuşmalar', 'Ali Cimen - Tarihi Degistiren Konusmalar.epub', '.epub', 6918558, 17),
(6, 'Simyacılar', 'Paulo Coelho - Simyaci.epub', '.epub', 189270, 0),
(7, 'Robin Hood', 'Howard Pyle - Robin Hood.epub', '.epub', 1034579, 0),
(8, 'Moby Dick', 'Moby Dick.pdf', '.pdf', 1058673, 18),
(9, 'İnsancıklar', 'Insanciklar - Dostoyevski.epub', '.epub', 178620, 0),
(10, 'Veba', 'Albert Camus - Veba.epub', '.epub', 609594, 1),
(11, 'Sefiller', 'Victor Hugo - Sefiller.epub', '.epub', 434518, 0),
(12, 'Cumhuriyetin İlk Yüzyılı', 'Ilber Ortayli - Cumhuriyetin Ilk Yuzyili.epub', '.epub', 377376, 2),
(13, 'Ateşten Gömlek', 'Halide Edip Adivar - Atesten Gomlek.epub', '.epub', 256460, 1),
(14, 'Bulut Atlası', 'Bulut Atlasi - David Mitchell.epub', '.epub', 759639, 2),
(15, 'Suç Ve Ceza', 'Fyodor Mihailovic Dostoyevski - Suc ve Ceza.epub', '.epub', 573546, 2);

--
-- Dökümü yapılmış tablolar için indeksler
--

--
-- Tablo için indeksler `kutuphane`
--
ALTER TABLE `kutuphane`
  ADD PRIMARY KEY (`id`);

--
-- Dökümü yapılmış tablolar için AUTO_INCREMENT değeri
--

--
-- Tablo için AUTO_INCREMENT değeri `kutuphane`
--
ALTER TABLE `kutuphane`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
