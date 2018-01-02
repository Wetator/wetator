/*
 * Copyright (c) 2008-2018 wetator.org
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


package org.wetator.jenkins.result;

import java.io.Serializable;

/**
 * Contains the information about the error occurred.
 *
 * @author frank.danek
 */
public class TestError implements Serializable {

  private static final long serialVersionUID = -2907486519741761958L;

  private ErrorType type = ErrorType.TEST;
  private String file;
  private String error;

  /**
   * @return the type
   */
  public ErrorType getType() {
    return type;
  }

  /**
   * @param aType the type to set
   */
  public void setType(ErrorType aType) {
    type = aType;
  }

  /**
   * @return the file
   */
  public String getFile() {
    return file;
  }

  /**
   * @param aFile the file to set
   */
  public void setFile(String aFile) {
    file = aFile;
  }

  /**
   * @return the error
   */
  public String getError() {
    return error;
  }

  /**
   * @param aError the error to set
   */
  public void setError(String aError) {
    error = aError;
  }

  /**
   * @author frank.danek
   */
  public enum ErrorType {
    TEST,
    STEP
  }
}
