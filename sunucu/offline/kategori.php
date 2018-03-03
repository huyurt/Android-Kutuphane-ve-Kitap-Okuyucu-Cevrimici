<?php
include("veritabaniBaglantisi.php");

$sorgu = $baglanti->prepare("SELECT k FROM db1");
$sorgu->execute();
if($sorgu->rowCount() > 0) {
	while($row = $sorgu->fetch(PDO::FETCH_ASSOC)){
        $dizi[] = $row['k'];
    }
}

$sonuc = "";
for($i = 0; $i < count($dizi); $i++) {
	$sonuc .= $dizi[$i]."|";
}

$dizi2 = explode("|", $sonuc);
$result = array_unique($dizi2);
$result2 = array_count_values($dizi2);
array_splice($result, -1, 1);
array_splice($result2, -1, 1);

$sonuc = "";
for ($i = 0; $i < count($result); $i++) {
    $sonuc .= $result[$i]." (".$result2[$result[$i]].")|";
}

$last = explode("|", $sonuc);
array_splice($last, -1, 1);
sort($last);

$sonuc = "";
foreach ($last as $s) {
    $sonuc .= $s."|";
}

echo $sonuc;

?>