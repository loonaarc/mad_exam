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

# Backlog Item: MAD-03 - Resolve the statistics-screen crash when no tasks exist

## Urspruengliche Beschreibung

A user reported that the app sometimes crashes when navigating to the Statistics screen.

Acceptance criteria:

- The underlying cause of the crash is identified and fixed.
- The fix does not introduce regressions when statistics are shown for existing tasks.

## Zweck

Der Statistics-Screen soll auch dann stabil funktionieren, wenn noch keine Aufgaben in der Datenbank vorhanden sind. Ohne diesen Fix kann die App beim Oeffnen der Statistik abstuerzen, obwohl der Screen eigentlich einen Empty-State anzeigen soll.

## Implementierte Dateien

- `app/src/main/java/at/ac/hcw/procrastinot/statistics/StatisticsUtils.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/statistics/StatisticsViewModel.kt`
- `docs/kotlin-beginner-explanations.md`

## Geaenderte Klassen/Funktionen

- `getActiveAndCompletedStats(tasks)`
- `StatisticsViewModel.produceStatisticsUiState(...)`

## Technische Umsetzung

1. Die Ursache lag in `getActiveAndCompletedStats`.
2. Die Funktion hat vorher `tasks!!` verwendet und danach durch `tasks.size` geteilt.
3. Wenn die Liste leer war, war `tasks.size` gleich `0`. Eine Division durch `0` fuehrt zum Crash.
4. Die Funktion prueft jetzt zuerst `tasks.isNullOrEmpty()`.
5. Fuer `null` oder leere Listen werden `0f` aktive und `0f` erledigte Aufgaben zurueckgegeben.
6. Fuer vorhandene Tasks wird mit `Float`-Division gerechnet, damit Prozentwerte wie `50.0%` korrekt entstehen.
7. Das ViewModel verwendet `taskLoad.data.orEmpty()` und braucht dadurch kein riskantes `!!` mehr.
8. Der vorhandene Empty-State im Compose-Screen bleibt unveraendert und zeigt `statistics_no_tasks`.

Datenfluss:

```text
Room Flow mit leerer Task-Liste
-> StatisticsViewModel
-> getActiveAndCompletedStats(emptyList())
-> StatsResult(0f, 0f)
-> StatisticsUiState(isEmpty = true)
-> StatisticsScreen zeigt Empty-State statt Crash
```

## Kotlin-Erklaerung

- `!!`: Der Not-null-Operator erzwingt, dass ein Wert nicht null sein darf. Wenn er doch null ist, entsteht ein Crash. Deshalb wurde er hier entfernt.
- `isNullOrEmpty()`: Diese Kotlin-Funktion prueft gleichzeitig auf `null` und auf eine leere Liste.
- `orEmpty()`: Wandelt eine nullable Liste in eine normale Liste um. Bei `null` entsteht `emptyList()`.
- `Float`: Prozentwerte brauchen Kommazahlen. Deshalb wird nicht mehr mit reiner Integer-Division gerechnet.
- Early Return: Die Funktion gibt bei leerer Liste sofort `0f/0f` zurueck und fuehrt die Prozentrechnung gar nicht erst aus.

## Android-/Compose-Erklaerung

- Der Crash wurde in der Berechnungsschicht behoben, nicht in der UI versteckt.
- Das ViewModel liefert `isEmpty = true`, wenn keine Tasks vorhanden sind.
- Compose zeigt dann den vorhandenen Empty-State an.
- Die Composable-Funktion muss keine Sonderlogik fuer Division durch Null kennen.
- Das passt zu MVVM: Berechnung im Utility/ViewModel, Anzeige im Composable.

## Warum diese Loesung?

- Sie behebt die echte Ursache: Division durch `0` und riskanter Null-Zugriff.
- Sie ist minimal-invasiv und veraendert keine Navigation, keine Datenbank und kein UI-Layout.
- Sie respektiert `agent.md`, weil Business-/Berechnungslogik nicht in Composables verschoben wurde.
- Bestehende Statistiken fuer vorhandene Tasks funktionieren weiter, jetzt mit korrekter Prozentrechnung.
- Die Loesung ist leicht in einer Pruefung zu erklaeren.

