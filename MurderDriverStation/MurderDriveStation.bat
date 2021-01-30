@echo off

tasklist /fi "imagename eq DriverStation.exe"

taskkill /fi "imagename eq DriverStation.exe" /T /F

pause