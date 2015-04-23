HOW TO SETUP THE IDE
====================
Currently we use
    Eclipse 4.4.2 (Luna)
    Eclipse Checkstyle Plugin 6.4.0 (based on Checkstyle 6.4.1)
    Maven 3.3.1
with optionally
	Eclipse m2e Plugin 1.6.0
	m2e connector for build-helper-maven-plugin	0.15
as IDE. All other settings (code format, warnings, etc.) are stored with the project.


HOW TO SETUP THE PROJECT
========================
Run
    mvn clean install
to retrieve all needed libraries (and their sources if available).


HOW TO RELEASE THE PROJECT
==========================
Just run
    mvn release:clean release:prepare release:perform
and everything will be done and placed into the folder /deploy.