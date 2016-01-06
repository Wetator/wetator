/*
 * Copyright (c) 2008-2016 wetator.org
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


package org.wetator.jenkins.util;

import org.wetator.jenkins.result.StepError;
import org.wetator.jenkins.result.StepError.CauseType;
import org.wetator.jenkins.result.TestError;
import org.wetator.jenkins.result.TestError.ErrorType;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * @author frank.danek
 */
public class ErrorConverter implements Converter {

  private final Mapper mapper;
  private final Converter defaultConverter;
  private final ReflectionProvider reflectionProvider;

  /**
   * The constructor.
   *
   * @param aMapper the mapper
   * @param aDefaultConverter the default converter for the type
   * @param aReflectionProvider the reflection provider
   */
  public ErrorConverter(Mapper aMapper, Converter aDefaultConverter, ReflectionProvider aReflectionProvider) {
    mapper = aMapper;
    defaultConverter = aDefaultConverter;
    reflectionProvider = aReflectionProvider;
  }

  @Override
  public boolean canConvert(@SuppressWarnings("rawtypes") Class aType) {
    return TestError.class.isAssignableFrom(aType);
  }

  @Override
  public void marshal(Object aSource, HierarchicalStreamWriter aWriter, MarshallingContext aContext) {
    if (TestError.class.equals(aSource.getClass())) {
      aWriter.addAttribute(mapper.aliasForSystemAttribute("class"), mapper.serializedClass(aSource.getClass()));
    }

    defaultConverter.marshal(aSource, aWriter, aContext);
  }

  @Override
  public Object unmarshal(HierarchicalStreamReader aReader, UnmarshallingContext aContext) {
    String tmpClassName = aReader.getAttribute(mapper.aliasForSystemAttribute("class"));
    Class<?> tmpClass = StepError.class;
    if (tmpClassName != null) {
      tmpClass = mapper.realClass(tmpClassName);
    }
    Object tmpResult = reflectionProvider.newInstance(tmpClass);
    Object tmpObject = aContext.convertAnother(tmpResult, tmpClass, defaultConverter);
    if (tmpObject instanceof StepError) {
      StepError tmpStepError = (StepError) tmpObject;
      if (tmpStepError.getType() == null) {
        tmpStepError.setType(ErrorType.STEP);
      }
      if (tmpStepError.getCauseType() == null) {
        tmpStepError.setCauseType(CauseType.ERROR);
      }
    } else {
      TestError tmpTestError = (TestError) tmpObject;
      if (tmpTestError.getType() == null) {
        tmpTestError.setType(ErrorType.TEST);
      }
    }
    return tmpObject;
  }
}
