<?php

include("veritabaniBaglantisi.php");
$sorgu = $baglanti->prepare("SELECT versiyon, tur, boyut, link FROM application");
$sorgu->execute();
if($sorgu->rowCount() > 0){
	while($row = $sorgu->fetch(PDO::FETCH_ASSOC)){
		$dizi[] = $row['versiyon'];
		$dizi[] = $row['tur'];
		$dizi[] = $row['boyut'];
		$dizi[] = $row['link'];
	}
}
for($i = 0; $i < count($dizi); $i++){
   $dizi2[] = array("Versiyon" => $dizi[$i], "Apk" => $dizi[++$i], "Boyut" => $dizi[++$i], "Adres" => $dizi[++$i]);
}

header("Content-Type:Application/json");
echo json_encode($dizi2, JSON_UNESCAPED_UNICODE);

?>