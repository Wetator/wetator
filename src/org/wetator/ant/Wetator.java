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


package org.wetator.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.wetator.Version;
import org.wetator.core.WetConfiguration;
import org.wetator.core.WetEngine;

/**
 * The AntTask to execute test within an ant script.
 * 
 * @author rbri
 */
public class Wetator extends Task {
  private String config;
  private Path classpath;
  private FileSet fileset;
  private List<Property> properties = new ArrayList<Property>();
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
        throw new BuildException(Version.getProductName()
            + " Ant: Fileset is required (define a fileset for all your test files).");
      }

      final WetEngine tmpWetEngine = new WetEngine();
      if (classpath != null) {
        log("Classpath:", Project.MSG_INFO);
        final String[] tmpPaths = classpath.list();
        for (int i = 0; i < tmpPaths.length; i++) {
          log("    '" + tmpPaths[i] + "'", Project.MSG_INFO);
        }

        // AntClassLoader
        // We are using the system classloader, because the loader is only needed
        // for the 'Exec Java' command.<br>
        // And the 'Exec Java' command needs nothing from ant; normally the ant stuff only disturbs.
        final AntClassLoader tmpClassLoader = new AntClassLoader(ClassLoader.getSystemClassLoader(), getProject(),
            classpath, false);
        tmpClassLoader.setThreadContextLoader();
      }

      // configuration is relative to the base dir of the project
      final File tmpConfigFile = new File(getProject().getBaseDir(), getConfig());
      tmpWetEngine.setConfigFileName(tmpConfigFile.getAbsolutePath());

      final Map<String, String> tmpOurProperties = getPropertiesFromAnt();
      tmpWetEngine.setExternalProperties(tmpOurProperties);
      final AntOutProgressListener tmpListener = new AntOutProgressListener(this);
      tmpWetEngine.addProgressListener(tmpListener);
      tmpWetEngine.init();

      // add all files
      final DirectoryScanner tmpDirScanner = getFileset().getDirectoryScanner(getProject());
      final String[] tmpListOfFiles = tmpDirScanner.getIncludedFiles();

      for (int i = 0; i < tmpListOfFiles.length; i++) {
        tmpWetEngine.addTestFile(new File(tmpDirScanner.getBasedir(), tmpListOfFiles[i]));
      }

      tmpWetEngine.executeTests();

      // failures
      if (tmpListener.getFailureCount() + tmpListener.getErrorCount() > 0) {
        if (null != getFailureProperty()) {
          getProject().setNewProperty(getFailureProperty(), "true");
        }

        if (isHaltOnFailure()) {
          throw new BuildException(Version.getProductName() + ": AntTask failed. (" + tmpListener.getFailureCount()
              + " failures " + tmpListener.getErrorCount() + " errors)");
        }
      }

    } catch (final Throwable e) {
      throw new BuildException(Version.getProductName() + ": AntTask failed. (" + e.getMessage() + ")", e);
    }
  }

  /**
   * Reads and returns the properties form ant project and from wetator task
   * 
   * @return a map with properties
   */
  @SuppressWarnings("unchecked")
  protected Map<String, String> getPropertiesFromAnt() {
    // read the properties from project
    final Map<String, String> tmpProjectProperties = getProject().getProperties();
    final Map<String, String> tmpOurProperties = new HashMap<String, String>();
    final Set<String> tmpKeys = tmpProjectProperties.keySet();
    for (String tmpKey : tmpKeys) {
      if (tmpKey.startsWith(WetConfiguration.VARIABLE_PREFIX + WetConfiguration.SECRET_PREFIX)) {
        tmpOurProperties.put(tmpKey, tmpProjectProperties.get(tmpKey));
        log("set property '" + tmpKey + "' to '****' (from project)", Project.MSG_INFO);
      } else if (tmpKey.startsWith(WetConfiguration.PROPERTY_PREFIX)
          || tmpKey.startsWith(WetConfiguration.VARIABLE_PREFIX)) {
        tmpOurProperties.put(tmpKey, tmpProjectProperties.get(tmpKey));
        log("set property '" + tmpKey + "' to '" + tmpProjectProperties.get(tmpKey) + "' (from project)",
            Project.MSG_INFO);
      }
    }

    // read the properties from property sets
    for (Property tmpProperty : properties) {
      final String tmpName = tmpProperty.getName();
      if (tmpName.startsWith(WetConfiguration.VARIABLE_PREFIX + WetConfiguration.SECRET_PREFIX)) {
        log("set property '" + tmpName + "' to '****'", Project.MSG_INFO);
        tmpOurProperties.put(tmpName, tmpProperty.getValue());
      } else if (tmpName.startsWith(WetConfiguration.PROPERTY_PREFIX)
          || tmpName.startsWith(WetConfiguration.VARIABLE_PREFIX)) {
        log("set property '" + tmpName + "' to '" + tmpProperty.getValue() + "'", Project.MSG_INFO);
        tmpOurProperties.put(tmpName, tmpProperty.getValue());
      }
    }

    return tmpOurProperties;
  }

  /**
   * Getter for attribute config
   * 
   * @return current config
   */
  protected String getConfig() {
    return config;
  }

  /**
   * Setter for attribute config
   * 
   * @param aConfig the new config
   */
  public void setConfig(final String aConfig) {
    config = aConfig;
  }

  /**
   * Getter for attribute fileset
   * 
   * @return current fileset
   */
  protected FileSet getFileset() {
    return fileset;
  }

  /**
   * Creates a new file set and stores it in attribute fileset
   * 
   * @return the new file set
   */
  public FileSet createFileSet() {
    fileset = new FileSet();
    return fileset;
  }

  /**
   * Lazy initialization for attribute classpath
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
   * Adds a property to the list of known properties
   * 
   * @param aProperty the new proptery
   */
  public void addProperty(final Property aProperty) {
    properties.add(aProperty);
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
