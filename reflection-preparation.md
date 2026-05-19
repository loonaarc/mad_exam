# Reflection Preparation — Procrastinot

> Mock Oral Exam Vorbereitung für technische Reflexionsgespräche
> Ziel: Implementierung nicht nur gebaut haben, sondern sie überzeugend technisch verteidigen können.
> Ergänzt: `architecture.md` | Projekt: MAD Practical

---

## Leitprinzipien für die Prüfung

**Was bewertet wird:**
- Hast du verstanden *warum* Entscheidungen so getroffen wurden — nicht nur *was* du gebaut hast?
- Kannst du Trade-offs benennen und verteidigen?
- Hast du AI kritisch eingesetzt oder blind kopiert?
- Erkennst du Schwächen in deiner eigenen Implementierung?

**Was immer gut ankommt:**
- "Wir haben X gewählt, Alternative wäre Y gewesen. X hat den Vorteil … aber den Nachteil …"
- "In der Praxis würde ich … anders machen, weil …"
- "AI hat … vorgeschlagen, wir haben es aber angepasst weil …"

---

# TEIL 1: AI-ASSISTED DEVELOPMENT

---

## Frage 1.1

**Wie habt ihr AI bei der Entwicklung eingesetzt?**

### Kurzantwort

AI wurde als Werkzeug für konkrete Teilprobleme eingesetzt — Code-Generierung für repetitive Muster, Debugging-Unterstützung, und Architekturvorschläge. Jeder generierte Code wurde verstanden, validiert und ggf. angepasst bevor er integriert wurde.

### Detaillierte Antwort

AI war in mehreren Bereichen hilfreich:

1. **Boilerplate-Reduktion**: Hilt-Module, Room-DAOs und ModelMapping-Funktionen haben repetitive Strukturen. AI kann diese schnell generieren — es wäre ineffizient, diese manuell zu tippen wenn das Muster klar ist.

2. **Architekturberatung**: "Wie strukturiere ich ein Repository das sowohl Room als auch eine Mock-Network-Source kombiniert?" — AI kann etablierte Patterns vorschlagen (Single Source of Truth, offline-first).

3. **Debugging**: Bei Compile-Fehler in KSP-generierten Hilt-Bindings ist AI hilfreich um die Fehlermeldungen zu interpretieren.

**Wichtige Einschränkung:** AI-Output wurde nie blind übernommen. Konkret:
- Vorgeschlagener Code wurde verstanden (warum macht er das?) bevor er eingefügt wurde
- Der Build musste erfolgreich sein
- Vorhandene Tests mussten grün bleiben
- Konsistenz mit der bestehenden Architektur wurde geprüft

### Projektbezug

- `di/DataModules.kt` — Hilt-Modul-Struktur folgt AI-vorgeschlagenem Pattern, wurde aber angepasst (Seed-Logik in `DatabaseModule` wurde eigenständig ergänzt)
- `data/ModelMappingExt.kt` — Extension-Functions-Muster wurde über AI-Vorschlag verstanden und dann eigenständig für Priority-Mapping erweitert
- Swipe-to-Delete: AI schlug `SwipeToDismiss` (altes API) vor, wir haben auf `SwipeToDismissBox` (aktuelles Material3-API) gewechselt

### Mögliche Follow-up Fragen

- "Welchen konkreten Code hat AI vorgeschlagen, den ihr NICHT verwendet habt?"
- "Woran hast du erkannt dass ein AI-Vorschlag schlecht war?"
- "Wie habt ihr sichergestellt, dass ihr den generierten Code wirklich versteht?"

### Typische schlechte Antworten

- "AI hat den Code geschrieben, ich hab ihn eingefügt." — Kein Verständnis demonstriert
- "Wir haben AI nur für Kommentare verwendet." — Klingt wie Ausweichen
- "AI ist immer korrekt bei etablierten Patterns." — Kritikloses Vertrauen

### Wichtige Begriffe

AI-assisted development, Code-Validierung, Architekturkonsistenz, kritisches Review, Boilerplate-Generierung

---

## Frage 1.2

**Kannst du ein Beispiel nennen wo AI einen Fehler gemacht oder einen suboptimalen Vorschlag gemacht hat?**

### Kurzantwort

Ein konkretes Beispiel: AI schlug veraltetes API vor (`SwipeToDismiss` statt `SwipeToDismissBox`), was erst beim Studium der aktuellen Dokumentation aufgefallen ist. Generell tendiert AI dazu, veraltete Lösungen vorzuschlagen wenn es viele Trainingsdaten für die alte API gibt.

### Detaillierte Antwort

AI-Modelle haben einen Trainingsdaten-Cutoff. Bei sich schnell ändernden APIs wie Jetpack Compose (Material3) kann das problematisch sein:

**Problem 1 — Veraltete APIs:**
Compose Material3 hat `SwipeToDismiss` durch `SwipeToDismissBox` ersetzt. AI schlägt oft die ältere Version vor weil sie in mehr Trainingsdaten vorkommt. Das führt zu Deprecation-Warnings oder Compile-Fehlern.

**Problem 2 — Überengineering:**
AI neigt dazu mehr Abstraktionsschichten vorzuschlagen als nötig. Für ein Uni-Projekt ist ein einfaches Repository ohne zusätzliche Use-Case-Klassen oft besser als "Clean Architecture mit separaten Interactors" die AI manchmal vorschlägt.

**Problem 3 — Fehlende Projektkontextkenntnis:**
AI kennt die bestehende Architektur nicht. Vorgeschlagener Code kann Inkonsistenz in Naming, Error-Handling oder Scope-Verwendung haben.

**Validierungsstrategie:**
- Offizielle Android-Dokumentation als Primärquelle
- Build-Erfolg als Mindestanforderung
- Code-Review: "Passt das zu unserem bisherigen Stil?"

### Projektbezug

- `tasks/TasksScreen.kt` — SwipeToDismissBox-Migration
- Allgemein: Hilt-Annotations werden oft mit veralteter Syntax vorgeschlagen (z.B. `@AndroidEntryPoint` auf falscher Klasse)

### Mögliche Follow-up Fragen

- "Wie hast du festgestellt, dass die API veraltet war?"
- "Was wäre passiert wenn ihr den fehlerhaften Code verwendet hättet?"
- "Welche anderen Fehlerquellen gibt es bei AI-generiertem Android-Code?"

### Wichtige Begriffe

API-Deprecation, Trainingsdaten-Cutoff, Code-Review, Offizielle Dokumentation, Versionskonflikte

---

## Frage 1.3

**Wie unterscheidet sich AI-assisted Development von Copy-Paste aus Stack Overflow?**

### Kurzantwort

Konzeptuell ähnlich — beides sind externe Quellen die verstanden werden müssen. Der Unterschied: AI liefert kontextualisierteren Code (kann auf die Frage eingehen), ist aber weniger zuverlässig verifiziert als hochgewertete Stack-Overflow-Antworten.

### Detaillierte Antwort

**Gemeinsamkeiten:**
- Beide Quellen liefern Code der nicht blind übernommen werden darf
- Beide erfordern Verständnis vor Integration
- Beide können veraltet oder falsch sein

**Unterschiede:**

