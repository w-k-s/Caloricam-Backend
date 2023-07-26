FROM tomcat:7-jre8

COPY target/CalorieApp-1.0-SNAPSHOT.war $CATALINA_HOME/webapps/
ENTRYPOINT ["/bin/bash", "/usr/local/tomcat/bin/catalina.sh", "run"]