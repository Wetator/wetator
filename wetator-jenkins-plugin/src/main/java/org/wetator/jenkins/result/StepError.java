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


package org.wetator.jenkins.result;

import java.util.List;

/**
 * Contains the information about the error occurred.
 * 
 * @author frank.danek
 */
public class StepError extends TestError {

  private static final long serialVersionUID = 3119972170800383706L;

  private CauseType causeType;
  private int line;
  private String command;
  private List<String> parameters;

  /**
   * The constructor.
   */
  public StepError() {
    setType(ErrorType.STEP);
  }

  /**
   * @return the causeType
   */
  public CauseType getCauseType() {
    return causeType;
  }

  /**
   * @param aCauseType the causeType to set
   */
  public void setCauseType(CauseType aCauseType) {
    causeType = aCauseType;
  }

  /**
   * @return the line
   */
  public int getLine() {
    return line;
  }

  /**
   * @param aLine the line to set
   */
  public void setLine(int aLine) {
    line = aLine;
  }

  /**
   * @return the command
   */
  public String getCommand() {
    return command;
  }

  /**
   * @param aCommand the command to set
   */
  public void setCommand(String aCommand) {
    command = aCommand;
  }

  /**
   * @return the parameters
   */
  public List<String> getParameters() {
    return parameters;
  }

  /**
   * @param aParameters the parameters to set
   */
  public void setParameters(List<String> aParameters) {
    parameters = aParameters;
  }

  /**
   * @author frank.danek
   */
  public enum CauseType {
    ERROR,
    FAILURE
  }
}
