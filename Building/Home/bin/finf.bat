@echo off
setlocal
java -p "%~dp0..\lib" -m freecrumbs.finf/freecrumbs.finf.main.Main %*
