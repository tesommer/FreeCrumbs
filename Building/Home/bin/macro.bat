@echo off
setlocal
java -p "%~dp0..\lib" -m freecrumbs.macro/freecrumbs.macro.main.Main %*
