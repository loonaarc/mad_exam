# Backlog Item: MAD-01 - Complete and activate task behavior

## Urspruengliche Beschreibung

Tasks should be toggled between active and completed when the user clicks the checkbox. This functionality needs to be added across the task list and task detail screens.

Acceptance criteria:

- Clicking the checkbox toggles a task between completed and active.
- The behavior works on the Tasks screen and Task Detail screen.
- The UI updates correctly after the task status changes.
- A Snackbar message is shown after completing a task.
- A Snackbar message is shown after reactivating a task.
- The task `isCompleted` flag is correctly saved to the database.

## Zweck

Dieses Feature macht die Checkboxen der App wirklich funktional. Vorher wurde beim Anklicken nur eine "Not implemented yet..."-Meldung angezeigt. Jetzt wird der Status einer Aufgabe dauerhaft in Room gespeichert und die UI aktualisiert sich automatisch ueber den bestehenden Datenfluss.

## Implementierte Dateien

- `app/src/main/java/at/ac/hcw/procrastinot/data/TaskRepository.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/data/DefaultTaskRepository.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/tasks/TasksViewModel.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/taskdetail/TaskDetailViewModel.kt`
- `docs/kotlin-beginner-explanations.md`

## Geaenderte Klassen/Funktionen

- `TaskRepository.updateCompleted(taskId, completed)`
- `DefaultTaskRepository.updateCompleted(taskId, completed)`
- `TasksViewModel.completeTask(task, completed)`
- `TaskDetailViewModel.setCompleted(completed)`

## Technische Umsetzung

1. Die Compose-Screens waren bereits richtig vorbereitet:
   `TasksScreen` und `TaskDetailScreen` senden Checkbox-Aenderungen an ihr jeweiliges ViewModel.
2. Das Repository-Interface hat eine neue Funktion `updateCompleted` bekommen. Dadurch muessen ViewModels nicht direkt mit Room oder dem DAO arbeiten.
3. `DefaultTaskRepository` ruft `TaskDao.updateCompleted(taskId, completed)` auf. Danach wird `saveTasksToNetwork()` ausgefuehrt, wie bei anderen Datenbankaenderungen im Projekt.
4. `TasksViewModel.completeTask` speichert den neuen Status ueber das Repository und setzt danach die passende Snackbar-Nachricht.
5. `TaskDetailViewModel.setCompleted` macht dasselbe fuer den Detail-Screen.
6. Room sendet durch den bestehenden `Flow` neue Daten. Die ViewModels bauen daraus ihren `StateFlow`, und Compose rendert die Checkboxen neu.

Datenfluss:

```text
Checkbox in Compose
-> ViewModel-Funktion
-> TaskRepository
-> TaskDao
-> Room-Datenbank
-> Flow aus Room
-> ViewModel StateFlow
-> collectAsStateWithLifecycle in Compose
-> Recomposition der UI
```

## Kotlin-Erklaerung

- `suspend`: `updateCompleted` ist eine suspend-Funktion, weil Datenbankzugriffe nicht blockierend im UI-Thread laufen sollen.
- `Coroutine`: Das ViewModel startet die Arbeit mit `viewModelScope.launch`. Die Coroutine wird automatisch beendet, wenn das ViewModel zerstoert wird.
- `Flow`: Room liefert Task-Listen und einzelne Tasks als `Flow`. Wenn sich die Datenbank aendert, wird automatisch ein neuer Wert gesendet.
- `StateFlow`: Die ViewModels stellen der UI einen stabilen Zustand als `StateFlow` bereit. Die UI liest nur diesen Zustand.
- `data class Task`: `isCompleted` ist Teil des Task-Modells. Daraus berechnet `isActive`, ob eine Aufgabe aktiv ist.

## Android-/Compose-Erklaerung

- `ViewModel`: Enthaelt die Bildschirm-Logik. Die Composables speichern den Task-Status nicht selbst.
- `Repository`: Ist der zentrale Einstieg in die Datenschicht. Dadurch bleibt Room aus der UI heraus.
- `Room`: Speichert den Wert `isCompleted` dauerhaft in der Tabelle `task`.
- `Compose State`: `collectAsStateWithLifecycle` beobachtet den `StateFlow`. Wenn Room neue Daten liefert, wird Compose neu gezeichnet.
- `Recomposition`: Nach dem Speichern aktualisiert Compose die Checkbox automatisch, weil sich der beobachtete State geaendert hat.
- `Snackbar`: Das ViewModel setzt eine Message-ID. Der Screen zeigt die Snackbar und ruft danach `snackbarMessageShown()` auf.

## Warum diese Loesung?

- Sie folgt der bestehenden Architektur aus `agent.md`: Compose -> ViewModel -> Repository -> DAO/Room.
- Sie ist minimal-invasiv: Keine neuen Packages, keine neuen Libraries, keine Versionsaenderungen und keine Room-Migration.
- Die vorhandene DAO-Funktion `updateCompleted` wird wiederverwendet.
- Die UI war bereits korrekt vorbereitet, deshalb musste keine Business-Logik in Composables verschoben werden.
- Die Loesung ist pruefungsfreundlich, weil der Datenfluss einfach erklaerbar bleibt.

## Wichtige Pruefungs-/Professor-Erklaerungen

- Die Checkbox ruft nur ein Event im ViewModel auf; sie speichert nicht selbst in der Datenbank.
- Das ViewModel ist die Stelle fuer Screen-Logik und Snackbar-Events.
- Das Repository kapselt die Datenschicht und verhindert direkten DAO-Zugriff aus der UI.
- Room speichert `isCompleted`, deshalb bleibt der Status nach App-Neustart erhalten.
- Flow und StateFlow sorgen dafuer, dass die UI nach der Datenbankaenderung automatisch aktualisiert wird.
