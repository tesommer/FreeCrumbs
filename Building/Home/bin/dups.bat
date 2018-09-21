@echo off
setlocal
"%~dp0dups.pl" | finf -c - %* | "%~dp0dups.pl" x
