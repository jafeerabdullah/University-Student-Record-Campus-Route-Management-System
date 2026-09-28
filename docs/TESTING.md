# Testing the complete integrated application

Run `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/build.ps1 test` on Windows or `sh scripts/build.sh test` in a POSIX shell. Both scripts clean generated output, compile for Java 17 with compiler lint checks, and run the dependency-free test runner.

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
PASS: 12 suites, 53409 assertions.
```

| Contribution | Verified behavior |
| --- | --- |
| Abdullah: students | Validation, duplicates, add/update/search, head/middle/tail deletion, reuse and record snapshots |
| Asra: queue/stack | FIFO/LIFO, peek, display, empty states, queue reuse and history-only removal |
| Dilsath: hash/tree/algorithms | Deliberate collisions, resizing, deletion, BST inorder ordering, all AVL rotation cases, randomized balance checks, binary/linear search and merge-sort ordering |
| Nifra: campus graph | Symmetric edges, cycles, isolated vertices, BFS/DFS order, invalid roads, vertex removal and index compaction |
| Shared integration | Index agreement after 600 deterministic mutations, pending-request protection, all 16 menu options, four exact sample students, invalid input and interrupted edits |

AVL tests insert/delete 400 keys and check actual height, ordering and balance. Hash tests verify lookup after growth and collision-chain deletion. Graph tests cover both connected and disconnected locations. Console tests assert the behavior of working features rather than unavailable-feature notices.

The earlier 7-suite result applied to the student/queue/stack-only stage. The current result covers all accepted contributions. Local results do not claim a GitHub Actions run; that workflow runs separately after pushing.
