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


package org.wetator.backend.control;

import java.io.File;

import org.wetator.core.WetatorContext;
import org.wetator.exception.ActionException;
import org.wetator.exception.AssertionException;
import org.wetator.util.SecretString;

/**
 * This interface marks all settable {@link IControl}s. These controls are returned by
 * {@link org.wetator.backend.IControlFinder#getAllSettables(org.wetator.backend.WPath)}.
 * 
 * @author frank.danek
 */
public interface ISettable extends IControl {

  /**
   * Sets the value of the control.
   * 
   * @param aContext the context
   * @param aValue the new value of the control
   * @param aDirectory parameter only used for file upload controls; for this the aValue is the name of a file and
   *        aDirectory points to the directory for searching the file
   * @throws ActionException if an error occurred during the set
   */
  void setValue(WetatorContext aContext, SecretString aValue, File aDirectory) throws ActionException;

  /**
   * Asserts that the value of the control is equal to the given (expected) value.
   * 
   * @param aContext the context
   * @param anExpectedValue the expected value of the control
   * @throws AssertionException if the value of the control does not match the expected value
   */
  void assertValue(WetatorContext aContext, SecretString anExpectedValue) throws AssertionException;
}
