/*
 * Copyright (c) 2008-2017 wetator.org
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


package org.wetator.ant;

import java.io.File;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

/**
 * The AntTask to execute test within an ant script.
 *
 * @author rbri
 * @author frank.danek
 */
public class Wetator extends Task {
  private String config;
  private Path classpath;
  private FileSet fileset;
  private List<Property> properties = new ArrayList<Property>();
  private List<Environment.Variable> sysproperties = new ArrayList<Environment.Variable>();
  private boolean haltOnFailure;
  private String failureProperty;

  /**
   * The main method called by Ant.
   */
  @Override
  public void execute() {
    try {
      // check the input

      // config is required
      if (null == getConfig()) {
        throw new BuildException(Version.getProductName() + " Ant: Config-File is required (set property config).");
      }

      if (null == getFileset()) {
        throw new BuildException(
            Version.getProductName() + " Ant: Fileset is required (define a fileset for all your test files).");
      }

      if (classpath == null) {
        log("Classpath not defined", Project.MSG_INFO);

        throw new RuntimeException("Classpath not defined");
      }

      log("Classpath:", Project.MSG_INFO);
      final String[] tmpPaths = classpath.list();
      for (int i = 0; i < tmpPaths.length; i++) {
        log("    '" + tmpPaths[i] + "'", Project.MSG_INFO);
      }

      // AntClassLoader
      // We are using the system classloader, because the loader is only needed
      // for the 'Exec Java' command.<br>
      // And the 'Exec Java' command needs nothing from ant; normally the ant stuff only disturbs.
      final AntClassLoader tmpAntClassLoader = new AntClassLoader(ClassLoader.getSystemClassLoader(), getProject(),
          classpath, false);
      try {
        tmpAntClassLoader.setThreadContextLoader();

        // process sysproperties
        for (final Environment.Variable tmpVar : sysproperties) {
          final String tmpKey = tmpVar.getKey();
          if (tmpKey != null && !tmpKey.isEmpty()) {
            System.setProperty(tmpKey, tmpVar.getValue());
          }
        }

        // add all files
        final DirectoryScanner tmpDirScanner = getFileset().getDirectoryScanner(getProject());
        final String[] tmpListOfFiles = tmpDirScanner.getIncludedFiles();

        // do the
        final Class<?> tmpExecutorClass = tmpAntClassLoader.loadClass("org.wetator.ant.WetatorExecutor");
        final Constructor<?> tmpConstructor = tmpExecutorClass.getConstructor(File.class, String.class, File.class,
            String[].class, Map.class, Writer.class);
        final Object tmpExecutor = tmpConstructor.newInstance(getProject().getBaseDir(), getConfig(),
            tmpDirScanner.getBasedir(), tmpListOfFiles, getPropertiesFromAnt(), new AntWriter(this));
        final Method tmpRunMethod = tmpExecutorClass.getDeclaredMethod("runWetator");

        final long[] tmpResult = (long[]) tmpRunMethod.invoke(tmpExecutor);

        // failures
        if (tmpResult[3] > 0) {
          if (null != getFailureProperty()) {
            getProject().setNewProperty(getFailureProperty(), "true");
          }

          if (isHaltOnFailure()) {
            throw new BuildException(
                Version.getProductName() + ": AntTask failed. (" + tmpResult[3] + " TestRun errors)");
          }
        } else if (tmpResult[0] + tmpResult[1] > 0) {
          if (null != getFailureProperty()) {
            getProject().setNewProperty(getFailureProperty(), "true");
          }

          if (isHaltOnFailure()) {
            throw new BuildException(Version.getProductName() + ": AntTask failed. (" + tmpResult[1] + " failures "
                + tmpResult[0] + " errors)");
          }
        }
      } finally {
        tmpAntClassLoader.resetThreadContextLoader();
        tmpAntClassLoader.cleanup();
      }
    } catch (final InvocationTargetException e) {
      // use e.toString() instead of e.getMessage() because e.g. the message for
      // ClassNotFoundException is not understandable when calling only getMessage
      final String tmpMessage = e.getTargetException().toString();
      throw new BuildException(Version.getProductName() + ": AntTask failed. (" + tmpMessage + ")", e);
    } catch (final Throwable e) {
      // use e.toString() instead of e.getMessage() because e.g. the message for
      // ClassNotFoundException is not understandable when calling only getMessage
      final String tmpMessage = e.toString();
      throw new BuildException(Version.getProductName() + ": AntTask failed. (" + tmpMessage + ")", e);
    }
  }

  /**
   * @return current config
   */
  protected String getConfig() {
    return config;
  }

  /**
   * @param aConfig the new config
   */
  public void setConfig(final String aConfig) {
    config = aConfig;
  }

  /**
   * @return current fileset
   */
  protected FileSet getFileset() {
    return fileset;
  }

  /**
   * Creates a new file set and stores it in attribute fileset.
   *
   * @return the new file set
   */
  public FileSet createFileSet() {
    fileset = new FileSet();
    return fileset;
  }

  /**
   * Lazy initialization for attribute classpath.
   *
   * @return the attribute classpath
   */
  public Path createClasspath() {
    if (null == classpath) {
      classpath = new Path(getProject());
    }
    return classpath;
  }

  /**
   * Adds a property to the list of known properties.
   *
   * @param aProperty the new property
   */
  public void addProperty(final Property aProperty) {
    properties.add(aProperty);
  }

  /**
   * Reads and returns the properties form ant project and from wetator task.
   *
   * @return a map with properties
   */
  protected Map<String, String> getPropertiesFromAnt() {
    final Map<String, String> tmpOurProperties = new HashMap<String, String>();

    // read the properties from project
    final Map<String, Object> tmpProjectProperties = getProject().getProperties();
    for (final Entry<String, Object> tmpEntry : tmpProjectProperties.entrySet()) {
      tmpOurProperties.put(tmpEntry.getKey(), tmpEntry.getValue() == null ? null : String.valueOf(tmpEntry.getValue()));
    }

    // read the properties from property sets
    for (final Property tmpProperty : properties) {
      tmpOurProperties.put(tmpProperty.getName(), tmpProperty.getValue());
    }

    return tmpOurProperties;
  }

  /**
   * Adds a system property.
   *
   * @param anEnvironmentVariable the new property
   */
  public void addSysproperty(final Environment.Variable anEnvironmentVariable) {
    sysproperties.add(anEnvironmentVariable);
  }

  /**
   * @return the haltOnFailure
   */
  public boolean isHaltOnFailure() {
    return haltOnFailure;
  }

  /**
   * @param aHaltOnFailure the haltOnFailure to set
   */
  public void setHaltOnFailure(final boolean aHaltOnFailure) {
    haltOnFailure = aHaltOnFailure;
  }

  /**
   * @return the failureProperty
   */
  public String getFailureProperty() {
    return failureProperty;
  }

  /**
   * @param aFailureProperty the failureProperty to set
   */
  public void setFailureProperty(final String aFailureProperty) {
    failureProperty = aFailureProperty;
  }
}
