<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<html>
  <head>
    <link type="text/css" href="../wet.css" rel="stylesheet">
    <title>Wetator / Redirect via JavaScript</title>
    <meta name="ROBOTS" content="NOINDEX,NOFOLLOW,NOARCHIVE"/>

<script type="text/javascript">
<?php
echo "  function redirect(){\n";

if (isset($_GET['target'])) {
  echo "    window.location = '".$_GET['target']."'\n";
} else {
  echo "    window.location = 'http://www.wetator.org'\n";
}

echo "  }\n";
echo "\n";
echo "  function startRedirect() {\n";
if (isset($_GET['wait'])) {
  echo "    setTimeout('redirect()', ".$_GET['wait'].");\n";
} else {
  echo "    setTimeout('redirect()', 444);\n";
}
echo "  }\n";
?>
</script>

  </head>

<body onLoad="startRedirect();">

  <div class="header">
    <img src="../images/wetator.png" alt="Wetator">
  </div>

  <h1 class="command_test">Wetator / Redirect via JavaScript</h1>

</body>
</html>


