<?php
function emu_getallheaders() {
   foreach($_SERVER as $name => $value)
       if(substr($name, 0, 5) == 'HTTP_')
           $headers[str_replace(' ', '-', ucwords(strtolower(str_replace('_', ' ', substr($name, 5)))))] = $value;
   return $headers;
}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=ISO-8859-1">
    <link type="text/css" href="../wet.css" rel="stylesheet">
    <title>Wetator / Request Snoopy</title>
    <meta name="ROBOTS" content="NOINDEX,NOFOLLOW,NOARCHIVE"/>
  </head>

<body>

  <h1 class='command'>Wetator / Request Snoopy</h1>

  <!-- content -->
  <div class='content'>
    <h1>GET Parameters</h1>
    <table border='0' cellpadding='4' cellspacing='4'>
      <tr>
        <th>Key</th>
        <th>Value</th>
      </tr>

<?php
foreach($_GET as $key=>$value)
{
    echo "      <tr>\n";
    echo "        <td>".$key."</td>\n";
    if (is_array($value)) {
      echo "        <td>";
      $isNotFirst = False;
      foreach($value as $mul_key=>$mul_value) {
        if ($isNotFirst) {
          echo ", ";
        }
        $isNotFirst = True;
        echo $mul_value;
      }
      echo "</td>\n";
    } else {
      echo "        <td>".$value."</td>\n";
    }
    echo "      </tr>\n";
}
?>

    </table>
  </div>


  <div class='content'>
    <h1>POST Parameters</h1>
    <table border='0' cellpadding='4' cellspacing='4'>
      <tr>
        <th>Key</th>
        <th>Value</th>
      </tr>

<?php
foreach($_POST as $key=>$value)
{
    echo "      <tr>\n";
    echo "        <td>".$key."</td>\n";
    echo "        <td>".$value."</td>\n";
    echo "      </tr>\n";
}
?>

    </table>
  </div>

<?php
if ($_FILES) {
?>
  <div class='content'>
    <h1>File Upload</h1>
    <table border='0' cellpadding='4' cellspacing='4'>
      <tr>
        <th>Upload Control</th>
        <th>Values</th>
      </tr>

<?php
foreach($_FILES as $key=>$value)
{
    echo "      <tr>\n";
    echo "        <td><b>".$key."</b></td>\n";
    if (is_array($value)) {
      echo "        <td>\n";
      echo "          <table border='0' cellpadding='3' cellspacing='0'>\n";

      foreach($value as $mul_key=>$mul_value) {
        echo "            <tr>\n";
        echo "              <td>".$mul_key."</td>\n";
        echo "              <td>".$mul_value."</td>\n";
        echo "            </tr>\n";
      }

      echo "            <tr>\n";
      echo "              <td>SampleData</td>\n";

      if ($_FILES[$key]['type'] == "text/plain") {
        $tmpFile = $_FILES[$key]['tmp_name'];
        $tmpFileHandle = fopen($tmpFile, 'r');
        $tmpData = fread($tmpFileHandle, 13);
        fclose($tmpFileHandle);

        echo "              <td>".$tmpData."....</td>\n";
      } else {
        echo "              <td>unsupported type</td>\n";
      }

      echo "            </tr>\n";

      echo "          </table>\n";
      echo "        </td>\n";
    } else {
      echo "        <td>".$value."</td>\n";
    }
    echo "      </tr>\n";
}
?>

    </table>
  </div>

<?php
}
?>


  <div class='content'>
    <h1>Headers</h1>
    <table border='0' cellpadding='4' cellspacing='4'>
      <tr>
        <th>Key</th>
        <th>Value</th>
      </tr>

<?php
foreach(emu_getallheaders() as $key=>$value)
{
    echo "      <tr>\n";
    echo "        <td>".$key."</td>\n";
    echo "        <td>".$value."</td>\n";
    echo "      </tr>\n";
}
?>
    </table>
  </div>

  <div class='content'>
    <p>&copy; rbri 2007, 2008</p>
  </div>

</body>
</html>