## Wichtige Pruefungs-/Professor-Erklaerungen

- Der Crash entstand durch eine Division durch `0`, wenn keine Tasks vorhanden waren.
- Der Fix prueft leere Listen vor der Prozentrechnung.
- `!!` wurde vermieden, weil es bei unerwartetem `null` einen Crash ausloesen kann.
- Der Empty-State wird weiterhin vom Compose-Screen angezeigt.
- Die Prozentrechnung wurde nicht in die UI verschoben, sondern in der Statistiklogik repariert.

# Backlog Item: MAD-04 - Seed the database with initial tasks

## Urspruengliche Beschreibung

The app currently starts without predefined task data. The database should be seeded with default tasks when the app is used for the first time.

Acceptance criteria:

- The database is populated with initial task data on first app launch.
- Seed data is inserted only once and is not duplicated on later launches.
- Existing user-created tasks are not overwritten or deleted.
- The seeded tasks are visible on the Tasks screen after the app starts.

## Zweck

Dieses Feature sorgt dafuer, dass neue Nutzerinnen und Nutzer beim ersten Start direkt Beispielaufgaben sehen. Die App wirkt dadurch nicht leer, und die vorhandenen Listen-, Filter- und Statistikfunktionen koennen sofort ausprobiert werden.

## Implementierte Dateien

- `app/src/main/java/at/ac/hcw/procrastinot/di/DataModules.kt`
- `docs/kotlin-beginner-explanations.md`

## Geaenderte Klassen/Funktionen

- `DatabaseModule.provideDataBase(...)`
- `DatabaseModule.taskTableIsEmpty(...)`
- `DatabaseModule.seedInitialTasks(...)`
- `InitialTask`
- `INITIAL_TASKS`

## Technische Umsetzung

1. Die Seed-Logik sitzt dort, wo die Room-Datenbank erstellt wird: in `DatabaseModule`.
2. Beim Oeffnen der Datenbank wird ein `RoomDatabase.Callback` ausgefuehrt.
3. `SharedPreferences` speichert mit `initial_tasks_seeded`, ob der Seed-Vorgang schon erledigt wurde.
4. Wenn dieser Wert bereits `true` ist, passiert nichts.
5. Wenn der Wert noch `false` ist, wird geprueft, ob die Tabelle `task` leer ist.
6. Nur bei leerer Tabelle werden drei Default-Tasks eingefuegt.
7. Wenn bereits User-Tasks existieren, wird nichts eingefuegt und nichts geloescht.
8. Danach wird `initial_tasks_seeded` auf `true` gesetzt, damit spaetere App-Starts keine Duplikate erzeugen.
9. Die Inserts laufen in einer Datenbanktransaktion. Entweder werden alle Seed-Tasks eingefuegt oder keiner.

Datenfluss:

```text
App startet
-> Hilt erstellt ToDoDatabase
-> Room oeffnet Tasks.db
-> RoomDatabase.Callback.onOpen
-> SharedPreferences pruefen
-> task-Tabelle zaehlen
-> Default-Tasks einfuegen, falls noch nicht seeded und leer
-> TasksScreen beobachtet Room Flow
-> Seed-Tasks erscheinen in der Liste
```

## Kotlin-Erklaerung

- `private const val`: Wird fuer feste Schluessel wie den SharedPreferences-Namen verwendet.
- `data class InitialTask`: Beschreibt die Seed-Daten klar und typisiert.
- `listOf(...)`: Erzeugt die feste Liste der Default-Tasks.
- `forEach`: Fuehrt fuer jeden Seed-Task ein Insert aus.
- `try/finally`: Stellt sicher, dass `endTransaction()` immer aufgerufen wird.
- `use`: Schliesst den Datenbank-Cursor automatisch nach dem Lesen.

## Android-/Compose-Erklaerung

- `RoomDatabase.Callback`: Erlaubt Code beim Oeffnen der Datenbank auszufuehren.
- `SupportSQLiteDatabase`: Wird hier fuer einfache SQL-Statements beim Seed genutzt.
- `SharedPreferences`: Speichert einen kleinen lokalen Boolean, damit das Seeding nur einmal passiert.
- `Transaction`: Sichert, dass die Seed-Daten konsistent eingefuegt werden.
- Compose musste nicht angepasst werden, weil der `TasksScreen` bereits den Room-Flow beobachtet.

