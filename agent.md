
````md
# AGENT.md — instructions for MAD practical exam project

## 0. project context

this is an android / kotlin / jetpack compose practical exam project for the course mobile app development.

the exam repository url may be different from the preparation repository.

known preparation repository:

```text
https://github.com/leonardo1710/procrasti-not/tree/main/app/src/main
````

possible exam repository:

```text
https://github.com/leonardo1710/mad-practical-bb
```

important:

do not rely only on the repository name or url.

before implementing anything, inspect the actual current project structure and use the files that really exist in the opened repository.

the package is expected to be:

```text
at.ac.hcw.procrastinot
```

but this must still be verified in the current repository.

---

## 1. exam situation

this project is used for a practical android programming exam.

expected exam conditions:

* implement a small feature / mini-app end-to-end
* time: approximately 60–90 minutes
* ai usage is allowed / required
* after coding, there may be oral or written questions
* the implementation must be simple, clean, explainable, and exam-friendly

main goal:

```text
implement exactly the requested assignment, not more and not less.
```

do not invent features before the real assignment is provided.

do not overengineer.

the actual assignment text is always the source of truth.

---

## 2. expected project type

the project will probably be similar to the `procrasti-not` task management app.

expected technologies may include:

* kotlin
* jetpack compose
* material 3
* mvvm
* hilt
* room
* repository pattern
* navigation compose
* coroutines
* flow / stateflow
* timber

expected feature areas may include:

* task list
* add/edit task
* task detail
* statistics
* filtering tasks
* completing tasks
* deleting tasks
* local persistence with room
* navigation between screens

however, do not assume that all of these files already exist.

first inspect the actual repository.

---

## 3. hard rules

### 3.1 do not change versions unless absolutely necessary

do not change:

* gradle version
* kotlin version
* android gradle plugin version
* compose version
* compileSdk / minSdk / targetSdk
* existing dependency versions

only add or change dependencies if the assignment absolutely requires it and the existing project does not already contain the needed library.

before adding dependencies, check:

```text
gradle/libs.versions.toml
app/build.gradle.kts
```

prefer using existing dependencies.

---

### 3.2 do not edit tests

never edit, delete, disable, or bypass tests.

do not make tests pass by weakening or removing test logic.

if a test fails, fix the implementation, not the test.

---

### 3.3 keep changes minimal

the solution should be:

* simple
* readable
* exam-friendly
* easy to explain
* consistent with the existing codebase
* not unnecessarily clever
* not overabstracted

prefer small, localized changes.

do not rewrite the whole app.

do not do unrelated refactorings.

---

### 3.4 inspect first, code second

before writing code, inspect the existing project.

check at least:

```text
app/src/main/java/
app/src/main/AndroidManifest.xml
app/build.gradle.kts
gradle/libs.versions.toml
```

then inspect relevant packages, for example if they exist:

```text
data/
di/
tasks/
taskdetail/
addedittask/
statistics/
navigation/
presentation/
model/
```

do not invent file paths.

if a file does not exist, say so and adapt the plan.

---

## 4. ai assistance style

act as a pair-programming assistant.

do not immediately dump a huge complete solution.

first give:

1. short requirement summary
2. affected layers
3. files to inspect/change
4. small implementation plan
5. build/test command

then provide code file by file.

the student must be able to understand and explain the solution.

for common tasks, prefer guided implementation over blind copy-paste.

if the user explicitly asks for full code, provide it, but keep it localized and explainable.

---

## 5. workflow when the real assignment is provided

when the exam assignment is pasted or shown:

### step 1: analyze requirements

extract:

* required screens
* required user actions
* required data model
* required persistence
* required api/network usage
* required navigation
* required lifecycle behavior
* required validation
* required ui elements

### step 2: classify the task

classify it as one or more of:

* ui-only task
* viewmodel/state task
* data-layer/room task
* repository task
* navigation task
* validation task
* statistics task
* lifecycle/state preservation task
* bugfix
* end-to-end feature

### step 3: inspect relevant files

before coding, inspect current files and identify exact places to change.

### step 4: implement in small slices

keep the project buildable after each important step.

### step 5: build and verify

run the relevant gradle command.

### step 6: prepare explanation

after implementation, prepare a short oral/written explanation.

---

## 6. preferred response format

for implementation tasks, use this format:

```text
short plan

