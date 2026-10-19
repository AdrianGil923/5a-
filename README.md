# Actividad 5A: Regla de Simpson

Proyecto Maven con Java 21 para integrar la distribución t de Student mediante la regla de Simpson.

## Ejecutar

```bash
mvn compile exec:java -Dexec.mainClass=App
```

Ingresa valor inicial, valor final y DOF. El resultado se muestra en consola y se guarda en `output/output5.txt`.

## Pruebas y cobertura

```bash
mvn clean verify
```

El reporte JaCoCo se genera en `target/site/jacoco/index.html`.
