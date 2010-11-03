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


package org.rbri.wet.backend.htmlunit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.rbri.wet.backend.control.Clickable;
import org.rbri.wet.backend.control.Control;
import org.rbri.wet.backend.control.Deselectable;
import org.rbri.wet.backend.control.Selectable;
import org.rbri.wet.backend.control.Settable;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl;
import org.rbri.wet.backend.htmlunit.control.HtmlUnitBaseControl.Identifiers;
import org.rbri.wet.backend.htmlunit.control.identifier.AbstractHtmlUnitElementIdentifier;

/**
 * Central repository for all supported {@link HtmlUnitBaseControl}s.
 * 
 * @author frank.danek
 */
public class HtmlUnitControlRepository {

  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> settableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> clickableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> selectableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> deselectableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitElementIdentifier>> otherIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitElementIdentifier>>();

  /**
   * @param aControlClassList the classes of the controls to add
   */
  public void addAll(List<Class<? extends Control>> aControlClassList) {
    if (aControlClassList != null) {
      for (Class<? extends Control> tmpControlClass : aControlClassList) {
        add(tmpControlClass);
      }
    }
  }

  /**
   * @param aControlClass the class of the control to add
   */
  public void add(Class<? extends Control> aControlClass) {
    if (aControlClass == null) {
      return;
    }
    if (HtmlUnitBaseControl.class.isAssignableFrom(aControlClass)) {
      Identifiers tmpIdentifiers = aControlClass.getAnnotation(Identifiers.class);
      if (tmpIdentifiers != null) {
        List<Class<? extends AbstractHtmlUnitElementIdentifier>> tmpIdentifierClasses = Arrays.asList(tmpIdentifiers
            .value());

        boolean tmpFound = false;
        if (Settable.class.isAssignableFrom(aControlClass)) {
          tmpFound = true;
          settableIdentifiers.addAll(tmpIdentifierClasses);
        }
        if (Clickable.class.isAssignableFrom(aControlClass)) {
          tmpFound = true;
          clickableIdentifiers.addAll(tmpIdentifierClasses);
        }
        if (Selectable.class.isAssignableFrom(aControlClass)) {
          tmpFound = true;
          selectableIdentifiers.addAll(tmpIdentifierClasses);
        }
        if (Deselectable.class.isAssignableFrom(aControlClass)) {
          tmpFound = true;
          deselectableIdentifiers.addAll(tmpIdentifierClasses);
        }
        if (!tmpFound) {
          otherIdentifiers.addAll(tmpIdentifierClasses);
        }
      }
    }
  }

  /**
   * @return the settableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getSettableIdentifiers() {
    return settableIdentifiers;
  }

  /**
   * @return the clickableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getClickableIdentifiers() {
    return clickableIdentifiers;
  }

  /**
   * @return the selectableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getSelectableIdentifiers() {
    return selectableIdentifiers;
  }

  /**
   * @return the deselectableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getDeselectableIdentifiers() {
    return deselectableIdentifiers;
  }

  /**
   * @return the otherIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitElementIdentifier>> getOtherIdentifiers() {
    return otherIdentifiers;
  }
}
