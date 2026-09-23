# Testing details

## Running the tests

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\scripts\build.ps1 test
```

```sh
sh scripts/build.sh test
```

The scripts compile production and test sources with `--release 17 -Xlint:all`, then run `TestRunner`. Assertions are explicit checks, so `-ea` is not needed. No testing framework or Java collection implementation is used.

## Verification result

The local verification environment was Windows with JDK 23.0.2, targeting Java 17 bytecode. Both the PowerShell script and the shell script (through Git Bash) passed all suites. Compilation completed without warnings. The captured demo and the launcher's EOF handling were also run directly. The Java 17 GitHub Actions workflow is supplied for verification on the target runtime when the project is pushed; it has not been run remotely as part of local delivery.

```text
PASS: Student validation and normalization
PASS: Singly linked list mutations
PASS: FIFO queue and LIFO stack
PASS: Hash collisions, deletion and resizing
PASS: BST deletion and inorder traversal
PASS: AVL rotations and randomized insert/delete
PASS: Linear/binary search and merge sorting
PASS: Graph traversal, cycles and vertex/road removal
PASS: Coordinated records, requests and history
PASS: Randomized index synchronization
PASS: All 16 console options and invalid input
PASS: Empty structures and interrupted input
PASS: 12 suites, 53400 assertions.
```

## Coverage

| Area | Meaningful scenarios |
| --- | --- |
| Student validation | ID normalization, empty names/IDs, GPA endpoints and out-of-range/nonfinite values, age bounds, invalid email/contact. |
| Linked list | Duplicate insert, update, missing record, head/middle/tail/sole-node deletion, reuse after empty, defensive array snapshots. |
| Queue | FIFO order, non-removing peek, traversal, empty dequeue, reuse after rear resets, null rejection. |
| Stack | LIFO order, non-removing peek, display order, empty pop, null rejection. |
| Hash table | Four deliberately colliding IDs, duplicate rejection, chain head/middle/tail deletion, update, 300 inserts across resizing, lookup/deletion after rehash. |
| BST | Inorder key order, duplicate insertion, two-child root deletion, leaf/one-child deletion, update and missing keys. |
| AVL | LL/RR/LR/RL rotations, 400 ascending inserts, shuffled deletions, shuffled reinsertion, actual height/order/balance checks after each mutation. |
| Searching/sorting | Unsorted input, exact case-insensitive duplicate names, missing values, empty arrays, deterministic GPA/name ties, original array preservation. |
| Graph | Cycle traversal, isolated vertex, fixed BFS/DFS order, symmetric weights, reverse duplicate roads, invalid endpoints/weights/self-loops, edge and vertex removal, vertex-array expansion and compaction. |
| Service integration | Synchronized list/hash/tree updates and deletions, unique request IDs, pending-request deletion protection, no history-based undo, demo fixture. |
| Model-based integration | 600 deterministic add/update/delete steps, compared against an independent 50-slot reference array; checks every key and ordered record after every step. |
| Console | All 16 top-level choices, every extra submenu operation, invalid menu input, blank names, invalid ages/email/contact/GPA/distance, duplicate IDs/locations, missing students. |
| Empty/EOF | Empty list/tree/queue/stack/graph, missing hash result, input interruption during add/update with unchanged stored records. |

## Manual demonstration checklist

1. Launch empty, try options 4, 6, 7, 8, 9, 14, and 15, and observe clear empty/missing messages.
2. Add a student, try the same ID with different casing, then update their GPA and inspect options 4, 8, and 9.
3. Add multiple service requests through option 5 and process them using option 6. Observe FIFO order.
4. Display and peek/pop history through option 7. Confirm popping history leaves student data unchanged.
5. Use option 4 for name/ID searching, alphabetical sorting, GPA ranking, and visible hash buckets.
6. Launch demo mode and inspect campus connections. Traverse with BFS and DFS from Main Gate.
7. Remove Library and confirm its roads disappear from all remaining adjacency lists. Add an isolated location and traverse from it.
8. Replay `docs/demo-input.txt` against `Main --demo` and compare with `docs/sample-session.txt`.

These checks validate behavior and data structure invariants; they are not performance benchmarks. Very large unbalanced trees can exhaust the recursive BST's call stack; the interactive system uses the balanced AVL implementation. All user data is session-only.
