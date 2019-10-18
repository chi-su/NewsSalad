<?php
$host =  ‘localhost’;
$user = ‘root‘;
$pw = ‘wltn27‘;
$dbName = ‘test’;
$mysqli = new mysqli($host, $user, $pw, $dbName);
if($mysqli){ echo “MySQLsucess”;
    } else {

    echo “MySQLfail”;
}
?>
