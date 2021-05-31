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
  * adjust version info in file build-properties.xml and commit
  * create svn tag
    - create folder tags/version_x_x_xx
    - copy trunk/wetator to the new folder
  * build the release
    - provide your credential as ant call parameters -Dsonatype.username=???? -Dsonatype.passwd=???? -Dpgp.passphrase=???? -v
    - ant clean
    - ant publish-sonatype-releases
  * test the release
    - ant test-release
  * go to https://oss.sonatype.org/index.html#stagingRepositories
      close the repository
      release the repository

WETATOR-ANT
  * change ivy.xml to point to the latest wetator release
  * force resolve-dependencies and adjust classpath of the eclipse project and commit
  * force jenkins build and check for success
  * adjust version info in file build-properties.xml and commit
  * create svn tag
    - copy trunk/wetator-ant-task to the new folder
  * build the release
    - ant clean
    - ant publish-sonatype-releases
  * test the release
    - ant test-release
  * go to https://oss.sonatype.org/index.html#stagingRepositories
      close the repository
      release the repository

  * update website
    - download page
    - release notes
    - news
