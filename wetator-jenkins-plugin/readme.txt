HOW TO SETUP THE IDE
====================
Currently we use
    Eclipse 4.5.1 (Mars)
    Eclipse Checkstyle Plugin 6.11.2 (based on Checkstyle 6.11.2)
    Maven 3.3.9
with optionally
	Eclipse m2e Plugin 1.6.2
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