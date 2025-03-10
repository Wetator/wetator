/*
 * Copyright (c) 2008-2025 wetator.org
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

import org.apache.commons.lang3.StringUtils;
import org.htmlunit.html.HtmlElement;
import org.wetator.backend.ControlFeature;
import org.wetator.backend.control.IClickable;
import org.wetator.backend.control.IControl;
import org.wetator.backend.control.IDeselectable;
import org.wetator.backend.control.IDisableable;
import org.wetator.backend.control.IFocusable;
import org.wetator.backend.control.ISelectable;
import org.wetator.backend.control.ISettable;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.ForHtmlElement;
import org.wetator.backend.htmlunit.control.HtmlUnitBaseControl.IdentifiedBy;
import org.wetator.backend.htmlunit.control.identifier.AbstractHtmlUnitControlIdentifier;

/**
 * Central repository for all supported {@link HtmlUnitBaseControl}s.
 *
 * @author frank.danek
 */
public class HtmlUnitControlRepository {

  private Map<String, Class<HtmlUnitBaseControl<?>>> forElementMap = new HashMap<>();
  private Map<String, Map<String, Class<HtmlUnitBaseControl<?>>>> forElementAndAttributeMap = new HashMap<>();

  private Map<ControlFeature, List<Class<? extends AbstractHtmlUnitControlIdentifier>>> identifiers = new HashMap<>();

  /**
   * Initializes the repository.
   */
  public HtmlUnitControlRepository() {
    for (final ControlFeature tmpAction : ControlFeature.values()) {
      identifiers.put(tmpAction, new LinkedList<>());
    }
  }

  /**
   * @param aControlClassList the classes of the controls to add
   */
  public void addAll(final List<Class<? extends IControl>> aControlClassList) {
    if (aControlClassList != null) {
      for (final Class<? extends IControl> tmpControlClass : aControlClassList) {
        add(tmpControlClass);
      }
    }
  }

  /**
   * @param aControlClass the class of the control to add
   */
  @SuppressWarnings("unchecked")
  public void add(final Class<? extends IControl> aControlClass) {
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
            tmpAttributeMap = new HashMap<>();
            forElementAndAttributeMap.put(tmpHtmlElementClass.getName(), tmpAttributeMap);
          }
          for (final String tmpValue : tmpAttributeValues) {
            tmpAttributeMap.put(tmpAttributeName + "||" + tmpValue, (Class<HtmlUnitBaseControl<?>>) aControlClass);
          }
        }
      }

      final IdentifiedBy tmpIdentifiers = aControlClass.getAnnotation(IdentifiedBy.class);
      if (tmpIdentifiers != null) {
        final List<Class<? extends AbstractHtmlUnitControlIdentifier>> tmpIdentifierClasses = Arrays
            .asList(tmpIdentifiers.value());

        if (IClickable.class.isAssignableFrom(aControlClass)) {
          identifiers.get(ControlFeature.CLICK).addAll(tmpIdentifierClasses);
          identifiers.get(ControlFeature.CLICK_DOUBLE).addAll(tmpIdentifierClasses);
          identifiers.get(ControlFeature.CLICK_RIGHT).addAll(tmpIdentifierClasses);
        }
        if (ISettable.class.isAssignableFrom(aControlClass)) {
          identifiers.get(ControlFeature.SET).addAll(tmpIdentifierClasses);
        }
        if (ISelectable.class.isAssignableFrom(aControlClass)) {
          identifiers.get(ControlFeature.SELECT).addAll(tmpIdentifierClasses);
        }
        if (IDeselectable.class.isAssignableFrom(aControlClass)) {
          identifiers.get(ControlFeature.DESELECT).addAll(tmpIdentifierClasses);
        }
        if (IDisableable.class.isAssignableFrom(aControlClass)) {
          identifiers.get(ControlFeature.DISABLE).addAll(tmpIdentifierClasses);
        }
        if (IFocusable.class.isAssignableFrom(aControlClass)) {
          identifiers.get(ControlFeature.FOCUS).addAll(tmpIdentifierClasses);
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
    final Map<String, Class<HtmlUnitBaseControl<?>>> tmpAttributeMap = forElementAndAttributeMap
        .get(anHtmlElement.getClass().getName());
    if (tmpAttributeMap != null) {
      for (final Entry<String, Class<HtmlUnitBaseControl<?>>> tmpEntry : tmpAttributeMap.entrySet()) {
        final String[] tmpParts = tmpEntry.getKey().split("\\|\\|");
        if (tmpParts[1].equals(anHtmlElement.getAttribute(tmpParts[0]))) {
          return tmpEntry.getValue();
        }
      }
    }
    return forElementMap.get(anHtmlElement.getClass().getName());
  }

  /**
   * @param aFeature the {@link ControlFeature} to get the identifiers for
   * @return a list containing the identifiers or an empty list
   */
  public List<Class<? extends AbstractHtmlUnitControlIdentifier>> getIdentifiers(final ControlFeature aFeature) {
    return identifiers.get(aFeature);
  }
}
