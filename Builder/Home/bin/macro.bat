@echo off

setlocal

set "CP="
for %%I in ("%~dp0..\lib\*.jar") do call :concat %%I

java -cp "%CP%" freecrumbs.macro.Main %*

goto :eof

:concat
set CP=%CP%;%1
