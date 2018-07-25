## JPT Actions

This module defines:
  * a JPT measurement unit - user action
  * Jira Core and Jira Software action type instances
  
### Version management
SCM is an ultimate source of truth about project version and you will not find it hardcoded in the source code.
To get the current version of the project from Git execute `./gradlew currentVersion`.

### Releasing
This project uses [Bitbucket pipelines](bitbucket-pipelines.yml) to run the tests and release the project.
Simply execute the *Release project* manual stage on *master* branch or corresponding *Release snapshot* stage 
if you wish to release a new *SNAPSHOT* version from your branch.
                 

#### Releasing manually
To release a new version of the project execute `./gradlew release`.
You should release new version only from *master* branch.
To publish a new version execute `./gradlew publish`. The project is configured to publish its versions to Atlassian 
*maven-private* repository and *SNAPSHOT* versions are published to Atlassian *maven-private-snapshot* repositories instead.

#### Marking new version
If you wish to mark a new *major* / *minor* version simply execute ` ./gradlew markNextVersion -Prelease.version=**version**`. 
For example to start a new version `0.2.0` execute ` ./gradlew markNextVersion -Prelease.version=0.2.0`.

For more information please refer to [axion-release-plugin docs](http://axion-release-plugin.readthedocs.io/en/latest/index.html) 
and [Maven Publish Plugin docs](https://docs.gradle.org/current/userguide/publishing_maven.html)