files to change:
- file 1: why
- file 2: why
- file 3: why

step 1:
explanation
code

step 2:
explanation
code

build/test:
.\gradlew.bat :app:assembleDebug --no-daemon
```

if using git bash on windows:

```bash
./gradlew.bat :app:assembleDebug --no-daemon
```

if `.bat` causes problems in git bash:

```bash
cmd //c gradlew.bat :app:assembleDebug --no-daemon
```

if tests are available:

```bash
./gradlew.bat :app:testDebugUnitTest --no-daemon
```

---

## 7. architecture guideline

prefer the existing architecture of the project.

if the project already has mvvm + repository + room, follow that pattern.

typical data flow:

```text
compose ui
-> viewmodel
-> repository
-> dao / local data source
-> room database
```

for data coming back to ui:

```text
room / flow
-> repository
-> viewmodel stateflow
-> compose collect state
-> recomposition
```

for small ui-only features, do not create fake layers.

only create packages/classes that are really needed.

---

## 8. expected procrasti-not structure

if the project is the full `procrasti-not` app, relevant files may include something like:

```text
app/src/main/java/at/ac/hcw/procrastinot/

MainActivity.kt
MainApplication.kt

data/
data/source/
data/task.kt
data/taskrepository.kt
data/defaulttaskrepository.kt

di/

tasks/
taskscreen or tasksscreen
tasksviewmodel
tasksfiltertype

addedittask/
addedittaskscreen
addedittaskviewmodel

taskdetail/
taskdetailscreen
taskdetailviewmodel

statistics/
statisticsscreen
statisticsviewmodel

todonavgraph.kt
todonavigation.kt
```

important:

these names are examples from the expected project type.

always verify exact filenames in the actual repository before changing code.

---

## 9. compose rules

use jetpack compose properly.

rules:

* composables should mainly display ui
* keep composables small and readable
* avoid business logic inside composables
* avoid room/network calls directly inside composables
* state goes down, events go up
* use `remember` only for local ui state
* use `rememberSaveable` if local ui state should survive configuration changes
* use viewmodel for screen state and business logic
* avoid expensive work inside composables
* use correct side-effect apis when needed, such as `LaunchedEffect` or `DisposableEffect`

preferred state collection:

```kotlin
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

if `collectAsStateWithLifecycle` is not available, use the existing lifecycle/compose approach already used in the project.

---

## 10. viewmodel rules

use a viewmodel when the screen has meaningful state or logic.

the viewmodel should:

* expose immutable state to the ui
* keep mutable state private
* contain screen logic
* call repository functions
* not depend on composable state
* not render ui
* not hold references to compose ui elements

recommended pattern:

```kotlin
data class ExampleUiState(
    val items: List<Item> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val repository: ExampleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExampleUiState())
    val uiState: StateFlow<ExampleUiState> = _uiState.asStateFlow()

    fun onSomethingClicked() {
        // update state or call repository
    }
}
```

---

## 11. repository rules

repositories are the entry point to the data layer.

do not access dao directly from composables.

do not put data-source logic into ui.

prefer:

```text
viewmodel -> repository -> dao / api
```

repository responsibilities:

* expose data to viewmodels
* centralize data changes
* hide room/api implementation details
* combine local and remote data if needed
* provide simple functions for app features

---

## 12. room rules

if the assignment requires persistence, use existing room setup if available.

check:

```text
entity/model
dao
database
repository
hilt module
viewmodel
ui
```

room entities should be simple and explainable.

dao functions should be clear.

example:

```kotlin
@Dao
interface ItemDao {

    @Query("SELECT * FROM items")
    fun observeItems(): Flow<List<ItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItemEntity)

    @Delete
    suspend fun deleteItem(item: ItemEntity)
}
```

