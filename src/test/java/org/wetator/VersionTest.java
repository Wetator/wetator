/*
 * Copyright (c) 2008-2024 wetator.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.wetator;

import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.junit.Test;

/**
 * Test cases for class Version.
 *
 * @author rbri
 */
public class VersionTest {

  @Test
  public void versionPattern() {
    matches("wetator.jar");
    matches("wetator-0.11.jar");
    matches("wetator-0.1.1.jar");
    matches("wetator-1.7.3.jar");

    matches("wetator-0.11-snapshot.jar");
    matches("wetator-0.1.1-snapshot.jar");
    matches("wetator-1.7.3-snapshot.jar");

    matches("wetator-3.0.0-snapshot-all.jar");
    matches("wetator-3.0.0-all.jar");

    matches(
        "jar:file:/c:/rbri/git_repo/wetator/target/test-release/app/lib/wetator-3.0.0-snapshot-all.jar!/meta-inf/manifest.mf");

    org.junit.Assert.assertFalse("wetator-ant.jar".matches(Version.WETATOR_JAR_PATTERN));
    org.junit.Assert.assertFalse("wetator-ext.jar".matches(Version.WETATOR_JAR_PATTERN));
  }

  private void matches(final String aJarName) {
    final Pattern tmpPattern = Pattern.compile(Version.WETATOR_JAR_PATTERN);
    final Matcher tmpMatcher = tmpPattern.matcher(aJarName);
    org.junit.Assert.assertTrue(tmpMatcher.find());
  }

  @Test
  public void htmlunitVersion() throws Exception {
    final MavenXpp3Reader tmpReader = new MavenXpp3Reader();
    final Model tmpModel = tmpReader.read(new FileReader("pom.xml"));
    final String tmpHtmlunitVersion = tmpModel.getProperties().get("htmlunit.version").toString();

    org.junit.Assert.assertEquals(tmpHtmlunitVersion, Version.HTMLUNIT_VERSION);
  }
}
