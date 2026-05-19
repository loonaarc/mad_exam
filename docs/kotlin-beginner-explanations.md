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

# Backlog Item: MAD-02 - Add task priorities and priority filtering

## Urspruengliche Beschreibung

Users should be able to assign priorities to tasks when adding or updating them. Priorities should also be shown visually on the Tasks screen and usable as a filter.

Acceptance criteria:

- Users can select a priority when adding a task.
- Users can update the priority of an existing task.
- Supported priorities are High (red), Medium (orange), and Low (blue).
- The selected priority is persisted in the database.
- Users can filter tasks by priority.

## Zweck

Dieses Feature hilft Benutzerinnen und Benutzern, wichtige Aufgaben schneller zu erkennen und die Liste nach Wichtigkeit zu filtern. Die Prioritaet gehoert zu den Task-Daten, deshalb wird sie nicht nur in der UI gehalten, sondern in Room gespeichert.

## Implementierte Dateien

- `app/src/main/java/at/ac/hcw/procrastinot/data/Task.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/data/source/local/LocalTask.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/data/source/local/ToDoDatabase.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/data/ModelMappingExt.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/data/TaskRepository.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/data/DefaultTaskRepository.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/addedittask/AddEditTaskViewModel.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/addedittask/AddEditTaskScreen.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/tasks/TasksFilterType.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/tasks/TasksViewModel.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/tasks/TasksScreen.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/util/TopAppBars.kt`
- `app/src/main/res/values/strings.xml`
- `docs/kotlin-beginner-explanations.md`

## Geaenderte Klassen/Funktionen

- `TaskPriority`
- `Task.priority`
- `LocalTask.priority`
- `ToDoDatabase.MIGRATION_1_2`
- `TaskRepository.createTask(..., priority)`
- `TaskRepository.updateTask(..., priority)`
- `AddEditTaskUiState.priority`
- `AddEditTaskViewModel.updatePriority`
- `TasksFilterType.HIGH_PRIORITY_TASKS`
- `TasksFilterType.MEDIUM_PRIORITY_TASKS`
- `TasksFilterType.LOW_PRIORITY_TASKS`
- `TasksViewModel.filterTasks`
- `TasksTopAppBar`

## Technische Umsetzung

1. Im Datenmodell gibt es jetzt `TaskPriority` mit `HIGH`, `MEDIUM` und `LOW`.
2. `Task` enthaelt eine `priority`. Standardwert ist `MEDIUM`, damit bestehende Konstruktoren und neue Tasks einen sicheren Default haben.
3. Room speichert die Prioritaet als `Int` in `LocalTask`. Die Datenbankversion wurde von 1 auf 2 erhoeht.
4. `MIGRATION_1_2` fuegt die Spalte `priority` mit Default `2` hinzu. Dadurch werden bestehende Datenbanken nicht geloescht.
5. Die Mapping-Funktionen wandeln zwischen `TaskPriority` im App-Modell und `Int` in Room/Network um.
6. Das Repository nimmt die Prioritaet beim Erstellen und Bearbeiten entgegen und speichert sie zusammen mit Titel und Beschreibung.
7. Das Add/Edit-ViewModel haelt die ausgewaehlte Prioritaet im `AddEditTaskUiState`.
8. Der Add/Edit-Screen zeigt drei `FilterChip`s fuer High, Medium und Low.
9. Die Tasks-Liste zeigt pro Task einen farbigen Punkt und den Prioritaetsnamen.
10. Das vorhandene Filtermenue wurde um High-, Medium- und Low-Priority-Filter erweitert.

Datenfluss beim Speichern:

```text
Priority-Chip in Compose
-> AddEditTaskViewModel.updatePriority
-> AddEditTaskUiState.priority
-> saveTask()
-> TaskRepository.createTask/updateTask
-> LocalTask.priority in Room
-> Room Flow
-> TasksViewModel
-> TasksScreen zeigt Farbe und Label
```

## Kotlin-Erklaerung

- `enum class`: `TaskPriority` ist ein Enum, weil nur drei feste Werte erlaubt sind.
- `companion object`: `TaskPriority.fromValue(...)` wandelt gespeicherte Zahlen aus Room/Network wieder in ein Enum um.
- Default-Parameter: `priority: TaskPriority = TaskPriority.MEDIUM` sorgt dafuer, dass bestehender Code ohne explizite Prioritaet weiter sinnvoll funktioniert.
- `copy`: Beim Bearbeiten wird ein bestehender `Task` kopiert und die neue Prioritaet eingesetzt.
- `StateFlow`: Die Add/Edit-UI liest `priority` aus dem ViewModel-State und aktualisiert sich automatisch.

## Android-/Compose-Erklaerung

- `FilterChip`: Wird fuer die Prioritaetsauswahl verwendet, weil genau ein Wert aus wenigen Optionen gewaehlt wird.
- `Room Migration`: Da eine neue Spalte in der Datenbank gebraucht wird, wurde die Version erhoeht und eine einfache Migration hinzugefuegt.
- `State Hoisting`: Der Screen zeigt die Auswahl, aber das ViewModel besitzt den Zustand.
- `Recomposition`: Wenn `priority` im State geaendert wird, werden die Chips neu gezeichnet.
- `TopAppBar DropdownMenu`: Das bestehende Filtermenue wurde erweitert, statt eine zweite Filter-Navigation einzubauen.

## Warum diese Loesung?

- Sie folgt der bestehenden Architektur aus `agent.md`: UI -> ViewModel -> Repository -> Room.
- Sie nutzt vorhandene Strukturen wie `TasksFilterType`, `FilteringUiInfo`, `TopAppBars` und Repository-Methoden.
- Sie fuegt keine neuen Libraries und keine Versionsaenderungen hinzu.
- Die Room-Migration ist klein und nachvollziehbar.
- Die UI bleibt einfach: Chips im Formular, farbiger Punkt in der Liste, bestehendes Menue fuer Filter.

## Wichtige Pruefungs-/Professor-Erklaerungen

- Prioritaet ist Teil des Task-Modells, nicht nur ein UI-Zustand.
- Room speichert die Prioritaet als Zahl, das App-Modell arbeitet aber mit einem lesbaren Enum.
- Die Migration schuetzt bestehende Daten, weil sie die Tabelle nicht loescht.
- Das ViewModel ist die Single Source of Truth fuer das Add/Edit-Formular.
- Die Filterung passiert im `TasksViewModel`, nicht in der Composable-Funktion.
