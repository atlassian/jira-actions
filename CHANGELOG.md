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
[Unreleased]: https://bitbucket.org/atlassian/jira-actions/branches/compare/master%0Drelease-3.1.0

## [3.1.0] - 2018-12-20
[3.1.0]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-3.1.0%0Drelease-3.0.2

### Added
- Add `drilldown` property to `ActionMetric`. See [JPERF-316].
- Record standard W3C performance entries via `W3cPerformanceTimeline`. Provide raw data for [JPERF-316].
- Let `ActionMeter`s copy themselves with a different `W3cPerformanceTimeline`.

### Deprecated
- Deprecate constructors of `ActionMetric`.
- Deprecate the data class status of `ActionMetric`.
- Deprecate serialization details of `ActionMetric`.
- Deprecate the internal `Recording` data structure.

[JPERF-316]: https://ecosystem.atlassian.net/browse/JPERF-316

## [3.0.2] - 2018-12-18
[3.0.2]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-3.0.2%0Drelease-3.0.1

### Fixed
- Stop assuming description field is always available. Resolves [JPERF-303].

[JPERF-303]: https://ecosystem.atlassian.net/browse/JPERF-303

## [3.0.1] - 2018-12-06
[3.0.1]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-3.0.1%0Drelease-3.0.0

### Fixed
- Wait for web elements while disabling RTE. Resolves [JPERF-296].

[JPERF-296]: https://ecosystem.atlassian.net/browse/JPERF-296

## [3.0.0] - 2018-11-13
[3.0.0]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-3.0.0%0Drelease-2.3.0

### Added
- Customize Jira login and setup. Resolves [JPERF-127] and [JPERF-150].

### Removed
- Named parameters in Scenario are no longer supported.

## [2.3.0] - 2018-11-13
[2.3.0]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-2.3.0%0Drelease-2.2.0

### Fixed
- Restore `com.atlassian.performance.tools.jiraactions.api.scenario.Scenario` source compatibility with `2.1.0`
 by reverting [JPERF-127] and [JPERF-150].

## [2.2.0] - 2018-11-08
[2.2.0]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-2.2.0%0Drelease-2.1.2

### INCOMPATIBILITY BUG
Breaks source compatibility for `com.atlassian.performance.tools.jiraactions.api.scenario.Scenario`. See [JPERF-260].
Roll back to `2.1.2` to restore this compatibility.

[JPERF-260]: https://ecosystem.atlassian.net/browse/JPERF-260

### Added
- Customize Jira login and setup. Resolves [JPERF-127] and [JPERF-150].

WARNING, this new API is unstable. It cannot be shipped without breaking API.
Therefore it is removed in `2.3.0` as per SemVer spec. The feature is reintroduced in `3.0.0`.

[JPERF-127]: https://ecosystem.atlassian.net/browse/JPERF-127
[JPERF-150]: https://ecosystem.atlassian.net/browse/JPERF-150

## [2.1.2] - 2018-10-22
[2.1.2]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-2.1.2%0Drelease-2.1.1

### Fixed
- Do not fail 'Browse Projects' for low number of projects. Fix [JPERF-151].

[JPERF-151]: https://ecosystem.atlassian.net/browse/JPERF-151

## [2.1.1] - 2018-10-09
[2.1.1]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-2.1.1%0Drelease-2.1.0

### Fixed
- Decrease log level for actions complaining about skipping run, which resolves [JPERF-162].
- Ensure Rich Text Editor toggle is disabled after clicking it during Jira setup [JPERF-183].
- Fix navigation to the issue edit form and comment form when using a Jira with a context path. Fix [JPERF-193].

[JPERF-162]: https://ecosystem.atlassian.net/browse/JPERF-162
[JPERF-183]: https://ecosystem.atlassian.net/browse/JPERF-183
[JPERF-193]: https://ecosystem.atlassian.net/browse/JPERF-193

## [2.1.0] - 2018-09-04
[2.1.0]: https://bitbucket.org/atlassian/jira-actions/branches/compare/release-2.1.0%0Drelease-2.0.0

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
