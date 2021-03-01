@echo off

dir /s/b stylesheet.css>myfile.dat

for /F "tokens=*" %%A in (myfile.dat) do if not "%%A"=="%cd%\darkmodestylesheet.css" copy /Y "darkmodestylesheet.css" "%%A" >NUL

dir /s/b index.html>myfile.dat

for /F "tokens=*" %%A in (myfile.dat) do "%%A" 

del myfile.dat

if ERRORLEVEL 1 (echo Fail. Your eyes must burn) else (echo Success. Your eyes are saved)
pause>NUL