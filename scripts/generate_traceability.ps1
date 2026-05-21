Set-Location "C:\TESTWARE\POC motor\Drools"

$excelPath = "C:\Users\drburban\Downloads\Bodegas.xlsx"
$outCsv = "docs\traceabilidad-excel-drl-fila-por-fila.csv"
$outMd = "docs\TRACEABILIDAD-EXCEL-DRL.md"

function Norm-Val([string]$v) {
  if ($null -eq $v) { return "" }
  return ($v.Trim() -replace "\s+", " ")
}

function Map-Enum([string]$field, [string]$value) {
  $v = (Norm-Val $value).ToUpperInvariant()
  switch ($field) {
    "assetType" {
      if ($v -like "*BODEGA*") { return "BODEGA" }
      return ""
    }
    "propertyCondition" {
      if ($v -eq "NUEVO") { return "NUEVO" }
      if ($v -eq "USADO") { return "USADO" }
      return ""
    }
    "propertyAgeRange" {
      if ($v -like "*MENOR*5*") { return "MENOR_5" }
      if ($v -like "*ENTRE*5*20*") { return "ENTRE_5_20" }
      if ($v -like "*MAYOR*20*") { return "MAYOR_20" }
      return ""
    }
    "propertyLocation" {
      if ($v -like "*PARQUE INDUSTRIAL*") { return "PARQUE_INDUSTRIAL" }
      if ($v -like "*ZONA FRANCA*") { return "ZONA_FRANCA" }
      if ($v -like "*ZONA INDUSTRIAL*") { return "ZONA_INDUSTRIAL" }
      if ($v -like "*OTRA UBICACI*") { return "OTRA_UBICACION" }
      return ""
    }
    "clearHeightRange" {
      if ($v -like "*MENOR*7*") { return "MENOR_7" }
      if ($v -like "*7 MTS*9 MTS*DOBLE*") { return "DOBLE_ALTURA" }
      if ($v -like "*MAYOR*9*TRIPLE*") { return "TRIPLE_ALTURA" }
      return ""
    }
    "accessDoorHeightRange" {
      if ($v -like "*MENOR*3,5*" -or $v -like "*MENOR*3.5*") { return "MENOR_3_5" }
      if ($v -like "*3,5*4,5*" -or $v -like "*3.5*4.5*") { return "ENTRE_3_5_4_5" }
      if ($v -like "*MAYOR*4,5*" -or $v -like "*MAYOR*4.5*") { return "MAYOR_4_5" }
      return ""
    }
    "parkingAvailability" {
      if ($v -eq "SI") { return "SI" }
      if ($v -eq "NO") { return "NO" }
      if ($v -eq "INCIERTO") { return "INCIERTO" }
      return ""
    }
    "parkingIndexRange" {
      if ($v -like "*MENOR*50*") { return "MENOR_50" }
      if ($v -like "*ENTRE*50*100*") { return "ENTRE_50_100" }
      if ($v -like "*MAYOR*100*") { return "MAYOR_100" }
      return ""
    }
    "saleableAreaRange" {
      if ($v -like "*MENOR*500*") { return "MENOR_500" }
      if ($v -like "*ENTRE 500 M2 Y 1000 M2*" -or $v -like "*ENTRE*500*1000*") { return "ENTRE_500_1000" }
      if ($v -like "*ENTRE 1000 M2 Y 5000 M2*" -or $v -like "*ENTRE*1000*5000*") { return "ENTRE_1000_5000" }
      if ($v -like "*MAYOR*5000*") { return "MAYOR_5000" }
      return ""
    }
    "accessRoadType" {
      if ($v -like "*VIA PRINCIPAL*") { return "VIA_PRINCIPAL" }
      if ($v -like "*SECUNDARIA*BUENAS*") { return "VIA_SECUNDARIA_BUENAS" }
      if ($v -like "*SECUNDARIA*REGULARES*") { return "VIA_SECUNDARIA_REGULARES" }
      return ""
    }
    "sustainabilityCertification" {
      if ($v -eq "SI") { return "true" }
      if ($v -eq "NO") { return "false" }
      return ""
    }
    "subleaseContractRange" {
      if ($v -eq "PROPIO") { return "PROPIO" }
      if ($v -like "*INFERIOR*1 A*" -or $v -like "*MENOR*1*") { return "MENOR_1" }
      if ($v -like "*ENTRE*1*5*") { return "ENTRE_1_5" }
      if ($v -like "*ENTRE*5*10*") { return "ENTRE_5_10" }
      if ($v -like "*MAYOR*10*") { return "MAYOR_10" }
      return ""
    }
    default { return "" }
  }
}

