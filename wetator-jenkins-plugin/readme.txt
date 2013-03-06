HOW TO SETUP THE IDE

Currently we use
    Eclipse 3.7.2 (Indigo)
    Eclipse Checkstyle Plugin 5.6.0
    Maven 3.0.4
with optionally
	Eclipse m2e Plugin 1.0
	m2e connector for build-helper-maven-plugin	0.14
as IDE. All other settings (code format, warnings, etc.) are stored with the project.


HOW TO SETUP THE PROJECT

Run
    mvn clean install
to retrieve all needed libraries (and their sources if available).


HOW TO RELEASE THE PROJECT

Just run
    mvn release:clean release:prepare release:perform
and everything will be done and placed into the folder /deploy.