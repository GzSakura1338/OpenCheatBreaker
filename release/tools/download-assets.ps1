param(
    [string]$Target = 'assets',
    [int]$Limit = 0
)

$ErrorActionPreference = 'Stop'
$ProgressPreference = 'SilentlyContinue'
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$indexName = '1.8'
$indexSha1 = 'f6ad102bcaa53b1a58358f16e376d548d44933ec'
$indexUrls = @(
    "https://launchermeta.mojang.com/v1/packages/$indexSha1/$indexName.json",
    "https://bmclapi2.bangbang93.com/v1/packages/$indexSha1/$indexName.json"
)
$objectBases = @('https://resources.download.minecraft.net', 'https://bmclapi2.bangbang93.com/assets')

$proxy = @{}
if ($env:HTTPS_PROXY) {
    $proxy.Proxy = $env:HTTPS_PROXY
}

function Get-Sha1([string]$path) {
    return (Get-FileHash -Algorithm SHA1 -LiteralPath $path).Hash.ToLower()
}

function Save-File([string[]]$urls, [string]$path, [string]$sha1) {
    foreach ($url in $urls) {
        for ($attempt = 1; $attempt -le 3; $attempt++) {
            try {
                Invoke-WebRequest -Uri $url -OutFile $path -UseBasicParsing @proxy | Out-Null
                if ((Get-Sha1 $path) -ne $sha1) {
                    throw 'checksum mismatch'
                }
                return $true
            } catch {
                Remove-Item -LiteralPath $path -ErrorAction SilentlyContinue
            }
        }
    }
    return $false
}

$indexDir = Join-Path $Target 'indexes'
$objectDir = Join-Path $Target 'objects'
New-Item -ItemType Directory -Force -Path $indexDir, $objectDir | Out-Null

$indexPath = Join-Path $indexDir "$indexName.json"
if (-not (Test-Path -LiteralPath $indexPath) -or (Get-Sha1 $indexPath) -ne $indexSha1) {
    Write-Host "Downloading the asset index $indexName"
    if (-not (Save-File $indexUrls $indexPath $indexSha1)) {
        Write-Host 'Could not download the asset index.'
        exit 1
    }
}

$index = Get-Content -LiteralPath $indexPath -Raw -Encoding UTF8 | ConvertFrom-Json
$objects = @($index.objects.PSObject.Properties | ForEach-Object { $_.Value })
if ($Limit -gt 0) {
    $objects = @($objects | Select-Object -First $Limit)
}

$total = $objects.Count
$done = 0
$failed = 0
foreach ($object in $objects) {
    $hash = [string]$object.hash
    $prefix = $hash.Substring(0, 2)
    $path = Join-Path (Join-Path $objectDir $prefix) $hash
    $present = (Test-Path -LiteralPath $path) -and ((Get-Item -LiteralPath $path).Length -eq [long]$object.size)
    if (-not $present) {
        New-Item -ItemType Directory -Force -Path (Split-Path $path) | Out-Null
        $urls = @($objectBases | ForEach-Object { "$_/$prefix/$hash" })
        if (-not (Save-File $urls $path $hash)) {
            $failed++
            Write-Host "Failed: $hash"
        }
    }
    $done++
    if ($done % 50 -eq 0) {
        Write-Host ("{0}/{1}" -f $done, $total)
    }
}

if ($failed -gt 0) {
    Write-Host "$failed of $total assets could not be downloaded."
    exit 1
}

Write-Host "Assets ready: $total files in $Target"
