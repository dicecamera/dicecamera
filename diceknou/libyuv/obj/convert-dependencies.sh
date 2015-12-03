#!/bin/sh
# AUTO-GENERATED FILE, DO NOT EDIT!
if [ -f $1.org ]; then
  sed -e 's!^S:/cygwin/lib!/usr/lib!ig;s! S:/cygwin/lib! /usr/lib!ig;s!^S:/cygwin/bin!/usr/bin!ig;s! S:/cygwin/bin! /usr/bin!ig;s!^S:/cygwin/!/!ig;s! S:/cygwin/! /!ig;s!^S:!/cygdrive/s!ig;s! S:! /cygdrive/s!ig;s!^C:!/cygdrive/c!ig;s! C:! /cygdrive/c!ig;' $1.org > $1 && rm -f $1.org
fi
