# Sample outputs: student, queue and stack stage

Start with `java -cp out Main --demo` after building. Use option 4 to display the four sample student records; its submenu option 1 searches by ID through the linked list.

```text
Loaded 4 sample students.
Records are kept in memory for this session.
Options 8-15 will become available in a later update.
```

The demonstration transcript in [sample-session.txt](sample-session.txt) is captured from a real run driven by [demo-input.txt](demo-input.txt). It displays the students, searches for Asra (0002), creates/processes a transcript request for Abdullah (0001), and shows the unavailable-feature notices.

```text
Added to queue: REQ-1 | Student 0001 | Transcript | Printed transcript copy
Waiting requests (FIFO, next request first)
REQ-1 | Student 0001 | Transcript | Printed transcript copy
```

```text
Processed: REQ-1 | Student 0001 | Transcript | Printed transcript copy
```

Selecting option 8 or 9:

```text
Tree and hash searching features are not available yet.
```

Selecting an option from 10 through 15:

```text
Campus route features are not available yet.
```

These features become active only after the respective contributor PRs are accepted. The four sample students remain available independently of those future modules.
