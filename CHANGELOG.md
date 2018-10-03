# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## Compatibility
The library offers compatibility contracts on the Java API and the POM.

### Java API
The API covers all public Java types from `com.atlassian.performance.tools.jiraactions.api` and its subpackages:

  * [source compatibility]
  * [binary compatibility]
  * [behavioral compatibility] with behavioral contracts expressed via Javadoc

[source compatibility]: http://cr.openjdk.java.net/~darcy/OpenJdkDevGuide/OpenJdkDevelopersGuide.v0.777.html#source_compatibility
[binary compatibility]: http://cr.openjdk.java.net/~darcy/OpenJdkDevGuide/OpenJdkDevelopersGuide.v0.777.html#binary_compatibility
[behavioral compatibility]: http://cr.openjdk.java.net/~darcy/OpenJdkDevGuide/OpenJdkDevelopersGuide.v0.777.html#behavioral_compatibility

### POM
Changing the license is breaking a contract.
Adding a requirement of a major version of a dependency is breaking a contract.
Dropping a requirement of a major version of a dependency is a new contract.

## [Unreleased]
[Unreleased]: https://bitbucket.org/atlassian/jira-actions/branches/compare/master%0Drelease-2.1.0

### Fixed
- Decrease log level for actions complaining about skipping run, which resolves [JPERF-162].

[JPERF-162]: https://ecosystem.atlassian.net/browse/JPERF-162

## [2.1.0] - 2018-09-04
[2.0.0]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-2.1.0%0Drelease-2.0.0

### Added
- Expose `JiraErrors` page object.

## [2.0.0] - 2018-09-04
[2.0.0]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-2.0.0%0Drelease-1.0.0

### Changed
- Require APT `concurrency:1`.

### Added
- Include the POM in the compatibility contract.
- Gain freedom from APT `concurrency:0`.

## [1.0.0] - 2018-08-31
[1.0.0]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-1.0.0%0Drelease-0.0.1

### Changed
- Define the public API.
- Add this change log.

## [0.0.1] - 2018-08-01
[0.0.1]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-0.0.1%0Dinitial-commit

### Added
- Extract performance reporting from [JPT submodule].
- Add [README.md](README.md).
- Configure Bitbucket Pipelines.

[JPT submodule]: https://stash.atlassian.com/projects/JIRASERVER/repos/jira-performance-tests/browse/actions?at=3dfb21b8b65cc0c1c26ad9aeff58f5d23fdabf5b
