#!/bin/bash
set -ev
git status
git branch ${TRAVIS_BRANCH} -u origin/${TRAVIS_BRANCH}
git status
./gradlew release -Prelease.customUsername=${github_pushback_personal_token}
