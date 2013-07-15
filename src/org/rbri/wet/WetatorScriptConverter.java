/*
 * Copyright (c) 2008-2010 Ronald Brill
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

package org.rbri.wet;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.gui.DialogUtil;
import org.rbri.wet.scriptconverter.WetScriptConverter;
import org.rbri.wet.scriptcreator.ScriptCreator;
import org.rbri.wet.scriptcreator.WetScriptCreator;
import org.rbri.wet.scriptcreator.XmlScriptCreator;
import org.rbri.wet.scripter.Scripter;
import org.rbri.wet.scripter.WetScripter;

/**
 * The command line interface for converting test scripts.
 *
 * @author tobwoerk
 */
public final class WetatorScriptConverter {

	private static final Log LOG = LogFactory.getLog(WetatorScriptConverter.class);;

	/**
	 * The start point for the command line call
	 *
	 * @param anArgsArray
	 *            the command line arguments
	 */
	public static void main(String[] anArgsArray) {
		LOG.info(Version.getFullProductName());
		LOG.info("    " + com.gargoylesoftware.htmlunit.Version.getProductName() + " " + com.gargoylesoftware.htmlunit.Version.getProductVersion());

		if (null == anArgsArray || anArgsArray.length < 3) {
			System.err.println("Parameters: <scripter> <script creator> <outputDir> (<dtd type> <dtd>)");
			System.err.println("example1: xsl xml /Users/me/tests");
			System.err.println("example2: xsl xml /Users/me/tests SYSTEM testcase.dtd");
			System.exit(1);
		}
		String tmpScripterType = anArgsArray[0];
		String tmpScriptCreatorType = anArgsArray[1];
		String tmpOutputDir = anArgsArray[2];
		System.out.println("Starting converter using scripter '" + tmpScripterType + "', script creator '" + tmpScriptCreatorType + " and output directory '" + tmpOutputDir + "'.");

		WetScriptConverter tmpConverter = new WetScriptConverter();
		try {
			Scripter tmpScripter = Scripter.valueOf(tmpScripterType.toUpperCase());
			WetScripter tmpWetScripter = tmpScripter.getWetScripter();
			ScriptCreator tmpScriptCreator = ScriptCreator.valueOf(tmpScriptCreatorType.toUpperCase());
			WetScriptCreator tmpCreator = tmpScriptCreator.getWetScriptCreator();
			tmpCreator.setOutputDir(tmpOutputDir);
			if (tmpCreator instanceof XmlScriptCreator && anArgsArray.length == 5) {
				String tmpDtd = anArgsArray[3] + " \"" + anArgsArray[4] + "\"";
				System.out.println("Using DTD '" + tmpDtd + "'.");
				((XmlScriptCreator) tmpCreator).setDtd(tmpDtd);
			}
			tmpConverter.setScripter(tmpWetScripter);
			tmpConverter.setCreator(tmpCreator);
			File[] tmpFiles = DialogUtil.chooseFiles();
			if (null == tmpFiles || (tmpFiles.length < 1)) {
				return;
			}

			for (int i = 0; i < tmpFiles.length; i++) {
				tmpConverter.addTestFile(tmpFiles[i]);
			}

			tmpConverter.convert();
		} catch (WetException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
