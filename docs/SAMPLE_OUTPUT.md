# Sample outputs: all four contributions integrated

Build and launch with `java -cp out Main --demo`. The full captured execution is [sample-session.txt](sample-session.txt), driven by [demo-input.txt](demo-input.txt).

## AVL display (option 8)

```text
ID             Name                   Programme              GPA
0001           Abdullah               BAIT                   3.75
0002           Asra                   IT                     3.50
0003           Dilsath                Computer Science       3.90
0004           Nifra                  Computer Science       3.60
Total students: 4
```

Option 9 with ID `0002` returns Asra's complete student record using the hash table.

## GPA ranking (option 4, tool 6)

```text
1. 0003 | Dilsath | Computer Science | GPA 3.90
2. 0001 | Abdullah | BAIT | GPA 3.75
3. 0004 | Nifra | Computer Science | GPA 3.60
4. 0002 | Asra | IT | GPA 3.50
```

## Service queue (options 5 and 6)

```text
Processed: REQ-1 | Student 0001 | Transcript | Printed transcript copy
```

## Campus traversal (option 15)

```text
BFS traversal: Main Gate -> Library -> Engineering Faculty -> Computer Laboratory -> Cafeteria -> Lecture Hall -> Hostel
Visited 7 of 7 locations from the starting location.
```

```text
DFS traversal: Main Gate -> Library -> Computer Laboratory -> Lecture Hall -> Engineering Faculty -> Cafeteria -> Hostel
Visited 7 of 7 locations from the starting location.
```

The demo graph has seven locations and eight bidirectional roads with illustrative distances. Routes can be inspected through option 14 and edited through options 10-13. All main-menu options are active.
