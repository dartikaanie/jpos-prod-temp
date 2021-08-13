# Pull base image.
FROM java:8-jdk

MAINTAINER  Author Name <boys.mtv@gmail.com>

EXPOSE 1001

COPY target/pcs-jpos-template-1.0/bin/ /jpos/bin/
COPY target/pcs-jpos-template-1.0/cfg/ /jpos/cfg/
COPY target/pcs-jpos-template-1.0/cfg/packager/ /jpos/cfg/packager/
COPY target/pcs-jpos-template-1.0/deploy/ /jpos/deploy/
COPY target/pcs-jpos-template-1.0/lib/ /jpos/lib/
COPY target/pcs-jpos-template-1.0/pcs-jpos-template-1.0.jar /jpos/pcs-jpos-template-1.0.jar


WORKDIR /jpos/  

# Define default command.
CMD ["java","-jar","/jpos/pcs-jpos-template-1.0.jar"]