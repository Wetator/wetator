<?php
  $tmp = $_GET['code'];

  if ($tmp == '204') {
    $tmpHeader = "HTTP/1.0 204 No Content";

  } elseif ($tmp == '205') {
    $tmpHeader = "HTTP/1.0 205 Reset Content";

  } elseif ($tmp == '206') {
    $tmpHeader = "HTTP/1.0 206 Partial Content";

  } elseif ($tmp == '400') {
    $tmpHeader = "HTTP/1.0 400 Bad Request";

  } elseif ($tmp == '404') {
    $tmpHeader = "HTTP/1.0 404 Not Found";

  } elseif ($tmp == '500') {
    $tmpHeader = "HTTP/1.0 500 Server Error";

  } else {
    $tmpHeader = "HTTP/1.0 200 OK";
  }

  header($tmpHeader, true);
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<?php
    echo "    <title>".$tmpHeader."</title>\n";
?>
    <meta name="ROBOTS" content="NOINDEX,NOFOLLOW,NOARCHIVE"/>
  </head>

<body>
<?php
    echo "    <h2>".$tmpHeader."</h2>\n";
?>

</body>
</html>


