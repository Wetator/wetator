<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" version="1.0">
    <xsl:output method="html" encoding="UTF-8" doctype-public="-//Wf3C//Dtd HTML 4.01 Transitional//EN" omit-xml-declaration="yes"/>

    <xsl:variable name="browserPicture.IE">resources/ie.png</xsl:variable>
    <xsl:variable name="browserPicture.Firefox">resources/firefox.png</xsl:variable>

    <xsl:variable name="greenColor">#ACC952</xsl:variable>
    <xsl:variable name="orangeColor">#E75013</xsl:variable>
    <xsl:variable name="blueColor">#00769C</xsl:variable>
    <xsl:variable name="greyColor">#57575A</xsl:variable>
    <xsl:variable name="lightGreyColor">#858588</xsl:variable>
    <xsl:variable name="ignoredColor">#FFFFFF</xsl:variable>

    <xsl:variable name="previewWidth">1600</xsl:variable>
    <xsl:variable name="previewHeight">1000</xsl:variable>
    <xsl:variable name="previewZoom">0.6</xsl:variable>

    <xsl:variable name="noOfStepsInLine" select="150"/>

    <xsl:variable name="testCaseCount" select="count(/wet/testcase)"/>
    <xsl:variable name="browserCount" select="count(/wet/testcase/testrun) div $testCaseCount"/>
    <xsl:variable name="testCount" select="$testCaseCount * $browserCount"/>
    <xsl:variable name="testStepCount" select="count(/wet/testcase/testrun/testfile/command[not(@isComment)])"/>

    <xsl:variable name="testCaseFailureCount" select="count(/wet/testcase[boolean(descendant::failure and not(descendant::command/error) and not(descendant::testfile/error))])"/>
    <xsl:variable name="testCaseErrorCount" select="count(/wet/testcase[boolean(descendant::testfile/error or descendant::command/error)])"/>
    <xsl:variable name="testCaseNotOkCount" select="$testCaseFailureCount + $testCaseErrorCount"/>
    <xsl:variable name="testCaseOkCount" select="$testCaseCount - $testCaseNotOkCount"/>
    <xsl:variable name="testFailureCount" select="count(/wet/testcase/testrun/testfile[boolean(descendant::failure and not(descendant::command/error) and not(descendant::testfile/error))])"/>
    <xsl:variable name="testErrorCount" select="count(/wet/testcase/testrun/testfile[boolean(error or descendant-or-self::command/error or descendant::testfile/error)])"/>
    <xsl:variable name="testIgnoredCount" select="count(/wet/testcase/testrun/ignored)"/>

    <xsl:variable name="stepsOkCount" select="count(/wet/testcase/testrun/testfile/command[not(@isComment) and not(descendant-or-self::failure) and not(descendant-or-self::error) and not(descendant-or-self::ignored)])"/>
    <xsl:variable name="stepsFailureCount" select="count(/wet/testcase/testrun/testfile/command[(descendant-or-self::failure) and not(descendant::command/error) and not(descendant::testfile/error)])"/>
    <xsl:variable name="stepsErrorCount" select="count(/wet/testcase/testrun/testfile[boolean(descendant-or-self::command/error or descendant::testfile/error)])"/>
    <xsl:variable name="stepsIgnoredCount" select="count(/wet/testcase/testrun/testfile/command[(descendant-or-self::ignored) and not(descendant::failure) and not(descendant::command/error) and not(descendant::testfile/error)])"/>
    <xsl:variable name="stepsNotOkCount" select="$testStepCount - $stepsOkCount"/>

    <xsl:variable name="testCaseFailurePercentage" select="format-number($testCaseFailureCount * 100 div $testCaseCount, '#')"/>
    <xsl:variable name="testCaseErrorPercentage" select="format-number($testCaseErrorCount * 100 div $testCaseCount, '#')"/>
    <xsl:variable name="testCaseOkPercentage" select="100 - $testCaseFailurePercentage - $testCaseErrorPercentage"/>
    <xsl:variable name="testFailurePercentage" select="format-number($testFailureCount * 100 div $testCount, '#')"/>
    <xsl:variable name="testErrorPercentage" select="format-number($testErrorCount * 100 div $testCount, '#')"/>
    <xsl:variable name="testIgnoredPercentage" select="format-number($testIgnoredCount * 100 div $testCount, '#')"/>
    <xsl:variable name="testNotOkPercentage" select="$testFailurePercentage + $testErrorPercentage + $testIgnoredPercentage"/>
    <xsl:variable name="testOkPercentage" select="100 - $testNotOkPercentage"/>

    <xsl:variable name="stepsOkPercentage" select="format-number($stepsOkCount * 100 div $testStepCount, '#')"/>
    <xsl:variable name="stepsFailurePercentage" select="format-number($stepsFailureCount * 100 div $testStepCount, '#')"/>
    <xsl:variable name="stepsErrorPercentage" select="format-number($stepsErrorCount * 100 div $testStepCount, '#')"/>
    <xsl:variable name="stepsIgnoredPercentage" select="100 - $stepsOkPercentage - $stepsFailurePercentage - $stepsErrorPercentage"/>

    <xsl:template match="/">
        <html>
            <head>
                <META http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <META http-equiv="content-style-type" content="text/css"/>
                <title>Wetator - Test Result</title>

                <script src="resources/jquery-1.10.2.min.js"></script>
                <script src="resources/wetator_report.js"></script>

                <style type="text/css">
                    body { background-color: #FFFFFF; font-size: 10pt; font-family: Arial, Helvetica, sans-serif; margin: 4px; }
                    table { font-size: 10pt; empty-cells: show; border-collapse: collapse; }
                    table.overview { width: 80%; }
                    th { font-weight: bold; color: #FFFFFF; background-color: #555555; text-align: left; }
                    td.step { border:1px solid #999; color: #000000; text-align: center; }
                    td.light { background-color: #f8f8f8; }
                    td.topBorder { border-top: 1px solid #999; }
                    td.message { background-color: #fff8dc; color: #666666; font-size: 10pt; }
                    td.properties { background-color: #f8f8f8; font-size: 10pt; }

                    td.success { background-color: <xsl:value-of select="$greenColor"/>; color: #ffffff; width: 4px; }
                    td.ignored { background-color: <xsl:value-of select="$ignoredColor"/>; color: #717173; width: 4px; }
                    td.failure { background-color: <xsl:value-of select="$blueColor"/>; color: #ffffff; width: 8px; }
                    td.error { background-color: <xsl:value-of select="$orangeColor"/>; color: #ffffff; width: 8px; }

                    td.errorJumper { text-align: right; vertical-align: top; }
                    td.comment { background-color: #DDDDDD; color: #717173; }
                    h1 { font-size: 12pt; color: #000000; margin-top: 20px; }
                    h2 { font-size: 10pt; color: #4682b4; margin-top: 16px; }
                    p.blue { color: #768bc2; }
                    pre.details { margin-top: 1px; margin-bottom: 1px; margin-left: 40px; font-family: Courier new, monospace, sans-serif; white-space: pre; }
                    a, a:link, a:visited, a:active, a:hover { color: #666666; text-decoration: none; }
                    a.link:hover { text-decoration: underline; }
                    a.linkToCommand { font-size: smaller; display: block; }
                    img { border: 0; }
                    div.header { color: #768bc2; margin-left: 10px; }
                    div.header img { margin-left: -10px; border: 0; }
                    div.colorBar { height: 1em; border: 0; margin-left: 2px; margin-right: 1px; }
                    .smallBorder { border: 1px solid #999999; }
                    .bold { font-weight: bold; }
                    #testSummary { border: 1px solid #555555; }
                    table#testSummary td { padding: 5px; }
                    p.backToTop img { padding-right: 3px; }
                    .bars { color: #ffffff; }
                    #debuginfo {display: none;}
                    #debugtestbrowseroverviewinfo { display: none; }

                    .describe { color: #3D2117; }
                    .describe h1 { font-size: 1.5em; font-weight: bold; color: #3D2117; }
                    .describe h2 { font-size: 1.17em; font-weight: bold; color: #3D2117; }
                    .describe h3 { font-size: 1em; font-weight: bold; color: #3D2117; }
                    .describe h4 { font-size: 1em; font-weight: bold; color: #3D2117; }
                    .describe h5 { font-size: 1em; font-weight: bold; color: #3D2117; }
                    .describe h6 { font-size: 1em; font-weight: bold; color: #3D2117; }
                </style>
            </head>

            <body>
                <div id="debuginfo">
                    testCaseCount <xsl:value-of select="$testCaseCount"/><br/>
                    browserCount <xsl:value-of select="$browserCount"/><br/>
                    testCount <xsl:value-of select="$testCount"/><br/>
                    testStepCount <xsl:value-of select="$testStepCount"/><br/>
                    <br/>
                    testCaseFailureCount <xsl:value-of select="$testCaseFailureCount"/><br/>
                    testCaseErrorCount <xsl:value-of select="$testCaseErrorCount"/><br/>
                    testFailureCount <xsl:value-of select="$testFailureCount"/><br/>
                    testErrorCount <xsl:value-of select="$testErrorCount"/><br/>
                    testIgnoredCount <xsl:value-of select="$testIgnoredCount"/><br/>
                    <br/>
                    testCaseFailurePercentage <xsl:value-of select="$testCaseFailurePercentage"/><br/>
                    testCaseErrorPercentage <xsl:value-of select="$testCaseErrorPercentage"/><br/>
                    testCaseOkPercentage <xsl:value-of select="$testCaseOkPercentage"/><br/>
                    testFailurePercentage <xsl:value-of select="$testFailurePercentage"/><br/>
                    testErrorPercentage <xsl:value-of select="$testErrorPercentage"/><br/>
                    testIgnoredPercentage <xsl:value-of select="$testIgnoredPercentage"/><br/>
                    testNotOkPercentage <xsl:value-of select="$testNotOkPercentage"/><br/>
                    testOkPercentage <xsl:value-of select="$testOkPercentage"/><br/>
                    <br/>
                    stepsOkCount <xsl:value-of select="$stepsOkCount"/><br/>
                    stepsFailureCount <xsl:value-of select="$stepsFailureCount"/><br/>
                    stepsErrorCount <xsl:value-of select="$stepsErrorCount"/><br/>
                    stepsIgnoredCount <xsl:value-of select="$stepsIgnoredCount"/><br/>
                    stepsNotOkCount <xsl:value-of select="$stepsNotOkCount"/><br/>
                    <br/>
                    stepsOkPercentage <xsl:value-of select="$stepsOkPercentage"/><br/>
                    stepsFailurePercentage <xsl:value-of select="$stepsFailurePercentage"/><br/>
                    stepsErrorPercentage <xsl:value-of select="$stepsErrorPercentage"/><br/>
                    stepsIgnoredPercentage <xsl:value-of select="$stepsIgnoredPercentage"/><br/>
                </div>

                <!-- preview -->
                <iframe id="preview" src=''>
                    <xsl:attribute name="style">
                        <xsl:text>overflow: hidden; display: none; position: absolute; background: white; border: solid 3px darkgray; box-shadow: 15px 15px 7px darkgray; </xsl:text>
                        <xsl:text>width: </xsl:text>
                        <xsl:value-of select="$previewWidth"/>
                        <xsl:text>px; height: </xsl:text>
                        <xsl:value-of select="$previewHeight"/>
                        <xsl:text>px; zoom: </xsl:text>
                        <xsl:value-of select="$previewZoom"/>
                        <xsl:text>; -moz-transform: scale(</xsl:text>
                        <xsl:value-of select="$previewZoom"/>
                        <xsl:text>); -moz-transform-origin: 0 0;</xsl:text>
                    </xsl:attribute>
                </iframe>

                <a name="top"/>

                <center><p><img src="resources/wetator.png" alt="Wetator"/></p></center>

                <table id="testSummary" align="center">
                    <tr>
                        <td class="bold">
                            Tests:
                        </td>
                        <td style="padding-left: 5px;">
                            <xsl:value-of select="$testCount"/>
                        </td>
                        <td class="bold" style="padding-left: 40px;">
                             <img src="resources/error.png" width="12" height="10" alt="error" title="error"/> Errors:
                        </td>
                        <td style="padding-left: 5px;">
                            <xsl:value-of select="$testErrorCount + $testIgnoredCount"/>
                        </td>
                        <td class="bold" style="padding-left: 40px;">
                             <img src="resources/failure.png" width="12" height="10" alt="failure" title="failure"/> Failures:
                        </td>
                        <td style="padding-left: 5px;">
                            <xsl:value-of select="$testFailureCount"/>
                        </td>

                        <td style="padding-left: 40px;">
                            Distribution:
                        </td>
                        <td style="padding-left: 5px;">
                            <xsl:value-of select="$testCaseCount"/> TestCase<xsl:if test="$testCaseCount > 1">s</xsl:if>
                            / <xsl:value-of select="$browserCount"/> Browser<xsl:if test="$browserCount > 1">s</xsl:if>
                        </td>
                        <td style="padding-left: 40px;">
                            Start Time:
                        </td>
                        <td style="padding-left: 5px;">
                        	<xsl:value-of select="wet/startTime"/>
                        </td>
                        <td style="padding-left: 40px;">
                            Total Time:
                        </td>
                        <td style="padding-left: 5px;">
                            <xsl:call-template name="time">
                                <xsl:with-param name="msecs" select="wet/executionTime"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </table>

                <table id="summaryoverview" class="overview" align="center" style="margin-top: 20px; text-align: center;"
                    onmouseover="switchTables(this, 'detailedoverview')">
                    <tr height="20px">
                        <td class="smallBorder" width="100%">
                            <xsl:if test="$testNotOkPercentage > 0">
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$orangeColor"/>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:if test="$testNotOkPercentage = 0">
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$greenColor"/>
                                </xsl:attribute>
                            </xsl:if>
                        </td>
                    </tr>
                </table>

                <table id="detailedoverview" class="overview bars"  align="center" style="display: none; margin-top: 20px; text-align: center;"
                    onmouseout="switchTables(this, 'summaryoverview')">
                    <tr height="20px;">
                        <xsl:if test="$testErrorPercentage > 0">
                            <td class="smallBorder">
                                <xsl:attribute name="width">
                                    <xsl:value-of select="$testErrorPercentage"/>%
                                </xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$orangeColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="'tests with error'"/>
                                </xsl:attribute>
                                <xsl:value-of select="$testErrorPercentage"/>%
                            </td>
                        </xsl:if>
                        <xsl:if test="$testFailurePercentage > 0">
                            <td class="smallBorder">
                                <xsl:attribute name="width">
                                    <xsl:value-of select="$testFailurePercentage"/>%
                                </xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$blueColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="'tests with failure'"/>
                                </xsl:attribute>
                                <xsl:value-of select="$testFailurePercentage"/>%
                            </td>
                        </xsl:if>
                        <xsl:if test="$testOkPercentage > 0">
                            <td class="smallBorder">
                                <xsl:attribute name="width">
                                    <xsl:value-of select="$testOkPercentage"/>%
                                </xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$greenColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="'successful tests'"/>
                                </xsl:attribute>
                                <xsl:value-of select="$testOkPercentage"/>%
                            </td>
                        </xsl:if>
                        <xsl:if test="$testIgnoredPercentage > 0">
                            <td class="smallBorder ignored">
                                <xsl:attribute name="width">
                                    <xsl:value-of select="$testIgnoredPercentage"/>%
                                </xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$ignoredColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="'ignored tests'"/>
                                </xsl:attribute>
                                <xsl:value-of select="$testIgnoredPercentage"/>%
                            </td>
                        </xsl:if>
                    </tr>
                </table>
                <div style="margin-top: -20px; margin-left: 90.4%;">
                    <img id="overviewswitcher" src="resources/expandall.png" alt="show/hide TestCases &amp; -Steps"
                        onclick="showOrHide(this, 'casesandsteps'); switchOverviewTables(this);" style="cursor: pointer;"/>
                </div>

                <table id="casesandsteps" class="overview" align="center" style="display: none; margin-top: 5px; float: center; text-align: center;">
                    <xsl:if test="$browserCount > 1">
                        <tr>
                            <td width="150px">
                                TestCases <xsl:if test="$testCaseNotOkCount > 0">(<xsl:if test="$testCaseErrorCount > 0"><span>
                                    <xsl:attribute name="style">
                                        <xsl:text>color: </xsl:text>
                                        <xsl:value-of select="$orangeColor"/>
                                        <xsl:text>; font-weight: bold;</xsl:text>
                                    </xsl:attribute>
                                    <xsl:value-of select="$testCaseErrorCount"/>
                                </span><xsl:if test="$testCaseFailureCount > 0">/</xsl:if></xsl:if><xsl:if test="$testCaseFailureCount > 0"><span>
                                    <xsl:attribute name="style">
                                        <xsl:text>color: </xsl:text>
                                        <xsl:value-of select="$blueColor"/>
                                        <xsl:text>; font-weight: bold;</xsl:text>
                                    </xsl:attribute>
                                    <xsl:value-of select="$testCaseFailureCount"/></span></xsl:if>/<xsl:value-of select="$testCaseCount"/>)</xsl:if>
                            </td>
                            <td style="padding-right: 0;">
                                <table class="bars" cellpadding="0" cellspacing="0" width="100%">
                                    <tr>
                                        <xsl:if test="$testCaseErrorPercentage > 0">
                                            <td class="smallBorder" style="text-align: center;">
                                                <xsl:attribute name="width">
                                                    <xsl:value-of select="$testCaseErrorPercentage"/>%
                                                </xsl:attribute>
                                                <xsl:attribute name="bgcolor">
                                                    <xsl:value-of select="$orangeColor"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="title">
                                                    <xsl:value-of select="'test cases with error'"/>
                                                </xsl:attribute>
                                                <xsl:value-of select="$testCaseErrorPercentage"/>%
                                            </td>
                                        </xsl:if>
                                        <xsl:if test="$testCaseFailurePercentage > 0">
                                            <td class="smallBorder" style="text-align: center;">
                                                <xsl:attribute name="width">
                                                    <xsl:value-of select="$testCaseFailurePercentage"/>%
                                                </xsl:attribute>
                                                <xsl:attribute name="bgcolor">
                                                    <xsl:value-of select="$blueColor"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="title">
                                                    <xsl:value-of select="'test cases with failure'"/>
                                                </xsl:attribute>
                                                <xsl:value-of select="$testCaseFailurePercentage"/>%
                                            </td>
                                        </xsl:if>
                                        <xsl:if test="$testCaseOkPercentage > 0">
                                            <td class="smallBorder" style="text-align: center;">
                                                <xsl:attribute name="width">
                                                    <xsl:value-of select="$testCaseOkPercentage"/>%
                                                </xsl:attribute>
                                                <xsl:attribute name="bgcolor">
                                                    <xsl:value-of select="$greenColor"/>
                                                </xsl:attribute>
                                                <xsl:attribute name="title">
                                                    <xsl:value-of select="'successful test cases'"/>
                                                </xsl:attribute>
                                                <xsl:value-of select="$testCaseOkPercentage"/>%
                                            </td>
                                        </xsl:if>
                                        <td></td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                        <tr><td/></tr>
                    </xsl:if>

                    <tr>
                        <td width="150px">
                                TestSteps <xsl:if test="$stepsNotOkCount > 0">(<xsl:if test="$stepsErrorCount > 0"><span>
                                    <xsl:attribute name="style">
                                        <xsl:text>color: </xsl:text>
                                        <xsl:value-of select="$orangeColor"/>
                                        <xsl:text>; font-weight: bold;</xsl:text>
                                    </xsl:attribute>
                                    <xsl:value-of select="$stepsErrorCount"/>
                                </span><xsl:if test="$stepsFailureCount > 0">/</xsl:if></xsl:if><xsl:if test="$stepsFailureCount > 0"><span>
                                    <xsl:attribute name="style">
                                        <xsl:text>color: </xsl:text>
                                        <xsl:value-of select="$blueColor"/>
                                        <xsl:text>; font-weight: bold;</xsl:text>
                                    </xsl:attribute>
                                    <xsl:value-of select="$stepsFailureCount"/></span></xsl:if>/<xsl:value-of select="$testStepCount"/>)</xsl:if>
                            </td>
                        <td style="padding-right: 0;">
                            <table class="bars" cellpadding="0" cellspacing="0" width="100%">
                                <tr>
                                    <!-- error steps -->
                                    <xsl:if test="$stepsErrorPercentage > 0">
                                        <td class="smallBorder" style="text-align: center;">
                                            <xsl:attribute name="width">
                                                <xsl:value-of select="$stepsErrorPercentage"/>%
                                            </xsl:attribute>
                                            <xsl:attribute name="bgcolor">
                                                <xsl:value-of select="$orangeColor"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="'steps with error'"/>
                                            </xsl:attribute>
                                            <xsl:if test="$stepsErrorPercentage >= 3">
                                                <xsl:value-of select="$stepsErrorPercentage"/>%
                                            </xsl:if>
                                        </td>
                                    </xsl:if>
                                    <xsl:if test="$stepsErrorPercentage = 0 and $testCaseErrorCount > 0">
                                        <td class="smallBorder">
                                            <xsl:attribute name="width">1%</xsl:attribute>
                                            <xsl:attribute name="bgcolor">
                                                <xsl:value-of select="$orangeColor"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="'steps with error'"/>
                                            </xsl:attribute>
                                        </td>
                                    </xsl:if>

                                    <!-- failure steps -->
                                    <xsl:if test="$stepsFailurePercentage > 0">
                                        <td class="smallBorder" style="text-align: center;">
                                            <xsl:attribute name="width">
                                                <xsl:value-of select="$stepsFailurePercentage"/>%
                                            </xsl:attribute>
                                            <xsl:attribute name="bgcolor">
                                                <xsl:value-of select="$blueColor"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="'steps with failure'"/>
                                            </xsl:attribute>
                                            <xsl:if test="$stepsFailurePercentage >= 3">
                                                <xsl:value-of select="$stepsFailurePercentage"/>%
                                            </xsl:if>
                                        </td>
                                    </xsl:if>
                                    <xsl:if test="$stepsFailurePercentage = 0 and $testCaseFailureCount > 0">
                                        <td class="smallBorder">
                                            <xsl:attribute name="width">1%</xsl:attribute>
                                            <xsl:attribute name="bgcolor">
                                                <xsl:value-of select="$blueColor"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="'steps with failure'"/>
                                            </xsl:attribute>
                                        </td>
                                    </xsl:if>

                                    <!-- successful steps -->
                                    <xsl:if test="$stepsOkPercentage > 0">
                                        <td class="smallBorder" style="text-align: center;">
                                            <xsl:attribute name="width">
                                                <xsl:value-of select="$stepsOkPercentage"/>%
                                            </xsl:attribute>
                                            <xsl:attribute name="bgcolor">
                                                <xsl:value-of select="$greenColor"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="'successful steps'"/>
                                            </xsl:attribute>
                                            <xsl:if test="$stepsOkPercentage >= 3">
                                                <xsl:value-of select="$stepsOkPercentage"/>%
                                            </xsl:if>
                                        </td>
                                    </xsl:if>
                                    <xsl:if test="$stepsOkPercentage = 0 and $stepsOkCount > 0">
                                        <td class="smallBorder">
                                            <xsl:attribute name="width">1%</xsl:attribute>
                                            <xsl:attribute name="bgcolor">
                                                <xsl:value-of select="$greenColor"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="'successful steps'"/>
                                            </xsl:attribute>
                                        </td>
                                    </xsl:if>

                                    <!-- ignored steps -->
                                    <xsl:if test="$testStepCount = 0">
                                        <td class="smallBorder ignored" style="text-align: center;">
                                            <xsl:attribute name="width">100%</xsl:attribute>
                                            <xsl:attribute name="bgcolor">
                                                <xsl:value-of select="$ignoredColor"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="'ignored steps'"/>
                                            </xsl:attribute>
                                            100%
                                        </td>
                                    </xsl:if>
                                    <xsl:if test="$stepsIgnoredPercentage > 0">
                                        <td class="smallBorder ignored" style="text-align: center;">
                                            <xsl:attribute name="width">
                                                <xsl:value-of select="$stepsIgnoredPercentage"/>%
                                            </xsl:attribute>
                                            <xsl:attribute name="bgcolor">
                                                <xsl:value-of select="$ignoredColor"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="'ignored steps'"/>
                                            </xsl:attribute>
                                            <xsl:if test="$stepsIgnoredPercentage >= 3">
                                                <xsl:value-of select="$stepsIgnoredPercentage"/>%
                                            </xsl:if>
                                        </td>
                                    </xsl:if>
                                    <xsl:if test="$stepsIgnoredPercentage = 0 and $stepsIgnoredCount > 0">
                                        <td class="smallBorder ignored">
                                            <xsl:attribute name="width">1%</xsl:attribute>
                                            <xsl:attribute name="bgcolor">
                                                <xsl:value-of select="$ignoredColor"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="'ignored steps'"/>
                                            </xsl:attribute>
                                        </td>
                                    </xsl:if>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>

                <xsl:if test="$browserCount > 1">
                    <table align="center" cellpadding="4" cellspacing="0" border="0" style="margin-top: 25px">
                        <tr>
                            <td/>
                                <td>
                                    <span class="bold">All</span>
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                    <img src="resources/expandall.png" alt="show/hide all browser overviews" style="cursor: pointer;">
                                        <xsl:attribute name="onclick">
                                            <xsl:text>showOrHideAll(this</xsl:text>
                                            <xsl:if test="/wet/testcase/testrun/@browser='IE8'">, 'ie8','ie8overview'</xsl:if>
                                            <xsl:if test="/wet/testcase/testrun/@browser='IE11'">, 'ie11','ie11overview'</xsl:if>
                                            <xsl:if test="/wet/testcase/testrun/@browser='Firefox24'">, 'ff24','ff24overview'</xsl:if>
                                            <xsl:if test="/wet/testcase/testrun/@browser='Firefox31'">, 'ff31','ff31overview'</xsl:if>
                                            <xsl:text>)</xsl:text>
                                        </xsl:attribute>
                                    </img>
                                </td>
                            <xsl:if test="/wet/testcase/testrun/@browser='IE8'">
                                <td>
                                    <span class="bold">IE8</span>
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                    <img id="ie8" src="resources/expandall.png" onclick="showOrHide(this, 'ie8overview')" alt="show/hide IE8 overview" style="cursor: pointer;"/>
                                </td>
                            </xsl:if>
                            <xsl:if test="/wet/testcase/testrun/@browser='IE11'">
                                <td>
                                    <span class="bold">IE11</span>
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                    <img id="ie11" src="resources/expandall.png" onclick="showOrHide(this, 'ie11overview')" alt="show/hide IE11 overview" style="cursor: pointer;"/>
                                </td>
                            </xsl:if>
                            <xsl:if test="/wet/testcase/testrun/@browser='Firefox24'">
                                <td>
                                    <span class="bold">FF24</span>
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                    <img id="ff24" src="resources/expandall.png" onclick="showOrHide(this, 'ff24overview')" alt="show/hide FF24 overview" style="cursor: pointer;"/>
                                </td>
                            </xsl:if>
                            <xsl:if test="/wet/testcase/testrun/@browser='Firefox31'">
                                <td>
                                    <span class="bold">FF31</span>
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                    <img id="ff31" src="resources/expandall.png" onclick="showOrHide(this, 'ff31overview')" alt="show/hide FF31 overview" style="cursor: pointer;"/>
                                </td>
                            </xsl:if>
                        </tr>
                    </table>

                    <table id="ie8overview" class="overview" align="center" style="display: none; text-align: center;">
                        <xsl:if test="/wet/testcase/testrun/@browser='IE8'">
                            <xsl:variable name="failedIE8" select="count(/wet/testcase/testrun[@browser='IE8']/testfile[boolean(descendant::failure and not(descendant::command/error) and not(descendant::testfile/error))])"/>
                            <xsl:variable name="errorsIE8" select="count(/wet/testcase/testrun[@browser='IE8']/testfile[boolean(error or descendant-or-self::command/error or descendant::testfile/error)])"/>
                            <xsl:variable name="ignoredIE8" select="count(/wet/testcase/testrun[@browser='IE8']/ignored)"/>
                            <xsl:variable name="stepsOkIE8" select="count(/wet/testcase/testrun[@browser='IE8']/testfile/command[not(@isComment) and not(descendant-or-self::failure) and not(descendant-or-self::error) and not(descendant-or-self::ignored)])"/>
                            <xsl:variable name="stepsFailureIE8" select="count(/wet/testcase/testrun[@browser='IE8']/testfile/command[(descendant-or-self::failure) and not(descendant::command/error)])"/>
                            <xsl:variable name="stepsErrorIE8" select="count(/wet/testcase/testrun[@browser='IE8']/testfile[boolean(descendant-or-self::command/error or descendant::testfile/error)])"/>
                            <xsl:variable name="stepsIgnoredIE8" select="count(/wet/testcase/testrun[@browser='IE8']/testfile/command[(descendant-or-self::ignored) and not(descendant::failure) and not(descendant::command/error)])"/>
                            <xsl:call-template name="testBrowserOverview">
                                <xsl:with-param name="browserPicture" select="$browserPicture.IE"/>
                                <xsl:with-param name="browserName">IE8</xsl:with-param>
                                <xsl:with-param name="browserTestFailureCount" select="$failedIE8"/>
                                <xsl:with-param name="browserTestErrorCount" select="$errorsIE8"/>
                                <xsl:with-param name="browserTestIgnoredCount" select="$ignoredIE8"/>
                                <xsl:with-param name="browserStepsOkCount" select="$stepsOkIE8"/>
                                <xsl:with-param name="browserStepsFailureCount" select="$stepsFailureIE8"/>
                                <xsl:with-param name="browserStepsErrorCount" select="$stepsErrorIE8"/>
                                <xsl:with-param name="browserStepsIgnoredCount" select="$stepsIgnoredIE8"/>
                            </xsl:call-template>
                        </xsl:if>
                    </table>
                    <table id="ie11overview" class="overview" align="center" style="display: none; text-align: center;">
                        <xsl:if test="/wet/testcase/testrun/@browser='IE11'">
                            <xsl:variable name="failedIE11" select="count(/wet/testcase/testrun[@browser='IE11']/testfile[boolean(descendant::failure and not(descendant::command/error) and not(descendant::testfile/error))])"/>
                            <xsl:variable name="errorsIE11" select="count(/wet/testcase/testrun[@browser='IE11']/testfile[boolean(error or descendant-or-self::command/error or descendant::testfile/error)])"/>
                            <xsl:variable name="ignoredIE11" select="count(/wet/testcase/testrun[@browser='IE11']/ignored)"/>
                            <xsl:variable name="stepsOkIE11" select="count(/wet/testcase/testrun[@browser='IE11']/testfile/command[not(@isComment) and not(descendant-or-self::failure) and not(descendant-or-self::error) and not(descendant-or-self::ignored)])"/>
                            <xsl:variable name="stepsFailureIE11" select="count(/wet/testcase/testrun[@browser='IE11']/testfile/command[(descendant-or-self::failure) and not(descendant::command/error)])"/>
                            <xsl:variable name="stepsErrorIE11" select="count(/wet/testcase/testrun[@browser='IE11']/testfile[boolean(descendant-or-self::command/error or descendant::testfile/error)])"/>
                            <xsl:variable name="stepsIgnoredIE11" select="count(/wet/testcase/testrun[@browser='IE11']/testfile/command[(descendant-or-self::ignored) and not(descendant::failure) and not(descendant::command/error)])"/>
                            <xsl:call-template name="testBrowserOverview">
                                <xsl:with-param name="browserPicture" select="$browserPicture.IE"/>
                                <xsl:with-param name="browserName">IE11</xsl:with-param>
                                <xsl:with-param name="browserTestFailureCount" select="$failedIE11"/>
                                <xsl:with-param name="browserTestErrorCount" select="$errorsIE11"/>
                                <xsl:with-param name="browserTestIgnoredCount" select="$ignoredIE11"/>
                                <xsl:with-param name="browserStepsOkCount" select="$stepsOkIE11"/>
                                <xsl:with-param name="browserStepsFailureCount" select="$stepsFailureIE11"/>
                                <xsl:with-param name="browserStepsErrorCount" select="$stepsErrorIE11"/>
                                <xsl:with-param name="browserStepsIgnoredCount" select="$stepsIgnoredIE11"/>
                            </xsl:call-template>
                        </xsl:if>
                    </table>
                    <table id="ff24overview" class="overview" align="center" style="display: none; text-align: center;">
                        <xsl:if test="/wet/testcase/testrun/@browser='Firefox24'">
                            <xsl:variable name="failedFirefox24" select="count(/wet/testcase/testrun[@browser='Firefox24']/testfile[boolean(descendant::failure and not(descendant::command/error) and not(descendant::testfile/error))])"/>
                            <xsl:variable name="errorsFirefox24" select="count(/wet/testcase/testrun[@browser='Firefox24']/testfile[boolean(error or descendant-or-self::command/error or descendant::testfile/error)])"/>
                            <xsl:variable name="ignoredFirefox24" select="count(/wet/testcase/testrun[@browser='Firefox24']/ignored)"/>
                            <xsl:variable name="stepsOkFirefox24" select="count(/wet/testcase/testrun[@browser='Firefox24']/testfile/command[not(@isComment) and not(descendant-or-self::failure) and not(descendant-or-self::error) and not(descendant-or-self::ignored)])"/>
                            <xsl:variable name="stepsFailureFirefox24" select="count(/wet/testcase/testrun[@browser='Firefox24']/testfile/command[(descendant-or-self::failure) and not(descendant::command/error)])"/>
                            <xsl:variable name="stepsErrorFirefox24" select="count(/wet/testcase/testrun[@browser='Firefox24']/testfile[boolean(descendant-or-self::command/error or descendant::testfile/error)])"/>
                            <xsl:variable name="stepsIgnoredFirefox24" select="count(/wet/testcase/testrun[@browser='Firefox24']/testfile/command[(descendant-or-self::ignored) and not(descendant::failure) and not(descendant::command/error)])"/>
                            <xsl:call-template name="testBrowserOverview">
                                <xsl:with-param name="browserPicture" select="$browserPicture.Firefox"/>
                                <xsl:with-param name="browserName">FF24</xsl:with-param>
                                <xsl:with-param name="browserTestFailureCount" select="$failedFirefox24"/>
                                <xsl:with-param name="browserTestErrorCount" select="$errorsFirefox24"/>
                                <xsl:with-param name="browserTestIgnoredCount" select="$ignoredFirefox24"/>
                                <xsl:with-param name="browserStepsOkCount" select="$stepsOkFirefox24"/>
                                <xsl:with-param name="browserStepsFailureCount" select="$stepsFailureFirefox24"/>
                                <xsl:with-param name="browserStepsErrorCount" select="$stepsErrorFirefox24"/>
                                <xsl:with-param name="browserStepsIgnoredCount" select="$stepsIgnoredFirefox24"/>
                            </xsl:call-template>
                        </xsl:if>
                    </table>
                    <table id="ff31overview" class="overview" align="center" style="display: none; text-align: center;">
                        <xsl:if test="/wet/testcase/testrun/@browser='Firefox31'">
                            <xsl:variable name="failedFirefox31" select="count(/wet/testcase/testrun[@browser='Firefox31']/testfile[boolean(descendant::failure and not(descendant::command/error) and not(descendant::testfile/error))])"/>
                            <xsl:variable name="errorsFirefox31" select="count(/wet/testcase/testrun[@browser='Firefox31']/testfile[boolean(error or descendant-or-self::command/error or descendant::testfile/error)])"/>
                            <xsl:variable name="ignoredFirefox31" select="count(/wet/testcase/testrun[@browser='Firefox31']/ignored)"/>
                            <xsl:variable name="stepsOkFirefox31" select="count(/wet/testcase/testrun[@browser='Firefox31']/testfile/command[not(@isComment) and not(descendant-or-self::failure) and not(descendant-or-self::error) and not(descendant-or-self::ignored)])"/>
                            <xsl:variable name="stepsFailureFirefox31" select="count(/wet/testcase/testrun[@browser='Firefox31']/testfile/command[(descendant-or-self::failure) and not(descendant::command/error)])"/>
                            <xsl:variable name="stepsErrorFirefox31" select="count(/wet/testcase/testrun[@browser='Firefox31']/testfile[boolean(descendant-or-self::command/error or descendant::testfile/error)])"/>
                            <xsl:variable name="stepsIgnoredFirefox31" select="count(/wet/testcase/testrun[@browser='Firefox31']/testfile/command[(descendant-or-self::ignored) and not(descendant::failure) and not(descendant::command/error)])"/>
                            <xsl:call-template name="testBrowserOverview">
                                <xsl:with-param name="browserPicture" select="$browserPicture.Firefox"/>
                                <xsl:with-param name="browserName">FF31</xsl:with-param>
                                <xsl:with-param name="browserTestFailureCount" select="$failedFirefox31"/>
                                <xsl:with-param name="browserTestErrorCount" select="$errorsFirefox31"/>
                                <xsl:with-param name="browserTestIgnoredCount" select="$ignoredFirefox31"/>
                                <xsl:with-param name="browserStepsOkCount" select="$stepsOkFirefox31"/>
                                <xsl:with-param name="browserStepsFailureCount" select="$stepsFailureFirefox31"/>
                                <xsl:with-param name="browserStepsErrorCount" select="$stepsErrorFirefox31"/>
                                <xsl:with-param name="browserStepsIgnoredCount" select="$stepsIgnoredFirefox31"/>
                            </xsl:call-template>
                        </xsl:if>
                    </table>
                </xsl:if>

                <!-- Configuration & Variables -->
                <table align="center" cellpadding="4" cellspacing="0" border="0" style="margin-top: 10px">
                    <tr>
                        <td>
                            <span class="bold">Configuration</span>
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                            <img src="resources/expandall.png" onclick="showOrHide(this, 'configuration')" alt="show/hide Configuration" style="cursor: pointer;"/>
                        </td>
                        <td width="50px"></td>
                        <td>
                            <span class="bold">Variables</span>
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                            <img src="resources/expandall.png" onclick="showOrHide(this, 'variables')" alt="show/hide Variables" style="cursor: pointer;"/>
                        </td>
                    </tr>
                </table>
                <xsl:call-template name="configuration"/>


                <!-- test cases overview -->
                <table width="100%" cellpadding="2" cellspacing="0" class="smallBorder" border="0">
                    <tr>
                        <th style="width: 30px;">No</th>
                        <!-- success marker -->
                        <th style="width: 15px;"/>
                        <th>Name</th>
                        <!-- browser -->
                        <th/>
                        <th style="text-align: center;">Steps</th>
                        <th style="width: 80%">Graph</th>
                        <th style="text-align: right;">Duration</th>
                    </tr>

                    <tr>
                        <td colspan="7" height="7px;"/>
                    </tr>

                    <xsl:for-each select="/wet/testcase">
                        <xsl:call-template name="testcaseOverview"/>
                    </xsl:for-each>
                </table>

                <!-- All individual test results -->
                <a name="details"/>

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

                <div id="preview-spacer">
                    <xsl:attribute name="style">
                        <xsl:text>margin-bottom: </xsl:text>
                        <xsl:value-of select="$previewHeight"/>
                        <xsl:text>px;</xsl:text>
                    </xsl:attribute>
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="testBrowserOverview">
        <xsl:param name="browserPicture" />
        <xsl:param name="browserName" />
        <xsl:param name="browserTestFailureCount" />
        <xsl:param name="browserTestErrorCount" />
        <xsl:param name="browserTestIgnoredCount" />
        <xsl:param name="browserStepsOkCount" />
        <xsl:param name="browserStepsFailureCount" />
        <xsl:param name="browserStepsErrorCount" />
        <xsl:param name="browserStepsIgnoredCount" />

        <xsl:variable name="browserTestFailurePercentage" select="format-number($browserTestFailureCount * 100 div $testCaseCount, '#')"/>
        <xsl:variable name="browserTestErrorPercentage" select="format-number($browserTestErrorCount * 100 div $testCaseCount, '#')"/>
        <xsl:variable name="browserTestIgnoredPercentage" select="format-number($browserTestIgnoredCount * 100 div $testCaseCount, '#')"/>
        <xsl:variable name="browserTestOkPercentage" select="100 - $browserTestFailurePercentage - $browserTestErrorPercentage - $browserTestIgnoredPercentage"/>

        <xsl:variable name="browserTestStepCount" select="$browserStepsOkCount + $browserStepsFailureCount + $browserStepsErrorCount + $browserStepsIgnoredCount"/>
        <xsl:variable name="browserStepsFailurePercentage" select="format-number($browserStepsFailureCount * 100 div $browserTestStepCount, '#')"/>
        <xsl:variable name="browserStepsErrorPercentage" select="format-number($browserStepsErrorCount * 100 div $browserTestStepCount, '#')"/>
        <xsl:variable name="browserStepsOkPercentage" select="format-number($browserStepsOkCount * 100 div $browserTestStepCount, '#')"/>
        <xsl:variable name="browserStepsIgnoredPercentage" select="100 - $browserStepsOkPercentage - $browserStepsFailurePercentage - $browserStepsErrorPercentage"/>

        <div id="debugtestbrowseroverviewinfo">
            <br/>
            browserTestStepCount <xsl:value-of select="$browserTestStepCount"/><br/>
            <br/>
            browserTestFailureCount <xsl:value-of select="$browserTestFailureCount"/><br/>
            browserTestErrorCount <xsl:value-of select="$browserTestErrorCount"/><br/>
            browserTestIgnoredCount <xsl:value-of select="$browserTestIgnoredCount"/><br/>
            <br/>
            browserTestFailurePercentage <xsl:value-of select="$browserTestFailurePercentage"/><br/>
            browserTestErrorPercentage <xsl:value-of select="$browserTestErrorPercentage"/><br/>
            browserTestIgnoredPercentage <xsl:value-of select="$browserTestIgnoredPercentage"/><br/>
            browserTestOkPercentage <xsl:value-of select="$browserTestOkPercentage"/><br/>
            <br/>
            browserStepsOkCount <xsl:value-of select="$browserStepsOkCount"/><br/>
            browserStepsFailureCount <xsl:value-of select="$browserStepsFailureCount"/><br/>
            browserStepsErrorCount <xsl:value-of select="$browserStepsErrorCount"/><br/>
            browserStepsIgnoredCount <xsl:value-of select="$browserStepsIgnoredCount"/><br/>
            <br/>
            browserStepsOkPercentage <xsl:value-of select="$browserStepsOkPercentage"/><br/>
            browserStepsFailurePercentage <xsl:value-of select="$browserStepsFailurePercentage"/><br/>
            browserStepsErrorPercentage <xsl:value-of select="$browserStepsErrorPercentage"/><br/>
            browserStepsIgnoredPercentage <xsl:value-of select="$browserStepsIgnoredPercentage"/><br/>
        </div>

        <tr>
            <td width="150px;">Tests in <xsl:value-of select="$browserName"/>
                <img style="padding-left: 4px;">
                    <xsl:attribute name="src">
                        <xsl:value-of select="$browserPicture"/>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                        <xsl:value-of select="$browserName"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="$browserName"/>
                    </xsl:attribute>
                </img>
            </td>
            <td style="padding-right: 0;">
                <table class="bars" cellpadding="0" cellspacing="0" width="100%">
                <tr>
                    <xsl:if test="$browserTestErrorPercentage > 0">
                        <td class="smallBorder" style="text-align: center;">
                            <xsl:attribute name="width">
                                <xsl:value-of select="$browserTestErrorPercentage"/>%
                            </xsl:attribute>
                            <xsl:attribute name="bgcolor">
                                <xsl:value-of select="$orangeColor"/>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="$browserName"/>
                                <xsl:value-of select="' tests with error'"/>
                            </xsl:attribute>
                            <xsl:value-of select="$browserTestErrorPercentage"/>%
                        </td>
                    </xsl:if>
                    <xsl:if test="$browserTestFailurePercentage > 0">
                        <td class="smallBorder" style="text-align: center;">
                            <xsl:attribute name="width">
                                <xsl:value-of select="$browserTestFailurePercentage"/>%
                            </xsl:attribute>
                            <xsl:attribute name="bgcolor">
                                <xsl:value-of select="$blueColor"/>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="$browserName"/>
                                <xsl:value-of select="' tests with failure'"/>
                            </xsl:attribute>
                            <xsl:value-of select="$browserTestFailurePercentage"/>%
                        </td>
                    </xsl:if>
                    <xsl:if test="$browserTestOkPercentage > 0">
                        <td class="smallBorder" style="text-align: center;">
                            <xsl:attribute name="width">
                                <xsl:value-of select="$browserTestOkPercentage"/>%
                            </xsl:attribute>
                            <xsl:attribute name="bgcolor">
                                <xsl:value-of select="$greenColor"/>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="'successful '"/>
                                <xsl:value-of select="$browserName"/>
                                <xsl:value-of select="' tests'"/>
                            </xsl:attribute>
                            <xsl:value-of select="$browserTestOkPercentage"/>%
                        </td>
                    </xsl:if>
                    <xsl:if test="$browserTestIgnoredPercentage > 0">
                        <td class="smallBorder ignored" style="text-align: center;">
                            <xsl:attribute name="width">
                                <xsl:value-of select="$browserTestIgnoredPercentage"/>%
                            </xsl:attribute>
                            <xsl:attribute name="bgcolor">
                                <xsl:value-of select="$ignoredColor"/>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="'ignored '"/>
                                <xsl:value-of select="$browserName"/>
                                <xsl:value-of select="' tests'"/>
                            </xsl:attribute>
                            <xsl:value-of select="$browserTestIgnoredPercentage"/>%
                        </td>
                    </xsl:if>
                    <td></td>
                </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>TestSteps in <xsl:value-of select="$browserName"/>
                <img style="padding-left: 4px;">
                    <xsl:attribute name="src">
                        <xsl:value-of select="$browserPicture"/>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                        <xsl:value-of select="$browserName"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="$browserName"/>
                    </xsl:attribute>
                </img>
            </td>
            <td style="padding-right: 0;">
                <table class="bars" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                        <!-- error steps -->
                        <xsl:if test="$browserStepsErrorPercentage > 0">
                            <td class="smallBorder" style="text-align: center;">
                                <xsl:attribute name="width">
                                    <xsl:value-of select="$browserStepsErrorPercentage"/>%
                                </xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$orangeColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="$browserName"/>
                                    <xsl:value-of select="' steps with error'"/>
                                </xsl:attribute>
                                <xsl:if test="$browserStepsErrorPercentage >= 3">
                                    <xsl:value-of select="$browserStepsErrorPercentage"/>%
                                </xsl:if>
                            </td>
                        </xsl:if>
                        <xsl:if test="$browserStepsErrorPercentage = 0 and $browserStepsErrorCount > 0">
                            <td class="smallBorder" style="text-align: center;">
                                <xsl:attribute name="width">1%</xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$orangeColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="$browserName"/>
                                    <xsl:value-of select="' steps with error'"/>
                                </xsl:attribute>
                            </td>
                        </xsl:if>

                        <!-- failure steps -->
                        <xsl:if test="$browserStepsFailurePercentage > 0">
                            <td class="smallBorder" style="text-align: center;">
                                <xsl:attribute name="width">
                                    <xsl:value-of select="$browserStepsFailurePercentage"/>%
                                </xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$blueColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="$browserName"/>
                                    <xsl:value-of select="' steps with failure'"/>
                                </xsl:attribute>
                                <xsl:if test="$browserStepsFailurePercentage >= 3">
                                    <xsl:value-of select="$browserStepsFailurePercentage"/>%
                                </xsl:if>
                            </td>
                        </xsl:if>
                        <xsl:if test="$browserStepsFailurePercentage = 0 and $browserStepsFailureCount > 0">
                            <td class="smallBorder" style="text-align: center;">
                                <xsl:attribute name="width">1%</xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$blueColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="$browserName"/>
                                    <xsl:value-of select="' steps with failure'"/>
                                </xsl:attribute>
                            </td>
                        </xsl:if>

                        <!-- successful steps -->
                        <xsl:if test="$browserStepsOkPercentage > 0">
                            <td class="smallBorder" style="text-align: center;">
                                <xsl:attribute name="width">
                                    <xsl:value-of select="$browserStepsOkPercentage"/>%
                                </xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$greenColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="'successful '"/>
                                    <xsl:value-of select="$browserName"/>
                                    <xsl:value-of select="' steps'"/>
                                </xsl:attribute>
                                <xsl:if test="$browserStepsOkPercentage >= 3">
                                    <xsl:value-of select="$browserStepsOkPercentage"/>%
                                </xsl:if>
                            </td>
                        </xsl:if>
                        <xsl:if test="$browserStepsOkPercentage = 0 and $browserStepsOkCount > 0">
                            <td class="smallBorder" style="text-align: center;">
                                <xsl:attribute name="width">1%</xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$greenColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="'successful '"/>
                                    <xsl:value-of select="$browserName"/>
                                    <xsl:value-of select="' steps'"/>
                                </xsl:attribute>
                            </td>
                        </xsl:if>

                        <!-- ignored steps -->
                        <xsl:if test="$browserTestStepCount = 0">
                            <td class="smallBorder ignored" style="text-align: center;">
                                <xsl:attribute name="width">
                                    100%
                                </xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$ignoredColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="'ignored '"/>
                                    <xsl:value-of select="$browserName"/>
                                    <xsl:value-of select="' steps'"/>
                                </xsl:attribute>
                                100%
                            </td>
                        </xsl:if>
                        <xsl:if test="$browserStepsIgnoredPercentage > 0">
                            <td class="smallBorder ignored" style="text-align: center;">
                                <xsl:attribute name="width">
                                    <xsl:value-of select="$browserStepsIgnoredPercentage"/>%
                                </xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$ignoredColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="'ignored '"/>
                                    <xsl:value-of select="$browserName"/>
                                    <xsl:value-of select="' steps'"/>
                                </xsl:attribute>
                                <xsl:if test="$browserStepsIgnoredPercentage >= 3">
                                    <xsl:value-of select="$browserStepsIgnoredPercentage"/>%
                                </xsl:if>
                            </td>
                        </xsl:if>
                        <xsl:if test="$browserStepsIgnoredPercentage = 0 and $browserStepsIgnoredCount > 0">
                            <td class="smallBorder ignored" style="text-align: center;">
                                <xsl:attribute name="width">1%</xsl:attribute>
                                <xsl:attribute name="bgcolor">
                                    <xsl:value-of select="$ignoredColor"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="'ignored '"/>
                                    <xsl:value-of select="$browserName"/>
                                    <xsl:value-of select="' steps'"/>
                                </xsl:attribute>
                            </td>
                        </xsl:if>
                    <td/>
                </tr>
            </table>
        </td>
    </tr>
    <tr><td height="10px" colspan="2"/></tr>
    </xsl:template>

    <xsl:template name="configuration">
        <table width="100%" cellpadding="4" cellspacing="0" border="0" style="margin-top: 5px;">
          <tr>
              <td align="right" valign="top" style="width: 50%;">
                  <table id="configuration" cellpadding="2" cellspacing="0" class="smallBorder" style="display: none; margin-left: 20px; margin-bottom: 20px;">
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
              <td valign="top" style="width: 50%;">
                  <table id="variables" cellpadding="2" cellspacing="0" class="smallBorder" style="display: none; margin-left: 20px; margin-bottom: 20px;">
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
        <tr>
            <td align="center">
                <xsl:attribute name="rowspan"><xsl:value-of select="count(testrun)"/></xsl:attribute>
                <xsl:number/>
            </td>

            <td>
                <xsl:attribute name="rowspan"><xsl:value-of select="count(testrun)"/></xsl:attribute>
                <xsl:call-template name="successIndicator"/>
            </td>

            <td>
                <xsl:attribute name="rowspan"><xsl:value-of select="count(testrun)"/></xsl:attribute>
                <a class="link">
                    <xsl:attribute name="href">
                        <xsl:text>#testspec_</xsl:text>
                        <xsl:value-of select="testrun[1]/@id"/>
                    </xsl:attribute>
                    <xsl:value-of select="@name"/>
                </a>
            </td>

            <xsl:for-each select="testrun">
                <xsl:choose>
                    <xsl:when test="ignored or testfile/error">
                        <td>
                            <img>
                                <xsl:attribute name="src">
                                    <xsl:if test="@browser='IE8'">
                                        <xsl:value-of select="$browserPicture.IE"/>
                                    </xsl:if>
                                    <xsl:if test="@browser='IE11'">
                                        <xsl:value-of select="$browserPicture.IE"/>
                                    </xsl:if>
                                    <xsl:if test="@browser='Firefox24'">
                                        <xsl:value-of select="$browserPicture.Firefox"/>
                                    </xsl:if>
                                    <xsl:if test="@browser='Firefox31'">
                                        <xsl:value-of select="$browserPicture.Firefox"/>
                                    </xsl:if>
                                </xsl:attribute>
                                <xsl:attribute name="alt">
                                    <xsl:value-of select="../@browser"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:value-of select="../@browser"/>
                                </xsl:attribute>
                            </img>
                        </td>
                        <xsl:choose>
                            <xsl:when test="ignored">
                                <td align="center" class="ignored">
                                    ignored
                                </td>
                                <td colspan="2"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <td align="center" class="error" colspan="3">
                                    <xsl:value-of select="testfile/error/message"/>
                                </td>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>

                    <xsl:otherwise>
                        <xsl:for-each select="testfile">
                            <td>
                                <img>
                                    <xsl:attribute name="src">
                                        <xsl:if test="../@browser='IE8'">
                                            <xsl:value-of select="$browserPicture.IE"/>
                                        </xsl:if>
                                        <xsl:if test="../@browser='IE11'">
                                            <xsl:value-of select="$browserPicture.IE"/>
                                        </xsl:if>
                                        <xsl:if test="../@browser='Firefox24'">
                                            <xsl:value-of select="$browserPicture.Firefox"/>
                                        </xsl:if>
                                        <xsl:if test="../@browser='Firefox31'">
                                            <xsl:value-of select="$browserPicture.Firefox"/>
                                        </xsl:if>
                                    </xsl:attribute>
                                    <xsl:attribute name="alt">
                                        <xsl:value-of select="../@browser"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="title">
                                        <xsl:value-of select="../@browser"/>
                                    </xsl:attribute>
                                </img>
                            </td>

                            <td align="center"><xsl:value-of select="count(command[not(@isComment) and not(ignored)])"/>/<xsl:value-of select="count(command[not(@isComment)])"/></td>

                            <td>
                                <xsl:variable name="linelength" select="0"/>

                                <table align="left" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <xsl:for-each select="command[not(@isComment)]">
                                            <xsl:variable name="noOfFailures" select="sum(descendant::failure)"/>
                                            <xsl:variable name="noOfErrors" select="count(error) + count(descendant::command/error) + count(descendant::testfile/error)"/>
                                            <xsl:variable name="noOfSubSteps" select="count(descendant::command[not(@isComment)])"/>
                                            <xsl:variable name="ignored" select="descendant-or-self::ignored"/>

                                            <!-- start new line if needed -->
                                            <xsl:if test="(position() mod $noOfStepsInLine = 1) and (position() &gt; 1)">
                                                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;&lt;tr&gt;</xsl:text>
                                            </xsl:if>

                                            <td>
                                                <xsl:attribute name="class">
                                                    <xsl:text>step</xsl:text>
                                                    <xsl:choose>
                                                        <xsl:when test="$noOfErrors = 0 and $noOfFailures = 0">
                                                            <xsl:choose>
                                                                <xsl:when test="$ignored">
                                                                    <xsl:text> ignored</xsl:text>
                                                                </xsl:when>
                                                                <xsl:otherwise>
                                                                    <xsl:text> success</xsl:text>
                                                                </xsl:otherwise>
                                                            </xsl:choose>
                                                        </xsl:when>
                                                        <xsl:when test="$noOfErrors != 0">
                                                            <xsl:text> error</xsl:text>
                                                        </xsl:when>
                                                        <xsl:otherwise>
                                                            <xsl:text> failure</xsl:text>
                                                        </xsl:otherwise>
                                                    </xsl:choose>
                                                </xsl:attribute>

                                                <xsl:choose>
                                                  <xsl:when test="failure">
                                                     <xsl:attribute name="title">
                                                         <xsl:value-of select="failure/message"/>
                                                       </xsl:attribute>
                                                  </xsl:when>
                                                  <xsl:when test="error">
                                                     <xsl:attribute name="title">
                                                         <xsl:value-of select="error/message"/>
                                                       </xsl:attribute>
                                                  </xsl:when>
                                                  <xsl:when test="$noOfErrors != 0">
                                                    <xsl:attribute name="title">
                                                         <xsl:value-of select="normalize-space(testcase/command/error)"/>
                                                         <xsl:value-of select="normalize-space(testfile/error/message)"/>
                                                       </xsl:attribute>
                                                  </xsl:when>
                                                </xsl:choose>

                                                <xsl:element name="a">
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

                            <xsl:variable name="duration" select="sum(command/executionTime)"/>
                            <td align="right">
                                <xsl:call-template name="time">
                                    <xsl:with-param name="msecs" select="$duration"/>
                                </xsl:call-template>
                            </td>

                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>

                <xsl:text disable-output-escaping="yes">&lt;/tr&gt;</xsl:text>
                <xsl:text disable-output-escaping="yes">&lt;tr&gt;</xsl:text>
            </xsl:for-each>
        </tr>

        <tr><td colspan="7" height="7px"/></tr>
    </xsl:template>

    <xsl:template name="testresult">
        <xsl:for-each select="testrun/testfile">
            <h2>
                <xsl:call-template name="successIndicator"/>
                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                <a>
                    <xsl:attribute name="name">
                        <xsl:text>testspec_</xsl:text>
                        <xsl:value-of select="../@id"/>
                    </xsl:attribute>
                    <xsl:value-of select="@file"/>
                </a>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="../@browser"/>
                <xsl:text>)</xsl:text>
            </h2>
            <xsl:call-template name="testcaseTable" />

            <p class="backToTop">
                <a class="link" href="#top">
                    <img src="resources/top.png" width="11" height="10" alt="top"/>Back to top
                </a>
            </p>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="testcaseTable">
        <xsl:choose>
            <xsl:when test="count(command) > 0">
                <table cellpadding="2" cellspacing="0" width="100%" class="smallBorder">
                    <tr>
                        <th style="width: 30px;">Line</th>
                        <!-- success marker -->
                        <th style="width: 15px;"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                        <th style="width: 120px;">Command</th>
                        <!-- module expand/collapse -->
                        <th style="width: 20px;"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                        <th style="width: 50%;"><xsl:text disable-output-escaping="yes">Parameter&amp;nbsp;1</xsl:text></th>
                        <th><xsl:text disable-output-escaping="yes">Parameter&amp;nbsp;2</xsl:text></th>
                        <th><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                        <th style="text-align: right;">Duration</th>
                    </tr>
                    <xsl:for-each select="command">
                        <xsl:call-template name="command"/>
                    </xsl:for-each>
                    <tr>
                        <td class='light topBorder' colspan='7'></td>

                        <xsl:variable name="testcaseDuration" select="sum(command/executionTime)"/>
                        <td class='light topBorder' align="right">
                            <xsl:call-template name="time">
                                <xsl:with-param name="msecs" select="$testcaseDuration"/>
                            </xsl:call-template>
                        </td>
                    </tr>
                </table>
            </xsl:when>

            <xsl:when test="count(error) or count(descendant::command/error) &gt; 0">
                <table cellpadding="2" cellspacing="0" width="100%" class="smallBorder">
                    <tr>
                        <th style="width: 30px;"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></th>
                        <th>Error</th>
                    </tr>
                    <xsl:for-each select="error">
                    <tr>
                      <td>
                        <img src="resources/error.png" width="12" height="10" alt="error"/>
                      </td>
                      <td class="error">
                        <xsl:value-of select="message"/>
                      </td>
                    </tr>
                  </xsl:for-each>
                </table>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="command">
        <xsl:variable name="lineStyle">
            <xsl:choose>
                <xsl:when test="count(*[(descendant-or-self::ignored) and not(descendant::failure) and not(descendant::error)]) &gt; 0">
                    <xsl:text>ignored topBorder</xsl:text>
                </xsl:when>
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
            <xsl:text disable-output-escaping="yes">" align="center"&gt;</xsl:text>
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
                    <xsl:when test="count(error) or count(descendant::command/error) or count(descendant::testfile/error) &gt; 0">
                        <img src="resources/error.png" width="12" height="10" alt="error" title="error"/>
                    </xsl:when>
                    <xsl:when test="(count(descendant-or-self::failure)) &gt; 0">
                        <img src="resources/failure.png" width="12" height="10" alt="failure" title="failure"/>
                    </xsl:when>
                    <xsl:when test="(count(descendant-or-self::ignored)) &gt; 0">
                        <!-- nothing -->
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="resources/ok.png" width="12" height="10" alt="success" title="success"/>
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
                <xsl:if test="count(testfile) &gt; 0 and count(descendant-or-self::testfile/error) &lt; 1">
                    <img src="resources/expandall.png" alt="Show/Hide tests from called module" style="cursor: pointer;">
                        <xsl:attribute name="id">
                            <xsl:text>showHide_testfile_</xsl:text>
                            <xsl:value-of select="testfile/@id" />
                        </xsl:attribute>
                        <xsl:attribute name="onclick">
                          <xsl:text>showOrHide(this, 'testfile_</xsl:text>
                          <xsl:value-of select="testfile/@id" />
                          <xsl:text>');</xsl:text>
                        </xsl:attribute>
                    </img>
                </xsl:if>
                <xsl:if test="count(response) &gt; 0">
                    <xsl:for-each select="response">
                        <a target="_blank">
                            <xsl:attribute name="href">
                                <xsl:value-of select="."/>
                            </xsl:attribute>
                            <xsl:attribute name="onmouseover">
                                <xsl:text>showPreview(event,'</xsl:text>
                                <xsl:value-of select="."/>
                                <xsl:text>')</xsl:text>
                            </xsl:attribute>
                            <xsl:attribute name="onmouseout">
                                <xsl:text>hidePreview()</xsl:text>
                            </xsl:attribute>
                            <img src="resources/response.png" alt="view response"/>
                        </a>
                    </xsl:for-each>
                </xsl:if>
            <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>

            <xsl:choose>
                <xsl:when test="count(describe) &gt; 0">
                    <!-- describe -->
                    <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
                    <xsl:value-of select="$lineStyle" />
                    <xsl:text disable-output-escaping="yes">" colspan="2" &gt;</xsl:text>
                    <div class="describe">
                        <xsl:copy-of select="describe/node()"/>
                    </div>
                    <xsl:text disable-output-escaping="yes">&lt;/td&gt;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <!-- param0 -->
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
        
                    <!-- param1 -->
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
                </xsl:otherwise>
            </xsl:choose>

            <xsl:text disable-output-escaping="yes">&lt;td class="</xsl:text>
            <xsl:value-of select="$lineStyle" />
            <xsl:text disable-output-escaping="yes">" &gt;</xsl:text>
                <xsl:if test="count(log) &gt; 0 or count(error/details) &gt; 0">
                    <img src="resources/expandlogwarn.png" alt="Show/Hide log entries" style="cursor: pointer;">
                        <xsl:choose>
                            <xsl:when test="count(log/level[text() = 'WARN']) &gt; 0 or count(error/details) &gt; 0">
                                <xsl:attribute name="src">
                                    <xsl:text>resources/expandlogwarn.png</xsl:text>
                                </xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="src">
                                    <xsl:text>resources/expandlog.png</xsl:text>
                                </xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
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

        <xsl:if test="count(descendant-or-self::failure) or count(descendant-or-self::error) &gt; 0">
            <tr>
                <td class="light"/>
                <td class="light"/>
                <td class="light errorJumper">
                    <!-- link to previous failure/error if exists -->
                    <xsl:choose>
                        <xsl:when test="ancestor::command[count(descendant-or-self::failure) &gt; 0 or count(descendant-or-self::error) &gt; 0]">
                            <xsl:for-each select="ancestor::command[count(descendant-or-self::failure) &gt; 0 or count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="onclick">
                                         <xsl:text>makeVisible('testfile_</xsl:text>
                                      <xsl:value-of select="parent::testfile/@id" />
                                         <xsl:text>');</xsl:text>
                                    </xsl:attribute>

                                    <img src="resources/previous.png" width="11" height="10" alt="previous failure/error" title="previous failure/error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="preceding::command[count(descendant-or-self::failure) &gt; 0 or count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="onclick">
                                         <xsl:text>makeVisible('testfile_</xsl:text>
                                      <xsl:value-of select="parent::testfile/@id" />
                                         <xsl:text>');</xsl:text>
                                    </xsl:attribute>

                                    <img src="resources/previous.png" width="11" height="10" alt="previous failure/error" title="previous failure/error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>

                    <!-- link to next failure/error if exists -->
                    <xsl:choose>
                        <xsl:when test="count(descendant::command/failure) &gt; 0 or count(descendant::command/error) &gt; 0">
                            <xsl:for-each select="descendant::command[count(descendant-or-self::command/failure) &gt; 0 or count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="onclick">
                                         <xsl:text>makeVisible('testfile_</xsl:text>
                                      <xsl:value-of select="parent::testfile/@id" />
                                         <xsl:text>');</xsl:text>
                                    </xsl:attribute>

                                    <img src="resources/next.png" width="11" height="10" alt="next failure/error" title="next failure/error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="following::command[count(descendant-or-self::command/failure) &gt; 0 or count(descendant-or-self::error) &gt; 0][1]">
                                <a>
                                    <xsl:attribute name="href">
                                        <xsl:text>#</xsl:text>
                                        <xsl:value-of select="@id"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="onclick">
                                         <xsl:text>makeVisible('testfile_</xsl:text>
                                      <xsl:value-of select="parent::testfile/@id" />
                                         <xsl:text>');</xsl:text>
                                    </xsl:attribute>

                                    <img src="resources/next.png" width="11" height="10" alt="next failure/error" title="next failure/error"/>
                                </a>
                            </xsl:for-each>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
                <td class="light"/>
                <xsl:choose>
                    <xsl:when test="descendant-or-self::failure and not(descendant::command/error) and not(descendant::testfile/error)">
                        <td class="failure" colspan="4">
                            <xsl:value-of select="failure/message"/>
                        </td>
                    </xsl:when>
                    <xsl:otherwise>
                        <td class="error" colspan="4">
                            <xsl:value-of select="error/message"/>
                            <xsl:value-of select="testfile/error/message"/>
                        </td>
                    </xsl:otherwise>
                </xsl:choose>
            </tr>
        </xsl:if>

        <xsl:if test="count(log) &gt; 0 or count(error/details) &gt; 0">
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
                        <xsl:for-each select="error">
                            <tr>
                                <td>
                                    <img src="resources/log_warn.png" width="11" height="11" alt="error"/>
                                </td>
                                <td>
                                    <pre class='details'><xsl:value-of select="details"/></pre>
                                </td>
                            </tr>
                        </xsl:for-each>
                        <xsl:for-each select="log">
                            <tr>
                                <td>
                            <xsl:choose>
                                <xsl:when test="level[text() = 'INFO']">
                                    <img src="resources/log_info.png" width="11" height="11" alt="info"/>
                                </xsl:when>
                                <xsl:when test="level[text() = 'WARN']">
                                    <img src="resources/log_warn.png" width="11" height="11" alt="warn"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                                </td>
                                <td>
                                    <xsl:value-of select="message"/>
                                    <xsl:if test="details">
                                      <pre class='details'><xsl:value-of select="details"/></pre>
                                    </xsl:if>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </td>
            </tr>
        </xsl:if>

        <xsl:if test="(count(testfile)) &gt; 0 and count(testfile/error) &lt; 1">
              <tr style="display: none">
                   <xsl:attribute name="id">
                       <xsl:text>testfile_</xsl:text>
                       <xsl:value-of select="testfile/@id" />
                   </xsl:attribute>
                   <td class="light"/>
                   <td class="light"/>
                   <td class="light"/>
                   <td class="light"/>
                   <td class="light" colspan="4">
                       <xsl:for-each select="testfile">
                            <xsl:call-template name="testcaseTable"/>
                       </xsl:for-each>
                   </td>
              </tr>
          </xsl:if>
    </xsl:template>


    <!-- subroutines -->
    <xsl:template name="successIndicator">
        <xsl:choose>
            <xsl:when test="count(error) or count(descendant::command/error) or count(descendant::testfile/error) &gt; 0">
                <img src="resources/error.png" width="12" height="10" alt="error" title="error"/>
            </xsl:when>
            <xsl:when test="count(descendant-or-self::failure) &gt; 0">
                <img src="resources/failure.png" width="12" height="10" alt="failure" title="failure"/>
            </xsl:when>
            <xsl:otherwise>
                <img src="resources/ok.png" width="12" height="10" alt="success" title="success"/>
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
                <xsl:text>s</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>