## Warum diese Loesung?

- Sie ist minimal-invasiv und nutzt die vorhandene Room-/Hilt-Struktur.
- Es werden keine neuen Libraries und keine neuen Architektur-Layer eingefuehrt.
- Bestehende User-Daten werden nicht geloescht oder ueberschrieben.
- Seed-Daten werden nicht dupliziert, weil ein persistenter Boolean den Seed-Vorgang sperrt.
- Die Tasks erscheinen automatisch in der bestehenden UI, weil Room bereits per Flow beobachtet wird.

## Wichtige Pruefungs-/Professor-Erklaerungen

- Das Seeding passiert in der Datenbankschicht, nicht im Composable.
- `SharedPreferences` verhindert doppelte Seed-Ausfuehrungen.
- Die Tabelle wird zuerst gezaehlt, damit bestehende User-Tasks nicht ueberschrieben werden.
- Die Transaktion verhindert halb eingefuegte Seed-Daten.
- Die UI musste nicht geaendert werden, weil sie bereits reaktiv auf Room-Daten reagiert.

# Backlog Item: MAD-05 - Update the Statistics screen UI

## Urspruengliche Beschreibung

The current Statistics screen does not match the provided reference design.

Acceptance criteria:

- The Statistics screen layout matches the provided reference mockup.
- The screen works correctly when tasks exist and when no tasks exist.

## Zweck

Der Statistics-Screen soll die aktiven und erledigten Aufgaben nicht mehr als einfache Textzeilen anzeigen. Stattdessen sollen zwei gut sichtbare farbige Bereiche angezeigt werden, damit die Prozentwerte schneller erfasst werden koennen.

## Implementierte Dateien

- `app/src/main/java/at/ac/hcw/procrastinot/statistics/StatisticsScreen.kt`
- `app/src/main/res/values/strings.xml`
- `docs/kotlin-beginner-explanations.md`

## Geaenderte Klassen/Funktionen

- `StatisticsContent(...)`
- `StatisticsCard(...)`
- `StatisticsContentEmptyPreview()`
- `statistics_active_tasks_label`
- `statistics_completed_tasks_label`

## Technische Umsetzung

1. Die bestehende ViewModel-Logik bleibt unveraendert.
2. `StatisticsContent` bekommt weiterhin `activeTasksPercent` und `completedTasksPercent`.
3. Statt zwei einfachen `Text`-Zeilen werden zwei `StatisticsCard`-Bereiche angezeigt.
4. Die erste Karte ist orange und zeigt die aktiven Tasks.
5. Die zweite Karte ist gruen und zeigt die erledigten Tasks.
6. Jede Karte zeigt oben ein kleines Label und darunter die Prozentzahl als groesseren fetten Text.
7. Der Empty-State bleibt erhalten. Wenn keine Tasks existieren, zeigt `LoadingContent` weiter `statistics_no_tasks`.
8. Die Preview fuer den Empty-State ruft jetzt direkt `StatisticsContent` auf, damit sie keine Hilt-ViewModel-Instanz braucht.

Datenfluss:

```text
Room Flow
-> StatisticsViewModel
-> StatisticsUiState(activeTasksPercent, completedTasksPercent, isEmpty)
-> StatisticsScreen
-> StatisticsContent
-> StatisticsCard fuer Active und Completed
```

## Kotlin-Erklaerung

- `@StringRes`: Markiert Parameter, die eine String-Resource-ID erwarten.
- `private val`: Die Farben sind private Konstanten in der Datei und werden nur fuer diesen Screen verwendet.
- `String.format`: `"%.1f%%".format(percent)` formatiert die Prozentzahl mit einer Nachkommastelle.
- Wiederverwendbare Funktion: `StatisticsCard` vermeidet doppelten Code fuer die beiden fast gleichen Statistikbereiche.

## Android-/Compose-Erklaerung

