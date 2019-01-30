# Contributing

Thanks for taking the time to contribute! 

The following is a set of guidelines for contributing to [jira-actions](README.md).
All the changes are welcome. Please help us to improve code, examples and documentation.

## Testing 

    ./gradlew test
    
## Developer’s environment

You can build and run virtual users on MacOS, Windows or Linux. You'll need JDK 8-11 to build and test the project.

## Submitting changes
 
Pull requests, issues and comments are welcome. For pull requests:

  - Create your own [fork] of the repository and raise a pull request targeting master branch in the main repository
  - Enable Bitbucket pipelines, it is important that you do it via *Settings* menu and not *Pipelines* menu otherwise 
    Bitbucket will create an empty commit polluting Git history of your fork
  - Add tests for new features and bug fixes
  - Follow the existing style
  - Separate unrelated changes into multiple pull requests

[fork]: https://confluence.atlassian.com/bitbucket/forking-a-repository-221449527.html
  
See the [existing issues](https://ecosystem.atlassian.net/projects/JPERF/issues/?filter=allissues) for things to start contributing.

For bigger changes, make sure you start a discussion first by creating
an issue and explaining the intended change.

All the pull requests and other changes will be accepted and merged by Atlassians.

Atlassian requires contributors to sign a Contributor License Agreement,
known as a CLA. This serves as a record stating that the contributor is
entitled to contribute the code/documentation/translation to the project
and is willing to have it used in distributions and derivative works
(or is willing to transfer ownership).

Prior to accepting your contributions we ask that you please follow the appropriate
link below to digitally sign the CLA. The Corporate CLA is for those who are
contributing as a member of an organization and the individual CLA is for
those contributing as an individual.

* [CLA for corporate contributors](https://na2.docusign.net/Member/PowerFormSigning.aspx?PowerFormId=e1c17c66-ca4d-4aab-a953-2c231af4a20b)
* [CLA for individuals](https://na2.docusign.net/Member/PowerFormSigning.aspx?PowerFormId=3f94fbdc-2fbe-46ac-b14c-5d152700ae5d)

## Style Guide / Coding conventions

[Git commit messages](https://chris.beams.io/posts/git-commit/)

## Releasing

Versioning, releasing and distribution are managed by the [gradle-release] plugin.

[gradle-release]: https://bitbucket.org/atlassian/gradle-release/src/release-0.5.0/README.md