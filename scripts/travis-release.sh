#!/bin/bash
set -ev
git checkout ${TRAVIS_BRANCH}
./gradlew release -Prelease.customUsername=${github_pushback_personal_token}
./gradlew publish
