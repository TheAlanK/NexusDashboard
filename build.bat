@echo off
REM ============================================================
REM  NexusDashboard - Build Script
REM ============================================================

echo === NexusDashboard Build ===
echo.

set STARSECTOR_DIR=E:\Games\Starsector
set MOD_DIR=%~dp0
set SRC_DIR=%MOD_DIR%src
set OUT_DIR=%MOD_DIR%build\classes
set JAR_DIR=%MOD_DIR%jars
set JAR_NAME=NexusDashboard.jar

REM --- Classpath ---
set CP=%STARSECTOR_DIR%\starsector-core\starfarer.api.jar
set CP=%CP%;%STARSECTOR_DIR%\starsector-core\starfarer_obf.jar
set CP=%CP%;%STARSECTOR_DIR%\starsector-core\lwjgl.jar
set CP=%CP%;%STARSECTOR_DIR%\starsector-core\lwjgl_util.jar
set CP=%CP%;%STARSECTOR_DIR%\starsector-core\log4j-1.2.9.jar
set CP=%CP%;%STARSECTOR_DIR%\starsector-core\json.jar
set CP=%CP%;%STARSECTOR_DIR%\starsector-core\xstream-1.4.10.jar
set CP=%CP%;%STARSECTOR_DIR%\mods\LazyLib\jars\LazyLib.jar
REM NexusUI dependency - from the sibling mod during development
set CP=%CP%;%MOD_DIR%..\NexusUI\jars\NexusUI.jar

echo [1/4] Cleaning...
if exist "%OUT_DIR%" rmdir /s /q "%OUT_DIR%"
mkdir "%OUT_DIR%"
if not exist "%JAR_DIR%" mkdir "%JAR_DIR%"

echo [2/4] Finding source files...
dir /s /b "%SRC_DIR%\com\nexusdashboard\*.java" > "%MOD_DIR%build\sources.txt"
for /f %%A in ('type "%MOD_DIR%build\sources.txt" ^| find /c /v ""') do echo     Found %%A source files

echo [3/4] Compiling...
javac -source 8 -target 8 -encoding UTF-8 -Xlint:-options -cp "%CP%" -d "%OUT_DIR%" @"%MOD_DIR%build\sources.txt" 2>&1

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo *** COMPILATION FAILED ***
    pause
    exit /b 1
)

echo     Compilation successful!

echo [4/4] Packaging JAR...
cd /d "%OUT_DIR%"
jar cf "%JAR_DIR%\%JAR_NAME%" -C "%OUT_DIR%" .

echo.
echo === BUILD SUCCESSFUL ===
echo Output: %JAR_DIR%\%JAR_NAME%
echo.

echo Installing to Starsector mods folder...
if not exist "%STARSECTOR_DIR%\mods\NexusDashboard" mkdir "%STARSECTOR_DIR%\mods\NexusDashboard"
xcopy /s /y /q "%MOD_DIR%jars" "%STARSECTOR_DIR%\mods\NexusDashboard\jars\" >nul
xcopy /s /y /q "%MOD_DIR%data" "%STARSECTOR_DIR%\mods\NexusDashboard\data\" >nul
copy /y "%MOD_DIR%mod_info.json" "%STARSECTOR_DIR%\mods\NexusDashboard\" >nul
echo Installed!
pause
