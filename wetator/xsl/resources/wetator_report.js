/*
 * Copyright (c) 2008-2015 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/* JS functions used by the Wetator report */


    function showOrHideAll(image, imageId, id) {
        var tmpSearch = "expandall.png";
        if (toggleImage(image)) {
            tmpSearch = "collapseall.png";
        }
        for (var i=1; i<arguments.length; i=i+2) {
            var tmpImage=document.getElementById(arguments[i]);
            if (tmpImage && tmpImage.src.indexOf(tmpSearch) != -1) {
                showOrHide(tmpImage, arguments[i+1]);
            }
        }
    }

    function showOrHide(image, id) {
        var tmpElement=document.getElementById(id);
        if (toggleImage(image)) {
            tmpElement.style.display = "none";
        } else {
            tmpElement.style.display = "";
        }
    }

    function toggleImage(image) {
        if (image.src.indexOf("collapseall.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"expandall.png";
            return true;
        }
        if (image.src.indexOf("expandall.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"collapseall.png";
            return false;
        }
        if (image.src.indexOf("collapselog.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"expandlog.png";
            return true;
        }
        if (image.src.indexOf("expandlog.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"collapselog.png";
            return false;
        }
        if (image.src.indexOf("collapselogwarn.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"expandlogwarn.png";
            return true;
        }
        if (image.src.indexOf("expandlogwarn.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"collapselogwarn.png";
            return false;
        }
    }

    function makeVisible(id) {
        var tmpImage = document.getElementById('showHide_' + id);
        if (tmpImage && tmpImage.src.indexOf("expandall.png") != -1) {
            showOrHide(tmpImage, id);
        }
    }

    function switchTables(tableToHide, tableToShowId, force) {
        var overviewSwitcher=document.getElementById('overviewswitcher');
        if (force || overviewSwitcher.src.indexOf("expandall.png") != -1) {
            tableToHide.style.display = "none";
            document.getElementById(tableToShowId).style.display = "";
        }
    }

    function switchOverviewTables(image) {
        if (image.src.indexOf("expandall.png") != -1) {
            var tmpDetailedOverview=document.getElementById('detailedoverview');
            switchTables(tmpDetailedOverview, 'summaryoverview', true);
        } else if (image.src.indexOf("collapseall.png") != -1) {
            var tmpSummaryOverview=document.getElementById('summaryoverview');
            switchTables(tmpSummaryOverview, 'detailedoverview', true);
        }
    }

    function showAllTestCases() {
    	$('.successful').removeClass('hidden');
    	$('#hideSuccessful').removeClass('hidden');
    	$('#showAll').addClass('hidden');
    }
    
    function hideSuccessfulTestCases() {
    	$('.successful').addClass('hidden');
    	$('#hideSuccessful').addClass('hidden');
    	$('#showAll').removeClass('hidden');
    }

    var wetFrameHeight = -1;

    function showPreview(e, src) {
        // preview only for html and txt files
        if ((src.lastIndexOf(".html") != src.length - 5)
            && (src.indexOf(".html?highlight=") < 0)
            && (src.indexOf(".txt") != src.length - 4)) {
            return;
        }

        var tmpFrame = document.getElementById('preview');
        tmpFrame.contentWindow.location.replace(src);
        tmpFrame.onload = function() {

            if (wetFrameHeight < 0) {
                tmpFrame.style.display = 'block';
                wetFrameHeight = tmpFrame.getBoundingClientRect().height;
                wetFrameHeight += 30;
            }

            var box = e.target.getBoundingClientRect();
            var tmpPosX = box.right + 20;
            var tmpPosY = Math.max(Math.min(box.top, window.innerHeight - wetFrameHeight), 10);

            if(window.pageYOffset) {
                tmpPosX = tmpPosX + window.pageXOffset;
                tmpPosY = tmpPosY + window.pageYOffset;
            } else {
                tmpPosX = tmpPosX + document.body.scrollLeft;
                tmpPosY = tmpPosY + document.body.scrollTop;
            }

            tmpFrame.style.left = tmpPosX + "px";
            tmpFrame.style.top = tmpPosY + "px";
            tmpFrame.style.display='block';
            tmpFrame.contentWindow.document.onmouseout=hidePreview;
            tmpFrame.contentWindow.document.onmouseover=hidePreview;

            tmpFrame.contentWindow.highlight();
        }
    }

    function hidePreview() {
        var tmpFrame = document.getElementById('preview');
        tmpFrame.style.display='none';
    }

    function highlight() {
        var elem = $(getParameterByName('highlight'));
        elem.css('box-shadow', '0 0 2px 2px #E65212');
        var offset = elem.offset();
        offset.left -= 200;
        offset.top -= 400;
        $('html, body').animate({
            scrollTop: offset.top,
            scrollLeft: offset.left
        })
    }

    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
        var results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }
