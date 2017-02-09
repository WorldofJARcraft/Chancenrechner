<!DOCTYPE html>
<html>
<head>
<title> Mit Chancenrechner verbinden </title>
<meta charset="UTF-8" />
</head>
<body>
<h1>Verbindung mit einer "Chancenrechner"-Distribution herstellen</h1>
<?php
//http://www.php-kurs.com/mysql-datenbank-auslesen.htm
header("Content-type:text/html; charset='utf8'");
error_reporting(E_ALL);
 
// Zum Aufbau der Verbindung zur Datenbank, Windows: Benutzer='root', Kennwort=''; Linux: Kennwort='user', Kennwort='user'
define ( 'MYSQL_HOST',      'localhost' );
define ( 'MYSQL_BENUTZER',  'root' );
define ( 'MYSQL_KENNWORT',  '' );
define ( 'MYSQL_DATENBANK', 'chancenrechner' );
 
$db_link = mysqli_connect (MYSQL_HOST, 
                           MYSQL_BENUTZER, 
                           MYSQL_KENNWORT, 
                           MYSQL_DATENBANK);

$bname=$_GET['bname'];
$pwort=$_GET['pwort'];
$bname="'".$bname."'";
$pwort="'".$pwort."'";

if ( $db_link )
{
    //echo 'Verbindung erfolgreich: ';
   // print_r( $db_link);
}
else
{
    // hier sollte dann später dem Programmierer eine
    // E-Mail mit dem Problem zukommen gelassen werden
    die('keine Verbindung möglich: ' . mysqli_error());
}
 $sql="SELECT * FROM android_connect WHERE user=".$bname."AND password=".$pwort;
 //echo $sql;
$db_erg = mysqli_query( $db_link, $sql ); 
if ( ! $db_erg )
{
  die('Ungültige Abfrage: ' . mysqli_error());
}
 
if ($zeile = mysqli_fetch_array( $db_erg, MYSQL_ASSOC))
{
echo $zeile['Empfehlungen'];
echo "<br><a href = 'chancenrechner.php?bname=".$_GET['bname']."&pwort=".$_GET['pwort']."'>Aktualisieren</a>";
}
else{
echo "Falscher Benutzername oder falsches Passwort!<br><a href = 'chancenrechner.html'>Bitte melden Sie sich erneut an!</a>";
}
mysqli_free_result( $db_erg );
?>
</form>
</body>
</html>