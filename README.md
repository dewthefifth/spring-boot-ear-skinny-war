# Overview: Spring Boot EAR with Skinny WARs

An example project showing the problems deploying Spring Boot applications to JBOSS/Wildfly in an EAR deployment using skinny WARs.

This project consist of several deployment scenarios, being tested against several application servers:


## Tested Servers
- JBOSS EAP 6.4
- JBOSS EAP 6.4 (Modified)
- JBOSS EAP 7.0
- JBOSS EAP 7.1
- Wildfy 8.2.1.Final
- Wildfly 10.0.0.Final
- Wildfly 12.0.0.Final


## Scenarios
### Scenario 1: Single Enterprise Application Archive
A single EAR containing two Spring Boot Web Applications, `web1` and `web2`.

To avoid duplicate dependencies, and reduce the overall size of deliverables, as many of the dependencies as possible are packaged in the EAR/lib and shared between the WARs. Specifically, all but one dependency of `web1` and `web2` are included in the EAR and excluded from the WARs.

### Scenario 2: Dependency Platform (for JBoss/WildFly)
There exists a `platform` EAR which contains common libraries used across EAR/WAR components, which rarely changes. For example, using Spring Boot 2.0, you could include all of the Spring Boot 2.0 managed dependencies in a single EAR. Other EARs and WARs could then depend on the `platform` and exclude those libraries from their own packaging. The result would be the ability to build an entire Spring Boot 2.0 compatible ecosystem of WAR/EAR deployments which are significantly lighter thanks to the exclusion of the `platform` libraries.

To prove this scenario/point, a second EAR named `skinny-ear` has been built containing the same `web1` and `web2` as was used in the previous scenario. This is possible because `web1` and `web2` from the prior scenario already excluded any of the `platform` dependencies by merit of excluding nearly all dependencies. It would be more complicated if these applications already dependencies of their own, but presumably in an actual dev/production environment the web application in question would be built specifically to target the platform.

Finally, a standalone WAR called `skinny-war` and using the context `/skinny-web` is included to demonstrate deployment of a WAR on top of the platform.

### Scenario 3: Fat WAR deployment
A single WAR, identified as `web0`, which should deploy normally on the application server, is included to function as the control case.


# Building Specific Deployments
## JBOSS EAP 6.4
To build artifacts intended for JBOSS EAP 6.4 run ```mvn clean install -P "jboss-eap-6.4"```

## JBOSS EAP 6.4 (Modified)
To build artifacts intended for the modified version of JBOSS EAP 6.4 run ```mvn clean install```

*Note:*<br>
The modified version of JBOSS EAP 6.4 will actually update the subsystems which are causing problems, allowing the exact same artifacts used by the other servers to work on the modified server. For specific information on how to modify the server, see "Modifying JBOSS EAP 6.4" at the bottom of this document.

## JBOSS EAP 7.0
To build artifacts intended for JBOSS EAP 7.0 run ```mvn clean install```

## JBOSS EAP 7.1
To build artifacts intended for JBOSS EAP 7.1 run ```mvn clean install```

## Wildfly 8.2.1.Final
To build artifacts intended for Wildfly 8.2.1.Final run ```mvn clean install```

## Wildfly 10.0.0.Final
To build artifacts intended for Wildfly 10.0.0.Final run ```mvn clean install```

## Wildfly 12.0.0.Final
To build artifacts intended for Wildfly 12.0.0.Final run ```mvn clean install```


# Status
## JBOSS EAP 6.4
### Scenario 1
Broken

JBOSS appears to be loading javax.validation into the classloader as part of the EAR processing, prior to reading the jboss-deployment-structure, and refuses to use the included version. Unfortunately, Spring 5 requires the use of javax.validation version 1.1+ and JBOSS EAP includes javax.validation 1.0. This means that anything short of upgrading the version of javax.validation fails to load a compatible version for Spring.

### Scenario 2
Untested

### Scenario 3
Functional

It's not actually clear why this portion functions without server modifications while the other sections do not. However, it does...

## JBOSS EAP 7.0
### Scenario 1
Functional
### Scenario 2
Functional
### Scenario 3
Functional
### Additional Notes
The skinny-war module will fail to deploy when deploying everything at once during JBOSS startup. However, deploying it after the startup will succeed. It appears to have something to do with the JMX module registration, and can likely be corrected by disabling JMX in the application.properties

## JBOSS EAP 7.1
### Scenario 1
Functional
### Scenario 2
Functional
### Scenario 3
Functional
### Additional Notes
The regular, non-skinny/platform, EAR module will fail to deploy when deploying everything at once during JBOSS startup. However, deploying it after the startup will succeed. It appears to have something to do with the JMX module registration, and can likely be corrected by disabling JMX in the application.properties

## Wildfly 8.2.1.Final
### Scenario 1
Functional
### Scenario 2
Functional
### Scenario 3
Functional

## Wildfly 10.0.0.Final
### Scenario 1
Functional
### Scenario 2
Functional
### Scenario 3
Functional

## Wildfly 12.0.0.Final
### Scenario 1
Functional
### Scenario 2
Functional
### Scenario 3
Functional

# Misc. Resolved Problems

1. Wildfly does not find `SpringServletContainerInitializer` if it is in the EAR `lib` directory. This is 
discussed on https://issues.jboss.org/browse/WFLY-4205 and https://jira.spring.io/browse/SPR-12555. A fix should be in 
9.0.0.Beta1 upwards, but it did not work for me.
So I had to create  the `servlet-container-initializer-meta-inf` module and force it to be included in each `WEB-INF/lib` directory.
2. When `SpringServletContainerInitializer` is called it does not find Spring Boot `WebApplicationInitializer` classes in
`WEB-INF/classes`. When it interrogates the class loader, it only finds classes from the EAR `lib` folder. However, the 
WAR classes are on the class path and are found by Spring once it starts up. As a temporary work-around, I created a common Spring Boot
application class and included it as a common JAR.