function Add-Rule([System.Collections.Generic.List[string]]$rules, [string]$name, [ref]$score, [int]$delta) {
  $rules.Add($name)
  $score.Value += $delta
}

$excel = New-Object -ComObject Excel.Application
$excel.Visible = $false
$excel.DisplayAlerts = $false

$rowsOut = New-Object System.Collections.Generic.List[object]

try {
  $wb = $excel.Workbooks.Open($excelPath, 3, $true)
  $ws = $wb.Worksheets | Where-Object { $_.Name -like "Regla Dependiente*" } | Select-Object -First 1
  if (-not $ws) { throw "No se encontro la hoja Regla Dependiente" }

  $used = $ws.UsedRange
  $maxR = $used.Rows.Count

  $ff = @{ a="";b="";c="";d="";e="";f="";g="";h="";i="";j="";k="";l="" }

  for ($r = 1; $r -le $maxR; $r++) {
    $c1 = Norm-Val $used.Cells.Item($r,1).Text
    $c2 = Norm-Val $used.Cells.Item($r,2).Text
    $c3 = Norm-Val $used.Cells.Item($r,3).Text
    $c4 = Norm-Val $used.Cells.Item($r,4).Text
    $c5 = Norm-Val $used.Cells.Item($r,5).Text
    $c6 = Norm-Val $used.Cells.Item($r,6).Text
    $c7 = Norm-Val $used.Cells.Item($r,7).Text
    $c8 = Norm-Val $used.Cells.Item($r,8).Text
    $c9 = Norm-Val $used.Cells.Item($r,9).Text
    $c10 = Norm-Val $used.Cells.Item($r,10).Text
    $c11 = Norm-Val $used.Cells.Item($r,11).Text
    $c12 = Norm-Val $used.Cells.Item($r,12).Text

    $allText = ($c1+$c2+$c3+$c4+$c5+$c6+$c7+$c8+$c9+$c10+$c11+$c12)
    if ([string]::IsNullOrWhiteSpace($allText)) { continue }

    if ($c1 -eq "Entrada" -or $c1 -eq "Tipo de activo") {
      $ff = @{ a="";b="";c="";d="";e="";f="";g="";h="";i="";j="";k="";l="" }
      continue
    }

    if ($c1 -ne "") { $ff.a = $c1 }
    if ($c2 -ne "") { $ff.b = $c2 }
    if ($c3 -ne "") { $ff.c = $c3 }
    if ($c4 -ne "") { $ff.d = $c4 }
    if ($c5 -ne "") { $ff.e = $c5 }
    if ($c6 -ne "") { $ff.f = $c6 }
    if ($c7 -ne "") { $ff.g = $c7 }
    if ($c8 -ne "") { $ff.h = $c8 }
    if ($c9 -ne "") { $ff.i = $c9 }
    if ($c10 -ne "") { $ff.j = $c10 }
    if ($c11 -ne "") { $ff.k = $c11 }
    if ($c12 -ne "") { $ff.l = $c12 }

    $assetType = Map-Enum "assetType" $ff.a
    if ($assetType -ne "BODEGA") { continue }

    $cond = Map-Enum "propertyCondition" $ff.b
    $age = Map-Enum "propertyAgeRange" $ff.c
    $loc = Map-Enum "propertyLocation" $ff.d
    $height = Map-Enum "clearHeightRange" $ff.e
    $door = Map-Enum "accessDoorHeightRange" $ff.f
    $park = Map-Enum "parkingAvailability" $ff.g
    $parkIdx = Map-Enum "parkingIndexRange" $ff.h
    $area = Map-Enum "saleableAreaRange" $ff.i
    $road = Map-Enum "accessRoadType" $ff.j
    $sust = Map-Enum "sustainabilityCertification" $ff.k
    $sub = Map-Enum "subleaseContractRange" $ff.l

    $rules = New-Object System.Collections.Generic.List[string]
    $score = 0

    switch ($cond) { "NUEVO" { Add-Rule $rules "Bodega Matrix - Condition New" ([ref]$score) -3 }; "USADO" { Add-Rule $rules "Bodega Matrix - Condition Used" ([ref]$score) 6 } }
    switch ($age) { "MENOR_5" { Add-Rule $rules "Bodega Matrix - Age Menor 5" ([ref]$score) -6 }; "ENTRE_5_20" { Add-Rule $rules "Bodega Matrix - Age Entre 5 20" ([ref]$score) 6 }; "MAYOR_20" { Add-Rule $rules "Bodega Matrix - Age Mayor 20" ([ref]$score) 16 } }
    switch ($loc) { "PARQUE_INDUSTRIAL" { Add-Rule $rules "Bodega Matrix - Location Parque" ([ref]$score) -6 }; "ZONA_FRANCA" { Add-Rule $rules "Bodega Matrix - Location Zona Franca" ([ref]$score) -3 }; "ZONA_INDUSTRIAL" { Add-Rule $rules "Bodega Matrix - Location Zona Industrial" ([ref]$score) 4 }; "OTRA_UBICACION" { Add-Rule $rules "Bodega Matrix - Location Otra" ([ref]$score) 12 } }
    switch ($height) { "MENOR_7" { Add-Rule $rules "Bodega Matrix - Height Menor 7" ([ref]$score) 14 }; "DOBLE_ALTURA" { Add-Rule $rules "Bodega Matrix - Height Doble" ([ref]$score) -4 }; "TRIPLE_ALTURA" { Add-Rule $rules "Bodega Matrix - Height Triple" ([ref]$score) -6 } }
    switch ($door) { "MENOR_3_5" { Add-Rule $rules "Bodega Matrix - Access Door Menor 3 5" ([ref]$score) 8 }; "ENTRE_3_5_4_5" { Add-Rule $rules "Bodega Matrix - Access Door Entre 3 5 4 5" ([ref]$score) -2 }; "MAYOR_4_5" { Add-Rule $rules "Bodega Matrix - Access Door Mayor 4 5" ([ref]$score) -4 } }
    switch ($area) { "MENOR_500" { Add-Rule $rules "Bodega Matrix - Area Menor 500" ([ref]$score) 4 }; "ENTRE_500_1000" { Add-Rule $rules "Bodega Matrix - Area Entre 500 1000" ([ref]$score) 1 }; "ENTRE_1000_5000" { Add-Rule $rules "Bodega Matrix - Area Entre 1000 5000" ([ref]$score) -3 }; "MAYOR_5000" { Add-Rule $rules "Bodega Matrix - Area Mayor 5000" ([ref]$score) 15 } }
    switch ($road) { "VIA_PRINCIPAL" { Add-Rule $rules "Bodega Matrix - Access Road Principal" ([ref]$score) -4 }; "VIA_SECUNDARIA_BUENAS" { Add-Rule $rules "Bodega Matrix - Access Road Secondary Good" ([ref]$score) 0 }; "VIA_SECUNDARIA_REGULARES" { Add-Rule $rules "Bodega Matrix - Access Road Secondary Regular" ([ref]$score) 7 } }
    switch ($park) { "SI" { Add-Rule $rules "Bodega Matrix - Parking Availability Yes" ([ref]$score) -3 }; "NO" { Add-Rule $rules "Bodega Matrix - Parking Availability No" ([ref]$score) 7 }; "INCIERTO" { Add-Rule $rules "Bodega Matrix - Parking Availability Uncertain" ([ref]$score) 5 } }
    switch ($parkIdx) { "MENOR_50" { Add-Rule $rules "Bodega Matrix - Parking Index Menor 50" ([ref]$score) -2 }; "MAYOR_100" { Add-Rule $rules "Bodega Matrix - Parking Index Mayor 100" ([ref]$score) 6 } }
    switch ($sust) { "true" { Add-Rule $rules "Bodega Matrix - Sustainability Certification" ([ref]$score) -6 }; "false" { Add-Rule $rules "Bodega Matrix - No Sustainability Certification" ([ref]$score) 3 } }
    switch ($sub) { "PROPIO" { Add-Rule $rules "Bodega Matrix - Sublease Own" ([ref]$score) -4 }; "MENOR_1" { Add-Rule $rules "Bodega Matrix - Sublease Less Than 1" ([ref]$score) 10 }; "ENTRE_1_5" { Add-Rule $rules "Bodega Matrix - Sublease Between 1 5" ([ref]$score) 4 }; "ENTRE_5_10" { Add-Rule $rules "Bodega Matrix - Sublease Between 5 10" ([ref]$score) -1 }; "MAYOR_10" { Add-Rule $rules "Bodega Matrix - Sublease Greater Than 10" ([ref]$score) -4 } }

    if ($cond -eq "NUEVO" -and $age -eq "MENOR_5" -and $loc -eq "PARQUE_INDUSTRIAL" -and ($height -eq "DOBLE_ALTURA" -or $height -eq "TRIPLE_ALTURA") -and $door -eq "MENOR_3_5" -and $road -eq "VIA_PRINCIPAL") { Add-Rule $rules "Bodega Favorable Core Combination" ([ref]$score) -12 }
    if ($cond -eq "NUEVO" -and $loc -eq "PARQUE_INDUSTRIAL" -and $height -eq "TRIPLE_ALTURA" -and $door -eq "MAYOR_4_5" -and $area -eq "ENTRE_1000_5000" -and $road -eq "VIA_PRINCIPAL") { Add-Rule $rules "Bodega Favorable High Capacity Combination" ([ref]$score) -10 }
    if ($cond -eq "USADO" -and $age -eq "ENTRE_5_20" -and ($loc -eq "ZONA_FRANCA" -or $loc -eq "ZONA_INDUSTRIAL") -and $height -eq "DOBLE_ALTURA" -and $door -eq "ENTRE_3_5_4_5") { Add-Rule $rules "Bodega Medium Combination Requires Specialist" ([ref]$score) 10 }
    if ($cond -eq "USADO" -and $age -eq "MAYOR_20" -and $loc -eq "ZONA_INDUSTRIAL" -and $height -eq "DOBLE_ALTURA" -and $road -eq "VIA_SECUNDARIA_BUENAS") { Add-Rule $rules "Bodega Medium Legacy Combination" ([ref]$score) 12 }
    if ($age -eq "MAYOR_20" -and $area -eq "MAYOR_5000") { Add-Rule $rules "Bodega Not Favorable Old And Very Large" ([ref]$score) 35 }
    if ($height -eq "MENOR_7" -and $area -eq "MAYOR_5000") { Add-Rule $rules "Bodega Not Favorable Low Height And Very Large" ([ref]$score) 30 }
    if ($loc -eq "OTRA_UBICACION" -and $area -eq "MAYOR_5000") { Add-Rule $rules "Bodega Not Favorable Other Location And Very Large" ([ref]$score) 30 }
    if ($age -eq "MAYOR_20" -and $loc -eq "OTRA_UBICACION" -and $height -eq "MENOR_7") { Add-Rule $rules "Bodega Critical Not Favorable Combination" ([ref]$score) 45 }
    if ($age -eq "MAYOR_20" -and $loc -eq "OTRA_UBICACION" -and $height -eq "MENOR_7" -and $door -eq "MENOR_3_5" -and $parkIdx -eq "MAYOR_100") { Add-Rule $rules "Bodega Extreme Critical Combination" ([ref]$score) 50 }

    $derivedClass = ""
    $manual = "false"
    $action = ""
    if ($score -le 20) {
      $derivedClass = "FAVORABLE"
      $rules.Add("Bodega Matrix Final Classification Favorable")
      $action = "APPROVE_OR_FAVORABLE_CONCEPT"
    } elseif ($score -lt 55) {
      $derivedClass = "MEDIO"
      $rules.Add("Bodega Matrix Final Classification Medium")
      $manual = "true"
      $action = "REQUIERE_CONCEPTO_ESPECIALISTA"
    } else {
      $derivedClass = "NO_FAVORABLE"
      $rules.Add("Bodega Matrix Final Classification Not Favorable")
      $manual = "true"
      $action = "MANUAL_REVIEW_OR_REJECT"
    }

    $rowsOut.Add([pscustomobject]@{
      ExcelRow = $r
      TipoActivo = $assetType
      Condicion = $cond
      Antiguedad = $age
      Ubicacion = $loc
      AlturaLibre = $height
      AlturaPuerta = $door
      Parqueadero = $park
      IndiceParqueaderos = $parkIdx
      AreaVendible = $area
      ViasAcceso = $road
      CertificacionSostenibilidad = $sust
      Subarriendo = $sub
      ExcelComercializacion = (Norm-Val $used.Cells.Item($r,14).Text)
      ExcelRestitucion = (Norm-Val $used.Cells.Item($r,15).Text)
      ExcelNormativo = (Norm-Val $used.Cells.Item($r,16).Text)
      ExcelAmbiental = (Norm-Val $used.Cells.Item($r,17).Text)
      ExcelGeneral = (Norm-Val $used.Cells.Item($r,18).Text)
      ExcelLeasingFinanciero = (Norm-Val $used.Cells.Item($r,19).Text)
      ExcelPlanFlexible = (Norm-Val $used.Cells.Item($r,20).Text)
      ReglasDRLAplicadas = ($rules -join " | ")
      DeltaRiesgo = $score
      ClasificacionDerivada = $derivedClass
      RequiereRevisionManual = $manual
      AccionDerivada = $action
    })
  }

  $rowsOut | Sort-Object ExcelRow | Export-Csv -Path $outCsv -NoTypeInformation -Encoding UTF8

  $total = $rowsOut.Count
  $fav = ($rowsOut | Where-Object { $_.ClasificacionDerivada -eq "FAVORABLE" }).Count
  $med = ($rowsOut | Where-Object { $_.ClasificacionDerivada -eq "MEDIO" }).Count
  $nf = ($rowsOut | Where-Object { $_.ClasificacionDerivada -eq "NO_FAVORABLE" }).Count
  $top = $rowsOut | Sort-Object ExcelRow | Select-Object -First 25

  $md = @()
  $md += "# Trazabilidad Excel a DRL por fila"
  $md += ""
  $md += "Archivo fuente: Bodegas.xlsx (hoja Regla Dependiente (3))."
  $md += ""
  $md += "Total filas trazadas: $total"
  $md += "- Favorable: $fav"
  $md += "- Medio: $med"
  $md += "- No favorable: $nf"
  $md += ""
  $md += "Tabla completa (fila por fila): docs/traceabilidad-excel-drl-fila-por-fila.csv"
  $md += ""
  $md += "Vista previa (primeras 25 filas):"
  $md += ""
  $md += "| ExcelRow | Condicion | Antiguedad | Ubicacion | AlturaLibre | AreaVendible | ExcelGeneral | ClasificacionDerivada | DeltaRiesgo |"
  $md += "|---|---|---|---|---|---|---|---|---|"
  foreach ($t in $top) {
    $md += "| $($t.ExcelRow) | $($t.Condicion) | $($t.Antiguedad) | $($t.Ubicacion) | $($t.AlturaLibre) | $($t.AreaVendible) | $($t.ExcelGeneral) | $($t.ClasificacionDerivada) | $($t.DeltaRiesgo) |"
  }
  $md += ""
  $md += "Nota: si una fila en Excel trae celdas vacias en columnas de entrada, se aplica forward-fill con el ultimo valor explicito observado en la misma matriz para poder mapearla a reglas DRL."

  Set-Content -Path $outMd -Value $md -Encoding UTF8

  Write-Host "CSV generado: $outCsv"
  Write-Host "MD generado: $outMd"
  Write-Host "Filas trazadas: $total"
}
finally {
  if ($wb) { $wb.Close($false) | Out-Null }
  $excel.Quit()
  [System.Runtime.Interopservices.Marshal]::ReleaseComObject($excel) | Out-Null
}
