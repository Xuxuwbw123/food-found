@REM Maven Wrapper script for Windows
@REM Usage: mvnw.cmd clean package -DskipTests

@echo off
setlocal

set MAVEN_HOME=%~dp0.mvn\wrapper\apache-maven-3.9.6
if exist "%MAVEN_HOME%\bin\mvn.cmd" (
    "%MAVEN_HOME%\bin\mvn.cmd" %*
    goto end
)

echo Downloading Maven...
powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip' -OutFile '.mvn\wrapper\maven.zip'"
powershell -Command "Expand-Archive -Path '.mvn\wrapper\maven.zip' -DestinationPath '.mvn\wrapper' -Force"
del ".mvn\wrapper\maven.zip"

"%MAVEN_HOME%\bin\mvn.cmd" %*

:end
