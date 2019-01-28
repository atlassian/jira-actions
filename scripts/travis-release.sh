#!/bin/bash
set -ev
git remote set-url origin https://${github_pushback_personal_token}@github.com/${TRAVIS_REPO_SLUG}
./gradlew release
