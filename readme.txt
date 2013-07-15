HOW TO SETUP THE PROJECT

You need to download the following jar-files and place them into the folder /ant_lib:
    ivy-2.1.0.jar (http://ant.apache.org/ivy/download.cgi)
    one-jar-ant-task-0.96.jar (http://one-jar.sourceforge.net/index.php?page=downloads&file=downloads)
    version_tool.jar (http://sourceforge.net/projects/antversiontool/)

Run
    ant resolve
to retrieve all needed libraries (and their sources if available) into the folder /lib (and /lib_src).

!IMPORTANT! NEVER check in the folders /build and /deploy or the content of the folders /ant_lib, /lib and /lib_src.


HOW TO BUILD THE PROJECT

Just run
    ant build-release
and everything will be done and placed into the folder /deploy.


HOW TO ADD LIBRARIES

ToDo