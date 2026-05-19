# Oral Reflection Stichworte

## MAD-01 - Complete / Active

- Checkbox toggelt `isCompleted`
- Tasks-Screen und Detail-Screen
- UI -> ViewModel -> Repository -> Room
- Status dauerhaft gespeichert
- Snackbar: complete / active

## MAD-02 - Priority

- `TaskPriority`: High, Medium, Low
- gespeichert in Task / Room / Network-Mapping
- Add/Edit: Priority-Chips
- Tasks-Liste: Farbe + Text
- Filter: High / Medium / Low
- Room Migration Version 1 -> 2

## MAD-03 - Statistics Crash

- Problem: leere Task-Liste
- Risiko: Division durch 0
- Fix: `isNullOrEmpty()`
- Ergebnis bei leer: `0% / 0%`
- Empty-State bleibt

## MAD-04 - Seed Tasks

- Seed beim ersten DB-Open
- `RoomDatabase.Callback.onOpen`
- `SharedPreferences` verhindert Duplikate
- nur wenn Tabelle leer
- drei Default-Tasks
- Refresh loescht lokale Tasks nicht mehr
- `upsertAll`: insert oder update

## MAD-05 - Statistics UI

- zwei Karten
- orange: Active Tasks
- gruen: Completed Tasks
- Prozent gross und zentriert
- Empty-State bleibt
- ViewModel unveraendert

## MAD-06 - Snackbar Bug

- Problem: "Task added" kam nach Navigation nochmal
- Snackbar = einmaliges Event
- Navigation-Argument wird auf `0` gesetzt
- `SavedStateHandle` wird auch auf `0` gesetzt
- Ergebnis: Snackbar nur einmal

## MAD-07 - Swipe To Delete

- Links-Swipe in Task-Liste
- `SwipeToDismissBox`
- `TasksViewModel.deleteTask()`
- Repository loescht aus Room
- Liste aktualisiert automatisch
- Snackbar: Task deleted

## Architektur

- MVVM
- Compose nur UI
- ViewModel: State + Events
- Repository: Datenzugriff kapseln
- DAO/Room: lokale Datenbank
- Flow/StateFlow: automatische UI-Updates

## AI-Reflexion

- AI half bei Analyse
- AI half bei Planung
- AI half bei Code-Review
- AI half bei Doku
- Code nicht blind uebernommen
- Build/Tests nach Items
- einzelne Commits pro Backlog-Item
