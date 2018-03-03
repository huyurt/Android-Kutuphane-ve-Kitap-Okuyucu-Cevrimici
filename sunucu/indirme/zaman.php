<?php
date_default_timezone_set("Europe/Moscow"); 
$dizi2[] = array("time" => time());

header("Content-Type:Application/json");
echo json_encode($dizi2, JSON_UNESCAPED_UNICODE);

?>