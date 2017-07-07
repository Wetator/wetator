/*
 * Copyright (c) 2008-2017 wetator.org
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
        var tmpSearch = "expand-square.png";
        if (toggleImage(image)) {
            tmpSearch = "collapse-square.png";
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
        if (image.src.indexOf("collapse-square.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"expand-square.png";
            return true;
        }
        if (image.src.indexOf("expand-square.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"collapse-square.png";
            return false;
        }
        if (image.src.indexOf("collapse-circle.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"expand-circle.png";
            return true;
        }
        if (image.src.indexOf("expand-circle.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"collapse-circle.png";
            return false;
        }
        if (image.src.indexOf("collapse-circle-blue.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"expand-circle-blue.png";
            return true;
        }
        if (image.src.indexOf("expand-circle-blue.png") != -1) {
            image.src = image.src.substr(0, image.src.lastIndexOf("/")+1)+"collapse-circle-blue.png";
            return false;
        }
    }

    function makeVisible(id) {
        var tmpImage = document.getElementById('showHide_' + id);
        if (tmpImage && tmpImage.src.indexOf("expand-square.png") != -1) {
            showOrHide(tmpImage, id);
        }
    }

    function switchTables(tableToHide, tableToShowId, force) {
        var overviewSwitcher=document.getElementById('overviewswitcher');
        if (force || overviewSwitcher.src.indexOf("expand-square.png") != -1) {
            tableToHide.style.display = "none";
            document.getElementById(tableToShowId).style.display = "";
        }
    }

    function switchOverviewTables(image) {
        if (image.src.indexOf("expand-square.png") != -1) {
            var tmpDetailedOverview=document.getElementById('detailedoverview');
            switchTables(tmpDetailedOverview, 'summaryoverview', true);
        } else if (image.src.indexOf("collapse-square.png") != -1) {
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
        setTimeout( function() {
            var orange = jQuery.Color('#E65212');
            var blue = jQuery.Color('#0000ff');

            var win = window;
            var sel = getParameterByName(win, 'highlight');
            while ((!sel || sel.length === 0) && (win.parent && win.parent != win)) {
                win = win.parent;
                sel = getParameterByName(win, 'highlight');
            }
            if (!sel || sel.length === 0) { return; }
  
            var elem = $(sel);
            if (!elem) { return; }

            var shadow = '0 0 2px 2px ';
            var bkg = jQuery.Color(elem.css('background-color'));
            if (Math.abs(bkg.hue() - orange.hue()) > 10) {
                shadow = shadow + orange;
            } else {
                shadow = shadow + blue;
            }

            var inset = '';
            if (elem.attr('type') === 'checkbox'
                    || elem.attr('type') === 'radio'
                    || elem.is('select')
                    || elem.is('button')
                    || elem.is('submit')
                    || elem.is('reset')
                ) {
                // we can't set the shadow to 'inset' for these controls
                // force some margin instead
                if (elem.outerHeight(true) - elem.innerHeight() < 2) {
                    elem.css('margin', ' 2px');
                } else if (elem.outerWidth(true) - elem.innerWidth() < 2) {
                    elem.css('margin', ' 2px');
                }
            } else {
                if (elem.outerHeight(true) - elem.innerHeight() < 2) {
                    inset = ' inset';
                } else if (elem.outerWidth(true) - elem.innerWidth() < 2) {
                    inset = ' inset';
                }
            }

            elem.attr('style', function(i,s) { return s + '; box-shadow: ' + shadow + inset + ' !important;' });

            // images inside anchors are special
            if (elem.is('a')) {
                elem.children('img').first().css('box-shadow', shadow);
            }


            var offset = elem.offset();
            offset.left -= 200;
            offset.top -= 400;
            $('html, body').animate({
                scrollTop: offset.top,
                scrollLeft: offset.left
            })
        }, 44);
    }

    function getParameterByName(win, name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)");
        var results = regex.exec(win.location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }