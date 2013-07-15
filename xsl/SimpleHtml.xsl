<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" version="1.0">
    <xsl:output method="html" encoding="UTF-8" doctype-public="-//Wf3C//DTD HTML 4.01 Transitional//EN" omit-xml-declaration="yes"/>

    <xsl:variable name="noOfStepsInLine" select="150"/>

    <xsl:template match="/">
        <html>
            <head>
                <META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <META http-equiv="content-style-type" content="text/css"/>
                <title>Wetator - Test Result</title>
                <style type="text/css">
                    BODY {BACKGROUND-COLOR: #f8f8f8; FONT-SIZE: 10pt; FONT-FAMILY: Arial, Helvetica, sans-serif; margin: 4px;}
                    TABLE {font-size: 10pt; empty-cells: show; border-collapse: collapse; }
                    TH {FONT-WEIGHT: bold; BACKGROUND-COLOR: #ccddff; text-align: center;}
                    TD.step {border:1px solid #999; color: #000000; text-align: center;}
                    TD.light {BACKGROUND-COLOR: #f8f8f8;}
                    TD.topBorder {border-top: 1px solid #999;}
                    TD.message {BACKGROUND-COLOR: #fff8dc; COLOR: #666666; FONT-SIZE: 10pt;}
                    TD.properties {BACKGROUND-COLOR: #f8f8f8; FONT-SIZE: 10pt;}
                    TD.error {BACKGROUND-COLOR: #F14F12;}
                    TD.comment {BACKGROUND-COLOR: #eeeeee;}
                    H1 {FONT-SIZE: 12pt; COLOR: #000000; margin-top:20px;}
                    H2 {FONT-SIZE: 10pt; COLOR: #4682b4; margin-top:16px;}
                    P.blue {COLOR: #768bc2;}
                    PRE.text {FONT-FAMILY: Courier new, monospace, sans-serif;FONT-WEIGHT: bold;WHITE-SPACE: pre;}
                    A {COLOR: #768bc2; TEXT-DECORATION: none;}
                    A:link {COLOR: #768bc2; TEXT-DECORATION: none;}
                    A:visited {COLOR: #768bc2; TEXT-DECORATION: none;}
                    A:active {COLOR: #768bc2; TEXT-DECORATION: none;}
                    A:hover {TEXT-DECORATION: none;}
                    .step A:hover{width:13px;border:1px;}
                    A.linkToCommand {font-size: smaller; display: block;}
                    img {border: 0;}
                    DIV.header { color: #768bc2; margin-left: 10px; }
                    DIV.header IMG { margin-left: -10px; border:0; }
                    DIV.colorBar { height: 1em; border: 0; margin-left: 2px; margin-right: 1px; }
                    .smallBorder {border: 1px solid #999;}
                    .bold {font-weight: bold; }
                </style>

                <script type="text/javascript" language="JavaScript"><![CDATA[
                function showOrHide(image, id) {
                    if (image.src.indexOf("collapseall.png") != -1) {
                        var tmpElement=document.getElementById(id);
                        tmpElement.style.display = "none";
                        image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"expandall.png";
                    } else if (image.src.indexOf("expandall.png") != -1) {
                        var tmpElement=document.getElementById(id);
                        tmpElement.style.display = "";
                        image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"collapseall.png";
                    } else if (image.src.indexOf("collapselog.png") != -1) {
                        var tmpElement=document.getElementById(id);
                        tmpElement.style.display = "none";
                        image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"expandlog.png";
                    } else if (image.src.indexOf("expandlog.png") != -1) {
                        var tmpElement=document.getElementById(id);
                        tmpElement.style.display = "";
                        image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"collapselog.png";
                    }
                }
                function makeVisible(id) {
                    tmpImage = document.getElementById('showHide_' + id);
                    if (tmpImage.src.indexOf("expandall.png") != -1) {
                        showOrHide(tmpImage, id);
                    }
                }
                ]]></script>
            </head>

            <body>
                <center><p><img src="images/wetator.png" alt="Wetator"/></p></center>

                <!-- Overview -->
                <a name="overview"/>
                <h1>Overview</h1>

              <xsl:variable name="overview.okColor">#339F00</xsl:variable>
              <xsl:variable name="overview.failedColor">#F14F12</xsl:variable>
              <xsl:variable name="overview.vacantOkColor">#E4FFEE</xsl:variable>
              <xsl:variable name="overview.vacantFailedColor">#FFEBEB</xsl:variable>

                <xsl:variable name="testcase.total" select="count(/wet/testcase)"/>
                <xsl:variable name="testcase.failed" select="count(wet/testcase[boolean(descendant-or-self::error)])"/>

                <xsl:variable name="testcase.stepsTotal" select="count(/wet/testcase/command[not(@isComment)])"/>
                <xsl:variable name="testcase.stepsOk" select="count(/wet/testcase/command[not(@isComment) and not(error) and not(preceding-sibling::*/error)])"/>
                <xsl:variable name="testcase.stepsGreen" select="count(/wet/testcase/command[not(@isComment) and not(error)])"/>
                <xsl:variable name="testcase.stepsVacantFailed" select="$testcase.stepsTotal - $testcase.failed - $testcase.stepsGreen"/>
                <xsl:variable name="testcase.stepsVacantOk" select="$testcase.stepsTotal - $testcase.failed - $testcase.stepsVacantFailed - $testcase.stepsOk"/>

                <xsl:variable name="testcase.failedPercent" select="ceiling($testcase.failed * 100 div $testcase.total)"/>
                <xsl:variable name="testcase.okPercent" select="100 - $testcase.failedPercent"/>

                <xsl:variable name="testcase.stepsGreenPercent" select="format-number($testcase.stepsGreen * 100 div $testcase.stepsTotal, '#')"/>
                <xsl:variable name="testcase.stepsRedPercent" select="format-number(100 - $testcase.stepsGreenPercent, '#')"/>
                <xsl:variable name="testcase.stepsOkPercent" select="format-number($testcase.stepsOk * 100 div $testcase.stepsTotal, '#')"/>
                <xsl:variable name="testcase.stepsVacantOkPercent" select="format-number($testcase.stepsGreenPercent - $testcase.stepsOkPercent, '#')"/>
                <xsl:variable name="testcase.stepsFailedPercent" select="format-number($testcase.failed * 100 div $testcase.stepsTotal, '#')"/>
                <xsl:variable name="testcase.stepsVacantFailedPercent" select="format-number($testcase.stepsRedPercent - $testcase.stepsFailedPercent, '#')"/>

                <table cellpadding="4" cellspacing="0" class="smallBorder" border="0" width="100%">
                    <tr>
                        <th width="25%">
                            Tests started at:&#32;
                            <xsl:value-of select="wet/startTime"/>
                        </th>
                        <th width="2%" style="text-align: center;"><img src="./images/failed.png" width="12" height="10" alt="failed" title="failed"/></th>
                        <th width="2%" style="text-align: center;"><img src="./images/ok.png" width="12" height="10" alt="ok" title="ok"/></th>
                        <th width="2%" style="color: grey; text-align: center;" alt="vacant" title="vacant">vac</th>
                        <th style="text-align: right;">
                            Total time:&#32;
                            <xsl:call-template name="time">
                                <xsl:with-param name="msecs" select="wet/executionTime"/>
                            </xsl:call-template>.
                        </th>
                    </tr>

                    <tr>
                        <td class="bold">Distribution TestCase Level (<xsl:value-of select="$testcase.total"/> test<xsl:if  test="$testcase.total != 1">s</xsl:if> run)</td>
                        <td style="text-align: center;">
                            <span style="color: #F14F12; font-weight: bold;">
                                <xsl:value-of select="$testcase.failed"/>
                            </span>
                        </td>
                        <td style="text-align: center;">
                            <span style="color: green; font-weight: bold;">
                                <xsl:value-of select="$testcase.total - $testcase.failed"/>
                            </span>
                        </td>
                        <td/>

                        <td width="70%">
                            <table cellpadding="0" cellspacing="0" width="100%">
                            <tr>
                                <xsl:if test="$testcase.failedPercent > 0">
                                    <td class="smallBorder"  style="text-align: center;">
                                        <xsl:attribute name="width">
                                            <xsl:value-of select="$testcase.failedPercent"/>%
                                        </xsl:attribute>
                                        <xsl:attribute name="bgcolor">
                                            <xsl:value-of select="$overview.failedColor"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="'Failed test cases'"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="$testcase.failedPercent"/>%
                                    </td>
                                </xsl:if>
                                <xsl:if test="$testcase.okPercent > 0">
                                    <td class="smallBorder" style="text-align: center;">
                                        <xsl:attribute name="width">
                                            <xsl:value-of select="$testcase.okPercent"/>%
                                        </xsl:attribute>
                                        <xsl:attribute name="bgcolor">
                                            <xsl:value-of select="$overview.okColor"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="'Successful test cases'"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="$testcase.okPercent"/>%
                                    </td>
                                </xsl:if>
                                <td></td>
                            </tr>
                            </table>
                        </td>
                    </tr>

                    <tr>
                        <td class="bold">Distribution TestStep Level (<xsl:value-of select="$testcase.stepsTotal"/> step<xsl:if  test="$testcase.stepsTotal != 1">s</xsl:if> in total)</td>

                        <td style="text-align: center;">
                            <span style="color: #F14F12; font-weight: bold;">
                                <xsl:value-of select="$testcase.stepsTotal - $testcase.stepsGreen"/>
                            </span>
                        </td>
                        <td style="text-align: center;">
                            <span style="color: green; font-weight: bold;">
                                <xsl:value-of select="$testcase.stepsGreen"/>
                            </span>
                        </td>
                        <td style="text-align: center;">
                            <span style="color: grey; font-weight: bold;">
                                <xsl:value-of select="$testcase.stepsTotal - $testcase.failed - $testcase.stepsOk"/>
                            </span>
                        </td>

                        <td>
                            <table cellpadding="0" cellspacing="0" width="100%">
                              <tr>
                                  <!-- failed steps -->
                                  <xsl:if test="$testcase.stepsFailedPercent > 0">
                                      <td class="smallBorder"  style="text-align: center;">
                                          <xsl:attribute name="width">
                                              <xsl:value-of select="$testcase.stepsFailedPercent"/>%
                                          </xsl:attribute>
                                          <xsl:attribute name="bgcolor">
                                              <xsl:value-of select="$overview.failedColor"/>
                                          </xsl:attribute>
                                          <xsl:attribute name="title">
                                              <xsl:value-of select="'Failed steps'"/>
                                          </xsl:attribute>
                                          <xsl:value-of select="$testcase.stepsFailedPercent"/>%
                                      </td>
                                  </xsl:if>
                                  <xsl:if test="$testcase.stepsFailedPercent = 0 and $testcase.failed > 0">
                                      <td class="smallBorder"  style="text-align: center;">
                                          <xsl:attribute name="width">1%</xsl:attribute>
                                          <xsl:attribute name="bgcolor">
                                              <xsl:value-of select="$overview.failedColor"/>
                                          </xsl:attribute>
                                          <xsl:attribute name="title">
                                              <xsl:value-of select="'Failed steps'"/>
                                          </xsl:attribute>
                                      </td>
                                  </xsl:if>

                                  <!-- vacant failed steps -->
                                  <xsl:if test="$testcase.stepsVacantFailedPercent > 0">
                                      <td class="smallBorder" style="text-align: center;">
                                          <xsl:attribute name="width">
                                              <xsl:value-of select="$testcase.stepsVacantFailedPercent"/>%
                                          </xsl:attribute>
                                          <xsl:attribute name="bgcolor">
                                              <xsl:value-of select="$overview.vacantFailedColor"/>
                                          </xsl:attribute>
                                          <xsl:attribute name="title">
                                              <xsl:value-of select="'Vacant failed steps'"/>
                                          </xsl:attribute>
                                          <xsl:value-of select="$testcase.stepsVacantFailedPercent"/>%
                                      </td>
                                  </xsl:if>
                                  <xsl:if test="$testcase.stepsVacantFailedPercent = 0 and $testcase.stepsVacantFailed > 0">
                                      <td class="smallBorder"  style="text-align: center;">
                                          <xsl:attribute name="width">1%</xsl:attribute>
                                          <xsl:attribute name="bgcolor">
                                              <xsl:value-of select="$overview.vacantFailedColor"/>
                                          </xsl:attribute>
                                          <xsl:attribute name="title">
                                              <xsl:value-of select="'Vacant failed steps'"/>
                                          </xsl:attribute>
                                      </td>
                                  </xsl:if>

                                  <!-- successful steps -->
                                  <xsl:if test="$testcase.stepsOkPercent > 0">
                                      <td class="smallBorder" style="text-align: center;">
                                          <xsl:attribute name="width">
                                              <xsl:value-of select="$testcase.stepsOkPercent"/>%
                                          </xsl:attribute>
                                          <xsl:attribute name="bgcolor">
                                              <xsl:value-of select="$overview.okColor"/>
                                          </xsl:attribute>
                                          <xsl:attribute name="title">
                                              <xsl:value-of select="'Successful steps'"/>
                                          </xsl:attribute>
                                          <xsl:value-of select="$testcase.stepsOkPercent"/>%
                                      </td>
                                  </xsl:if>
                                  <xsl:if test="$testcase.stepsOkPercent = 0 and $testcase.stepsOk > 0">
                                      <td class="smallBorder"  style="text-align: center;">
                                          <xsl:attribute name="width">1%</xsl:attribute>
                                          <xsl:attribute name="bgcolor">
                                              <xsl:value-of select="$overview.okColor"/>
                                          </xsl:attribute>
                                          <xsl:attribute name="title">
                                              <xsl:value-of select="'Successful steps'"/>
                                          </xsl:attribute>
                                      </td>
                                  </xsl:if>

                                  <!-- vacant successful steps -->
                                  <xsl:if test="$testcase.stepsVacantOkPercent > 0">
                                      <td class="smallBorder" style="text-align: center;">
                                          <xsl:attribute name="width">
                                              <xsl:value-of select="$testcase.stepsVacantOkPercent"/>%
                                          </xsl:attribute>
                                          <xsl:attribute name="bgcolor">
                                              <xsl:value-of select="$overview.vacantOkColor"/>
                                          </xsl:attribute>
                                          <xsl:attribute name="title">
                                              <xsl:value-of select="'Vacant successful steps'"/>
                                          </xsl:attribute>
                                          <xsl:value-of select="$testcase.stepsVacantOkPercent"/>%
                                      </td>
                                  </xsl:if>
                                  <xsl:if test="$testcase.stepsVacantOkPercent = 0 and $testcase.stepsVacantOk > 0">
                                      <td class="smallBorder"  style="text-align: center;">
                                          <xsl:attribute name="width">1%</xsl:attribute>
                                          <xsl:attribute name="bgcolor">
                                              <xsl:value-of select="$overview.vacantOkColor"/>
                                          </xsl:attribute>
                                          <xsl:attribute name="title">
                                              <xsl:value-of select="'Vacant successful steps'"/>
                                          </xsl:attribute>
                                      </td>
                                  </xsl:if>
                                  <td/>
                                </tr>
                            </table>
                        </td>
                    </tr>
            </table>

            <table cellpadding="4" cellspacing="0" class="smallBorder" border="0">
                    <tr>
                        <th>No</th>
                        <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                        <th>Name</th>
                        <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                        <th colspan="2">Steps</th>
                        <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                        <th>Graph</th>
                        <th>Duration</th>
                    </tr>

                    <xsl:for-each select="/wet/testcase">
                        <xsl:call-template name="testcaseOverview"/>
                    </xsl:for-each>

                    <tr>
                        <td class="bold">Sum</td>
                        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                        <td align="right" class="bold">
                             <xsl:value-of select="count(/wet/testcase/command[not(@isComment)])"/>
                        </td>
                        <td align="right" class="bold">
                             <xsl:value-of select="count(descendant::command[not(@isComment)])"/>
                        </td>
                        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                        <td><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>

                        <xsl:variable name="duration" select="sum(/wet/testcase/command/executionTime)"/>
                        <td align="right" class="bold">
                            <xsl:call-template name="time">
                                <xsl:with-param name="msecs" select="$duration"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </table>

                <!-- configuration -->
                <xsl:call-template name="configuration"/>

                <!-- All individual test results -->
                <a name="details"/>
                <h1>Result Details</h1>
                <xsl:for-each select="wet/testcase">
                    <xsl:call-template name="testresult" />
                </xsl:for-each>

                <!-- Footer -->
                <hr/>
                <xsl:text>Created using&#32;</xsl:text>
                <xsl:value-of select="wet/about/product"/>
                <xsl:text>&#32;version:&#32;</xsl:text>
                <xsl:value-of select="wet/about/version"/>
                <xsl:text>.</xsl:text>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="configuration">
        <table cellpadding="4" cellspacing="0" border="0" style="margin-top: 10px">
          <tr>
              <td>
                  <span class="bold">Configuration</span>
                  <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                  <img src="images/expandall.png" onclick="showOrHide(this, 'configuration')" alt="show/hide Configuration" style="cursor: pointer;"/>
              </td>
              <td width="50px"></td>
              <td>
                  <span class="bold">Variables</span>
                  <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                  <img src="images/expandall.png" onclick="showOrHide(this, 'variables')" alt="show/hide Variables" style="cursor: pointer;"/>
              </td>
          </tr>

          <tr>
              <td valign="top">
                  <table id="configuration" cellpadding="2" cellspacing="0" class="smallBorder" style="display: none; margin-left: 20px; margin-bottom: 20px">
                      <tr>
                          <th>Key</th>
                          <th></th>
                          <th>Value</th>
                      </tr>

                      <xsl:for-each select="/wet/configuration/property">
                          <tr>
                              <td><xsl:value-of select="@key"/></td>
                              <td><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
                              <td><xsl:value-of select="@value"/></td>
                          </tr>
                      </xsl:for-each>
                  </table>
              </td>
              <td></td>
              <td valign="top">
                  <table id="variables" cellpadding="2" cellspacing="0" class="smallBorder" style="display: none; margin-left: 20px">
                      <tr>
                          <th>Name</th>
                          <th></th>
                          <th>Value</th>
                      </tr>

                      <xsl:for-each select="/wet/configuration/variables/variable">
                          <xsl:sort select="@name"/>
                          <tr>
                              <td><xsl:value-of select="@name"/></td>
                              <td><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text></td>
                              <td><xsl:value-of select="@value"/></td>
                          </tr>
                      </xsl:for-each>
                  </table>
              </td>
          </tr>
        </table>
    </xsl:template>

    <xsl:template name="testcaseOverview">

        <xsl:variable name="step.okColor">lightgreen</xsl:variable>
        <xsl:variable name="step.vacantOkColor">#E4FFEE</xsl:variable>
        <xsl:variable name="step.failedColor">#F14F12</xsl:variable>
        <xsl:variable name="step.vacantFailedColor">#FDCCDB</xsl:variable>

        <tr>
            <td align="right"><xsl:number/></td>
            <td align="center">
                <xsl:call-template name="successIndicator"/>
            </td>
            <td>
                <a>
                    <xsl:attribute name="href">
                        <xsl:text>#testspec</xsl:text>
                        <xsl:number/>
                    </xsl:attribute>
                    <xsl:value-of select="@name"/>
                </a>
            </td>
            <td>
                <img>
                    <xsl:attribute name="src">
                        <xsl:choose>
                            <xsl:when test="@browser='Firefox2'">
                                <xsl:text disable-output-escaping="yes">images/firefox.png</xsl:text>
                            </xsl:when>
                            <xsl:when test="@browser='Firefox3'">
                                <xsl:text disable-output-escaping="yes">images/firefox.png</xsl:text>
                            </xsl:when>
                            <xsl:when test="@browser='IE6'">
                                <xsl:text disable-output-escaping="yes">images/ie6.png</xsl:text>
                            </xsl:when>
                            <xsl:when test="@browser='IE7'">
                                <xsl:text disable-output-escaping="yes">images/ie7.png</xsl:text>
                            </xsl:when>
                            <xsl:when test="@browser='IE8'">
                                <xsl:text disable-output-escaping="yes">images/ie8.png</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text disable-output-escaping="yes">images/firefox.png</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                        <xsl:value-of select="@browser"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="@browser"/>
                    </xsl:attribute>
                </img>
            </td>
            <td align="right">
                 <xsl:value-of select="count(command[not(@isComment)])"/>
            </td>
            <td align="right">
                 (<xsl:value-of select="count(descendant::command[not(@isComment)])"/>)
            </td>

            <td width="2px"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
            <td width="80%">
                <xsl:variable name="linelength" select="0"/>

                <table align="left" cellpadding="0" cellspacing="0">
                    <tr>
                        <xsl:for-each select="./command[not(@isComment)]">
                            <xsl:variable name="noOfErrors" select="sum(descendant-or-self::error)"/>
                            <xsl:variable name="noOfSubSteps" select="count(descendant::command[not(@isComment)])"/>
                            <xsl:variable name="vacant" select="preceding-sibling::*[descendant-or-self::error]"/>

                            <!-- start new line if needed -->
                            <xsl:if test="(position() mod $noOfStepsInLine = 1) and (position() &gt; 1)">
                                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
                            </xsl:if>

                            <td class="step" width="4px">
                                <xsl:attribute name="bgcolor">
                                    <xsl:choose>
                                        <xsl:when test="$noOfErrors = 0">
                                            <xsl:choose>
                                                <xsl:when test="$vacant">
                                                    <xsl:value-of select="$step.vacantOkColor"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="$step.okColor"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:choose>
                                                <xsl:when test="$vacant">
                                                    <xsl:value-of select="$step.vacantFailedColor"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="$step.failedColor"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:attribute>

                                <xsl:choose>
                                  <xsl:when test="error">
                                     <xsl:attribute name="title">
                                         <xsl:value-of select="./error/message"/>
                                       </xsl:attribute>
                                  </xsl:when>
                                  <xsl:when test="$noOfErrors != 0">
                                    <xsl:attribute name="title">
                                         <xsl:value-of select="normalize-space(./testcase/command/error)"/>
                                       </xsl:attribute>
                                  </xsl:when>
                                </xsl:choose>

                                <xsl:element name="a">
                                  <xsl:attribute name="width">4px</xsl:attribute>
                                    <xsl:attribute name="class">linkToCommand</xsl:attribute>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:choose>
                                        <xsl:when test="$noOfSubSteps &gt; 0">
                                            <xsl:value-of select="$noOfSubSteps"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:element>
                            </td>


                        </xsl:for-each>
                        <td style="border-left:1px solid #999;"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></td>
                    </tr>
                </table>
            </td>
            <xsl:variable name="duration" select="sum(./command/executionTime)"/>
            <td align="right">
                <xsl:call-template name="time">
                    <xsl:with-param name="msecs" select="$duration"/>
                </xsl:call-template>
            </td>
        </tr>
    </xsl:template>

    <xsl:template name="testresult">
        <h2>
            <xsl:call-template name="successIndicator"/>
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            <a>
                <xsl:attribute name="name">
                    <xsl:text>testspec</xsl:text>
                    <xsl:number/>
                </xsl:attribute>
                <xsl:value-of select="@name"/>
            </a>
            <xsl:text> (</xsl:text>
            <xsl:value-of select="@browser"/>
            <xsl:text>)</xsl:text>
        </h2>
        <xsl:call-template name="testcaseTable" />

        <p>
            <a href="#overview">
                <img src="images/top.png" width="11" height="10" alt="top"/>
                Back to Test Report Overview
            </a>
        </p>
    </xsl:template>

    <xsl:template name="testcaseTable">
        <xsl:if test="count(command) > 0">
            <table cellpadding="2" cellspacing="0" width="100%" class="smallBorder">
                <tr>
                    <th>Line</th>
                    <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                    <th>Command</th>
                    <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                    <th><xsl:text disable-output-escaping="yes">Parameter&amp;nbsp;1</xsl:text></th>
                    <th><xsl:text disable-output-escaping="yes">Parameter&amp;nbsp;2</xsl:text></th>
                    <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                    <th>Duration</th>
                </tr>
                <xsl:for-each select="./command">
                    <xsl:call-template name="command"/>
                </xsl:for-each>
            </table>
        </xsl:if>
    </xsl:template>

    <xsl:template name="command">
        <xsl:variable name="lineStyle">
            <xsl:choose>
                <xsl:when test="@isComment">
                    <xsl:text>comment topBorder</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>light topBorder</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <tr>
            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" align="right"&gt;</xsl:text>
            <a>
                <xsl:attribute name="name">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </a>
            <xsl:value-of select="@line"/>

            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" align="center"&gt;</xsl:text>
                <xsl:choose>
                    <xsl:when test="@isComment">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </xsl:when>
                    <xsl:when test="(count(descendant-or-self::error)) &gt; 0">
                        <img src="./images/failed.png" width="12" height="10" alt="failed"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="./images/ok.png" width="12" height="10" alt="ok"/>
                    </xsl:otherwise>
                </xsl:choose>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" &gt;</xsl:text>
                <xsl:value-of select="@name"/>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" align="center"&gt;</xsl:text>
                <xsl:if test="count(./testcase) &gt; 0">
                    <img src="images/expandall.png" alt="Show/Hide sub testcase" style="cursor: pointer;">
                        <xsl:attribute name="id">
                            <xsl:text>showHide_testcase_</xsl:text>
                            <xsl:value-of select="testcase/@id" />
                        </xsl:attribute>
                        <xsl:attribute name="onclick">
                          <xsl:text>showOrHide(this, 'testcase_</xsl:text>
                          <xsl:value-of select="testcase/@id" />
                          <xsl:text>');</xsl:text>
                        </xsl:attribute>
                    </img>
                </xsl:if>
                <xsl:if test="count(./response) &gt; 0">
                    <xsl:for-each select="./response">
                        <a target="_blank">
                            <xsl:attribute name="href">
                                <xsl:value-of select="."/>
                            </xsl:attribute>
                            <img src="images/response.png" alt="view response"/>
                        </a>
                    </xsl:for-each>
                </xsl:if>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" &gt;</xsl:text>
                <xsl:choose>
                <xsl:when test="string-length(param0) &gt; 0">
                    <xsl:value-of select="param0"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>-</xsl:text>
                </xsl:otherwise>
                </xsl:choose>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" &gt;</xsl:text>
                <xsl:choose>
                    <xsl:when test="string-length(param1) &gt; 0">
                        <xsl:value-of select="param1"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>-</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" &gt;</xsl:text>
                <xsl:if test="count(./log) &gt; 0">
                    <img src="images/expandlog.png" alt="Show/Hide log entries" style="cursor: pointer;">
                        <xsl:attribute name="id">
                            <xsl:text>showHide_log_</xsl:text>
                            <xsl:value-of select="@id" />
                        </xsl:attribute>
                        <xsl:attribute name="onclick">
                            <xsl:text>showOrHide(this, 'log_</xsl:text>
                            <xsl:value-of select="@id" />
                            <xsl:text>');</xsl:text>
                        </xsl:attribute>
                    </img>
                </xsl:if>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" align="right"&gt;</xsl:text>
                &#32;
                <xsl:call-template name="time">
                    <xsl:with-param name="msecs" select="executionTime"/>
                </xsl:call-template>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
        </tr>

        <xsl:if test="count(descendant-or-self::error) &gt; 0">
            <tr>
                <td class="light"/>
                <td class="light"/>
                <td class="light" style="text-align:right;">
                    <!-- link to previous error if exists -->
                    <xsl:choose>
                        <xsl:when test="ancestor::command[count(descendant-or-self::error) &gt; 0]">
                            <xsl:for-each select="ancestor::command[count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="onclick">
                                         <xsl:text>makeVisible('testcase_</xsl:text>
                                      <xsl:value-of select="parent::testcase/@id" />
                                         <xsl:text>');</xsl:text>
                                    </xsl:attribute>

                                    <img src="images/previous.png" width="11" height="10" alt="previous error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="preceding::command[count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="onclick">
                                         <xsl:text>makeVisible('testcase_</xsl:text>
                                      <xsl:value-of select="parent::testcase/@id" />
                                         <xsl:text>');</xsl:text>
                                    </xsl:attribute>

                                    <img src="images/previous.png" width="11" height="10" alt="previous error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>

                    <!-- link to next error if exists -->
                    <xsl:choose>
                        <xsl:when test="count(descendant::command/error) &gt; 0">
                            <xsl:for-each select="descendant::command[count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="onclick">
                                         <xsl:text>makeVisible('testcase_</xsl:text>
                                      <xsl:value-of select="parent::testcase/@id" />
                                         <xsl:text>');</xsl:text>
                                    </xsl:attribute>

                                    <img src="images/next.png" width="11" height="10" alt="next error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="following::command[count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="onclick">
                                         <xsl:text>makeVisible('testcase_</xsl:text>
                                      <xsl:value-of select="parent::testcase/@id" />
                                         <xsl:text>');</xsl:text>
                                    </xsl:attribute>

                                    <img src="images/next.png" width="11" height="10" alt="next error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
                <td class="light"/>
                <td class="error" colspan="4">
                    <xsl:value-of select="error/message"/>
                </td>
            </tr>
        </xsl:if>

        <xsl:if test="count(./log) &gt; 0">
            <tr style="display: none;">
                <xsl:attribute name="id">
                    <xsl:text>log_</xsl:text>
                    <xsl:value-of select="@id" />
                </xsl:attribute>

                <td class="light"/>
                <td class="light"/>
                <td class="light"/>
                <td class="light"/>
                <td class="message" colspan="4">
	                <table cellpadding="1" cellspacing="0" width="100%">
	                    <xsl:for-each select="./log">
	                        <tr>
	                            <td>
	                        <xsl:choose>
	                            <xsl:when test="./level[text() = 'INFO']">
	                                <img src="./images/log_info.png" width="11" height="11" alt="failed"/>
	                            </xsl:when>
	                            <xsl:when test="./level[text() = 'WARN']">
	                                <img src="./images/log_warn.png" width="11" height="11" alt="failed"/>
	                            </xsl:when>
	                            <xsl:otherwise>
	                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
	                            </xsl:otherwise>
	                        </xsl:choose>
	                            </td>
	                            <td>
	                                <xsl:value-of select="./message"/>
	                            </td>
	                        </tr>
	                    </xsl:for-each>
	                </table>
                </td>
            </tr>
        </xsl:if>

        <xsl:if test="(count(./testcase)) &gt; 0">
              <tr style="display: none">
                   <xsl:attribute name="id">
                       <xsl:text>testcase_</xsl:text>
                     <xsl:value-of select="testcase/@id" />
                   </xsl:attribute>
                   <td class="light"/>
                   <td class="light"/>
                   <td class="light"/>
                   <td class="light"/>
                   <td class="light" colspan="4">
                       <xsl:for-each select="testcase">
                            <xsl:call-template name="testcaseTable"/>
                       </xsl:for-each>
                   </td>
              </tr>
          </xsl:if>
    </xsl:template>


    <!-- subroutines -->
    <xsl:template name="successIndicator">
        <xsl:choose>
            <xsl:when test="count(descendant-or-self::error) &gt; 0">
                <img src="./images/failed.png" width="12" height="10" alt="failed"/>
            </xsl:when>
            <xsl:otherwise>
                <img src="./images/ok.png" width="12" height="10" alt="ok"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="colorBar">
        <xsl:param name="percentage"/>
        <xsl:param name="color"/>
        <xsl:param name="title"/>

        <xsl:choose>
            <xsl:when test="$percentage>0">
                <table width="{$percentage}%" class="smallBorder" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td bgcolor="{$color}">
                        <xsl:if test="$title">
                            <xsl:attribute name="title">
                                <xsl:value-of select="$title"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                </tr>
                </table>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="time">
        <xsl:param name="msecs"/>

        <xsl:choose>
            <xsl:when test="$msecs > 5000">
                <xsl:variable name="base" select="round($msecs div 1000)"/>
                <xsl:variable name="hours" select="floor($base div 3600)"/>
                <xsl:variable name="mins" select="floor(($base - $hours*3600) div 60)"/>
                <xsl:variable name="secs" select="floor(($base - $hours*3600) - $mins*60)"/>

                <xsl:if test="10 > $hours">0</xsl:if>
                <xsl:value-of select="$hours"/>
                <xsl:text>:</xsl:text>
                <xsl:if test="10 > $mins">0</xsl:if>
                <xsl:value-of select="$mins"/>
                <xsl:text>:</xsl:text>
                <xsl:if test="10 > $secs">0</xsl:if>
                <xsl:value-of select="$secs"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="round($msecs div 100) div 10"/>
                <xsl:text> secs</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
