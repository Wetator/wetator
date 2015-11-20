HOW TO SETUP THE IDE
====================
Currently we use
    Eclipse 4.4.2 (Luna)
    Eclipse Checkstyle Plugin 6.11.1 (based on Checkstyle 6.11.2)
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
  * adjust version info in file build-properties.xml and commit
  * create svn tag
    - create folder tags/version_x_x_xx
    - copy trunk/wetator to the new folder
  * build the release
    - ant clean
    - ant publish-sonatype-releases
  * test the release
    - ant test-release
  * go to https://oss.sonatype.org/index.html#stagingRepositories
      close the repository
      release the repository

WETATOR-ANT
  * force jenkins build and check for success
  * adjust version info in file build-properties.xml and commit
  * verify that ivy.xml points to the correct wetator version
  * create svn tag
    - copy trunk/wetator-ant-task to the new folder
  * build the release
    - ant clean
    - ant publish-sonatype-snapshots
  * replace wetator-snapshot.jar inside wetator-ant-task\deploy\wetator-ant_0_9_12.zip
    with wetator.jar from wetator\build
  * test the release
    - ant test-release

  * update website
    - download page
    - release notes
    - news
