#!/bin/bash
set -ev
git tag pushback2
git remote set-url origin https://${github_pushback_personal_token}@github.com/${TRAVIS_REPO_SLUG}
git push origin pushback2