# Workarounds

## Spring Boot Legacy
Since the Servlet 3.0 spec make externalizing dependencies out of the war troublesome
we will instead use a method known to allow Spring Boot 1.x run within a Servlet 3.0 container.

Using the master branch of [dyser/spring-boot-legacy](https://github.com/dsyer/spring-boot-legacy) that has been updated to Spring Boot 2.0.3
we show that you can use Spring Boot 2 in an EAR with Skinny WARs in WildFly.

## Modifying JBOSS EAP 6.4
At a very high level, this section is going to walk through including custom versions of ```javax.validation.api```, ```org.hibernate.validator```, and ```com.fasterxml.classmate```. Doing so will effectively move the entire validation subcomponent of the server onto javax.validation.api version 2.0.

*Note:*<br>
All module.xml snippets were taken from either JBOSS EAP 6.4 or Wildfly 8.2.1 and modified to include the new jars and necessary dependencies. Basically, I took the module.xml from the existing server and changed the ```resource-root``` element's ```path``` attribute to be the name of the new jar.

### javax.validation:
- Copy the included ```validation-api-2.0.1.Final.jar``` into ```$JBOSS_HOME/modules/javax/validation/api/main```
- Copy the following snippet into ```$JBOSS_HOME/modules/javax/validation/api/main/module.xml```
```$xml
<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ JBoss, Home of Professional Open Source.
  ~ Copyright 2010, Red Hat, Inc., and individual contributors
  ~ as indicated by the @author tags. See the copyright.txt file in the
  ~ distribution for a full listing of individual contributors.
  ~
  ~ This is free software; you can redistribute it and/or modify it
  ~ under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 2.1 of
  ~ the License, or (at your option) any later version.
  ~
  ~ This software is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~ Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this software; if not, write to the Free
  ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  -->

<module xmlns="urn:jboss:module:1.1" name="javax.validation.api">
    <resources>
        <resource-root path="validation-api-2.0.1.Final.jar"/>
        <!-- Insert resources here -->
    </resources>

  <dependencies>
    <module name="org.jboss.logging"/>
  </dependencies>
</module>
```

### org.hibernate.validator
- Copy the included ```hibernate-validator-6.0.10.Final.jar``` into ```$JBOSS_HOME/modules/org/hibernate/validator/main```
- Copy the following snippet into ```$JBOSS_HOME/modules/org/hibernate/validator/main```
```$xml
<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ JBoss, Home of Professional Open Source.
  ~ Copyright 2010, Red Hat, Inc., and individual contributors
  ~ as indicated by the @author tags. See the copyright.txt file in the
  ~ distribution for a full listing of individual contributors.
  ~
  ~ This is free software; you can redistribute it and/or modify it
  ~ under the terms of the GNU Lesser General Public License as
  ~ published by the Free Software Foundation; either version 2.1 of
  ~ the License, or (at your option) any later version.
  ~
  ~ This software is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  ~ Lesser General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Lesser General Public
  ~ License along with this software; if not, write to the Free
  ~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  ~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  -->

<module xmlns="urn:jboss:module:1.1" name="org.hibernate.validator">
  <resources>
    <resource-root path="hibernate-validator-6.0.10.Final.jar"/>
        <!-- Insert resources here -->
  </resources>

  <dependencies>
    <module name="com.fasterxml.classmate"/>
    <module name="javax.api"/>
    <module name="javax.persistence.api"/>
    <module name="javax.validation.api"/>
    <module name="javax.persistence.api"/>
    <module name="javax.xml.bind.api"/>
    <module name="org.javassist"/>
    <module name="org.jboss.logging"/>
    <module name="org.jboss.common-core"/>
    <module name="org.jboss.weld.core"/>
    <module name="org.joda.time"/>
    <module name="org.slf4j"/>
    <module name="org.apache.xerces" services="import"/>
    <module name="sun.jdk" services="import"/>
  </dependencies>
</module>
```

### com.fasterxml.classmate
- Copy the included ```classmate-1.3.4.jar``` into ```$JBOSS_HOME/modules/com/fasterxml/classmate/main```
- Copy the following snippet into ```$JBOSS_HOME/modules/com/fasterxml/classmate/main/main.xml```
```$xml
<?xml version="1.0" encoding="UTF-8"?>

<!--
~ JBoss, Home of Professional Open Source.
~ Copyright 2013, Red Hat, Inc., and individual contributors
~ as indicated by the @author tags. See the copyright.txt file in the
~ distribution for a full listing of individual contributors.
~
~ This is free software; you can redistribute it and/or modify it
~ under the terms of the GNU Lesser General Public License as
~ published by the Free Software Foundation; either version 2.1 of
~ the License, or (at your option) any later version.
~
~ This software is distributed in the hope that it will be useful,
~ but WITHOUT ANY WARRANTY; without even the implied warranty of
~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
~ Lesser General Public License for more details.
~
~ You should have received a copy of the GNU Lesser General Public
~ License along with this software; if not, write to the Free
~ Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
~ 02110-1301 USA, or see the FSF site: http://www.fsf.org.
-->

<module xmlns="urn:jboss:module:1.3" name="com.fasterxml.classmate">
    <properties>
        <property name="jboss.api" value="private"/>
    </properties>

    <resources>
        <resource-root path="classmate-1.3.4.jar"/>
    </resources>
</module>
```




