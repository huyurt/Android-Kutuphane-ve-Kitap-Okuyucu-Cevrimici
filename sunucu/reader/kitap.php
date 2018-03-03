<?php
include("veritabaniBaglantisi.php");

if(isset($_POST['kitapIsmi'])) {
    $kitapIsmi = $_POST['kitapIsmi'];
	$sorgu = $baglanti->prepare("SELECT db2.y FROM db2 JOIN db1 ON db2.id = db1.id WHERE db1.n = :kitapIsmi");
	$sorgu->bindParam(':kitapIsmi', $kitapIsmi, PDO::PARAM_STR);
	$sorgu->execute();
	if($sorgu->rowCount() > 0) {
		while($row = $sorgu->fetch(PDO::FETCH_ASSOC)){
			$dizi[] = $row['y'];
	    }
    }
	for($i = 0; $i < count($dizi); $i++) {
		$dizi2[] = array("kitap" => $dizi[$i]);
	}

	header("Content-Type:Application/json");
	echo @json_encode($dizi2, JSON_UNESCAPED_UNICODE);
}
?>