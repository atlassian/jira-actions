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
[Unreleased]: https://github.com/atlassian/jira-actions/compare/release-3.28.2...master

## [3.23.2] - 2025-05-20
[3.23.2]: https://github.com/atlassian/jira-actions/compare/release-3.28.1...release-3.28.2

This version is not published to the https://mvnrepository.com/, but is available in the Atlassian's maven repositories:
https://developer.atlassian.com/server/framework/atlassian-sdk/atlassian-maven-repositories-2818705/

### Fixed
- Fix for `Create Issue` action to support both legacy and new create issue dialog's headers.

## [3.28.1] - 2024-06-18
[3.28.1]: https://github.com/atlassian/jira-actions/compare/release-3.28.0...release-3.28.1

### Fixed
- Avoid premature navigation when clicking the administration cog.

## [3.28.0] - 2024-06-17
[3.28.0]: https://github.com/atlassian/jira-actions/compare/release-3.27.0...release-3.28.0

### Added
- Split `SetUpAction` into `DisableRichTextEditor` and `HideHealthNotifications`.

### Deprecated
- Deprecate `SetUpAction` and `SET_UP`. It's too vague to interpret and track performance changes.
  Use specific UXes instead, like `DisableRichTextEditor` or `HideHealthNotifications`.

### Fixed
- Stop disabling Rich Text Editor by default in `Scenario`. RTE is supported since [3.5.0].

## [3.27.0] - 2023-01-12
[3.27.0]: https://github.com/atlassian/jira-actions/compare/release-3.26.0...release-3.27.0

### Added
- Add `RecordedPerformanceEntries.timeOrigin`. Help with [JPERF-1454].

[JPERF-1454]: https://ecosystem.atlassian.net/browse/JPERF-1454

## [3.26.0] - 2023-11-30
[3.26.0]: https://github.com/atlassian/jira-actions/compare/release-3.25.0...release-3.26.0

### Deprecated
- Deprecate `ActionMetric.toBackendTimeSlots`. It belongs to a module, which can:
  - formalize the dependency on `Server-Timing`s convention (e.g. `response-thread-plugin` public behavioral API)
  - encapsulate a specific `ActionMetric.drilldown` analysis (e.g. navigations are not the only backend interactions)

### Fixed
- Reduce overhead of `JavascriptW3cPerformanceTimeline`.

## [3.25.0] - 2023-11-09
[3.25.0]: https://github.com/atlassian/jira-actions/compare/release-3.24.0...release-3.25.0

### Added
- Add `ActionMetric.toBackendTimeSlots()`. Aid with [JPERF-1409].

### Fixed
- Speed up all JSON code, by reusing a `JsonProvider` instance. Fix the same root cause as in [JPERF-1401].

[JPERF-1401]: https://ecosystem.atlassian.net/browse/JPERF-1401

## [3.24.0] - 2023-10-24
[3.24.0]: https://github.com/atlassian/jira-actions/compare/release-3.23.1...release-3.24.0

### Added
- Add `PerformanceServerTiming`. Aid with [JPERF-1408].

[JPERF-1408]: https://ecosystem.atlassian.net/browse/JPERF-1408

## [3.23.1] - 2023-10-05
[3.23.1]: https://github.com/atlassian/jira-actions/compare/release-3.23.0...release-3.23.1

### Fixed
- Confess that we depend on `javax.json` in our API. Unblock [JPERF-1395].

## [3.23.0] - 2023-10-04
[3.23.0]: https://github.com/atlassian/jira-actions/compare/release-3.22.0...release-3.23.0

### Added
- Expose filtering for all JQLs currently created by `AdaptiveJqlMemory`. Unblock [JPERF-1395].

[JPERF-1395]: https://ecosystem.atlassian.net/browse/JPERF-1395

## [3.22.0] - 2023-09-14
[3.22.0]: https://github.com/atlassian/jira-actions/compare/release-3.21.2...release-3.22.0

### Added
- Expose adminPassword so it's available for `Scenario` [JPERF-126].

### Fixed
- Stop expecting a single iframe on `DashboardPage`. Fix [JPERF-149].

[JPERF-149]: https://ecosystem.atlassian.net/browse/JPERF-149
[JPERF-126]: https://ecosystem.atlassian.net/browse/JPERF-126

## [3.21.2] - 2023-08-03
[3.21.2]: https://github.com/atlassian/jira-actions/compare/release-3.21.1...release-3.21.2

### Fixed
- Search for unresolved issues via JQL. Seed the issue key memory on Jiras without resolved issues. Fix [JPERF-573].

[JPERF-573]: https://ecosystem.atlassian.net/browse/JPERF-573

## [3.21.1] - 2023-05-17
[3.21.1]: https://github.com/atlassian/jira-actions/compare/release-3.21.0...release-3.21.1

### Fixed
- Check if Create Issue button is clickable before starting the action. Fix [JPERF-1107].

[JPERF-1107]: https://ecosystem.atlassian.net/browse/JPERF-1107

## [3.21.0] - 2023-05-10
[3.21.0]: https://github.com/atlassian/jira-actions/compare/release-3.20.4...release-3.21.0

