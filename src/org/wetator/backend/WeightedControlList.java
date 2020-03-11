/*
 * Copyright (c) 2008-2020 wetator.org
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


package org.wetator.backend;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.wetator.backend.control.IControl;

/**
 * List to store {@link IControl}s together with some 'weight' information. Then it is possible to sort the list by this
 * criterion.
 *
 * @author rbri
 * @author frank.danek
 */
public final class WeightedControlList {

  /** An empty {@link WeightedControlList}. */
  public static final WeightedControlList EMPTY_LIST = new WeightedControlList();

  private final List<Entry> entries;

  /**
   * The constructor.
   */
  public WeightedControlList() {
    entries = Collections.synchronizedList(new LinkedList<Entry>());
  }

  /**
   * Creates a new entry and adds the entry to the list.
   *
   * @param aControl the control
   * @param aFoundType the found type
   * @param aDeviation the deviation
   * @param aDistance the distance
   * @param aStart the start
   * @param aHierarchy the hierarchy
   * @param anIndex the index
   */
  public void add(final IControl aControl, final FoundType aFoundType, final int aDeviation, final int aDistance,
      final int aStart, final String aHierarchy, final int anIndex) {
    final Entry tmpEntry = new Entry();
    tmpEntry.control = aControl;
    tmpEntry.foundType = aFoundType;
    tmpEntry.deviation = aDeviation;
    tmpEntry.distance = aDistance;
    tmpEntry.start = aStart;
    tmpEntry.hierarchy = aHierarchy;
    tmpEntry.index = anIndex;

    entries.add(tmpEntry);
  }

  /**
   * @return a new list of Entries sorted by weight
   */
  public List<Entry> getEntriesSorted() {
    Collections.sort(entries, new EntryComperator());

    final List<Entry> tmpResult = new LinkedList<>();
    for (final Entry tmpEntry : entries) {
      final IControl tmpControl = tmpEntry.getControl();

      boolean tmpNotPresent = true;
      for (final Entry tmpResultEntry : tmpResult) {
        final IControl tmpResultControl = tmpResultEntry.getControl();
        if (tmpResultControl.hasSameBackendControl(tmpControl)) {
          tmpNotPresent = false;
          break;
        }
      }
      if (tmpNotPresent) {
        tmpResult.add(tmpEntry);
      }
    }

    return tmpResult;
  }

  /**
   * Adds all entries from the given {@link WeightedControlList} to this list.
   *
   * @param anOtherWeightedControlList the list of entries to add
   */
  public void addAll(final WeightedControlList anOtherWeightedControlList) {
    entries.addAll(anOtherWeightedControlList.entries);
  }

  /**
   * @return <code>true</code>, if the list is empty
   */
  public boolean isEmpty() {
    return entries.isEmpty();
  }

  @Override
  public String toString() {
    return "WeightedControlList " + entries;
  }

  /**
   * Class for the different found by types.<br>
   * Smaller values are more important.
   */
  public static final class FoundType {
    /**
     * Found by title match.<br>
     * This is used from UnknownHtmlUnitControls finder because we
     * do a text search in this case and a title is not directly visible
     * to the user -&gt; larger value
     */
    public static final FoundType BY_TITLE_TEXT = new FoundType("BY_TITLE_TEXT", 9900);

    /** Found by text match. */
    public static final FoundType BY_TEXT = new FoundType("BY_TEXT", 9000);

    /** Found by table coordinates match. */
    public static final FoundType BY_TABLE_COORDINATE = new FoundType("BY_TABLE_COORDINATE", 6000);

    /** Found by aria-label attribute match. */
    public static final FoundType BY_ARIA_LABEL_ATTRIBUTE = new FoundType("BY_ARIA_LABEL_ATTRIBUTE", 5500);

    /** Found by image source attribute match. */
    public static final FoundType BY_IMG_SRC_ATTRIBUTE = new FoundType("BY_IMG_SRC_ATTRIBUTE", 5000);

    /** Found by image alt attribute match. */
    public static final FoundType BY_IMG_ALT_ATTRIBUTE = new FoundType("BY_IMG_ALT_ATTRIBUTE", 5000);

    /** Found by image title attribute match. */
    public static final FoundType BY_IMG_TITLE_ATTRIBUTE = new FoundType("BY_IMG_TITLE_ATTRIBUTE", 5000);

    /** Found by inner image source attribute match. */
    public static final FoundType BY_INNER_IMG_SRC_ATTRIBUTE = new FoundType("BY_INNER_IMG_SRC_ATTRIBUTE", 4000);

