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


package org.wetator.util;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;

/**
 * VersionUtil contains some useful extensions to read version info
 * from various sources.
 * 
 * @author rbri
 */
public final class VersionUtil {
  private static final Name BUNDLE_NAME = new Name("Bundle-Name");
  private static final Name BUNDLE_VERSION = new Name("Bundle-Version");

  /**
   * Returns the name of the jar file the class is loaded from.
   * 
   * @param aClass a class that is known to be loaded form the jar
   *        in question
   * @return the name of the jar file or "unknown"
   */
  public static String determineVersionFromJarFileName(final Class<?> aClass) {
    String tmpPath = aClass.getProtectionDomain().getCodeSource().getLocation().getPath();
    if (StringUtils.isNotBlank(tmpPath) && tmpPath.lastIndexOf('/') > -1) {
      tmpPath = tmpPath.substring(tmpPath.lastIndexOf('/') + 1);
      return tmpPath;
    }
    return "unknown";
  }

  /**
   * Returns the name of the jar file the class is loaded from.
   * 
   * @param aClass a class that is known to be loaded form the jar
   *        in question
   * @return the name of the jar file or "unknown"
   */
  public static String determineCreationDateFromJarFileName(final Class<?> aClass) {
    final String tmpPath = aClass.getProtectionDomain().getCodeSource().getLocation().getPath();
    String tmpClassFile = aClass.getName();
    tmpClassFile = tmpClassFile.replace('.', '/');
    tmpClassFile = tmpClassFile + ".class";
    try {
      final JarFile tmpJar = new JarFile(tmpPath);
      try {
        final JarEntry tmpJarEntry = tmpJar.getJarEntry(tmpClassFile);
        final Date tmpDate = new Date(tmpJarEntry.getTime());

        return new SimpleDateFormat("yyyy-MM-dd").format(tmpDate);
      } finally {
        tmpJar.close();
      }
    } catch (final Throwable e) {
      // ignore
    }
    return "unknown";
  }

  /**
   * Returns the version of the jar file the class is loaded from.
   * 
   * @param aClass a class that is known to be loaded form the jar
   *        in question
   * @param aPackage the name of the package or null
   * @return the name of the jar file or "unknown"
   */
  public static String determineVersionFromJarManifest(final Class<?> aClass, final String aPackage) {
    return readAttributeFromJarFile(aClass, Attributes.Name.IMPLEMENTATION_VERSION, aPackage);
  }

  /**
   * Returns the title of the jar file the class is loaded from.
   * 
   * @param aClass a class that is known to be loaded form the jar
   *        in question
   * @param aPackage the name of the package or null
   * @return the name of the jar file or "unknown"
   */
  public static String determineTitleFromJarManifest(final Class<?> aClass, final String aPackage) {
    return readAttributeFromJarFile(aClass, Attributes.Name.IMPLEMENTATION_TITLE, aPackage);
  }

  /**
   * Returns the title of the jar file the class is loaded from.
   * 
   * @param aClass a class that is known to be loaded form the jar
   *        in question
   * @param aPackage the name of the package or null
   * @return the name of the jar file or "unknown"
   */
  public static String determineBundleNameFromJarManifest(final Class<?> aClass, final String aPackage) {
    return readAttributeFromJarFile(aClass, BUNDLE_NAME, aPackage);
  }

  /**
   * Returns the version of the jar file the class is loaded from.
   * 
   * @param aClass a class that is known to be loaded form the jar
   *        in question
   * @param aPackage the name of the package or null
   * @return the name of the jar file or "unknown"
   */
  public static String determineBundleVersionFromJarManifest(final Class<?> aClass, final String aPackage) {
    return readAttributeFromJarFile(aClass, BUNDLE_VERSION, aPackage);
  }

  private static String readAttributeFromJarFile(final Class<?> aClass, final Attributes.Name anAttribute,
      final String aPackage) {
    final String tmpPath = aClass.getProtectionDomain().getCodeSource().getLocation().getPath();
    try {
      final JarFile tmpJar = new JarFile(tmpPath);
      try {
        final Manifest tmpManifest = tmpJar.getManifest();

        final Attributes tmpAttributes;
        if (null == aPackage) {
          tmpAttributes = tmpManifest.getMainAttributes();
        } else {
          tmpAttributes = tmpManifest.getAttributes(aPackage);
        }

        final String tmpAttribute = tmpAttributes.getValue(anAttribute);
        if (StringUtils.isNotBlank(tmpAttribute)) {
          return tmpAttribute;
        }
      } finally {
        tmpJar.close();
      }
    } catch (final Throwable e) {
      // ignore
    }
    return "unknown";
  }

  /**
   * Returns the attribute from the manifest of the jar file or the default
   * if not found.
   * 
   * @param aJarFileName the name of the jar file (including '.jar')
   * @param aClass a class that is known to be loaded form the jar
   *        in question
   * @param aPackage the name of the package or null
   * @param anAttributeName the name of the attribute to look for
   * @param aDefault the return value if not found
   * @return the name of the jar file or the given default
   */
  public static String readAttributeFromJarManifest(final String aJarFileName, final Class<?> aClass,
      final String aPackage, final String anAttributeName, final String aDefault) {
    try {
      final Enumeration<URL> tmpResources = aClass.getClassLoader().getResources("META-INF/MANIFEST.MF");
      while (tmpResources.hasMoreElements()) {
        final URL tmpUrl = tmpResources.nextElement();
        if (tmpUrl.toExternalForm().toLowerCase().contains(aJarFileName)) {
          final InputStream tmpStream = tmpUrl.openStream();
          try {
            final Manifest tmpManifest = new Manifest(tmpStream);

            final Attributes tmpAttributes;
            if (null == aPackage) {
              tmpAttributes = tmpManifest.getMainAttributes();
            } else {
              tmpAttributes = tmpManifest.getAttributes(aPackage);
            }

            final String tmpAttribute = tmpAttributes.getValue(anAttributeName);
            if (StringUtils.isNotBlank(tmpAttribute)) {
              return tmpAttribute;
            }
          } finally {
            tmpStream.close();
          }
        }
      }
    } catch (final Throwable e) {
      // fallback to default
    }
    return aDefault;
  }

  /**
   * Private constructor to be invisible.
   */
  private VersionUtil() {
    super();
  }
}