### Added
- Dismiss pin comment discovery dialog in `ViewIssueAction`. Fix [JPERF-1102].

[JPERF-1102]: https://ecosystem.atlassian.net/browse/JPERF-1102

## [3.20.4] - 2023-05-09
[3.20.4]: https://github.com/atlassian/jira-actions/compare/release-3.20.3...release-3.20.4

### Fixed
- Fix listing issue keys on list view. Fix [JPERF-1098].
- Counteract seed bias.
- Switch to Activity Tabs despite sticky comments footer (introduced in Jira 9.8.0). Fix [JPERF-1091].

[JPERF-1098]: https://ecosystem.atlassian.net/browse/JPERF-1098
[JPERF-1091]: https://ecosystem.atlassian.net/browse/JPERF-1091

## [3.20.3] - 2023-04-26
[3.20.3]: https://github.com/atlassian/jira-actions/compare/release-3.20.2...release-3.20.3

### Fixed
- Fix gathering priorities for JQL queries on Jira 9.9.0. Fix [JPERF-1088].

[JPERF-1088]: https://ecosystem.atlassian.net/browse/JPERF-1088

## [3.20.2] - 2023-04-04
[3.20.2]: https://github.com/atlassian/jira-actions/compare/release-3.20.1...release-3.20.2

### Fixed
- Close flags before using administration menu. Fix [JPERF-1060].

[JPERF-1060]: https://ecosystem.atlassian.net/browse/JPERF-1060

## [3.20.1] - 2023-04-03
[3.20.1]: https://github.com/atlassian/jira-actions/compare/release-3.20.0...release-3.20.1

### Fixed
- Change the default timeout for `WebDriver.wait` to 10 seconds. Fix [JPERF-1026].
- Apply `WebDriver.wait` consistently.

[JPERF-1026]: https://ecosystem.atlassian.net/browse/JPERF-1026

## [3.20.0] - 2023-03-31
[3.20.0]: https://github.com/atlassian/jira-actions/compare/release-3.19.0...release-3.20.0

### Added
- Add `AdaptiveJqlMemory.Companion` JQL filtering extension functions.
  With them, you can actually replace the deprecated `SearchJql*Action`s.

### Fixed
- Change the condition used to verify if the content is loading in `ViewHistoryTabAction` to avoid TimeoutException. Fix [JPERF-1052].

[JPERF-1052]: https://ecosystem.atlassian.net/browse/JPERF-1052

## [3.19.0] - 2023-03-30
[3.19.0]: https://github.com/atlassian/jira-actions/compare/release-3.18.1...release-3.19.0

### Added
- Add `SearchIssues` action, which supports various `IssueNavResultsView`s. Fix [JPERF-1043].

### Deprecated
- Deprecate most methods of `IssueNavigatorPage`,
  because they implicitly assumed `DetailView`, which won't work on `ListView`.
- Deprecate all copies of `SearchJqlAction` (in favour of `SearchIssues`):
  - `SearchJqlAction` itself
  - `SearchJqlChangelogAction`
  - `SearchJqlSimpleAction`
  - `SearchJqlWildcardAction`

### Fixed
- Disable instance health notifications in `SetUpAction`. Fix [JPERF-1050].
- Randomly pick between `ListView` and `DetailView` in `JiraCoreScenario`.
- Measure `IssueNavResultsView` switching.

[JPERF-1050]: https://ecosystem.atlassian.net/browse/JPERF-1050
[JPERF-1043]: https://ecosystem.atlassian.net/browse/JPERF-1043

## [3.18.1] - 2023-02-02
[3.18.1]: https://github.com/atlassian/jira-actions/compare/release-3.18.0...release-3.18.1

### Fixed
- Tolerate dirty form warnings in `BackupConfiguration`. Fix [JPERF-967].

[JPERF-967]: https://ecosystem.atlassian.net/browse/JPERF-967

## [3.18.0] - 2022-12-15
[3.18.0]: https://github.com/atlassian/jira-actions/compare/release-3.17.3...release-3.18.0

### Changed
- Remove `internal` modifier from `NotificationPopUps`. Unblock [JPERF-903]

## [3.17.3] - 2022-10-19
[3.17.3]: https://github.com/atlassian/jira-actions/compare/release-3.17.2...release-3.17.3

### Added
- Introduce `JqlRememberingProjectMemory` so that we have 2nd source of new JQL queries other than using `ViewIssueAction`, which already relies on `JqlMemory`.
- Add `ProjectJqlFactory` and `SimpleProjectJqlFactory` primarily as components of the new `JqlRememberingProjectMemory`.
- Introduce `LimitedJqlMemory` to allow for limiting number of queries remembered from specific source.

### Fixed
- Allow `JiraCoreScenario` to learn about project specific JQL, so that it will find out about issues even if none of them is matching JQLs present in memory at the start of the scenario. Fix [JPERF-816].
- Correct issue comment link reading to work with Jira versions prior to 8.17.0. Fix [JPERF-814].
- Correct issue active tab selector to work with Jira 7.2.0. Fix [JPERF-819].
- Add wait for dashboard to load after disabling RTE during setup. Reduce the flakiness of `SearchJqlAction` executed directly after setup. Fix [JPERF-818].

