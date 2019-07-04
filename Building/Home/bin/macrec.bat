@echo off
setlocal
java -p "%~dp0..\lib" -m freecrumbs.macrec/freecrumbs.macrec.MacroRecorder %*
