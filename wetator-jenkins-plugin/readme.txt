HOW TO SETUP THE IDE
====================
Currently we use
    Eclipse 4.8.0 (Photon)
    Eclipse Checkstyle Plugin 8.10.0 (based on Checkstyle 8.10)
    Maven 3.5.2
with optionally
	Eclipse m2e Plugin 1.9.0
	m2e connector for build-helper-maven-plugin	0.15.0
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