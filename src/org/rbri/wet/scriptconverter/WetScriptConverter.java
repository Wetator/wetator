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

package org.rbri.wet.scriptconverter;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.rbri.wet.core.WetCommand;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.scriptcreator.WetScriptCreator;
import org.rbri.wet.scripter.WetScripter;

/**
 * The converter for wetator test scripts. To use it set a scripter and a creator first.<br/>
 * Then - with addTestFile() - add the test files to convert and call convert() afterwards.
 *
 * @author tobwoerk
 */
public class WetScriptConverter {

	WetScripter scripter;
	WetScriptCreator creator;

	private LinkedList<File> inputFiles;

	/**
	 * Constructor
	 */
	public WetScriptConverter() {
		inputFiles = new LinkedList<File>();
	}

	/**
	 * @throws WetException in case of errors
	 */
	public void convert() throws WetException {
		for (File tmpInputFile : inputFiles) {
			System.out.println("Converting '" + tmpInputFile.getAbsolutePath() + "'...");
			scripter.setFile(tmpInputFile);
			List<WetCommand> tmpCommands = scripter.getCommands();

			String tmpFileName = tmpInputFile.getName().substring(0, tmpInputFile.getName().lastIndexOf('.'));
			creator.setFileName(tmpFileName);
			creator.setCommands(tmpCommands);
			creator.createScript();
			System.out.println("Converted '" + tmpInputFile.getAbsolutePath() + "'...");
		}
	}

	/**
	 * @return the scripter
	 */
	public WetScripter getScripter() {
		return scripter;
	}

	/**
	 * @param aScripter
	 *            the scripter to set
	 */
	public void setScripter(WetScripter aScripter) {
		scripter = aScripter;
	}

	/**
	 * @return the creator
	 */
	public WetScriptCreator getCreator() {
		return creator;
	}

	/**
	 * @param aCreator
	 *            the creator to set
	 */
	public void setCreator(WetScriptCreator aCreator) {
		creator = aCreator;
	}

	/**
	 * @param aFile
	 *            the file to add
	 * @throws WetException
	 *             if aFile does not exist
	 */
	public void addTestFile(File aFile) throws WetException {
		if (!aFile.exists()) {
			throw new WetException("The file '" + aFile.getAbsolutePath() + "' does not exist.");
		}
		inputFiles.add(aFile);
	}
}
