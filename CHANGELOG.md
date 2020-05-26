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
[Unreleased]: https://github.com/atlassian/jira-actions/compare/release-3.13.0...master

## [3.13.0] - 2020-05-26
[3.13.0]: https://github.com/atlassian/jira-actions/compare/release-3.12.0...release-3.13.0

### Added
- Add top navigation bar handler to API - `com.atlassian.performance.tools.jiraactions.api.page.TopNav`

### Fixed
- Make issue creation time no longer include the dashboard loading time

## [3.12.0] - 2020-04-15
[3.12.0]: https://github.com/atlassian/jira-actions/compare/release-3.11.0...release-3.12.0

### Added
- Allow overrides of output in `ActionMeter.Builder`.

## [3.11.0] - 2020-04-14
[3.11.0]: https://github.com/atlassian/jira-actions/compare/release-3.10.3...release-3.11.0

### Added
- `ActionMeter.Builder`.
- Add `DrillDownHook`.
- Add a post metric hook to `ActionMeter`.

## [3.10.3] - 2020-03-03
[3.10.3]: https://github.com/atlassian/jira-actions/compare/release-3.10.2...release-3.10.3

### Fixed
- Move Selenium-JS to API scope [JPERF-607].

## [3.10.2] - 2020-02-11
[3.10.2]: https://github.com/atlassian/jira-actions/compare/release-3.10.1...release-3.10.2

### Fixed
- Avoid using `Epic` issue type, it causes problems with some datasets.

## [3.10.1] - 2020-02-07
[3.10.1]: https://github.com/atlassian/jira-actions/compare/release-3.10.0...release-3.10.1

### Fixed
- Properly fill issue data in `CreateIssueAction`. Fix [JPERF-298].
- Upgrade Selenium to `3.141.59`.
- Remove `text ~ "a*"` from `AdaptiveJqlMemory`. Fix [JPERF-604].
- Harden sending key presses.

[JPERF-298]: https://ecosystem.atlassian.net/browse/JPERF-298
[JPERF-604]: https://ecosystem.atlassian.net/browse/JPERF-604

## [3.10.0] - 2019-09-27
[3.10.0]: https://github.com/atlassian/jira-actions/compare/release-3.9.0...release-3.10.0

### Added
- Parametrize `ActionMetricsParser` with `MetricJsonFormat`.
- Add `MetricCompactJsonFormat` to avoid drilldown parsing. Work around [JPERF-395].

[JPERF-395]: https://ecosystem.atlassian.net/browse/JPERF-395

## [3.9.0] - 2019-08-13
[3.9.0]: https://github.com/atlassian/jira-actions/compare/release-3.8.0...release-3.9.0

### Added
- Add ability to delete backup service. Resolve [JPERF-334].

[JPERF-344]: https://ecosystem.atlassian.net/browse/JPERF-344

## [3.8.0] - 2019-08-12
[3.8.0]: https://github.com/atlassian/jira-actions/compare/release-3.7.0...release-3.8.0

### Added
- Add `IssueCreateDialog.showAllFields`.
- Show all issue fields in `CreateIssueAction`.

### Fixed
- Avoid "please select a value" options in `Select`.

## [3.7.0] - 2019-07-25
[3.7.0]: https://github.com/atlassian/jira-actions/compare/release-3.6.0...release-3.7.0

### Added
- JqlMemory modified to allow recall queries by tag/query type. Resolve [JPERF-522].
- Add new action Search JQL wildcard. Resolve [JPERF-525].
- Add new action Search JQL by previous reporter. Resolve [JPERF-528].
- Add new action Search JQL simple search. Resolve [JPERF-526].

[JPERF-522]: https://ecosystem.atlassian.net/browse/JPERF-522
[JPERF-525]: https://ecosystem.atlassian.net/browse/JPERF-525
[JPERF-528]: https://ecosystem.atlassian.net/browse/JPERF-528
[JPERF-526]: https://ecosystem.atlassian.net/browse/JPERF-526

## [3.6.0] - 2019-06-06
[3.6.0]: https://github.com/atlassian/jira-actions/compare/release-3.5.1...release-3.6.0

### Added
- Expose `WebJira.accessAdmin()`. Resolve [JPERF-207].

[JPERF-207]: https://ecosystem.atlassian.net/browse/JPERF-207

## [3.5.1] - 2019-05-22
[3.5.1]: https://github.com/atlassian/jira-actions/compare/release-3.5.0...release-3.5.1

### Fixed
- Observe issue key and id in ViewIssueAction. Resolves [JPERF-482].
- Drill down into errored action metrics. Fix [JPERF-479].

[JPERF-482]: https://ecosystem.atlassian.net/browse/JPERF-482
[JPERF-479]: https://ecosystem.atlassian.net/browse/JPERF-479

## [3.5.0] - 2019-04-26
[3.5.0]: https://github.com/atlassian/jira-actions/compare/release-3.4.0...release-3.5.0

### Added
- Expose `IssueForm` to make custom issue form actions easier. Resolve [JPERF-450].

### Fixed
- Support RTE with a fallback to plain-text editor. Resolve [JPERF-184].

