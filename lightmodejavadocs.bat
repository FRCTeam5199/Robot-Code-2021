@echo off

dir /s/b stylesheet.css>myfile.dat

for /F "tokens=*" %%A in (myfile.dat) do if not "%%A"=="%cd%\lightmodestylesheet.css" copy /Y "lightmodestylesheet.css" "%%A" >NUL

dir /s/b index.html>myfile.dat

for /F "tokens=*" %%A in (myfile.dat) do "%%A"

del myfile.dat

if ERRORLEVEL 1 (echo Fail. You get what you deserve) else (echo Success. Your are burning)
pause>NUL