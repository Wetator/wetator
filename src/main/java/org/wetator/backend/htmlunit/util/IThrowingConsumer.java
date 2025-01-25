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


package org.wetator.backend.htmlunit.util;

import java.util.function.Consumer;

/**
 * Util interface to support throwing from {@link Consumer}.
 *
 * @param <T> the generic type
 * @author rbri
 */
@FunctionalInterface
public interface IThrowingConsumer<T> extends Consumer<T> {

  @Override
  default void accept(final T anElement) {
    try {
      acceptThrows(anElement);
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }

  void acceptThrows(T anElement) throws Exception;
}