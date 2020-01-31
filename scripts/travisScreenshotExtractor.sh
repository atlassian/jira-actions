#!/bin/sh

pbpaste | grep '=$' | base64 -d >screenshot.png
