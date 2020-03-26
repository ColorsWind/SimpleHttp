#!/bin/sh
java -server -Xmx128M -Xms128M -XX:+AggressiveOpts -XX:+UseCompressedOops -Dfile.encoding=utf8 -jar SimpleHttp-maven-version.jar