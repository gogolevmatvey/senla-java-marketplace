pushd "%~dp0\marketplace-api"
call mvn clean package

echo Set full path to Tomcat webapps:
set /p T_WEBAPPS_PATH=
copy /Y "target\marketplace-api.war" "%T_WEBAPPS_PATH%"
net stop Tomcat11
net start Tomcat11
pause
