/*
 * Copyright (c) 2008-2011 wetator.org
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


package org.wetator.jenkins;

import hudson.Extension;
import hudson.Plugin;

/**
 * The Wetator plug-in entry point.
 * 
 * @author frank.danek
 */
@Extension
public class PluginImpl extends Plugin {

  /**
   * The file name of the icon.
   */
  public static final String ICON_FILE_NAME = "/plugin/wetator-jenkins-plugin/icons/wetator-48x48.png";
  /**
   * The URL of the report.
   */
  public static final String URL_NAME = "wetatorReport";

  public static final String TEST_RESULTS_NAME = "WetatorResults";
}
