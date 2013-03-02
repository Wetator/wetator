HOW TO SETUP THE IDE

Currently we use
    Eclipse 3.7.2 (Indigo)
    Eclipse Checkstyle Plugin 5.6.0
as IDE. All other settings (code format, warnings, etc.) are stored with the project.


HOW TO SETUP THE PROJECT

Run
    ant resolve
to retrieve all needed libraries (and their sources if available) into the folder /lib (and /lib_src).

!IMPORTANT! NEVER check in the folders /build and /deploy or the content of the folders /lib and /lib_src.


HOW TO BUILD THE PROJECT

Just run
    ant build-release
and everything will be done and placed into the folder /deploy.


HOW TO ADD LIBRARIES

Just add the needed libraries as new dependencies to the file
    ivy.xml
(see IVY documentation for more details) and run
    ant resolve
to retrieve the new libraries into the folder /lib. After that you can just add them to the classpath.