| Aspekt | Stack Overflow | AI |
|---|---|---|
| Verifikation | Community-upvotes, Kommentare | Keine externe Verifikation |
| Aktualität | Datiert, man sieht das Datum | Unklar, kein Datum |
| Kontextanpassung | Minimal (Copy-Paste nötig) | Kann auf spezifische Frage eingehen |
| Fehlerhaftigkeit | Explizite Korrektur-Kommentare sichtbar | Fehler nicht immer offensichtlich |
| Nutzbarkeit | Direkt copy-pastebar | Braucht oft Anpassung |

**Kernaussage:** Beide Quellen sind legitime Werkzeuge. Der Unterschied liegt in der Verifikationsstrategie: Bei Stack Overflow prüft man Votes und Kommentare. Bei AI prüft man durch Build-Erfolg, Tests und eigenes Verständnis.

### Wichtige Begriffe

Code-Verifikation, externe Quellen, Kontextualisierung, Peer-Review vs. AI-Vertrauen

---

# TEIL 2: ARCHITEKTURENTSCHEIDUNGEN

---

## Frage 2.1

**Warum MVVM und nicht MVC oder MVP?**

### Kurzantwort

MVVM passt am besten zu Jetpack Compose und Android-Lifecycles. ViewModels überleben Konfigurationsänderungen nativ, und StateFlow integriert sich direkt in Compose. MVC hat kein klares UI-State-Konzept, MVP erfordert View-Interfaces die in Compose unnötig sind.

### Detaillierte Antwort

**MVC (Model-View-Controller):**
- Der Controller ist direkt mit View-Klassen gekoppelt — in Android oft die Activity
- Problem: Activity hat zu viele Verantwortlichkeiten (Lifecycle, Navigation, UI, Logik)
- Kein nativer State-Überleben bei Rotation

**MVP (Model-View-Presenter):**
- Presenter kommuniziert mit View via Interface
- In Compose unnötig komplex: Composables sind Funktionen, keine Klassen — kein sinnvolles Interface
- Presenter muss manuell Lifecycle beachten

**MVVM:**
- ViewModel ist Lifecycle-aware by design (`ViewModel`-Basisklasse überlebt Rotation)
- StateFlow passt zu Compose: ein State-Objekt das Composables direkt konsumieren können
- Klare Trennung: Composable = View, ViewModel = State + Logic, Repository = Data
- Unidirectional Data Flow gut durchsetzbar

**Trade-off:** MVVM kann bei falscher Implementierung zu aufgeblähten ViewModels führen (alle Logik im ViewModel). Dagegen hilft das Repository Pattern das Business-Logik nach unten zieht.

### Projektbezug

- `TasksViewModel.kt` — StateFlow-basierter State, `viewModelScope` für Lifecycle-Awareness
- `MainActivity.kt` — `@AndroidEntryPoint` + `setContent { TodoNavGraph() }` — minimale Activity-Logik

### Mögliche Follow-up Fragen

- "Welche Probleme könnte ein zu fettes ViewModel verursachen?"
- "Was ist der Unterschied zwischen ViewModel und Presenter Lifecycle-technisch?"
- "Würdest du heute noch MVVM wählen oder gibt es bessere Alternativen?"

### Typische schlechte Antworten

- "MVVM ist moderner." — Kein Argument
- "Google empfiehlt es." — Kein technisches Verständnis
- "MVP ist schwieriger." — Keine Begründung

### Wichtige Begriffe

Lifecycle-awareness, Separation of Concerns, StateFlow, Konfigurationsänderungen, Rotation-Safety

---

## Frage 2.2

**Warum wurde das Repository Pattern verwendet?**

### Kurzantwort

Das Repository abstrahiert Datenquellen. ViewModels wissen nicht ob Daten aus Room oder dem Netzwerk kommen. Das ermöglicht Testbarkeit (Fake-Repository), Austauschbarkeit (echte API statt Mock) und zentralisierte Synchronisierungslogik.

### Detaillierte Antwort

**Ohne Repository (schlechte Alternative):**
```kotlin
// ViewModel greift direkt auf DAO zu — schlecht!
class TasksViewModel(private val dao: TaskDao) : ViewModel() {
    val tasks = dao.observeAll() // ViewModel kennt Room direkt
    // Was wenn wir Netzwerk hinzufügen? Überall ändern!
}
```

**Mit Repository (gut):**
```kotlin
class TasksViewModel(private val repo: TaskRepository) : ViewModel() {
    val tasks = repo.getTasksStream() // ViewModel weiß nicht wie
    // Netzwerk hinzufügen? Nur DefaultTaskRepository ändern!
}
```

**Konkrete Vorteile im Projekt:**

1. **Testbarkeit**: In Tests kann `FakeTaskRepository` statt `DefaultTaskRepository` injiziert werden — kein echtes Room nötig
2. **Single Source of Truth**: Room ist die primäre Datenquelle. Das Netzwerk aktualisiert Room, Room emittiert Flow. Kein direktes Netzwerk → UI
3. **Netzwerk-Sync isoliert**: `refresh()` lädt vom Netzwerk, ersetzt lokale Daten — diese Logik ist nur in `DefaultTaskRepository`, nicht verteilt
4. **Interface-Trennung**: `TaskRepository` (Interface) vs. `DefaultTaskRepository` (Impl) — das Interface definiert den Vertrag, die Implementierung kann ausgetauscht werden

**Trade-offs:**
- Mehr Boilerplate (Interface + Implementierung)
- Eine zusätzliche Abstraktionsschicht bedeutet mehr Klassen
- Für sehr kleine Apps eventuell Overengineering — für dieses Projekt aber gerechtfertigt wegen der zwei Datenquellen (Room + Network)

### Projektbezug

- `data/TaskRepository.kt` — Interface mit 9 Methoden
- `data/DefaultTaskRepository.kt` — Kombiniert `TaskDao` und `NetworkDataSource`
- `di/DataModules.kt` — `RepositoryModule`: Hilt bindet Interface → Implementierung

### Mögliche Follow-up Fragen

- "Was wäre der Aufwand die Mock-Netzwerkimplementierung durch echtes Retrofit zu ersetzen?"
- "Wie würdest du das Repository in Unit Tests testen?"
- "Was ist der Unterschied zwischen Single Source of Truth und Cache-first?"

### Wichtige Begriffe

Single Source of Truth, Abstraktionsschicht, Testbarkeit, Interface vs. Implementierung, Offline-first

---

## Frage 2.3

**Warum gibt es drei verschiedene Datenmodelle (Task, LocalTask, NetworkTask)?**

### Kurzantwort

Weil jede Schicht andere Anforderungen hat: Room braucht `@Entity`-Annotationen, das Netzwerk hat andere Feldnamen (z.B. `TaskStatus` statt `isCompleted`), und das Domain-Modell soll sauber und unabhängig von Persistenz-Details sein.

### Detaillierte Antwort

**Problem ohne Trennung:** Man könnte versucht sein, eine einzige Klasse für alles zu verwenden:

```kotlin
// Schlechte Alternative: alles in einer Klasse
@Entity(tableName = "task")
data class Task(
    @PrimaryKey val id: String,
    val title: String,
    @SerializedName("task_status") val status: String, // Netzwerk-spezifisch!
    val isCompleted: Boolean,
    @ColumnInfo(name = "is_completed") val dbCompleted: Boolean // Room-spezifisch!
)
```
Das ist inkonsistent, schwer lesbar und führt zu Konflikten wenn Room und Netzwerk unterschiedliche Repräsentationen brauchen.

**Lösung mit drei Modellen:**

