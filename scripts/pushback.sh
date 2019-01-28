#!/bin/bash
set -ev
git tag pushback
git push https://${github_pushback_personal_token}@github.com/dagguh/jira-actions.git pushback
