@echo off

setlocal

set "CP="
for %%I in ("%~dp0..\lib\*.jar") do call :concat %%I

java -cp "%CP%" freecrumbs.finf.Main %1 %2 %3 %4 %5 %6 %7 %8 %9

goto :eof

:concat
set CP=%CP%;%1
