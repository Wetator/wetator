/*
 * Copyright (c) 2008-2011 www.wetator.org
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


package org.wetator.backend.htmlunit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.wetator.backend.control.Clickable;
import org.wetator.backend.control.Control;
import org.wetator.backend.control.Deselectable;
import org.wetator.backend.control.Selectable;
import org.wetator.backend.control.Settable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;

import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Central repository for all supported {@link HtmlUnitBaseControl}s.
 * 
 * @author frank.danek
 */
public class HtmlUnitControlRepository {

  private Map<String, Class<HtmlUnitBaseControl<?>>> forElementMap = new HashMap<String, Class<HtmlUnitBaseControl<?>>>();
  private Map<String, Map<String, Class<HtmlUnitBaseControl<?>>>> forElementAndAttributeMap = new HashMap<String, Map<String, Class<HtmlUnitBaseControl<?>>>>();

  private List<Class<? extends AbstractHtmlUnitControlIdentifier>> settableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitControlIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitControlIdentifier>> clickableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitControlIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitControlIdentifier>> selectableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitControlIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitControlIdentifier>> deselectableIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitControlIdentifier>>();
  private List<Class<? extends AbstractHtmlUnitControlIdentifier>> otherIdentifiers = new LinkedList<Class<? extends AbstractHtmlUnitControlIdentifier>>();

  /**
   * @param aControlClassList the classes of the controls to add
   */
  public void addAll(final List<Class<? extends Control>> aControlClassList) {
    if (aControlClassList != null) {
      for (Class<? extends Control> tmpControlClass : aControlClassList) {
        add(tmpControlClass);
      }
    }
  }

  /**
   * @param aControlClass the class of the control to add
   */
  @SuppressWarnings("unchecked")
  public void add(final Class<? extends Control> aControlClass) {
    if (aControlClass == null) {
      return;
    }
    if (HtmlUnitBaseControl.class.isAssignableFrom(aControlClass)) {
      final ForHtmlElement tmpForHtmlElement = aControlClass.getAnnotation(ForHtmlElement.class);
      if (tmpForHtmlElement != null) {
        final Class<? extends HtmlElement> tmpHtmlElementClass = tmpForHtmlElement.value();
        final String tmpAttributeName = tmpForHtmlElement.attributeName();
        final String[] tmpAttributeValues = tmpForHtmlElement.attributeValues();

        if (StringUtils.isEmpty(tmpAttributeName) || tmpAttributeValues == null || tmpAttributeValues.length == 0) {
          forElementMap.put(tmpHtmlElementClass.getName(), (Class<HtmlUnitBaseControl<?>>) aControlClass);
        } else {
          Map<String, Class<HtmlUnitBaseControl<?>>> tmpAttributeMap = forElementAndAttributeMap
              .get(tmpHtmlElementClass.getName());
          if (tmpAttributeMap == null) {
            tmpAttributeMap = new HashMap<String, Class<HtmlUnitBaseControl<?>>>();
            forElementAndAttributeMap.put(tmpHtmlElementClass.getName(), tmpAttributeMap);
          }
          for (String tmpValue : tmpAttributeValues) {
            tmpAttributeMap.put(tmpAttributeName + "||" + tmpValue, (Class<HtmlUnitBaseControl<?>>) aControlClass);
          }
        }
      }

      final IdentifiedBy tmpIdentifiers = aControlClass.getAnnotation(IdentifiedBy.class);
      if (tmpIdentifiers != null) {
        final List<Class<? extends AbstractHtmlUnitControlIdentifier>> tmpIdentifierClasses = Arrays
            .asList(tmpIdentifiers.value());

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
   * @param anHtmlElement the {@link HtmlElement}
   * @return the control for the given {@link HtmlElement}
   */
  public Class<? extends HtmlUnitBaseControl<?>> getForHtmlElement(final HtmlElement anHtmlElement) {
    if (anHtmlElement == null) {
      return null;
    }
    final Map<String, Class<HtmlUnitBaseControl<?>>> tmpAttributeMap = forElementAndAttributeMap.get(anHtmlElement
        .getClass().getName());
    if (tmpAttributeMap != null) {
      for (Entry<String, Class<HtmlUnitBaseControl<?>>> tmpEntry : tmpAttributeMap.entrySet()) {
        final String[] tmpParts = tmpEntry.getKey().split("\\|\\|");
        if (tmpParts[1].equals(anHtmlElement.getAttribute(tmpParts[0]))) {
          return tmpEntry.getValue();
        }
      }
    }
    return forElementMap.get(anHtmlElement.getClass().getName());
  }

  /**
   * @return the settableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitControlIdentifier>> getSettableIdentifiers() {
    return settableIdentifiers;
  }

  /**
   * @return the clickableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitControlIdentifier>> getClickableIdentifiers() {
    return clickableIdentifiers;
  }

  /**
   * @return the selectableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitControlIdentifier>> getSelectableIdentifiers() {
    return selectableIdentifiers;
  }

  /**
   * @return the deselectableIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitControlIdentifier>> getDeselectableIdentifiers() {
    return deselectableIdentifiers;
  }

  /**
   * @return the otherIdentifiers
   */
  public List<Class<? extends AbstractHtmlUnitControlIdentifier>> getOtherIdentifiers() {
    return otherIdentifiers;
  }
}
