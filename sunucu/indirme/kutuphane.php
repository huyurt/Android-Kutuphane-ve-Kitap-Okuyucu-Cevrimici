<?php
include("veritabaniBaglantisi.php");

if(isset($_POST['kitapId']) && isset($_POST['kitapIsmi']) && isset($_POST['kitapIndirmeDurumu'])) {
    $id = (int)$_POST['kitapId'];
    $isim = $_POST['kitapIsmi'];
	$durum = (int)$_POST['kitapIndirmeDurumu'];
	$indirme = 0;
	$sunucu = "ftp.openload.co";
	$kullaniciAdi = "7c3c9b7ab6325aa2";
	$kullaniciSifre = "O6-X9pp2";
	$sorgu = $baglanti->prepare("SELECT link, tur, boyut, indirme FROM kutuphane WHERE id = :id AND isim = :isim");
	$sorgu->bindParam(':id', $id, PDO::PARAM_STR);
	$sorgu->bindParam(':isim', $isim, PDO::PARAM_STR);
	$sorgu->execute();
	if($sorgu->rowCount() > 0) {
		while($row = $sorgu->fetch(PDO::FETCH_ASSOC)){
			$dizi[] = $row['link'];
			$dizi[] = $row['tur'];
			$dizi[] = $row['boyut'];
			$indirme = $row['indirme'];
		}
		
		if($durum == 0) {
		    $indirme += 1;
			$sorgu = $baglanti->prepare("UPDATE kutuphane SET indirme = :indirme WHERE id = :id");
			$sorgu->bindParam(':indirme', $indirme, PDO::PARAM_STR);
			$sorgu->bindParam(':id', $id, PDO::PARAM_STR);
			$sorgu->execute();
		}
	}

	for($i = 0; $i < count($dizi); $i++) {
		$dizi2[] = array("kitapLink" => $dizi[$i], "kitapTur" => $dizi[++$i], "kitapBoyut" => $dizi[++$i], "kitapS" => $sunucu, "kitapK1" => $kullaniciAdi, "kitapK2" => $kullaniciSifre);
    }

    header("Content-Type:Application/json");
    echo @json_encode($dizi2, JSON_UNESCAPED_UNICODE);
}
?>