```
LocalTask (@Entity) — für Room
- priority: Int (Room versteht kein Enum direkt)
- isCompleted: Boolean

NetworkTask (DTO) — für Netzwerk
- status: TaskStatus (ACTIVE/COMPLETE) — Netzwerk-Konvention
- priority: Int? (optional im Netzwerk)

Task (Domain) — für ViewModel/UI
- priority: TaskPriority (HIGH/MEDIUM/LOW) — typsicher
- isCompleted: Boolean — intuitiv
```

**Konvertierung in `ModelMappingExt.kt`:**
- `LocalTask.toExternal()` → `Task`: konvertiert `Int` zu `TaskPriority`
- `NetworkTask.toExternal()` → `Task`: konvertiert `TaskStatus.ACTIVE` zu `isCompleted = false`
- `Task.toLocal()` → `LocalTask`: konvertiert `TaskPriority` zu `Int`

**Trade-off:** Mehr Klassen, mehr Konvertierungscode. Aber: Die Domain-Klasse `Task` ist stabil — Änderungen an der DB-Struktur oder am Netzwerk-API betreffen nur die jeweiligen Modelle und `ModelMappingExt.kt`, nicht die ViewModels.

### Projektbezug

- `data/Task.kt` — Domain-Modell, `TaskPriority` Enum (HIGH=1, MEDIUM=2, LOW=3)
- `data/source/local/LocalTask.kt` — `@Entity`, `priority: Int`
- `data/source/network/NetworkTask.kt` — `TaskStatus` Enum (ACTIVE/COMPLETE)
- `data/ModelMappingExt.kt` — alle Konvertierungsfunktionen

### Mögliche Follow-up Fragen

- "Was passiert wenn du im Netzwerk ein neues Feld hinzufügst — wo musst du Änderungen vornehmen?"
- "Warum speichert Room Priority als Int statt als Enum?"
- "Wie behandelt ModelMappingExt.kt einen unbekannten Priority-Wert aus dem Netzwerk?"

### Wichtige Begriffe

Domain Model, DTO (Data Transfer Object), Entity, Separation of Concerns, Modell-Mapping, Anti-Corruption Layer

---

## Frage 2.4

**Warum wurde Hilt für Dependency Injection gewählt und nicht Koin oder manuelles DI?**

### Kurzantwort

Hilt validiert alle Abhängigkeiten zur **Compile-Zeit** — Fehler wie fehlende Bindings werden beim Build entdeckt, nicht erst zur Laufzeit. Es ist die offiziell von Google empfohlene Lösung und integriert sich nativ mit Android-Komponenten (ViewModel, Activity).

### Detaillierte Antwort

**Manuelles DI:**
```kotlin
// App-Klasse erstellt alle Abhängigkeiten manuell
class MainApplication : Application() {
    val database = Room.databaseBuilder(...).build()
    val dao = database.taskDao()
    val networkSource = TaskNetworkDataSource()
    val repository = DefaultTaskRepository(networkSource, dao, ...)
    // ViewModelFactory nötig für ViewModel-Erstellung — viel Boilerplate!
}
```
Wird schnell unhandhabbar, kein automatisches Scope-Management.

**Koin:**
- Laufzeit-DI (DSL-basiert)
- Einfachere Syntax
- Aber: Fehler erst zur Laufzeit (`NoBeanDefinitionFoundException`) — schwerer zu debuggen
- Weniger in Android-Ökosystem integriert (kein nativer `@HiltViewModel`-Support)

**Hilt:**
- Compile-Zeit-Validierung: Fehlendes Binding → Build-Fehler → sofort sichtbar
- `@HiltViewModel` — ViewModel-Erstellung ohne Factory-Boilerplate
- `@Singleton`, `@ActivityRetainedScoped` — Scopes passend zu Android-Lifecycle
- KSP-basierte Code-Generierung → schnellere Builds als KAPT

**Trade-off:** Hilt ist komplexer in der Einrichtung (Annotationen, Module, mehr Klassen). Für kleine Apps ist Koin oft schneller aufzusetzen. Für ein Projekt mit mehreren Scopes und Android-Lifecycle-Integration ist Hilt die robustere Wahl.

### Projektbezug

- `di/DataModules.kt` — 3 Module, alle `@InstallIn(SingletonComponent::class)`
- `di/CoroutinesModule.kt` — Custom Qualifier (`@IoDispatcher`, `@ApplicationScope`)
- `MainApplication.kt` — `@HiltAndroidApp`
- `MainActivity.kt` — `@AndroidEntryPoint`
- Alle ViewModels: `@HiltViewModel` + `@Inject constructor`

### Mögliche Follow-up Fragen

- "Was ist der Unterschied zwischen `@Binds` und `@Provides` in einem Hilt-Modul?"
- "Was passiert wenn Hilt eine zirkuläre Abhängigkeit erkennt?"
- "Welchen Scope hätte ein DAO typischerweise?"

### Wichtige Begriffe

Compile-Time vs. Runtime Dependency Resolution, `@Binds` vs. `@Provides`, Scoping, `SingletonComponent`, KSP

---

# TEIL 3: STATE MANAGEMENT

---

## Frage 3.1

**Warum StateFlow statt LiveData?**

### Kurzantwort

StateFlow integriert sich besser in Kotlin Coroutines und Compose. LiveData ist Android-spezifisch und braucht `Observer`-Pattern. StateFlow ist ein standard Kotlin-Typ der auch außerhalb von Android nutzbar ist (bessere Testbarkeit). Im Compose-Kontext funktioniert `collectAsStateWithLifecycle()` direkt.

### Detaillierte Antwort

**LiveData (ältere Alternative):**
- Nur im Android-Framework verfügbar (nicht in reinem Kotlin-Code testbar)
- `Observer` Interface — weniger idiomatisch in Kotlin
- Kein Back-Pressure-Support
- `observeForever()` vs. `observe()` muss manuell verwaltet werden

**StateFlow (aktuell):**
```kotlin
// StateFlow — Kotlin-nativ, direkt in Compose nutzbar
val uiState: StateFlow<TasksUiState> = ...

// Composable:
val state by viewModel.uiState.collectAsStateWithLifecycle()
// Lifecycle-aware, stoppt wenn App im Hintergrund
```

**Vorteile:**
1. **Kotlin-nativ**: Testbar in reinen JVM-Tests ohne Android-Framework
2. **Compose-Integration**: `collectAsStateWithLifecycle()` ist idiomatisch
3. **Immer ein Wert**: StateFlow hat immer `value` — kein "null before first emit" Problem
4. **Coroutine-Kompatibilität**: Flow-Operatoren (`map`, `combine`, `filter`) direkt nutzbar

**Risiko mit StateFlow:** Wenn nicht lifecycle-aware (`collectAsState()` statt `collectAsStateWithLifecycle()`), kann der Flow auch im Hintergrund laufen → Akku-/Performance-Problem. Im Projekt korrekt mit `collectAsStateWithLifecycle()` gelöst.

### Projektbezug

- `tasks/TasksViewModel.kt` — `combine()` + `stateIn(WhileUiSubscribed)`
- `util/CoroutinesUtils.kt` — `WhileUiSubscribed = SharingStarted.WhileSubscribed(5000)`
- `tasks/TasksScreen.kt` — `collectAsStateWithLifecycle()`

### Mögliche Follow-up Fragen

- "Was ist der Unterschied zwischen `stateIn` und `shareIn`?"
- "Warum 5000ms in WhileUiSubscribed — warum nicht 0 oder unendlich?"
- "Was wäre die Konsequenz von `collectAsState()` statt `collectAsStateWithLifecycle()`?"

