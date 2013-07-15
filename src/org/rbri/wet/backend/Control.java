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


package org.rbri.wet.backend;

import java.io.File;

import org.rbri.wet.exception.AssertionFailedException;
import org.rbri.wet.exception.WetException;
import org.rbri.wet.util.SecretString;





/**
 * The common interface for the
 * backend.
 *
 * @author rbri
 */
public interface Control {

    public String getDescribingText();

    public boolean isSelected() throws WetException, AssertionFailedException;
    public boolean isDisabled() throws WetException, AssertionFailedException;

    public String getValue() throws WetException, AssertionFailedException;

    public void select() throws WetException, AssertionFailedException;
    public void setValue(SecretString aValue, File aDirectory) throws WetException, AssertionFailedException;
    public void click() throws WetException, AssertionFailedException;
    public void mouseOver() throws WetException, AssertionFailedException;

    public boolean hasSameBackendControl(Control aControl);
}