- `Surface`: Zeichnet den farbigen Hintergrund der Statistikbereiche.
- `RoundedCornerShape`: Gibt den Bereichen leicht abgerundete Ecken wie im Mockup.
- `Column`: Ordnet Label und Prozentzahl vertikal an.
- `Arrangement.spacedBy`: Setzt gleichmaessigen Abstand zwischen den beiden Karten.
- `LoadingContent`: Behaelt das bestehende Verhalten fuer Loading und Empty-State.
- Recomposition: Wenn sich die Prozentwerte im State aendern, zeichnet Compose die Karten neu.

## Warum diese Loesung?

- Sie ist UI-fokussiert und laesst Statistikberechnung, Repository und Datenbank unveraendert.
- Sie folgt dem vorhandenen Compose-Aufbau mit `Scaffold`, `StatisticsTopAppBar` und `LoadingContent`.
- Sie verwendet Material-Compose-Bausteine, die bereits im Projekt vorhanden sind.
- Sie bleibt minimal und pruefungsfreundlich: zwei Karten, zwei Labels, keine neue Navigation.
- Sie funktioniert fuer vorhandene Tasks und nutzt den bestehenden Empty-State fuer keine Tasks.

## Wichtige Pruefungs-/Professor-Erklaerungen

- Die ViewModel-Daten wurden nicht veraendert, nur die Darstellung.
- `StatisticsCard` ist eine kleine wiederverwendbare Composable-Funktion.
- Die Farben orientieren sich am Mockup: orange fuer aktive Tasks, gruen fuer completed Tasks.
- Der Empty-State wurde nicht entfernt und bleibt ueber `LoadingContent` erreichbar.
- UI-Logik bleibt im Composable; Berechnungslogik bleibt im ViewModel/Utility.

# Backlog Item: MAD-06 - Fix repeated Snackbar after navigation

## Urspruengliche Beschreibung

When adding a task, the Snackbar correctly shows "Task added". However, after navigating to Statistics and returning to the Tasks screen, the Snackbar is shown again.

Acceptance criteria:

- The "Task added" Snackbar is shown only once after a task is added.
- Navigating away from and back to the Tasks screen does not show the old Snackbar again.
- Existing Snackbar messages for other task actions continue to work correctly.

## Zweck

Dieses Fix behandelt die Snackbar nach dem Hinzufuegen einer Task als einmaliges UI-Ereignis. Die Meldung soll direkt nach dem Speichern sichtbar sein, aber nicht erneut erscheinen, nur weil der Tasks-Screen durch Navigation wiederhergestellt wird.

## Implementierte Dateien

- `app/src/main/java/at/ac/hcw/procrastinot/tasks/TasksViewModel.kt`
- `docs/kotlin-beginner-explanations.md`

## Geaenderte Klassen/Funktionen

- `TasksViewModel.showEditResultMessage(result)`

## Technische Umsetzung

1. Der Add/Edit-Screen navigiert nach dem Speichern zurueck zum Tasks-Screen und gibt einen Ergebniswert als `userMessage`-Navigationsargument mit.
2. `TasksScreen` ruft bei einem Ergebniswert ungleich `0` weiterhin `showEditResultMessage(...)` im ViewModel auf.
3. `TasksViewModel.showEditResultMessage(...)` prueft jetzt zuerst den `SavedStateHandle`.
4. Wenn `USER_MESSAGE_ARG` dort bereits `0` ist, wurde das Navigationsresultat schon verbraucht und die Funktion beendet sich sofort.
5. Wenn das Resultat noch nicht verbraucht wurde, wird wie bisher die passende Snackbar-Message gesetzt.
6. Danach setzt das ViewModel `USER_MESSAGE_ARG` im `SavedStateHandle` auf `0`.
7. Wenn der Tasks-Screen spaeter nach Navigation wiederhergestellt wird, kann der alte Parameter zwar noch in der Composable ankommen, das ViewModel zeigt ihn aber nicht erneut an.
8. Andere Snackbar-Ausloeser wie Checkbox-Aenderungen, Loeschen oder "completed tasks cleared" bleiben unveraendert.

Datenfluss:

```text
AddEditTaskScreen speichert Task
-> Navigation zu Tasks mit ADD_EDIT_RESULT_OK
-> TasksScreen meldet Resultat ans ViewModel
-> TasksViewModel setzt Snackbar-Text
-> TasksViewModel setzt USER_MESSAGE_ARG auf 0
-> Snackbar wird angezeigt
-> Navigation zu Statistics und zurueck
-> altes Resultat wird im ViewModel ignoriert
```