do not do database work on the main thread.

if schema changes are needed:

1. inspect existing database setup
2. check if destructive migration is already used
3. follow existing project pattern
4. explain the decision

do not create complicated migrations unless required.

---

## 13. hilt rules

if hilt is already set up, keep using it.

`MainApplication` should usually have:

```kotlin
@HiltAndroidApp
```

`MainActivity` should usually have:

```kotlin
@AndroidEntryPoint
```

viewmodels should use:

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(...)
```

in composables, prefer:

```kotlin
val viewModel: MyViewModel = hiltViewModel()
```

if external classes need to be provided, use the existing module pattern, usually in:

```text
di/
```

do not manually instantiate repositories, daos, or databases inside composables if hilt already provides them.

---

## 14. navigation rules

if the assignment has multiple screens, use existing navigation setup.

prefer centralized routes instead of hardcoded strings everywhere.

if the project has route classes/objects, use them.

when passing arguments:

* keep route names consistent
* use the existing argument style
* extract arguments safely
* make back navigation match the assignment

do not create multiple unrelated navcontrollers.

---

## 15. material design / ui rules

use material 3 components already available in the project:

* `Scaffold`
* `TopAppBar`
* `Button`
* `OutlinedTextField`
* `Card`
* `LazyColumn`
* `Text`
* `IconButton`
* `FloatingActionButton`
* `AlertDialog`
* `Checkbox`
* `DropdownMenu`
* `FilterChip`

keep the ui simple and functional.

do not spend too much time on design unless the assignment explicitly requires it.

---

## 16. likely exam tasks

be prepared for tasks such as:

* add priority to tasks
* add due date / deadline to tasks
* show overdue tasks
* add search
* add category / tag
* add favorite / important flag
* add filtering or sorting
* improve statistics
* add validation to add/edit screen
* fix navigation behavior
* pass navigation arguments
* preserve state after rotation
* use viewmodel as single source of truth
* add room persistence
* read data from repository instead of hardcoded data
* add snackbar or dialog feedback
* fix a bug in task completion/delete/edit flow

for end-to-end features, think in this order:

```text
requirement
existing files
model/entity
dao/database if needed
repository
viewmodel/ui state
compose screen
navigation if needed
build/test
oral explanation
```

---

## 17. comments in code

write useful comments only where they help explanation.

good comment:

```kotlin
// the viewmodel is the single source of truth for this screen state.
// the composable only displays state and sends events back.
```

bad comment:

```kotlin
// create variable
val title = ""
```

write comments especially for:

* state management
* viewmodel responsibilities
* repository pattern
* room database access
* navigation arguments
* lifecycle handling
* fallback logic
* important user interactions

---

## 18. internal documentation

when implementing the actual assignment, create or update:

```text
readmeForUs.md
architecture.md
```

these files are internal helper documents for exam preparation and oral explanation.

they do not affect the app runtime.

they should document only the real current implementation.

do not invent architecture, screens, database tables, api calls, or flows that do not exist.

---

## 19. readmeForUs.md

`readmeForUs.md` should explain:

1. what the assignment asked for
2. what was implemented
3. which files were changed or created
4. how the app works from the user perspective
5. how the architecture works
6. how state management works
7. how navigation works, if used
8. how persistence works, if used
9. how hilt / dependency injection works, if used
10. what can be explained in the oral/written part
11. how to build and run the app

recommended structure:

````md
# readmeForUs

## 1. assignment summary

short description of the task.

## 2. implemented features

- feature 1
- feature 2

## 3. changed files

```text
path/to/file.kt
path/to/another/file.kt
````

## 4. how the app works

explain the user flow.

## 5. architecture

explain ui, viewmodel, repository, data layer.

## 6. state management

explain where state is stored and why.

## 7. navigation

explain routes and screen transitions.

## 8. persistence / room

explain entity, dao, database, repository.

## 9. dependency injection / hilt

explain what hilt provides.

## 10. oral explanation points

short bullet points for professor questions.

## 11. build command

```bash
./gradlew.bat :app:assembleDebug --no-daemon
```