### Wichtige Begriffe

StateFlow, LiveData, Lifecycle-awareness, `collectAsStateWithLifecycle`, `WhileSubscribed`, Back-Pressure

---

## Frage 3.2

**Was ist der Unterschied zwischen `MutableStateFlow` und `combine()` + `stateIn()`?**

### Kurzantwort

`MutableStateFlow` ist für direktes Setzen von Werten (Formularfelder). `combine()` + `stateIn()` ist für abgeleiteten State der aus mehreren Quellen berechnet wird (z.B. gefilterte Liste aus Datenbankstream + Filtertyp).

### Detaillierte Antwort

**`MutableStateFlow` — direktes Schreiben:**

```kotlin
// AddEditTaskViewModel.kt
private val _title = MutableStateFlow("")

fun updateTitle(newTitle: String) {
    _title.value = newTitle  // direktes Schreiben
}
```
Verwendet wenn der State einfach gesetzt wird und nicht aus anderen Flows abgeleitet ist.

**`combine()` + `stateIn()` — abgeleiteter State:**

```kotlin
// TasksViewModel.kt
val uiState: StateFlow<TasksUiState> = combine(
    _savedFilterType,               // MutableStateFlow<FilterType>
    taskRepository.getTasksStream() // Flow<List<Task>> aus Room
) { filterType, tasks ->
    // Berechne neuen State aus beiden Quellen
    TasksUiState(items = filterTasks(tasks, filterType), ...)
}.stateIn(
    scope = viewModelScope,
    started = WhileUiSubscribed,
    initialValue = TasksUiState(isLoading = true)
)
```

`combine()` emittiert immer wenn **einer** der Quell-Flows emittiert. So aktualisiert sich die gefilterte Liste sowohl wenn sich die DB ändert (neue Task) als auch wenn der Filtertyp wechselt — ohne neuen DB-Query!

**Warum das clever ist:** Kein zusätzlicher DB-Call beim Filtern. Die vollständige Task-Liste kommt aus Room, die Filterung passiert im ViewModel. Das spart DB-Queries und ist performanter.

### Projektbezug

- `addedittask/AddEditTaskViewModel.kt` — MutableStateFlow für Formularfelder
- `tasks/TasksViewModel.kt` — combine() für abgeleiteten State
- `statistics/StatisticsViewModel.kt` — map() für einfache Transformation

### Mögliche Follow-up Fragen

- "Was ist der Unterschied zwischen `combine()` und `flatMapLatest()`?"
- "Wann würdest du `merge()` statt `combine()` verwenden?"
- "Warum braucht `stateIn()` einen `initialValue`?"

### Wichtige Begriffe

`MutableStateFlow`, `combine()`, `stateIn()`, reaktive Programmierung, abgeleiteter State, Flow-Operatoren

---

## Frage 3.3

**Was ist State Hoisting und warum ist es wichtig?**

### Kurzantwort

State Hoisting bedeutet: State nach oben bewegen, näher zu dem Ort wo er gebraucht wird. In Compose: State liegt im ViewModel, nicht im Composable. Das macht Composables zustandslos (stateless), wiederverwendbar und testbar.

### Detaillierte Antwort

**Ohne State Hoisting (schlecht):**
```kotlin
@Composable
fun TasksScreen() {
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    // Problem: State ist im Composable — nicht testbar, nicht teilbar
    LaunchedEffect(Unit) {
        tasks = repository.getTasks() // falsch! Direkte Abhängigkeit
    }
}
```

**Mit State Hoisting (gut):**
```kotlin
// ViewModel hält State (gehoistet)
class TasksViewModel : ViewModel() {
    val uiState: StateFlow<TasksUiState> = ...
}

// Composable ist zustandslos — empfängt State als Parameter
@Composable
fun TasksScreen(
    uiState: TasksUiState,           // State kommt von oben
    onDeleteTask: (String) -> Unit    // Event geht nach oben
) {
    // Keine eigene State-Verwaltung!
}
```

**Vorteile:**
1. **Testbarkeit**: `TasksScreen` kann mit beliebigen `TasksUiState`-Instanzen getestet werden
2. **Wiederverwendbarkeit**: Composable kann mit verschiedenen State-Quellen verwendet werden
3. **Single Source of Truth**: State ist an einem Ort (ViewModel), nicht verteilt
4. **Preview-fähig**: `@Preview` kann direkt fiktiven State übergeben

### Projektbezug

Alle Screens: `TasksScreen(uiState: TasksUiState, ...)`, `AddEditTaskScreen(uiState: AddEditTaskUiState, ...)`

### Mögliche Follow-up Fragen

- "Wie weit nach oben sollte State gehoistet werden?"
- "Was ist der Unterschied zwischen Stateful und Stateless Composable?"
- "Wann ist es okay State im Composable zu behalten?"

### Wichtige Begriffe

State Hoisting, Stateless Composable, Single Source of Truth, Testbarkeit, `@Preview`

---

# TEIL 4: DATENBANK & ROOM

---

## Frage 4.1

**Wie funktioniert Room in diesem Projekt und warum wurde Room gewählt?**

### Kurzantwort

Room ist eine SQLite-Abstraktion die typsichere Queries, automatische Flow-Unterstützung und Compile-Zeit-Validierung von SQL bietet. Es wurde gewählt weil es direkt mit Kotlin Coroutines und Flow integriert — kein manuelles Threading nötig.

### Detaillierte Antwort

**Room-Komponenten im Projekt:**

1. **`LocalTask.kt`** — `@Entity`: Definiert die Tabelle
```kotlin
@Entity(tableName = "task")
data class LocalTask(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val priority: Int = DEFAULT_TASK_PRIORITY // = 2 (MEDIUM)
)
```

2. **`TaskDao.kt`** — `@Dao`: Typsichere SQL-Abfragen
```kotlin
@Dao
interface TaskDao {
    @Query("SELECT * FROM task")
    fun observeAll(): Flow<List<LocalTask>>   // reaktiv!

    @Upsert
    suspend fun upsert(task: LocalTask)        // INSERT OR REPLACE

    @Query("DELETE FROM task WHERE id = :taskId")
    suspend fun deleteById(taskId: String)
}
```

3. **`ToDoDatabase.kt`** — `@Database`: Container
```kotlin
@Database(entities = [LocalTask::class], version = 2)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
```

**Warum Flow in DAO?**
`observeAll(): Flow<List<LocalTask>>` ist das Herzstück des reaktiven Ansatzes. Room beobachtet die Tabelle und emittiert automatisch neue Daten wenn sich etwas ändert — kein Polling nötig.

**Trade-offs:**
- Room erfordert `@Entity`-Annotationen → separates Modell nötig (s. Frage 2.3)
- Schema-Änderungen erfordern Migrations — Fehler bei fehlender Migration crasht die App
- KSP-Code-Generierung verlangsamt den ersten Build

### Projektbezug

- `data/source/local/LocalTask.kt` — Entity
- `data/source/local/TaskDao.kt` — 9 Methoden (observe, get, upsert, delete)
- `data/source/local/ToDoDatabase.kt` — Version 2 + MIGRATION_1_2

### Mögliche Follow-up Fragen

- "Was passiert wenn du die Datenbankversion erhöhst ohne eine Migration zu schreiben?"
- "Was ist der Unterschied zwischen `@Insert` und `@Upsert`?"
- "Warum ist `observeAll()` ein `Flow` aber `getAll()` ein `suspend`?"

