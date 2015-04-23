HOW TO SETUP THE IDE
====================
Currently we use
    Eclipse 4.4.2 (Luna)
    Eclipse Checkstyle Plugin 6.4.0 (based on Checkstyle 6.4.1)
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