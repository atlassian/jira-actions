#!/bin/bash
set -ev
git status
git checkout ${TRAVIS_BRANCH}
git status
./gradlew release -Prelease.customUsername=${github_pushback_personal_token}