### Wichtige Begriffe

Room, DAO, Entity, Flow-reaktiv, `@Upsert`, Migration, KSP, SQLite

---

## Frage 4.2

**Erkläre die Room-Migration in diesem Projekt.**

### Kurzantwort

Migration 1→2 fügt die `priority`-Spalte zur `task`-Tabelle hinzu. Ohne diese Migration würde Room beim App-Start crashen weil das Schema der DB (Version 1) nicht mit dem Entity-Code (Version 2) übereinstimmt.

### Detaillierte Antwort

**Warum Migration?**
Room verwaltet ein Schema-Fingerprint. Wenn die Datenbankversion nicht übereinstimmt, schlägt Room mit `IllegalStateException` fehl. Migration gibt Room die SQL-Anweisung um alte Datenbanken auf den neuen Stand zu bringen.

**`MIGRATION_1_2` im Projekt:**
```kotlin
// ToDoDatabase.kt
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE task ADD COLUMN priority INTEGER NOT NULL DEFAULT 2"
        )
        // DEFAULT 2 = MEDIUM Priority für bestehende Tasks
    }
}
```

**Was passiert ohne Migration?**
```
App startet → Room liest DB-Version (1) → vergleicht mit Code-Version (2)
→ Kein Migration-Pfad → DatabaseException → App-Crash
```

**Alternative: fallbackToDestructiveMigration()**
```kotlin
Room.databaseBuilder(...)
    .fallbackToDestructiveMigration() // löscht und neu erstellt bei Versions-Mismatch
```
Das wäre für Produktion inakzeptabel (Datenverlust!), in der Entwicklung manchmal praktisch.

**Trade-off:** Migrations sind obligatorisch für Production-Apps. Das Schreiben korrekter SQL-Migrations erfordert Sorgfalt — falsches SQL kann zu Datenverlust oder korrupter DB führen. Room-Test-Bibliothek (`room-testing`) ermöglicht Migration-Tests.

### Projektbezug

- `data/source/local/ToDoDatabase.kt` — `MIGRATION_1_2`, version=2
- `di/DataModules.kt` — `.addMigrations(MIGRATION_1_2)` im Builder

### Mögliche Follow-up Fragen

- "Was ist der Unterschied zwischen `Migration` und `AutoMigration` in Room?"
- "Wie testet man Migrations?"
- "Was passiert wenn Nutzer Version 1 überspringen und direkt zu Version 3 updaten?"

### Wichtige Begriffe

Room Migration, Schema-Fingerprint, `ALTER TABLE`, `fallbackToDestructiveMigration`, `AutoMigration`

---

# TEIL 5: DATENFLUSS & ARCHITEKTUR

---

## Frage 5.1

**Erkläre den vollständigen Datenfluss vom Tippen eines Task-Titels bis zum Speichern in der Datenbank.**

### Kurzantwort

User tippt → Composable ruft `viewModel.updateTitle()` → MutableStateFlow aktualisiert → User drückt Speichern → ViewModel ruft `repository.saveTask()` → Repository ruft `dao.upsert()` → Room speichert → Flow emittiert neue Liste → UI aktualisiert sich automatisch.

### Detaillierte Antwort

**Schritt 1: Eingabe**
```
AddEditTaskScreen.kt:
TextField(
    value = uiState.title,
    onValueChange = { onTitleChanged(it) }  // Lambda an ViewModel
)
```

**Schritt 2: ViewModel nimmt Event entgegen**
```
AddEditTaskViewModel.kt:
fun updateTitle(newTitle: String) {
    _title.value = newTitle
}
// _title ist MutableStateFlow<String>
```

**Schritt 3: UiState wird aktualisiert**
```
combine(_title, _description, _priority, ...) → TasksUiState
→ StateFlow<AddEditTaskUiState> emittiert
→ Composable recomposed (TextField zeigt neuen Wert)
```

**Schritt 4: Speichern**
```
User drückt FAB → onSaveTask() → viewModel.saveTask()

AddEditTaskViewModel.saveTask():
viewModelScope.launch {
    repository.saveTask(
        Task(
            id = taskId ?: UUID.randomUUID().toString(),
            title = _title.value,
            description = _description.value,
            priority = _priority.value,
            isCompleted = false
        )
    )
    _isTaskSaved.value = true  // Navigation trigger
}
```

**Schritt 5: Repository speichert**
```
DefaultTaskRepository.saveTask(task):
    localDataSource.upsert(task.toLocal())  // Room
    applicationScope.launch {
        networkDataSource.saveTasks(getAllLocalTasks().map { it.toNetwork() })
    }  // fire-and-forget Netzwerk
```

**Schritt 6: Room emittiert neue Daten**
```
Room: INSERT OR REPLACE → task-Tabelle ändert sich
→ TaskDao.observeAll() Flow emittiert neue List<LocalTask>
→ DefaultTaskRepository.getTasksStream() map → List<Task>
→ TasksViewModel.combine() → neue TasksUiState
→ TasksScreen recomposed → neue Task in Liste sichtbar
```

### Projektbezug

Vollständiger Pfad durch: `AddEditTaskScreen.kt` → `AddEditTaskViewModel.kt` → `DefaultTaskRepository.kt` → `TaskDao.kt` → Room → `observeAll()` Flow → `TasksViewModel.kt` → `TasksScreen.kt`

### Mögliche Follow-up Fragen

- "Was passiert wenn Room beim Upsert fehlschlägt — wie wird der Fehler behandelt?"
- "Warum wird der Netzwerk-Save mit `applicationScope` statt `viewModelScope` ausgeführt?"
- "Was ist der Unterschied zwischen `INSERT` und `UPSERT` in Room?"

### Wichtige Begriffe

Unidirectional Data Flow, `viewModelScope`, `applicationScope`, Upsert, Reactive Streams, Fire-and-forget

---

## Frage 5.2

**Warum wird `applicationScope` für Netzwerk-Saves verwendet statt `viewModelScope`?**

### Kurzantwort

`viewModelScope` wird gecancelt wenn das ViewModel zerstört wird (z.B. Benutzer verlässt Screen). Netzwerk-Saves sollen aber auch dann abgeschlossen werden. `applicationScope` überlebt das ViewModel und läuft solange die App läuft.

### Detaillierte Antwort

```kotlin
// DefaultTaskRepository.kt
suspend fun saveTask(task: Task) {
    // Lokales Speichern: MUSS sofort passieren → viewModelScope ist ok
    localDataSource.upsert(task.toLocal())

    // Netzwerk-Sync: kann im Hintergrund passieren → applicationScope
    applicationScope.launch {
        networkDataSource.saveTasks(getAllLocalTasks().map { it.toNetwork() })
    }
    // "Fire-and-forget": wir warten nicht auf das Netzwerk
}
```

**Was würde mit `viewModelScope` passieren?**
1. User speichert Task
2. User drückt sofort Zurück → ViewModel wird zerstört
3. `viewModelScope.launch { ... }` wird gecancelt
4. Netzwerk-Save wurde nie abgeschlossen → **Datenverlust auf dem Server**

**Mit `applicationScope`:**
1. Netzwerk-Save läuft im App-weiten Scope
2. Auch wenn ViewModel zerstört wird, läuft der Coroutine weiter
3. Nur wenn die App vollständig beendet wird, werden laufende Scopes gecancelt

