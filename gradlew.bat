@rem Gradle start up script for Windows
@if "%DEBUG%" == "" @echo off
setlocal
set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_HOME=%DIRNAME%

if exist "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" (
    java -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*
) else (
    gradle %*
)
