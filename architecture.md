Zusätzlich zum bestehenden `agents.md` soll eine Datei erstellt und gepflegt werden:

`architecture.md`

Diese Datei dient als zentrale technische Architektur-Dokumentation des Projekts.

WICHTIG:

Die Datei soll:

- für Studenten/Kotlin-Anfänger verständlich sein
- die tatsächliche Projektstruktur widerspiegeln
- nach größeren Architekturänderungen aktualisiert werden
- als Lern- und Onboarding-Dokument dienen
- mit `agents.md` konsistent bleiben

Die Dokumentation soll klar strukturiert, technisch korrekt und leicht nachvollziehbar sein.

# architecture.md muss mindestens enthalten

# 1. Projektüberblick

Kurze Erklärung:

- Was ist das Projekt?
- Was ist der Zweck?
- Welche Hauptfeatures existieren?
- Welche Plattformen/Technologien werden verwendet?

Beispiel:

- Android App
- Kotlin
- Jetpack Compose
- MVVM
- Hilt
- Room
- Retrofit
- etc.

---

# 2. Architekturübersicht

Erklärung der Gesamtarchitektur:

- Welche Architektur wird verwendet?
    - MVVM
    - Clean Architecture
    - Layered Architecture
    - Repository Pattern
    - etc.

Erklärung:

- warum diese Architektur gewählt wurde
- Vorteile der Architektur
- wie Daten durch das System fließen

Beschreibe:

- UI Layer
- ViewModel Layer
- Domain Layer
- Data Layer
- Repository Layer
- Dependency Injection
- Navigation
- State Management

---

# 3. Projektstruktur / Aufteilung

Dokumentiere die Ordner- und Paketstruktur.

Beispiel:

```
app/
 ├── data/
 ├── domain/
 ├── presentation/
 ├── di/
 ├── navigation/
 ├── ui/
 └── utils/
```

Für jeden Bereich erklären:

- Zweck
- Verantwortlichkeiten
- welche Klassen dort hineingehören
- welche Klassen NICHT dort hineingehören

---

# 4. Zuständigkeiten (Responsibilities)

Erkläre klar die Verantwortlichkeiten jeder Schicht.

Mindestens dokumentieren:

## Composables

- nur UI darstellen
- möglichst wenig Business-Logik
- State konsumieren
- Events weitergeben

## ViewModels

- UI State verwalten
- Business-Logik koordinieren
- Repository verwenden
- keine Android-UI-Elemente enthalten

## Repository

- zentrale Datenquelle abstrahieren
- API/DB kombinieren
- Datenfluss kapseln

## Data Sources

- API
- Room
- lokale Speicherung
- Remote Calls

## Domain Models vs DTOs vs Entities

- Unterschiede erklären
- wann welches Modell verwendet wird

---

# 5. Technologien & Libraries

Dokumentiere:

- verwendete Technologien
- warum sie verwendet werden
- wofür sie zuständig sind

Beispiel:

| Technologie | Zweck |
| --- | --- |
| Kotlin | Hauptsprache |
| Jetpack Compose | UI |
| Hilt | Dependency Injection |
| Room | Lokale Datenbank |
| Retrofit | API Calls |
| Coroutines | Asynchronität |
| StateFlow | Reactive State |
| Navigation Compose | Navigation |

Zusätzlich:

- wichtige Konzepte kurz erklären
- typische Datenflüsse beschreiben

---

# 6. State Management

Dokumentieren:

- wie UI State verwaltet wird
- StateFlow vs mutableStateOf
- State Hoisting
- Recomposition
- Lifecycle Awareness

Erklären:

- wann welche Technik verwendet wird
- warum

---

# 7. Navigation

Beschreiben:

- wie Navigation aufgebaut ist
- Navigation Graph
- Routen
- Argumente
- Deep Links falls vorhanden

---

# 8. Dependency Injection

Dokumentieren:

- wie Hilt verwendet wird
- wo Module liegen
- wie Dependencies injected werden
- Singleton vs andere Scopes

---

# 9. Datenfluss

Schritt-für-Schritt erklären:

```
UI -> ViewModel -> Repository -> DataSource/API/DB
```

Und zurück:

```
DB/API -> Repository -> ViewModel -> UI State -> Compose UI
```

Mit konkreten Projektbeispielen erklären.

---

# 10. Diagramme

Die Datei MUSS Diagramme enthalten.

Mindestens:

## Architekturdiagramm

Beispiel:

```
UI
 ↓
ViewModel
 ↓
Repository
 ↓
Data Source
```

## Datenflussdiagramm

## Navigationsdiagramm

## Optional:

- Dependency Graph
- State Flow Diagram
- Feature Diagram

Diagramme bevorzugt in:

- Mermaid
- PlantUML
- oder einfache ASCII-Diagramme

---

# 11. Feature-Dokumentation

Für wichtige Features dokumentieren:

- beteiligte Klassen
- Datenfluss
- State Handling
- Navigation
- verwendete Technologien

---

# 12. Architekturregeln

Dokumentiere klare Regeln:

Beispiele:

- Keine Business-Logik in Composables
- ViewModels sprechen nicht direkt mit Retrofit
- UI konsumiert nur UI State
- Repository abstrahiert Datenquellen
- Navigation nicht hardcoden
- etc.

---

# 13. Anfänger-Erklärungen

Die Datei soll zusätzlich Lerncharakter haben.

Wichtige Konzepte einfach erklären:

- Coroutines
- suspend
- Flow
- StateFlow
- remember
- recomposition
- dependency injection
- repository pattern
- MVVM
- lifecycle

Immer mit:

- einfachen Beispielen
- Bezug zum aktuellen Projekt
- Erklärung warum das Konzept sinnvoll ist

---

# WICHTIG

Die Datei soll:

- praktisch verständlich sein
- nicht nur theoretisch
- echte Projektdateien referenzieren
- echte Klassen erwähnen
- reale Datenflüsse dokumentieren

Wenn möglich:

- automatisch aktualisieren
- bei neuen Features ergänzen
- Architekturentscheidungen dokumentieren

Die Datei soll langfristig als:

- Onboarding-Dokument
- Lernunterlage
- Architekturübersicht
- Prüfungs-/Interviewvorbereitung
    
    dienen.