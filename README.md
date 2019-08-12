[![Build Status](https://travis-ci.com/atlassian/jira-actions.svg?branch=master)](https://travis-ci.com/atlassian/jira-actions)

# Core Jira user actions
Define user experience with action metrics.
Reuse Jira Core user action types.

## Jira Core actions

Action              | Activity              |
------------------- | ----------------------
Create Issue        | Go to Dashboard → Wait for Dashboard to load → Open Issue Create Dialog → Show all fields → Fill in project, issue type, summary, description → Detect and fill in required fields → Submit → Wait for Submit to finish
Create Issue Submit | While in Create Issue Action: Press Submit → Wait for Submit to finish
Search with JQL     | Go to Issue Navigator with JQL in URL → Wait for results to load
View Issue          | Go to View Issue Page for an issue → Wait for Summary to be visible
Project Summary     | Go to Project Summary for a project → Wait for metadata column to be visible
View Dashboard      | Go to Dashboard → Wait for Dashboard to load
Edit Issue          | Go to Edit Issue form → Wait for it to load → Clear summary and enter new value → Clear description and enter new value → Check and if necessary fill required fields → Press Submit → Wait for the summary on view issue page to show
Edit Issue Submit   | While in Edit Issue Action: Press Submit → Wait for the summary on view issue page to show
Add Comment         | Go to Add Comment for an issue form → Wait for form to load → Enter comment text → Press Submit → Wait for the summary on view issue page to show
Add Comment Submit  | While in Add Comment Action: Press Submit → Wait for the summary on view issue page to show
Browse Projects     | Go to Browse Projects Page → Wait for the project list to load

## Reporting issues

We track all the changes in a [public issue tracker](https://ecosystem.atlassian.net/secure/RapidBoard.jspa?rapidView=457&projectKey=JPERF).
All the suggestions and bug reports are welcome.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License
Copyright (c) 2018 Atlassian and others.
Apache 2.0 licensed, see [LICENSE.txt](LICENSE.txt) file.