**Trade-off:** `applicationScope` schafft Coroutines die schwerer zu beobachten und zu testen sind. Fehler in `applicationScope`-Coroutines werden nicht automatisch ans ViewModel weitergeleitet. In einem echten Projekt würde man WorkManager für robustere Hintergrund-Sync-Aufgaben verwenden.

### Projektbezug

- `di/CoroutinesModule.kt` — `@ApplicationScope` Binding mit `SupervisorJob()`
- `data/DefaultTaskRepository.kt` — `applicationScope.launch { ... }` für Network

### Mögliche Follow-up Fragen

- "Was ist `SupervisorJob` und warum wird es in `ApplicationScope` verwendet?"
- "Was würde WorkManager hier besser machen?"
- "Wie würde man testen ob der Netzwerk-Save korrekt ausgeführt wird?"

### Wichtige Begriffe

`applicationScope`, `viewModelScope`, Coroutine-Cancellation, `SupervisorJob`, Fire-and-forget, WorkManager

---

# TEIL 6: COMPOSE & UI

---

## Frage 6.1

**Wie funktioniert Compose Recomposition und wie habt ihr sie in diesem Projekt minimiert?**

### Kurzantwort

Recomposition zeichnet nur die Composables neu, die sich geänderte State-Werte lesen. Minimiert wurde sie durch: `data class` UiState (effizientes `equals()`), `collectAsStateWithLifecycle()`, und UI-State-Konsolidierung in eine State-Klasse statt vieler einzelner States.

### Detaillierte Antwort

**Wie funktioniert Recomposition?**
```kotlin
@Composable
fun TaskItem(task: Task, onComplete: (String) -> Unit) {
    Text(task.title)        // liest task.title
    Checkbox(task.isCompleted) // liest task.isCompleted
}
```
Wenn `task.title` sich ändert aber `task.isCompleted` nicht → `Text` wird recomposed, `Checkbox` idealerweise nicht (mit `key()`-Optimierung).

**Minimierungsstrategien im Projekt:**

**1. `data class` für UiState:**
```kotlin
data class TasksUiState(
    val items: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    ...
)
// Compose kann equals() nutzen: wenn gleich → keine Recomposition
```

**2. `collectAsStateWithLifecycle()` statt `collectAsState()`:**
Stoppt Collection wenn UI nicht sichtbar → keine unnötigen Recompositions im Hintergrund.

**3. State Hoisting:**
State liegt im ViewModel → Composables sind Funktionen ohne eigenen State → klares Recomposition-Verhalten.

**4. Lambda-Stabilität:**
```kotlin
// Im ViewModel definierte Lambdas bleiben stabil zwischen Recompositions
val onDeleteTask: (String) -> Unit = viewModel::deleteTask
```

**Trade-off:** Über-Optimierung von Recomposition ist oft premature optimization. Compose ist von Grund auf für häufige Recompositions designed. Erst profilen (Compose Layout Inspector) bevor man komplexe `remember`/`key`-Konstrukte einbaut.

### Projektbezug

- Alle UiState-Klassen als `data class` definiert (z.B. `tasks/TasksViewModel.kt`)
- `tasks/TasksScreen.kt` — `collectAsStateWithLifecycle()`
- `util/CoroutinesUtils.kt` — `WhileUiSubscribed` verhindert unnötige Flow-Aktivität

### Mögliche Follow-up Fragen

- "Was ist der Unterschied zwischen `remember` und `rememberSaveable`?"
- "Was ist `Stable`/`Immutable` in Compose und wann wird es verwendet?"
- "Wie diagnostizierst du unnötige Recompositions?"

### Wichtige Begriffe

Recomposition, `data class equals()`, `key()`, `remember`, Compose Stability, Layout Inspector

---

## Frage 6.2

**Wie wurde Swipe-to-Delete implementiert und welche Alternativen gab es?**

### Kurzantwort

`SwipeToDismissBox` aus Compose Material3 mit `DismissDirection.StartToEnd`. Beim Abschluss der Swipe-Geste wird `onTaskDeleted(task.id)` aufgerufen. Alternative wäre eine manuelle `Draggable`-Implementierung oder ein Contextual Action Mode (Long-Press Menü).

### Detaillierte Antwort

**Implementierung:**
```kotlin
// TasksScreen.kt (vereinfacht)
@Composable
fun TaskItem(task: Task, onTaskDeleted: (String) -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Red),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    ) {
        TaskContent(task)
    }

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == DismissedToEnd) {
            onTaskDeleted(task.id)
        }
    }
}
```

**Alternativen:**
1. **Manuell via `Draggable` Modifier**: Mehr Kontrolle, aber viel mehr Code
2. **Contextual Action Mode** (Long-Press): Keine Swipe-Geste, aber Android-Standard
3. **Löschen-Button** direkt in der Listenzeile: Simpler, aber schlechtere UX
4. **Expandable Card**: Zeigt Delete-Button beim Tap — weniger fehlerlösungsanfällig

**Trade-offs von Swipe-to-Delete:**
- UX-Risiko: Accidental deletions (versehentliches Löschen) — Undo-Funktion wäre empfohlen
- Discoverability: Nutzer müssen wissen dass Swipe möglich ist
- Im Projekt: kein Undo implementiert — in Production-App problematisch

**Risiko erkannt:** Der aktuelle Code hat keinen Undo-Button. Ein Snackbar mit "Rückgängig" wäre die richtige UX-Lösung. Technisch: Task als "soft delete" markieren (isDeleted-Flag), dann nach 5s wirklich löschen.

### Projektbezug

- `tasks/TasksScreen.kt` — `SwipeToDismissBox` Implementierung
- `tasks/TasksViewModel.kt` — `deleteTask(taskId)` Methode

### Mögliche Follow-up Fragen

- "Wie würdest du Undo nach dem Löschen implementieren?"
- "Was passiert wenn der User sehr schnell swipet — gibt es Race Conditions?"
- "Warum ist `SwipeToDismissBox` und nicht `SwipeToDismiss`?"

### Wichtige Begriffe

`SwipeToDismissBox`, `rememberSwipeToDismissBoxState`, Gesture-Handling, Accidental Deletion, Soft Delete, Undo-Pattern

---

# TEIL 7: TESTING & QUALITÄT

---

## Frage 7.1

**Welche Teile des Projekts sind gut testbar und warum?**

### Kurzantwort

ViewModels und das Repository sind am besten testbar, weil sie keine Android-spezifischen Abhängigkeiten haben (mit Hilt-Fake-Injection). Room-DAOs sind via Room Testing-Library testbar. Composables via Compose-UI-Test-Framework.

### Detaillierte Antwort

**Gut testbar — ViewModels:**
```kotlin
// Test ohne Android-Emulator möglich
class TasksViewModelTest {
    private val fakeRepository = FakeTaskRepository()
    private val viewModel = TasksViewModel(fakeRepository, Dispatchers.Main)

    @Test
    fun `filter HIGH_PRIORITY shows only high priority tasks`() = runTest {
        fakeRepository.addTasks(listOf(
            Task("1", "Test", priority = TaskPriority.HIGH),
            Task("2", "Test2", priority = TaskPriority.LOW)
        ))
        viewModel.setFiltering(TasksFilterType.HIGH_PRIORITY)
        assertThat(viewModel.uiState.value.items).hasSize(1)
    }
}
```

