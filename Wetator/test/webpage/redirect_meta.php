<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">

<html>
    <head>
<?php
if (isset($_GET['target'])) {
  echo "        <meta http-equiv='refresh' content='4; URL=".$_GET['target']."'/>\n";
} else {
  echo "        <meta http-equiv='refresh' content='4; URL=http://www.wetator.org'/>\n";
}
?>
    </head>

    <body>

      <div class="header">
        <img src="../images/wetator.png" alt="Wetator">
      </div>

      <h1 class="command_test">Wetator / Redirect via meta tag</h1>

    </body>
</html>


