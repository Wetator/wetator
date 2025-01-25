HOW TO SETUP THE IDE
====================
Currently we use
    Eclipse 2021-03 (4.19.0)
    Eclipse Checkstyle Plugin 8.42.0 (based on Checkstyle 8.42)
as IDE. All other settings (code format, warnings, etc.) are stored with the project.


HOW TO SETUP THE PROJECT
========================
Run
    ant resolve-dependencies
to retrieve all needed libraries (and their sources if available) into the folder /lib (and /lib_src).

!IMPORTANT! NEVER check in the folders /build and /deploy or the content of the folders /lib and /lib_src.


HOW TO BUILD THE PROJECT
========================
Just run
    ant package
and everything will be done and placed into the folder /deploy.


HOW TO ADD LIBRARIES
====================
Just add the needed libraries as new dependencies to the file
    ivy.xml
(see IVY documentation for more details) and run
    ant resolve-dependencies
to retrieve the new libraries into the folder /lib. After that you can just add them to the classpath.


HOW TO RELEASE
==============

WETATOR
  * force jenkins build and check for success
  * build the release
    * Check all your files are checked in
    * Execute these mvn commands to be sure all tests are passing and everything is up to data
        mvn versions:display-plugin-updates
        mvn versions:display-dependency-updates
        mvn -U clean test

    * Update the version number in pom.xml and org.wetator.Version.java
    * Commit the changes

    * Execute the tests including integration tests
        mvn verify
    * Check target/test-release/app/logs/run_report.xsl.html for success

    * Build and deploy the artifacts 
        mvn -up clean deploy

  * go to https://oss.sonatype.org/index.html#stagingRepositories
      close the repository
      release the repository

  * create github tag
    - go to https://github.com/Wetator/wetator/releases
    - create new release


WETATOR-ANT
  * change ivy.xml to point to the latest wetator release
  * force resolve-dependencies and adjust classpath of the eclipse project and commit
  * force jenkins build and check for success
  * adjust version info in file build-properties.xml and commit
  * build the release
    - ant clean
    - ant publish-sonatype-releases
  * test the release
    - ant test-release
  * go to https://oss.sonatype.org/index.html#stagingRepositories
      close the repository
      release the repository
  * create github tag
    - go to https://github.com/Wetator/wetator-ant-task/releases
    - create new release

  * update website
    - download page
    - release notes
    - news
 