**Gut testbar — Repository:**
- `DefaultTaskRepository` kann mit Fake-DAO und Fake-NetworkSource getestet werden
- Synchronisierungslogik (`refresh()`) testbar ohne echtes Netzwerk

**Gut testbar — StatisticsUtils:**
```kotlin
// Pure Kotlin-Funktion — trivial testbar
@Test
fun `50% completed returns 50 50`() {
    val result = getActiveAndCompletedStats(listOf(
        Task(isCompleted = true), Task(isCompleted = false)
    ))
    assertThat(result.completedTasksPercent).isEqualTo(50f)
}
```

**Weniger gut testbar (aktuell):**
- Composables: Brauchen Compose-Test-Rule und Android-Instrumentierung
- `DefaultTaskRepository` mit echtem Room: Braucht `in-memory`-DB in Tests

**Was im Projekt aktuell fehlt:**
- Keine Unit-Tests für ViewModels implementiert
- Kein Fake-Repository vorhanden
- `SimpleCountingIdlingResource` vorhanden (Grundlage für Espresso-Tests) aber nicht genutzt

**Skalierungsaspekt:** Fehlende Tests sind technische Schuld. Bei wachsender Codebasis werden Refactorings riskant weil kein Netz. Die Architektur ermöglicht Tests — sie sind nur nicht geschrieben.

### Projektbezug

- `util/SimpleCountingIdlingResource.kt` — Grundlage für Espresso
- `statistics/StatisticsUtils.kt` — einfachste Unit-Test-Kandidatin
- `di/DataModules.kt` — `@Binds` ermöglicht Fake-Injection in Tests

### Mögliche Follow-up Fragen

- "Wie würdest du `TasksViewModel` testen?"
- "Was ist der Unterschied zwischen Unit-Test und Instrumentierungstest?"
- "Wie testet man Room-Migrations?"

### Typische schlechte Antworten

- "Wir haben keine Tests, weil es ein Uni-Projekt ist." — Kein Bewusstsein für technische Schuld
- "Tests sind nicht nötig wenn der Code gut ist." — Grundlegend falsch

### Wichtige Begriffe

Unit-Tests, Fake/Mock, `FakeTaskRepository`, Instrumentierungstest, `TestCoroutineDispatcher`, In-Memory-Room

---

# TEIL 8: KRITISCHE REFLEXION

---

## Frage 8.1

**Was würdest du an der Architektur verbessern wenn du das Projekt neu starten würdest?**

### Kurzantwort

Drei Hauptverbesserungen: (1) Type-Safe Navigation statt String-Routen, (2) Use-Case-Klassen für komplexe Business-Logik, (3) Error-Handling-Strategie für Netzwerkfehler.

### Detaillierte Antwort

**1. Type-Safe Navigation:**
```kotlin
// Aktuell (fehleranfällig):
const val TASK_DETAIL_ROUTE = "task/{taskId}"
navController.navigate("task/$taskId")  // String-Fehler erst zur Laufzeit

// Besser (seit Navigation 2.8):
@Serializable
data class TaskDetailRoute(val taskId: String)
navController.navigate(TaskDetailRoute(taskId))  // Compile-Fehler wenn falsch
```

**2. Use-Cases / Interactors (Clean Architecture):**
Das aktuelle ViewModel macht Daten-Koordination und UI-State-Verwaltung. Bei wachsender Komplexität wäre sinnvoll:
```
FilterTasksUseCase(repository) → filtert und gibt Flow<List<Task>> zurück
SaveTaskUseCase(repository)    → validiert + speichert
```
Vorteil: ViewModel bleibt schlank, Use-Case ist testbar ohne UI-State.

**3. Netzwerk-Error-Handling:**
Aktuell: `TaskNetworkDataSource` ist ein Mock — kein Error-Handling nötig. Bei echter API:
```kotlin
// Repository könnte Result<T> statt T zurückgeben:
suspend fun saveTask(task: Task): Result<Unit> = runCatching {
    networkDataSource.saveTask(task)
}
```
Fehler müssen bis zur UI propagiert werden (über `UiState.error`).

**4. Accompanist SwipeRefresh ersetzen:**
Material3 hat mittlerweile `PullToRefreshBox` — Accompanist-Abhängigkeit kann entfernt werden.

**Trade-off der aktuellen Architektur:** Sie ist für den Projektumfang angemessen. Use-Cases für einen 4-Screen Todo-App wäre Overengineering. Aber für eine App die wächst, wären sie wertvoll.

### Mögliche Follow-up Fragen

- "Was sind die Nachteile von Use-Cases?"
- "Wann ist Clean Architecture sinnvoll und wann Overengineering?"
- "Wie würde Type-Safe Navigation die Entwicklererfahrung verbessern?"

### Wichtige Begriffe

Type-Safe Navigation, Clean Architecture, Use-Cases/Interactors, Error-Propagation, `Result<T>`, Refactoring

---

## Frage 8.2

**Was sind die größten Risiken in der aktuellen Implementierung?**

### Kurzantwort

Drei Hauptrisiken: (1) Kein Undo nach Swipe-Delete (Datenverlust-Risiko), (2) Netzwerk-Sync ist Fire-and-Forget ohne Fehlerbehandlung, (3) Keine Tests bedeuten Regressionsrisiko bei Änderungen.

### Detaillierte Antwort

**Risiko 1 — Accidental Deletion ohne Undo:**
- Swipe-to-Delete ist irreversibel
- Kein Snackbar mit Undo-Option
- In Production: kritischer UX-Fehler, möglicher Nutzerverlust
- Lösung: Soft-Delete + Snackbar mit `LaunchedEffect` + Timer

**Risiko 2 — Netzwerk-Sync unzuverlässig:**
```kotlin
// DefaultTaskRepository.kt
applicationScope.launch {
    networkDataSource.saveTasks(...)  // was wenn das fehlschlägt?
    // Kein Error-Handling!
    // Lokale DB und Netzwerk können divergieren!
}
```
Bei Netzwerkfehler: Daten sind lokal korrekt, aber auf dem Server fehlen sie. Kein Retry-Mechanismus.
Lösung: WorkManager mit Retry-Logik für Netzwerk-Sync.

**Risiko 3 — Keine Tests:**
Jede Änderung kann Regressionen einführen die erst durch manuelle Tests auffallen. Die Architektur ist testbar, aber Tests fehlen.

**Risiko 4 — In-Memory Netzwerk-Mock:**
`TaskNetworkDataSource` verliert alle Daten bei App-Neustart. Das bedeutet `refresh()` holt immer leere oder inkonsistente Daten.

**Risiko 5 — Hartcodierte Seed-Daten:**
```kotlin
// DatabaseModule.kt
// 3 Tasks werden beim ersten DB-Start gesetzt
// Was wenn sich die Seed-Daten ändern müssen?
// → Build-Zeit-Konstante, nicht konfigurierbar
```

### Mögliche Follow-up Fragen

- "Wie priorisierst du diese Risiken?"
- "Welches Risiko hättest du zuerst behoben?"
- "Was wäre der Aufwand für einen Undo-Button?"

### Wichtige Begriffe

Risikobewertung, Soft-Delete, Idempotenz, WorkManager, Retry-Mechanismus, Technische Schuld

---

## Frage 8.3

**Wie skaliert diese Architektur wenn die App deutlich größer wird?**

### Kurzantwort

MVVM + Repository skaliert gut bis zu ~10-15 Screens. Ab dann wird ein modulares Setup mit Feature-Modulen und klaren Modul-Grenzen sinnvoll. Das aktuelle Single-Module-Setup wird bei wachsender Codebasis mit langen Build-Zeiten bestraft.

