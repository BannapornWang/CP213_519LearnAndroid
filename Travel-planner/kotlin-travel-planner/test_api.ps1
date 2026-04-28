# Read keys from local.properties
$propsFile = Join-Path $PSScriptRoot "local.properties"
$geminiKey = ""
$groqKey = ""
$modelName = "gemini-2.0-flash-lite"

Get-Content $propsFile | ForEach-Object {
    if ($_ -match "^GEMINI_API_KEY=(.+)$") { $geminiKey = $Matches[1].Trim() }
    if ($_ -match "^GEMINI_MODEL=(.+)$")   { $modelName = $Matches[1].Trim() }
    if ($_ -match "^GROQ_API_KEY=(.+)$")   { $groqKey   = $Matches[1].Trim() }
}

Write-Host "=== API Key Status ===" -ForegroundColor Cyan
Write-Host "Gemini Key: $(if ($geminiKey) { $geminiKey.Substring(0,[Math]::Min(8,$geminiKey.Length)) + '...' } else { 'NOT SET' })"
Write-Host "Groq Key:   $(if ($groqKey)   { $groqKey.Substring(0,[Math]::Min(8,$groqKey.Length)) + '...' }   else { 'NOT SET' })"
Write-Host ""

# Test Groq
if ($groqKey) {
    Write-Host "--- Testing Groq (llama-3.3-70b-versatile) ---" -ForegroundColor Yellow
    $groqBody = @{
        model = "llama-3.3-70b-versatile"
        messages = @(@{ role = "user"; content = "Say hello in one word." })
        max_tokens = 10
    } | ConvertTo-Json -Depth 5
    try {
        $r = Invoke-RestMethod -Uri "https://api.groq.com/openai/v1/chat/completions" `
            -Method POST -ContentType "application/json" `
            -Headers @{ Authorization = "Bearer $groqKey" } `
            -Body $groqBody
        Write-Host "OK [Groq] SUCCESS: $($r.choices[0].message.content)" -ForegroundColor Green
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        Write-Host "FAIL [Groq] HTTP $code - $($_.Exception.Message)" -ForegroundColor Red
    }
} else {
    Write-Host "SKIP [Groq] - GROQ_API_KEY not set in local.properties" -ForegroundColor Gray
}

Write-Host ""

# Test Gemini
if ($geminiKey) {
    Write-Host "--- Testing Gemini ($modelName) ---" -ForegroundColor Yellow
    $geminiBody = @{
        contents = @(@{ parts = @(@{ text = "hello" }) })
        generationConfig = @{ maxOutputTokens = 10 }
    } | ConvertTo-Json -Depth 6
    try {
        $r = Invoke-RestMethod -Uri "https://generativelanguage.googleapis.com/v1beta/models/${modelName}:generateContent?key=$geminiKey" `
            -Method POST -ContentType "application/json" -Body $geminiBody
        Write-Host "OK [Gemini] SUCCESS: $($r.candidates[0].content.parts[0].text)" -ForegroundColor Green
    } catch {
        $code = $_.Exception.Response.StatusCode.value__
        Write-Host "FAIL [Gemini] HTTP $code" -ForegroundColor Red
        if ($code -eq 429) { Write-Host "  -> Quota exceeded" -ForegroundColor Yellow }
        if ($code -eq 401) { Write-Host "  -> API Key invalid or revoked" -ForegroundColor Yellow }
    }
} else {
    Write-Host "SKIP [Gemini] - GEMINI_API_KEY not set" -ForegroundColor Gray
}
