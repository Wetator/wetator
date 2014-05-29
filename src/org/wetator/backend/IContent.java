/*
 * Copyright (c) 2008-2014 wetator.org
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


/**
 * The interface for all supported content.
 * 
 * @author rbri
 */
public interface IContent {

  /**
   * Enum for the supported content type.
   */
  public enum ContentType {
    /** html. */
    HTML,
    /** css. */
    CSS,
    /** javascript. */
    JAVASCRIPT,
    /** plain text. */
    TEXT,
    /** xml. */
    XML,
    /** pdf. */
    PDF,
    /** excel. */
    XLS,
    /** rtf. */
    RTF,
    /** png. */
    PNG,
    /** gif. */
    GIF,
    /** bmp. */
    BMP,
    /** jpeg. */
    JPEG,
    /** zip. */
    ZIP,
    /** the rest. */
    OTHER
  };
}