### Detaillierte Antwort

**Was gut skaliert:**
- Repository Pattern: Neue Datenquellen (z.B. echte API) ohne ViewModel-Änderung
- Hilt: Neue Module sind einfach hinzuzufügen
- Feature-Pakete: Bereits feature-orientiert strukturiert (tasks/, statistics/, etc.)
- StateFlow/Compose: Reaktives UI skaliert ohne Performance-Einbußen

**Was Probleme bereitet:**

**1. Single Module:**
Alle Features im gleichen Modul → bei großer Codebasis: alles compiliert immer neu. Lösung: Gradle-Feature-Module (`feature:tasks`, `feature:statistics`).

**2. Kein Use-Case-Layer:**
Bei komplexer Business-Logik (z.B. Reminder-System, Task-Dependencies) wird `DefaultTaskRepository` zu groß. Use-Cases würden Logik aufteilen.

**3. Kein Caching-Layer:**
Aktuell: immer aktuelle DB-Daten. Bei teuren Berechnungen (z.B. komplexe Statistiken) bräuchte man Caching.

**4. Navigation:**
String-basierte Routen skalieren schlecht bei vielen Screens. Type-Safe Navigation (`@Serializable`-Routes) ist nötig.

**5. State-Synchronisation zwischen Features:**
Wenn Statistics und Tasks den gleichen State brauchen, müssten beide das Repository observieren — das funktioniert gut. Aber bei UI-übergreifendem State (z.B. Notifications) bräuchte man einen geteilten State-Store.

**Fazit:** Die aktuelle Architektur ist für das Projektziel gut gewählt. Premature scalability wäre Overengineering. Aber ein Entwickler muss die Grenzen kennen.

### Wichtige Begriffe

Feature-Module, Multi-Module-Architektur, Use-Cases, Caching, Navigation-Skalierung, Build-Zeit-Optimierung

---

# TEIL 9: NAVIGATION & INTEGRATION

---

## Frage 9.1

**Wie funktioniert der Navigation Drawer und wie ist er in die Navigation integriert?**

### Kurzantwort

`AppModalDrawer()` wrapping die jeweiligen Screens (Tasks und Statistics). Drawer-Öffnung via `DrawerState` mit Coroutine (`scope.launch { drawerState.open() }`). Navigation zu anderen Screens via `NavigationActions`.

### Detaillierte Antwort

```kotlin
// AppModalDrawer (util/TodoDrawer.kt)
@Composable
fun AppModalDrawer(
    drawerState: DrawerState,
    currentRoute: String,
    navigationActions: TodoNavigationActions,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                navigateToTasks = navigationActions::navigateToTasks,
                navigateToStatistics = navigationActions::navigateToStatistics,
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        content()  // Hier liegt der eigentliche Screen-Inhalt
    }
}
```

**Integration in TasksScreen:**
```kotlin
@Composable
fun TasksScreen(openDrawer: () -> Unit, ...) {
    // Screen-Inhalt
    TasksTopAppBar(openDrawer = openDrawer)  // Hamburger-Button öffnet Drawer
}
```

**State-Persistenz beim Drawer-Wechsel:**
```kotlin
// TodoNavigationActions.kt
fun navigateToStatistics() {
    navController.navigate(STATISTICS_ROUTE) {
        popUpTo(TASKS_ROUTE) { saveState = true }  // Tasks-State erhalten
        launchSingleTop = true    // kein Doppel-Push
        restoreState = true       // Statistics-State wiederherstellen
    }
}
```

`saveState = true` und `restoreState = true` sorgen dafür, dass der Scroll-State beim Zurücknavigieren erhalten bleibt.

### Projektbezug

- `util/TodoDrawer.kt` — `AppModalDrawer`, `AppDrawer`, `DrawerButton`
- `TodoNavigation.kt` — `TodoNavigationActions.navigateToStatistics()`
- `TodoNavGraph.kt` — NavHost mit `rememberNavController()`

### Mögliche Follow-up Fragen

- "Was ist der Unterschied zwischen ModalNavigationDrawer und PermanentNavigationDrawer?"
- "Was passiert mit dem Backstack wenn man vom Drawer navigiert?"
- "Wie würde Deep-Link-Navigation mit dem Drawer interagieren?"

### Wichtige Begriffe

`ModalNavigationDrawer`, `DrawerState`, `saveState`, `launchSingleTop`, Backstack-Management

---

# TEIL 10: SCHNELLREFERENZ

---

## Wichtige Begriffe-Glossar

| Begriff | Erklärung | Im Projekt |
|---|---|---|
| **MVVM** | Model-View-ViewModel Architektur | Alle 4 Features |
| **Repository Pattern** | Datenzugriff abstrahieren | `DefaultTaskRepository` |
| **StateFlow** | Reaktiver State mit aktuellem Wert | Alle ViewModels |
| **Flow** | Asynchroner Datenstrom | Room DAO → Repository |
| **suspend** | Pausierbare Funktion | Alle Repository-Methoden |
| **viewModelScope** | Coroutine-Scope des ViewModels | Alle ViewModels |
| **applicationScope** | App-weiter Coroutine-Scope | Netzwerk-Fire-and-Forget |
| **Hilt** | Dependency Injection Framework | `@HiltViewModel`, Module |
| **Room** | SQLite-Abstraktion mit Flow | `TaskDao`, `ToDoDatabase` |
| **State Hoisting** | State nach oben bewegen | Alle Composables |
| **Recomposition** | Composable neu zeichnen | Durch StateFlow getriggert |
| **UiState** | Datenklasse für Screen-Zustand | `TasksUiState` etc. |
| **Domain Model** | Interne App-Repräsentation | `Task.kt` |
| **Entity** | Room-Datenbankmodell | `LocalTask.kt` |
| **DTO** | Netzwerk-Datentransfermodell | `NetworkTask.kt` |
| **KSP** | Kotlin Symbol Processing (Code-Gen) | Hilt + Room |
| **Single Source of Truth** | Eine Datenquelle als Wahrheit | Room als primary source |
| **UDF** | Unidirectional Data Flow | Gesamte Architektur |
| **WhileUiSubscribed** | StateFlow-Sharing-Strategie | `CoroutinesUtils.kt` |
| **Migration** | DB-Schema-Upgrade | MIGRATION_1_2 |

---

## Antwortemuster für mündliche Prüfung

**Bei "Warum X?":**
> "Wir haben X gewählt weil [technischer Grund]. Alternative wäre Y gewesen, hat aber [Nachteil]. X hat den Vorteil [Vorteil] für unseren Anwendungsfall."

**Bei "Was würdest du verbessern?":**
> "Ich würde [konkrete Verbesserung] einbauen, weil [technischer Grund]. Das würde [Problem] lösen und [Vorteil] bringen. Der Trade-off wäre [Nachteil/Aufwand]."

**Bei AI-Fragen:**
> "AI war hilfreich für [konkreter Einsatz]. Den generierten Code haben wir validiert durch [Methode]. Einen Vorschlag haben wir nicht übernommen weil [Grund]."

**Bei Fehlerfragen:**
> "Wir hatten [Problem]. Das war [Ursache]. Gelöst durch [Lösung]. Gelernt: [Lesson learned]."

---

*Für Architekturdetails: `architecture.md`*
*Für AI-Workflow: `agents.md`*
