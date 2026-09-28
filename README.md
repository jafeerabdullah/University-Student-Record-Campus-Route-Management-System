# University Student Record and Campus Route Management System

Java console project for **CIT300 - Data Structures and Algorithms**. Contributions are integrated into `main` module by module, through the corresponding contributor's pull request.

## Current contribution status

| Member | Student ID | Responsibility | Source folders | Status on main |
| --- | --- | --- | --- | --- |
| J.Abdullah | 23DA2-0575 | Student Linked List | `src/student/` | Integrated |
| SMF.Asra | 23DA2-0826 | Queue and Stack | `src/queue/`, `src/stack/` | Integrated |
| MSM.Dilsath | 23DA2-0576 | Hash Table and BST/AVL | `src/hashing/`, `src/tree/` | Integrated |
| MTF.Nifra | 23DA2-0729 | Campus Graph Routes | `src/graph/` | Integrated |

Dilsath's branch also contributes `src/searching/` and `src/sorting/`. All four members' assigned modules are now integrated from their respective branches.

`src/Main.java` and `src/app/` are shared console/integration code. They now connect all four accepted contributions. Contributor-owned classes remain unchanged; their branch histories are preserved by the merges.

## Available functionality

- Add, update, delete, search and display students using Abdullah's custom singly linked list.
- Store all nine student fields and validate IDs, age, required text, email, contact number and GPA.
- Queue student service requests and process them in FIFO order using Asra's queue.
- Push, peek, pop and display recent actions using Asra's stack.
- Search by hash table and display students in AVL inorder traversal.
- Search by ID/name using linear or binary search; merge-sort by name or descending GPA.
- Add/remove campus locations and bidirectional roads; display connections and traverse with BFS/DFS.
- Load four sample students, seven locations and eight illustrative roads through `--demo`.

All **16 main-menu options are active**. Options 1-7 manage students, requests and history; option 8 displays AVL-ordered records; option 9 performs hash lookup; options 10-15 manage and traverse campus routes; option 16 exits.

Option 4 displays complete records, then offers linear search by ID/name, binary search by ID/name, name sorting, GPA ranking and hash bucket display. Use `0` to return. Option 5 offers request creation and queue display. Option 7 offers history display, peek and pop. Popping history does not undo a data change.

## Run and test

Requires JDK 17 or above on `PATH`. No external Java dependencies or Java Collection Framework storage classes are used.

```powershell
# Build and run the sample students.
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/build.ps1 run -Demo

# Run regression tests for all integrated modules.
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/build.ps1 test
```

```sh
sh scripts/build.sh run --demo
sh scripts/build.sh test
```

After building:

```text
java -cp out Main --demo
```

Omit `--demo` to start with empty records. Data is kept in memory for the current session. Build scripts clean their own output directory before compilation so that removed modules cannot remain as stale class files.

## Current source structure

```text
src/
  Main.java
  app/
    ConsoleApplication.java
    ConsoleInput.java
    UniversitySystem.java
  student/                    # J.Abdullah
    Student.java
    StudentNode.java
    StudentLinkedList.java
  queue/                      # SMF.Asra
    ServiceRequest.java
    QueueNode.java
    ServiceQueue.java
  stack/                      # SMF.Asra
    Action.java
    StackNode.java
    ActionStack.java
  hashing/                    # MSM.Dilsath
    StudentHashTable.java
  tree/                       # MSM.Dilsath
    TreeNode.java
    StudentBST.java
    AVLTree.java
  searching/                  # MSM.Dilsath
    SearchAlgorithms.java
  sorting/                    # MSM.Dilsath
    SortingAlgorithms.java
  graph/                      # MTF.Nifra
    Location.java
    Edge.java
    CampusRouteGraph.java
```

`StudentLinkedList` stores records in insertion order and searches by ID through its node links. `ServiceQueue` links requests through front/rear references. `ActionStack` links timestamped actions from the top. `StudentHashTable` uses separate chaining and resizes its bucket array. `AVLTree` maintains Student ID order with rotations, and `CampusRouteGraph` uses custom adjacency lists with manual traversal queues/stacks. `UniversitySystem` keeps the student list, hash table and AVL tree synchronized and prevents deletion while requests still refer to a student.

The contributor-owned source files in all module folders match the corresponding contributor branches. Integration adaptations are confined to shared application code, tests, build configuration and documentation.

## Sample students

| Student ID | Name | Programme | GPA |
| --- | --- | --- | --- |
| 0001 | Abdullah | BAIT | 3.75 |
| 0002 | Asra | IT | 3.50 |
| 0003 | Dilsath | Computer Science | 3.90 |
| 0004 | Nifra | Computer Science | 3.60 |

Additional profile fields are defined in `UniversitySystem.loadDemoData()`. Demo campus locations are Main Gate, Library, Engineering Faculty, Computer Laboratory, Lecture Hall, Cafeteria and Hostel. Distances are in metres. BFS/DFS traverse reachable locations; they do not calculate a weighted shortest path.

## Testing

The full runner passes **12 suites and 53,409 assertions**. Coverage includes validation, linked-list boundaries, FIFO/LIFO behavior, deliberate hash collisions and resizing, BST deletion, AVL rotations and randomized operations, searching/sorting, graph cycles and removal, synchronized indexes, all 16 menu options, the four sample records and interrupted input.

See [testing details](docs/TESTING.md), [sample outputs](docs/SAMPLE_OUTPUT.md), and the [captured session](docs/sample-session.txt).

## Contribution workflow

| Branch | Purpose |
| --- | --- |
| `main` | Accepted contributions and shared application code |
| `student-management-J.Abdullah` | Student Linked List |
| `queue-stack-management-asra` | Queue and Stack |
| `hashing-tree-management-dilsath` | Hash Table and BST/AVL |
| `campus-route-management-nifra` | Campus Graph Routes |

Each contributor should submit a pull request for their assigned module. Keep the shared application and other members' files in the branch; restrict changes to the assigned module and necessary integration/tests. Do not clear the branch or delete unrelated source folders to demonstrate ownership. Ownership is shown by the contribution table, folder boundaries, commits and PR review history.

For an older branch that already deleted unrelated files, reconcile it with current `main` before requesting review. Review the PR's file changes to ensure it adds the intended module without removing the existing application. Run tests and include any required integration changes in the PR. The project owner accepts and merges each contribution when ready; the presence of a remote branch alone is not approval to copy its contents into `main`.

The hash/tree and campus modules were previously deferred pending contributor approval. They are now integrated following the project owner's explicit request to merge `hashing-tree-management-dilsath` and `campus-route-management-nifra`. Merge resolutions retain the shared project files and previously accepted contributions instead of applying unrelated deletions from older branch history.
