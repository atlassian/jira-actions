#!/bin/bash
set -ev
./gradlew release -Prelease.customUsername=${github_pushback_personal_token}
./gradlew publish