## Kotlin-Erklaerung

- `SavedStateHandle`: Speichert kleine Werte, die zu einem ViewModel und seinem Navigationseintrag gehoeren. Hier wird damit gemerkt, ob das Navigationsresultat schon verbraucht wurde.
- Early Return: `return` beendet die Funktion sofort, wenn kein neues Snackbar-Event mehr verarbeitet werden darf.
- Nullable Zugriff ueber `savedStateHandle[...]`: Der Zugriff kann `null` liefern, deshalb wird nur exakt `0` als "bereits verbraucht" behandelt.
- `when`: Ordnet die bekannten Result-Codes den passenden String-Resources fuer die Snackbar zu.

## Android-/Compose-Erklaerung

- Snackbar als Event: Eine Snackbar ist kein dauerhafter Screen-Zustand, sondern ein einmaliges Ereignis.
- Navigation Arguments: Der Tasks-Screen erhaelt nach Add/Edit/Delete einen Result-Code ueber die Route.
- Restore State: Beim Navigieren zu Statistics und zurueck kann Compose den alten Tasks-Screen-State wiederherstellen.
- ViewModel als Event-Besitzer: Das ViewModel entscheidet, ob ein Navigationsresultat noch neu ist oder bereits verbraucht wurde.
- Recomposition: Selbst wenn Compose neu startet oder neu zusammensetzt, wird ein verbrauchtes Resultat nicht nochmal in eine Snackbar umgewandelt.

## Warum diese Loesung?

- Sie ist minimal-invasiv und aendert weder Navigation noch Screen-Struktur grundlegend.
- Sie passt zur bestehenden Architektur: Compose meldet Events, das ViewModel verwaltet den UI-State.
- Sie vermeidet Business- oder Event-Logik in `MainActivity`.
- Sie repariert die Ursache des wiederholten Events, ohne andere Snackbar-Meldungen zu entfernen.
- Die Loesung bleibt gut pruefungsfreundlich erklaerbar: Navigationsresultat einmal konsumieren, danach auf `0` setzen.

## Wichtige Pruefungs-/Professor-Erklaerungen

- Die Snackbar wurde doppelt angezeigt, weil ein altes Navigationsresultat beim Restore erneut verarbeitet werden konnte.
- Das ViewModel speichert jetzt, dass dieses Resultat bereits konsumiert wurde.
- `SavedStateHandle` ist hier passend, weil das Resultat zum Navigationseintrag des Tasks-Screens gehoert.
- Andere Snackbar-Aktionen verwenden weiterhin den bestehenden `_userMessage`-Flow.
- Die UI bleibt reaktiv, aber einmalige Events werden nicht als dauerhaft gueltige Daten behandelt.

# Backlog Item: MAD-07 - Add swipe-to-delete on the Tasks screen

## Urspruengliche Beschreibung

Users should be able to delete tasks directly from the Tasks screen using a swipe gesture.

Acceptance criteria:

- Users can swipe left a task item on the Tasks screen to delete it.
- The deleted task is removed from the database, not only from the current UI state.
- The Tasks screen updates correctly after deletion.
- A Snackbar message confirms that the task was deleted.
- The implementation handles deleting active and completed tasks correctly.

## Zweck

Dieses Feature macht das Loeschen schneller, weil Benutzerinnen und Benutzer nicht erst in den Detail-Screen wechseln muessen. Die Aufgabe wird direkt aus der Liste per Swipe entfernt und dauerhaft aus Room geloescht.

## Implementierte Dateien

- `app/src/main/java/at/ac/hcw/procrastinot/tasks/TasksViewModel.kt`
- `app/src/main/java/at/ac/hcw/procrastinot/tasks/TasksScreen.kt`
- `docs/kotlin-beginner-explanations.md`

## Geaenderte Klassen/Funktionen

- `TasksViewModel.deleteTask(task)`
- `TasksContent(...)`
- `SwipeToDeleteTaskItem(...)`
- `DeleteTaskBackground()`

## Technische Umsetzung