[JPERF-184]: https://ecosystem.atlassian.net/browse/JPERF-184
[JPERF-450]: https://ecosystem.atlassian.net/browse/JPERF-450

## [3.4.0] - 2019-03-28
[3.4.0]: https://github.com/atlassian/jira-actions/compare/release-3.3.0...release-3.4.0

### Added
- Stream metrics in `MergingActionMetricsParser` and `ActionMetricsParser`. Unblock [JPERF-395].

### Deprecated
- Discourage memory leaks caused by `MergingActionMetricsParser.parse` and `ActionMetricsParser.parse`.

[JPERF-395]: https://ecosystem.atlassian.net/browse/JPERF-395

## [3.3.0] - 2019-01-29
[3.3.0]: https://github.com/atlassian/jira-actions/compare/release-3.2.0...release-3.3.0

### Added
- Provide Jira Core scenario. Resolves [JPERF-373].

### Fixed
- `Search with JQL` action handles JQL which returns no results. Resolves [JPERF-371].

[JPERF-373]: https://ecosystem.atlassian.net/browse/JPERF-373
[JPERF-371]: https://ecosystem.atlassian.net/browse/JPERF-371

## [3.2.0] - 2019-01-08
[3.2.0]: https://github.com/atlassian/jira-actions/compare/release-3.1.0...release-3.2.0

### Added
- Provide builder for ActionMetric. Resolves [JPERF-340]

[JPERF-340]: https://ecosystem.atlassian.net/browse/JPERF-340

## [3.1.0] - 2018-12-20
[3.1.0]: https://github.com/atlassian/jira-actions/compare/release-3.0.2...release-3.1.0

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
[3.0.2]: https://github.com/atlassian/jira-actions/compare/release-3.0.1...release-3.0.2

### Fixed
- Stop assuming description field is always available. Resolves [JPERF-303].

[JPERF-303]: https://ecosystem.atlassian.net/browse/JPERF-303

## [3.0.1] - 2018-12-06
[3.0.1]: https://github.com/atlassian/jira-actions/compare/release-3.0.0...release-3.0.1

### Fixed
- Wait for web elements while disabling RTE. Resolves [JPERF-296].

[JPERF-296]: https://ecosystem.atlassian.net/browse/JPERF-296

## [3.0.0] - 2018-11-13
[3.0.0]: https://github.com/atlassian/jira-actions/compare/release-2.3.0...release-3.0.0

### Added
- Customize Jira login and setup. Resolves [JPERF-127] and [JPERF-150].

### Removed
- Named parameters in Scenario are no longer supported.

## [2.3.0] - 2018-11-13
[2.3.0]: https://github.com/atlassian/jira-actions/compare/release-2.2.0...release-2.3.0

### Fixed
- Restore `com.atlassian.performance.tools.jiraactions.api.scenario.Scenario` source compatibility with `2.1.0`
 by reverting [JPERF-127] and [JPERF-150].

## [2.2.0] - 2018-11-08
[2.2.0]: https://github.com/atlassian/jira-actions/compare/release-2.1.2...release-2.2.0

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
[2.1.2]: https://github.com/atlassian/jira-actions/compare/release-2.1.1...release-2.1.2

### Fixed
- Do not fail 'Browse Projects' for low number of projects. Fix [JPERF-151].

[JPERF-151]: https://ecosystem.atlassian.net/browse/JPERF-151

## [2.1.1] - 2018-10-09
[2.1.1]: https://github.com/atlassian/jira-actions/compare/release-2.1.0...release-2.1.1

### Fixed
- Decrease log level for actions complaining about skipping run, which resolves [JPERF-162].
- Ensure Rich Text Editor toggle is disabled after clicking it during Jira setup [JPERF-183].
- Fix navigation to the issue edit form and comment form when using a Jira with a context path. Fix [JPERF-193].

[JPERF-162]: https://ecosystem.atlassian.net/browse/JPERF-162
[JPERF-183]: https://ecosystem.atlassian.net/browse/JPERF-183
[JPERF-193]: https://ecosystem.atlassian.net/browse/JPERF-193

## [2.1.0] - 2018-09-04
[2.1.0]: https://github.com/atlassian/jira-actions/compare/release-2.0.0...release-2.1.0

### Added
- Expose `JiraErrors` page object.

## [2.0.0] - 2018-09-04
[2.0.0]: https://github.com/atlassian/jira-actions/compare/release-1.0.0...release-2.0.0

### Changed
- Require APT `concurrency:1`.

### Added
- Include the POM in the compatibility contract.
- Gain freedom from APT `concurrency:0`.

## [1.0.0] - 2018-08-31
[1.0.0]: https://github.com/atlassian/jira-actions/compare/release-0.0.1...release-1.0.0

### Changed
- Define the public API.
- Add this change log.

## [0.0.1] - 2018-08-01
[0.0.1]: https://github.com/atlassian/jira-actions/compare/initial-commit...release-0.0.1

### Added
- Extract performance reporting from [JPT submodule].
- Add [README.md](README.md).
- Configure Bitbucket Pipelines.

[JPT submodule]: https://stash.atlassian.com/projects/JIRASERVER/repos/jira-performance-tests/browse/actions?at=3dfb21b8b65cc0c1c26ad9aeff58f5d23fdabf5b
