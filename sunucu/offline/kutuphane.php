<?php
include("veritabaniBaglantisi.php");

$sorgu = $baglanti->prepare("SELECT id, t, n, a, k, i, c, b FROM db1");
$sorgu->execute();
if($sorgu->rowCount() > 0) {
	while($row = $sorgu->fetch(PDO::FETCH_ASSOC)){
        $dizi[] = $row['id'];
        $dizi[] = $row['t'];
        $dizi[] = $row['n'];
        $dizi[] = $row['a'];
        $dizi[] = $row['k'];
        $dizi[] = $row['i'];
        $dizi[] = $row['c'];
        $dizi[] = $row['b'];
    }
}

for($i = 0; $i < count($dizi); $i++) {
	$dizi2[] = array("kitapId" => $dizi[$i], "kitapZamani" => $dizi[++$i], "kitapIsmi" => $dizi[++$i], "kitapYazari" => $dizi[++$i], "kitapKategorisi" => $dizi[++$i], "kitapOzeti" => $dizi[++$i], "kitapKapak" => $dizi[++$i], "kitapBoyut" => $dizi[++$i]);
}

header("Content-Type:Application/json");
echo @json_encode($dizi2, JSON_UNESCAPED_UNICODE);

?>