1. `TasksViewModel` hat eine neue Funktion `deleteTask(task)` bekommen.
2. Diese Funktion ruft das bereits vorhandene `TaskRepository.deleteTask(task.id)` auf.
3. Nach dem Loeschen setzt das ViewModel die bestehende Snackbar-Message `successfully_deleted_task_message`.
4. `TasksContent` bekommt einen neuen Callback `onTaskDelete`.
5. In der `LazyColumn` wird jedes Listenelement mit `SwipeToDeleteTaskItem` umschlossen.
6. `SwipeToDeleteTaskItem` nutzt `SwipeToDismissBox` aus Material 3.
7. Nur die Richtung `EndToStart` ist aktiviert. Bei Links-Swipe wird `onDelete()` ausgefuehrt.
8. `DeleteTaskBackground` zeigt beim Wischen einen roten Hintergrund mit Delete-Icon.
9. Die `LazyColumn` nutzt `key = { it.id }`, damit Compose beim Entfernen eines Elements stabile Listeneintraege hat.
10. Room sendet nach dem Delete automatisch eine neue Task-Liste, und der Tasks-Screen zeichnet sich neu.

Datenfluss:

```text
Links-Swipe auf Task-Zeile
-> SwipeToDismissBox bestaetigt EndToStart
-> TasksViewModel.deleteTask(task)
-> TaskRepository.deleteTask(task.id)
-> TaskDao.deleteById(taskId)
-> Room entfernt Task aus DB
-> Room Flow sendet neue Liste
-> TasksViewModel uiState aktualisiert sich
-> TasksScreen zeigt Liste ohne geloeschte Task
-> Snackbar "Task was deleted"
```

## Kotlin-Erklaerung

- Lambda Callback: `onTaskDelete: (Task) -> Unit` erlaubt der UI, ein Event nach oben ans ViewModel zu melden.
- `viewModelScope.launch`: Das Loeschen ist ein suspendierender Datenbankzugriff und wird deshalb in einer Coroutine gestartet.
- `key = { it.id }`: Gibt jedem Listeneintrag eine stabile Identitaet. Das hilft Compose, Animationen und State richtig dem passenden Task zuzuordnen.
- `when` wurde hier nicht gebraucht, weil nur eine Swipe-Richtung geloescht wird.

## Android-/Compose-Erklaerung

- `SwipeToDismissBox`: Material-Compose-Komponente fuer Wischgesten auf Listenelementen.
- `rememberSwipeToDismissBoxState`: Speichert den Swipe-Zustand fuer ein einzelnes Listenelement.
- `confirmValueChange`: Entscheidet, ob ein Swipe wirklich akzeptiert wird. Hier wird nur `EndToStart` als Delete behandelt.
- `LazyColumn`: Zeigt die Task-Liste effizient an und rendert nur sichtbare Eintraege.
- `Snackbar`: Die Bestaetigung wird wie bisher ueber den `userMessage`-State des ViewModels angezeigt.
- Recomposition: Nach dem Loeschen aktualisiert Room den Flow, wodurch Compose automatisch die kuerzere Liste rendert.

## Warum diese Loesung?

- Sie nutzt die bereits vorhandene Delete-Funktion im Repository und im DAO.
- Sie fuegt keine neue Library und keinen neuen Architektur-Layer hinzu.
- Business-Logik bleibt im ViewModel, Datenzugriff bleibt im Repository.
- Die Composable kennt nur das UI-Event "delete", aber nicht die Room-Details.
- Aktive und erledigte Tasks werden gleich behandelt, weil beide ueber ihre ID geloescht werden.

## Wichtige Pruefungs-/Professor-Erklaerungen

- Der Swipe entfernt die Task nicht nur optisch, sondern loescht sie ueber Repository und Room.
- Die Liste aktualisiert sich automatisch, weil der Tasks-Screen den Room-Flow beobachtet.
- `SwipeToDismissBox` ist eine passende Compose-Komponente fuer diese UI-Geste.
- Die Snackbar wird im ViewModel ausgeloest, nicht direkt in der Task-Zeile.
- `key = { it.id }` verhindert, dass Compose Swipe-State versehentlich falschen Listenelementen zuordnet.