[JPERF-816]: https://ecosystem.atlassian.net/browse/JPERF-816
[JPERF-814]: https://ecosystem.atlassian.net/browse/JPERF-814
[JPERF-819]: https://ecosystem.atlassian.net/browse/JPERF-819
[JPERF-818]: https://ecosystem.atlassian.net/browse/JPERF-818

## [3.17.2] - 2022-06-23
[3.17.2]: https://github.com/atlassian/jira-actions/compare/release-3.17.1...release-3.17.2

Empty release to test changes in release process.

## [3.17.1] - 2022-06-22
[3.17.1]: https://github.com/atlassian/jira-actions/compare/release-3.17.0...release-3.17.1

### Fixed
- Show all history entries in Jira 9.x
- Use proper locator when exploring comments in Jira 9
- Increase the waiting time when looking for presence of DOM elements in `ViewHistoryTabAction` and `ViewCommentAction`

## [3.17.0] - 2022-05-20
[3.17.0]: https://github.com/atlassian/jira-actions/compare/release-3.16.3...release-3.17.0

### Added
- Add new action View History Tab
- Add new action View Comment
- Add new page objects for Comment Tab Panel and History Tab Panel
- Add `ViewIssueAction.Builder`
- Extend `ViewIssueAction` with comment remembering capability
- Add `CommentMemory`
- Add new method to `WebJira` that allows visiting a comment

## [3.16.3] - 2022-04-08
[3.16.3]: https://github.com/atlassian/jira-actions/compare/release-3.16.2...release-3.16.3

### Fixed
- Bump log4j dependency to 2.17.2. Fix [JPERF-775].

[JPERF-775]: https://ecosystem.atlassian.net/browse/JPERF-775

## [3.16.2] - 2021-10-26
[3.16.2]: https://github.com/atlassian/jira-actions/compare/release-3.16.1...release-3.16.2

### Fixed
- Bump commons-codec, so that the module is compatible with recent versions of aws-resources (1.7.x). Fix [JPERF-730].

[JPERF-730]: https://ecosystem.atlassian.net/browse/JPERF-730

## [3.16.1] - 2021-08-02
[3.16.1]: https://github.com/atlassian/jira-actions/compare/release-3.16.0...release-3.16.1

### Fixed
- Make Create Issue action compatible with recent Jira versions. 

## [3.16.0] - 2021-07-06
[3.16.0]: https://github.com/atlassian/jira-actions/compare/release-3.15.1...release-3.16.0

### Added
- Add the possibility of element rendering time measurement with 
  [Element Timing API](https://wicg.github.io/element-timing/). 
  Resolve [JPERF-747].
  
[JPERF-747]: https://ecosystem.atlassian.net/browse/JPERF-747

## [3.15.1] - 2021-05-28
[3.15.1]: https://github.com/atlassian/jira-actions/compare/release-3.15.0...release-3.15.1

### Fixed
- Improve error logging in HardenedKeyboard.

## [3.15.0] - 2021-04-26
[3.15.0]: https://github.com/atlassian/jira-actions/compare/release-3.14.0...release-3.15.0

### Added
- Open IssueNavigatorPage.getIssueKeys() method. Resolve [JPERF-743].

[JPERF-743]: https://ecosystem.atlassian.net/browse/JPERF-743

## [3.14.0] - 2021-04-22
[3.14.0]: https://github.com/atlassian/jira-actions/compare/release-3.13.4...release-3.14.0

### Added
- Open IssueNavigatorPage class for extension. Resolve [JPERF-742].

[JPERF-742]: https://ecosystem.atlassian.net/browse/JPERF-742

## [3.13.4] - 2020-11-05
[3.13.4]: https://github.com/atlassian/jira-actions/compare/release-3.13.3...release-3.13.4

### Fixed
- Make Create Issue action compatible with Jira 8.14 changes. Fix [JPERF-681].

[JPERF-681]: https://ecosystem.atlassian.net/browse/JPERF-681

## [3.13.3] - 2020-08-06
[3.13.3]: https://github.com/atlassian/jira-actions/compare/release-3.13.2...release-3.13.3

### Fixed
- Make Create Issue action compatible with AUI 9. Fix [JPERF-657].

[JPERF-657]: https://ecosystem.atlassian.net/browse/JPERF-657

## [3.13.2] - 2020-07-22
[3.13.2]: https://github.com/atlassian/jira-actions/compare/release-3.13.1...release-3.13.2

### Fixed
- Fix LogIn Action not being able to close flags on AUI 9. Fix [JPERF-654].

[JPERF-654]: https://ecosystem.atlassian.net/browse/JPERF-654 

## [3.13.1] - 2020-06-29
[3.13.1]: https://github.com/atlassian/jira-actions/compare/release-3.13.0...release-3.13.1

### Fixed
- Make JQL search actions gracefully handle empty JQL queries memory condition. Fix [JPERF-652].

[JPERF-652]: https://ecosystem.atlassian.net/browse/JPERF-652

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
