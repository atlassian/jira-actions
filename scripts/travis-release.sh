#!/bin/bash
set -ev
git status
./gradlew release -Prelease.customUsername=${github_pushback_personal_token}