    /** Found by inner image alt attribute match. */
    public static final FoundType BY_INNER_IMG_ALT_ATTRIBUTE = new FoundType("BY_INNER_IMG_ALT_ATTRIBUTE", 4000);

    /** Found by inner image title attribute match. */
    public static final FoundType BY_INNER_IMG_TITLE_ATTRIBUTE = new FoundType("BY_INNER_IMG_TITLE_ATTRIBUTE", 4000);

    /** Found by title attribute match. */
    public static final FoundType BY_TITLE_ATTRIBUTE = new FoundType("BY_TITLE_ATTRIBUTE", 3500);

    /** Found by labeling text match. */
    public static final FoundType BY_LABELING_TEXT = new FoundType("BY_LABELING_TEXT", 3000);

    /** Found by placeholder text match. */
    public static final FoundType BY_PLACEHOLDER = new FoundType("BY_PLACEHOLDER", 2500);

    /** Found by label HTML element match. */
    public static final FoundType BY_LABEL_ELEMENT = new FoundType("BY_LABEL_ELEMENT", 2000);

    /** Found by label (the text on a control) match. */
    public static final FoundType BY_LABEL = new FoundType("BY_LABEL", 2000);

    /** Found by name match. */
    public static final FoundType BY_NAME = new FoundType("BY_NAME", 1000);

    /** Found by inner name match. */
    public static final FoundType BY_INNER_NAME = new FoundType("BY_INNER_NAME", 900);

    /** Found by id match. */
    public static final FoundType BY_ID = new FoundType("BY_ID", 400);

    private final String name;
    private final int value;

    /**
     * @param aName the name
     * @param aBaseType the type to be used as base for the new value calculation
     * @param anOffset the offset to be added
     */
    public FoundType(final String aName, final FoundType aBaseType, final int anOffset) {
      name = aName;
      value = aBaseType.value + anOffset;
    }

    /**
     * @param aName the name
     * @param aValue the weight
     */
    FoundType(final String aName, final int aValue) {
      name = aName;
      value = aValue;
    }

    /**
     * @return the current entry value
     */
    public int getValue() {
      return value;
    }

    @Override
    public String toString() {
      // return name + "(" + value + ")";
      return name;
    }
  }

  /**
   * An entry of a {@link WeightedControlList}.
   */
  public static final class Entry {
    private IControl control;
    private FoundType foundType;
    private int deviation;
    private int distance;
    private int start;
    private String hierarchy;
    private int index;

    /**
     * @return the encapsulated control
     */
    public IControl getControl() {
      return control;
    }

    @Override
    public String toString() {
      // @formatter:off
      final StringBuilder tmpResult = new StringBuilder(control.getDescribingText())
          .append(" found by: ")
          .append(foundType.toString())
          .append(" deviation: ")
          .append(Integer.toString(deviation))
          .append(" distance: ")
          .append(Integer.toString(distance))
          .append(" start: ")
          .append(Integer.toString(start))
          .append(" hierarchy: ")
          .append(hierarchy)
          .append(" index: ")
          .append(Integer.toString(index));
      // @formatter:on
      return tmpResult.toString();
    }
  }

  /**
   * The comparator used to sort {@link WeightedControlList} entries.
   */
  private static final class EntryComperator implements Comparator<Entry>, Serializable {

    private static final long serialVersionUID = 8655421244982375767L;

    @Override
    public int compare(final Entry anEntry1, final Entry anEntry2) {
      final int tmpWeightComp = anEntry1.foundType.getValue() - anEntry2.foundType.getValue();

      if (0 == tmpWeightComp) {
        final int tmpDeviationComp = anEntry1.deviation - anEntry2.deviation;

        if (0 == tmpDeviationComp) {
          final int tmpDistanceComp = anEntry1.distance - anEntry2.distance;

          if (0 == tmpDistanceComp) {
            final int tmpStartComp = anEntry1.start - anEntry2.start;

            if (0 == tmpStartComp) {
              if (anEntry1.hierarchy.startsWith(anEntry2.hierarchy)) {
                return -1;
              }
              if (anEntry2.hierarchy.startsWith(anEntry1.hierarchy)) {
                return 1;
              }

              return anEntry1.index - anEntry2.index;
            }

            return tmpStartComp;
          }

          return tmpDistanceComp;
        }

        return tmpDeviationComp;
      }

      return tmpWeightComp;
    }
  }
}