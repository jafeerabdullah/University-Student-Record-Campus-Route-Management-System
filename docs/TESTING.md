# Testing the currently integrated modules

Run `powershell -NoProfile -ExecutionPolicy Bypass -File scripts/build.ps1 test` on Windows or `sh scripts/build.sh test` in a POSIX shell. Both scripts compile for Java 17 and run the dependency-free test runner.

Current result:

```text
PASS: Student validation and normalization
PASS: Singly linked list mutations
PASS: FIFO queue and LIFO stack
PASS: Student, queue and stack integration
PASS: Four requested sample students
PASS: Available menu operations and pending features
PASS: Empty structures and interrupted input
PASS: 7 suites, 98 assertions.
```

| Area | Verified behavior |
| --- | --- |
| Student validation | ID normalization, duplicate/empty data, age bounds, email/contact checks, finite GPA limits |
| Linked list | Add/update/search, head/middle/tail/sole deletion, reuse after empty, independent snapshots |
| Queue | FIFO, peek, waiting requests, empty processing and front/rear reset |
| Stack | LIFO, peek/pop/display, empty stack and history-only removal |
| Integration | Student changes, existing-student requests, unique request IDs, pending-request deletion protection |
| Demo | Exact IDs 0001-0004, names, programmes and GPA values supplied by the user |
| Console | Working options 1-7 and 16; options 8-15 safely report unavailable without modifying records/history |
| EOF | Interrupted student creation/editing leaves existing records intact |

Hashing, trees, algorithm packages and campus routes are absent from this stage of `main`. Their tests will be integrated with the relevant contributor pull requests. Earlier full-application test counts describe a previous scope and do not apply to this version.