````

---

## 20. architecture.md

`architecture.md` should document the real current architecture.

recommended structure:

```md
# architecture.md

## 1. project overview

- what is this project?
- which features currently exist?
- which technologies are used?
- which technologies are available but not used yet?

## 2. current architecture overview

example:

```text
compose ui
 ↓
viewmodel
 ↓
repository
 ↓
room / data source
````

only write what is actually implemented.

## 3. project structure

document the real package and folder structure.

## 4. responsibilities

explain responsibilities of important classes.

## 5. technologies and libraries

| technology         | status         | purpose              |
| ------------------ | -------------- | -------------------- |
| kotlin             | used           | main language        |
| jetpack compose    | used           | ui                   |
| hilt               | used           | dependency injection |
| room               | used/available | local database       |
| navigation compose | used/available | screen navigation    |

## 6. state management

explain stateflow, remember, rememberSaveable, viewmodel if used.

## 7. navigation

explain current navigation.

## 8. dependency injection

explain hilt setup.

## 9. data flow

explain how data moves through the app.

## 10. diagrams

add simple ascii or mermaid diagrams.

## 11. architecture rules

* no business logic in composables
* viewmodels coordinate screen state
* repositories abstract data sources
* do not call room directly from composables
* do not hardcode navigation routes everywhere

````

---

## 21. build and test commands

before starting:

```bash
git status
````

build:

```bash
./gradlew.bat :app:assembleDebug --no-daemon
```

alternative:

```bash
cmd //c gradlew.bat :app:assembleDebug --no-daemon
```

unit tests if available:

```bash
./gradlew.bat :app:testDebugUnitTest --no-daemon
```

clean build if needed:

```bash
./gradlew.bat clean --no-daemon
./gradlew.bat :app:assembleDebug --no-daemon
```

if the build fails, fix the actual compiler/test error.

do not randomly change versions.

---

## 22. git rules

before implementation:

```bash
git status
```

after implementation:

```bash
git status
```

commit only meaningful source/documentation changes.

do not commit:

```text
.gradle/
build/
.kotlin/sessions/
.idea/workspace.xml
*.salive
```

commit message should be short and clear.

example:

```text
add task priority filter
```

commit body can include:

```text
- added priority field to task model
- updated add/edit screen
- added filter option in task list
- updated documentation for oral explanation
```

---

## 23. oral explanation preparation

after implementing a feature, prepare a short explanation.

the student should be able to answer:

* what was added?
* which files were changed?
* why was a viewmodel used?
* where is the single source of truth?
* how does state go down and events go up?
* how does recomposition happen?
* why was repository used?
* how does hilt provide dependencies?
* how does room store/load data?
* how does navigation pass arguments?
* how was the implementation tested?
* what did ai help with?
* how was ai-generated code verified?

example explanation:

```text
i added the new field to the task model and exposed it through the repository.
the viewmodel updates the ui state, and the compose screen only displays this state
and sends user events back to the viewmodel.
this follows mvvm, repository pattern, single source of truth, and unidirectional data flow.
```

---

## 24. ai usage reflection

for exam reflection, mention:

* ai helped analyze the assignment
* ai suggested an implementation plan
* ai helped generate boilerplate
* generated code was checked manually
* build/tests were run to verify correctness
* changes were kept small and understandable
* no code was accepted blindly

---

## 25. do not do these things

do not:

* rewrite the whole project unnecessarily
* change gradle/kotlin/compose versions without need
* add random dependencies
* edit/delete/disable tests
* put all logic into `MainActivity`
* put database/network logic into composables
* bypass repository and dao layers
* create global mutable state
* use blocking calls on the main thread
* invent extra features
* silently skip required features
* remove hilt setup
* remove working manifest/application setup
* ignore compile errors
* provide unexplained code dumps
* create documentation for features that do not exist

---

## 26. final rule

the actual exam assignment is the source of truth.

if this file conflicts with the assignment, follow the assignment.

implement the smallest clean solution that satisfies the task and can be explained clearly.

```
