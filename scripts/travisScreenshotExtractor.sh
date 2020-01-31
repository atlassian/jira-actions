#!/bin/sh

pbpaste | grep 'AAAAA' | base64 -d >screenshot.png
