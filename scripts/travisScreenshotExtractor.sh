#!/bin/bash

if [ -x /usr/bin/xclip ] ; then
    pasteCommand='xclip -selection clipboard -o'
else
    pasteCommand=pbpaste
fi

$pasteCommand | grep 'AAAAA' | sed 's/^ *//' >screenshots.base64

IFS="
"

count=0
for line in $(cat screenshots.base64); do
    echo $line | base64 -d >screenshot${count}.png
    count=$((++count))
done

rm screenshots.base64
