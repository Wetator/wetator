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


package org.wetator.commandset;

/**
 * Small class that offers some methods to be executed via 'Exec Java' command.
 * 
 * @author rbri
 */
@SuppressWarnings("unused")
public final class ExecTester {

  public static void staticVoidMethodWithoutParams() {
    // nothing to do
  }

  public static void staticVoidMethodWithOneParam(String aParam1) {
    // nothing to do
  }

  public static void staticVoidMethodWithTwoParams(String aParam1, String aParam2) {
    // nothing to do
  }

  public static void staticVoidMethodWithStringArray(String[] aParam) {
    // nothing to do
  }

  public static void staticVoidMethodWithVarargs(String... aParams) {
    // nothing to do
  }

  public static String staticMethodWithoutParams() {
    return "staticMethodWithoutParams";
  }

  public static String staticMethodWithOneParam(String aParam1) {
    return "staticMethodWithOneParam";
  }

  public static String staticMethodWithTwoParams(String aParam1, String aParam2) {
    return "staticMethodWithTwoParams";
  }

  public static String staticMethodWithStringArray(String[] aParam) {
    return "staticMethodWithStringArray";
  }

  public static String staticMethodWithVarargs(String... aParams) {
    return "staticMethodWithVarargs";
  }

  public void voidMethodWithoutParams() {
    // nothing to do
  }

  public void voidMethodWithOneParam(String aParam1) {
    // nothing to do
  }

  public void voidMethodWithTwoParams(String aParam1, String aParam2) {
    // nothing to do
  }

  public void voidMethodWithStringArray(String[] aParam) {
    // nothing to do
  }

  public void voidMethodWithVarargs(String... aParams) {
    // nothing to do
  }

  public String methodWithoutParams() {
    return "staticMethodWithoutParams";
  }

  public String methodWithOneParam(String aParam1) {
    return "staticMethodWithOneParam";
  }

  public String methodWithTwoParams(String aParam1, String aParam2) {
    return "staticMethodWithTwoParams";
  }

  public String methodWithStringArray(String[] aParam) {
    return "staticMethodWithStringArray";
  }

  public String methodWithVarargs(String... aParams) {
    return "staticMethodWithVarargs";
  }

  public void methodThrowingException() throws Exception {
    throw new Exception("methodThrowingException");
  }
}