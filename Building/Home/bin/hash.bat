@echo off
setlocal
java -p "%~dp0..\lib" -m freecrumbs.hash/freecrumbs.hash.Hash %*
