<?php

  if (isset($_GET['target'])) {
    header("Location: ".$_GET['target']);
  } else {
    header("Location: http://www.wetator.org");
  }
?>

