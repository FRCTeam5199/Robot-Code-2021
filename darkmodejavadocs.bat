@echo off

dir /s/b stylesheet.css>myfile.dat

for /F "tokens=*" %%A in (myfile.dat) do if not "%%A"=="%cd%\stylesheet.css" copy /Y "stylesheet.css" "%%A" >NUL

if ERRORLEVEL 1 (echo fail) else (echo Success)
pause>NUL