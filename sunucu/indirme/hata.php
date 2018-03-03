<?php
include("veritabaniBaglantisi.php");

if(isset($_POST['kitapGonderen']) && isset($_POST['kitapId']) && isset($_POST['kitapIsmi']) && isset($_POST['kitapKonu'])) {
    date_default_timezone_set('Europe/Moscow');
    $tarih = date("d.m.Y")." ".date("H:i");
    $gonderen = $_POST['kitapGonderen'];
    $id = (int)$_POST['kitapId'];
    $isim = $_POST['kitapIsmi'];
    $konu = $_POST['kitapKonu'];
	$sorgu = $baglanti->prepare("INSERT INTO mesaj (tarih, gonderen, kitapId, kitap, konu) VALUES (:tarih, :gonderen, :kitapId, :kitap, :konu)");
	$sorgu->bindParam(':tarih', $tarih, PDO::PARAM_STR);
	$sorgu->bindParam(':gonderen', $gonderen, PDO::PARAM_STR);
	$sorgu->bindParam(':kitapId', $id, PDO::PARAM_STR);
	$sorgu->bindParam(':kitap', $isim, PDO::PARAM_STR);
	$sorgu->bindParam(':konu', $konu, PDO::PARAM_STR);
	$sorgu->execute();
}

?>