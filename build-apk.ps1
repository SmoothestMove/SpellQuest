# Build Debug APK Script for SpellQuest
Write-Host "Building SpellQuest Debug APK..." -ForegroundColor Cyan

$SdkPath = "C:/Users/justi/AppData/Local/Android/Sdk"
$LocalProps = Join-Path $PSScriptRoot "local.properties"
$DebugKeystore = Join-Path $PSScriptRoot "debug.keystore"
$DefaultKeystore = Join-Path $env:USERPROFILE ".android\debug.keystore"

# Ensure local.properties exists
if (-not (Test-Path $LocalProps)) {
    Write-Host "Creating local.properties..." -ForegroundColor Yellow
    "sdk.dir=$SdkPath" | Out-File -FilePath $LocalProps -Encoding utf8
}

# Ensure debug.keystore exists
if (-not (Test-Path $DebugKeystore)) {
    if (Test-Path $DefaultKeystore) {
        Write-Host "Copying debug.keystore from user profile..." -ForegroundColor Yellow
        Copy-Item $DefaultKeystore $DebugKeystore
    } else {
        Write-Host "debug.keystore not found in user profile." -ForegroundColor Yellow
    }
}

# Run Gradle assembleDebug
gradle assembleDebug

if ($LASTEXITCODE -eq 0) {
    $ApkPath = Join-Path $PSScriptRoot "app\build\outputs\apk\debug\app-debug.apk"
    Write-Host "`n==========================================" -ForegroundColor Green
    Write-Host "APK BUILD SUCCESSFUL!" -ForegroundColor Green
    Write-Host "Location: $ApkPath" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
} else {
    Write-Host "`n==========================================" -ForegroundColor Red
    Write-Host "APK BUILD FAILED with code $LASTEXITCODE" -ForegroundColor Red
    Write-Host "==========================================" -ForegroundColor Red
    exit $LASTEXITCODE
}
