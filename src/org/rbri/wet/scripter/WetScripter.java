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


package org.rbri.wet.scripter;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.rbri.wet.core.WetCommand;
import org.rbri.wet.exception.WetException;


/**
 * The interface for all backends.
 * This interface contains the functions used
 * by the core CommandSet.   
 *  
 * @author rbri
 */
public interface WetScripter {
    
    /**
     * Sets the file this scripter works on.
     * Also this method must read the whole list of commands.
     * 
     * @param aFile the file
     * @throws WetException in case of error
     */
    public void setFile(File aFile) throws WetException;

    
    /**
     * Returns true, if this scripter is able to handle this
     * file.
     * @param aFile the file to check
     * 
     * @return true or false
     */
    public boolean isSupported(File aFile);


    /**
     * Returns the complete list of commands.
     * 
     * @return the complete list of commands.
     */
    public List<WetCommand> getCommands();

    public void initialize(Properties aConfiguration);
}
