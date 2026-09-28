# University Student Record and Campus Route Management System

Java console project for **CIT300 - Data Structures and Algorithms**. Contributions are integrated into `main` module by module, through the corresponding contributor's pull request.

## Current contribution status

| Member | Student ID | Responsibility | Source folders | Status on main |
| --- | --- | --- | --- | --- |
| J.Abdullah | 23DA2-0575 | Student Linked List | `src/student/` | Integrated |
| SMF.Asra | 23DA2-0826 | Queue and Stack | `src/queue/`, `src/stack/` | Integrated |
| MSM.Dilsath | 23DA2-0576 | Hash Table and BST/AVL | `src/hashing/`, `src/tree/` | Awaiting contributor PR; not included |
| MTF.Nifra | 23DA2-0729 | Campus Graph Routes | `src/graph/` | Awaiting contributor PR; not included |

The searching/sorting algorithm packages are also deferred. They are not copied from other branches into `main` ahead of the relevant contribution review.

`src/Main.java` and `src/app/` are shared console/integration code. At this stage they depend only on the student, queue and stack modules. The other contributors' branches remain separate and unchanged.

## Available functionality

- Add, update, delete, search and display students using Abdullah's custom singly linked list.
- Store all nine student fields and validate IDs, age, required text, email, contact number and GPA.
- Queue student service requests and process them in FIFO order using Asra's queue.
- Push, peek, pop and display recent actions using Asra's stack.
- Keep four sample students available through `--demo`.

The final assignment's 16 menu labels remain in place. Options **1-7 and 16** operate now. Options **8-15** report that their features are unavailable until the corresponding contributions are merged. There are no temporary hash, tree or graph implementations on `main`.

Option 4 displays all records, then offers `1` to search by student ID using the linked list and `0` to return. Option 5 offers request creation and queue display. Option 7 offers history display, peek and pop. Popping history does not undo a data change.

## Run and test

Requires JDK 17 or above on `PATH`. No external Java dependencies or Java Collection Framework storage classes are used.

```powershell
# Build and run the sample students.
powershell -NoProfile -ExecutionPolicy Bypass -File scripts/build.ps1 run -Demo

# Run regression tests for the currently integrated modules.
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
```

`StudentLinkedList` stores records in insertion order and searches by ID through its node links. `ServiceQueue` links requests through front/rear references. `ActionStack` links timestamped actions from the top. `UniversitySystem` coordinates these three structures and prevents deleting a student while requests still refer to that record.

The contributor-owned source files in `student`, `queue` and `stack` match the corresponding contributor branches. Integration adaptations are confined to shared application code, tests, build configuration and documentation.

## Sample students

| Student ID | Name | Programme | GPA |
| --- | --- | --- | --- |
| 0001 | Abdullah | BAIT | 3.75 |
| 0002 | Asra | IT | 3.50 |
| 0003 | Dilsath | Computer Science | 3.90 |
| 0004 | Nifra | Computer Science | 3.60 |

Additional profile fields are defined in `UniversitySystem.loadDemoData()`. Campus data will be integrated with the campus contribution.

## Testing

The current runner passes **7 suites and 98 assertions**, covering validation, linked-list boundaries, FIFO/LIFO behavior, integrated student/request/history operations, sample records, console operations, unavailable menu choices and interrupted input. No passing result is claimed for unmerged modules.

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

The previous integration included unapproved module folders. This correction removes those folders from the current `main` tree while preserving Git history and leaving contributor branches intact.
