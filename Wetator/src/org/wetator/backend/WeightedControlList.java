/*
 * Copyright (c) 2008-2013 wetator.org
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
 */
public final class WeightedControlList {

  /**
   * Enum for the different found by types.<br/>
   * Smaller values are more important.
   */
  public enum FoundType {
    /** Found by text match. */
    BY_TEXT(9999),

    /** Found by table coordindates match. */
    BY_TABLE_COORDINATE(6000),

    /** Found by image source attribute match. */
    BY_IMG_SRC_ATTRIBUTE(5000),
    /** Found by image alt attribute match. */
    BY_IMG_ALT_ATTRIBUTE(5000),
    /** Found by image title attribute match. */
    BY_IMG_TITLE_ATTRIBUTE(5000),

    /** Found by inner image source attribute match. */
    BY_INNER_IMG_SRC_ATTRIBUTE(4000),
    /** Found by inner image alt attribute match. */
    BY_INNER_IMG_ALT_ATTRIBUTE(4000),
    /** Found by inner image title attribute match. */
    BY_INNER_IMG_TITLE_ATTRIBUTE(4000),

    /** Found by title attribute match. */
    BY_TITLE_ATTRIBUTE(3500),

    /** Found by label text match. */
    BY_LABEL_TEXT(3000),

    /** Found by label match. */
    BY_LABEL(2000),

    /** Found by name match. */
    BY_NAME(1000),
    /** Found by inner name match. */
    BY_INNER_NAME(900),

    /** Found by id match. */
    BY_ID(400);

    private int value;

    private FoundType(final int aValue) {
      value = aValue;
    }

    /**
     * @return the current entry value
     */
    public int getValue() {
      return value;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return name();
    }
  }

  /**
   * The class for the WeightedControlList entries.
   */
  public static final class Entry {
    private IControl control;
    private FoundType foundType;
    private int coverage;
    private int distance;
    private int start;

    /**
     * @return the encapsulated control
     */
    public IControl getControl() {
      return control;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      final StringBuilder tmpResult = new StringBuilder(50);
      tmpResult.append(control.getDescribingText());
      tmpResult.append(" found by: ");
      tmpResult.append(foundType.toString());
      tmpResult.append(" coverage: ");
      tmpResult.append(Integer.toString(coverage));
      tmpResult.append(" distance: ");
      tmpResult.append(Integer.toString(distance));
      tmpResult.append(" start: ");
      tmpResult.append(Integer.toString(start));
      return tmpResult.toString();
    }
  }

  /**
   * The comparator used to sort WeightedControlList entries.
   */
  private static final class EntryComperator implements Comparator<Entry>, Serializable {

    private static final long serialVersionUID = 8655421244982375767L;

    /**
     * {@inheritDoc}
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(final Entry anEntry1, final Entry anEntry2) {
      final int tmpWeightComp = anEntry1.foundType.getValue() - anEntry2.foundType.getValue();

      if (0 == tmpWeightComp) {
        final int tmpCoverageComp = anEntry1.coverage - anEntry2.coverage;

        if (0 == tmpCoverageComp) {
          final int tmpDistanceComp = anEntry1.distance - anEntry2.distance;

          if (0 == tmpDistanceComp) {
            final int tmpStartComp = anEntry1.start - anEntry2.start;

            if (0 == tmpStartComp) {
              return anEntry1.control.getDescribingText().compareTo(anEntry2.control.getDescribingText());
            }

            return tmpStartComp;
          }

          return tmpDistanceComp;
        }

        return tmpCoverageComp;
      }

      return tmpWeightComp;
    }
  }

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
   * @param aCoverage the coverage
   * @param aDistance the distance
   * @param aStart the start
   */
  public void add(final IControl aControl, final FoundType aFoundType, final int aCoverage, final int aDistance,
      final int aStart) {
    final Entry tmpEntry = new Entry();
    tmpEntry.control = aControl;
    tmpEntry.foundType = aFoundType;
    tmpEntry.coverage = aCoverage;
    tmpEntry.distance = aDistance;
    tmpEntry.start = aStart;

    entries.add(tmpEntry);
  }

  /**
   * Returns a new list of Entries sorted by weight.
   * 
   * @return a new list
   */
  public List<Entry> getEntriesSorted() {
    Collections.sort(entries, new EntryComperator());

    final List<Entry> tmpResult = new LinkedList<Entry>();
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
   * Adds all entries from anOtherWeightedControlList to this list.
   * 
   * @param anOtherWeightedControlList the list of entries to add
   */
  public void addAll(final WeightedControlList anOtherWeightedControlList) {
    entries.addAll(anOtherWeightedControlList.entries);
  }

  /**
   * Returns true, if the list is empty.
   * 
   * @return true or false
   */
  public boolean isEmpty() {
    return entries.isEmpty();
  }
}