# Trazabilidad Excel a DRL por fila

Archivo fuente: Bodegas.xlsx (hoja Regla Dependiente (3)).

Total filas trazadas: 8645
- Favorable: 4660
- Medio: 2693
- No favorable: 1292

Tabla completa (fila por fila): docs/traceabilidad-excel-drl-fila-por-fila.csv

Vista previa (primeras 25 filas):

| ExcelRow | Condicion | Antiguedad | Ubicacion | AlturaLibre | AreaVendible | ExcelGeneral | ClasificacionDerivada | DeltaRiesgo |
|---|---|---|---|---|---|---|---|---|
| 3 | NUEVO | MENOR_5 | PARQUE_INDUSTRIAL | MENOR_7 | MENOR_500 | Bajo | FAVORABLE | -8 |
| 4 | USADO | ENTRE_5_20 | ZONA_FRANCA | DOBLE_ALTURA | ENTRE_500_1000 |  | MEDIO | 34 |
| 5 | USADO | MAYOR_20 | ZONA_INDUSTRIAL | TRIPLE_ALTURA | ENTRE_1000_5000 |  | MEDIO | 38 |
| 6 | USADO | MAYOR_20 | OTRA_UBICACION | TRIPLE_ALTURA | MAYOR_5000 |  | NO_FAVORABLE | 124 |
| 7 | USADO | MAYOR_20 | OTRA_UBICACION | TRIPLE_ALTURA | MAYOR_5000 |  | NO_FAVORABLE | 121 |
| 34 | USADO | MENOR_5 | PARQUE_INDUSTRIAL | MENOR_7 | MENOR_500 | Medio | FAVORABLE | 11 |
| 35 | USADO | ENTRE_5_20 | PARQUE_INDUSTRIAL | MENOR_7 | MENOR_500 | Medio | MEDIO | 23 |
| 36 | USADO | MAYOR_20 | PARQUE_INDUSTRIAL | MENOR_7 | MENOR_500 | Medio | MEDIO | 33 |
| 37 | USADO | MENOR_5 | ZONA_FRANCA | MENOR_7 | MENOR_500 | Medio | FAVORABLE | 14 |
| 38 | USADO | ENTRE_5_20 | ZONA_FRANCA | MENOR_7 | MENOR_500 | Medio | MEDIO | 26 |
| 39 | USADO | MAYOR_20 | ZONA_FRANCA | MENOR_7 | MENOR_500 | Medio | MEDIO | 36 |
| 40 | USADO | MENOR_5 | ZONA_INDUSTRIAL | MENOR_7 | MENOR_500 | Medio | MEDIO | 21 |
| 41 | USADO | ENTRE_5_20 | ZONA_INDUSTRIAL | MENOR_7 | MENOR_500 | Medio | MEDIO | 33 |
| 42 | USADO | MAYOR_20 | ZONA_INDUSTRIAL | MENOR_7 | MENOR_500 | Medio | MEDIO | 43 |
| 43 | USADO | MENOR_5 | OTRA_UBICACION | MENOR_7 | MENOR_500 | Medio | MEDIO | 29 |
| 44 | USADO | ENTRE_5_20 | OTRA_UBICACION | MENOR_7 | MENOR_500 | Medio | MEDIO | 41 |
| 45 | USADO | MAYOR_20 | OTRA_UBICACION | MENOR_7 | MENOR_500 | Medio | NO_FAVORABLE | 96 |
| 46 | USADO | MENOR_5 | PARQUE_INDUSTRIAL | DOBLE_ALTURA | MENOR_500 | Bajo | FAVORABLE | -7 |
| 47 | USADO | ENTRE_5_20 | PARQUE_INDUSTRIAL | DOBLE_ALTURA | MENOR_500 | Bajo | FAVORABLE | 5 |
| 48 | USADO | MAYOR_20 | PARQUE_INDUSTRIAL | DOBLE_ALTURA | MENOR_500 | Bajo | FAVORABLE | 15 |
| 49 | USADO | MENOR_5 | ZONA_FRANCA | DOBLE_ALTURA | MENOR_500 | Bajo | FAVORABLE | -4 |
| 50 | USADO | ENTRE_5_20 | ZONA_FRANCA | DOBLE_ALTURA | MENOR_500 | Bajo | FAVORABLE | 8 |
| 51 | USADO | MAYOR_20 | ZONA_FRANCA | DOBLE_ALTURA | MENOR_500 | Bajo | FAVORABLE | 18 |
| 52 | USADO | MENOR_5 | ZONA_INDUSTRIAL | DOBLE_ALTURA | MENOR_500 | Bajo | FAVORABLE | 3 |
| 53 | USADO | ENTRE_5_20 | ZONA_INDUSTRIAL | DOBLE_ALTURA | MENOR_500 | Bajo | FAVORABLE | 15 |

Nota: si una fila en Excel trae celdas vacias en columnas de entrada, se aplica forward-fill con el ultimo valor explicito observado en la misma matriz para poder mapearla a reglas DRL.
