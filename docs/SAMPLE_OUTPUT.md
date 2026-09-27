# Sample execution outputs

Build and launch with `--demo` for these fictional records and illustrative campus roads. The full captured output is in [sample-session.txt](sample-session.txt); the corresponding input sequence is [demo-input.txt](demo-input.txt). Input is not echoed when the script is piped, so some prompts share a line in the transcript.

## Student display: option 8

```text
Students using AVL inorder traversal (Student ID ascending)
------------------------------------------------------------
ID             Name                   Programme              GPA
23DA2-0001     Ahmed                  BAIT                   3.75
23DA2-0002     Ali                    IT                     3.50
23DA2-0003     Hassan                 Computer Science       3.90
Total students: 3
```

## Hash lookup: option 9, ID 23DA2-0002

```text
Student ID       : 23DA2-0002
Student Name     : Ali
Age              : 22
Gender           : Male
Degree Programme : IT
Email            : ali@example.com
Contact Number   : 0772345678
Address          : Kandy
GPA              : 3.50
```

## GPA ranking: option 4, then tool 6

```text
GPA ranking (merge sort; equal GPAs share a rank)
1. 23DA2-0003 | Hassan | Computer Science | GPA 3.90
2. 23DA2-0001 | Ahmed | BAIT | GPA 3.75
3. 23DA2-0002 | Ali | IT | GPA 3.50
```

## Service queue: option 5, then tool 1; process with option 6

```text
Added to queue: REQ-1 | Student 23DA2-0001 | Transcript | Printed transcript copy
Waiting requests (FIFO, next request first)
REQ-1 | Student 23DA2-0001 | Transcript | Printed transcript copy
```

```text
Processed: REQ-1 | Student 23DA2-0001 | Transcript | Printed transcript copy
```

## Campus connections: option 14

```text
Campus connections (bidirectional; metres)
Main Gate -> Library (150.00 m), Engineering Faculty (300.00 m)
Library -> Main Gate (150.00 m), Computer Laboratory (100.00 m), Cafeteria (120.00 m)
Engineering Faculty -> Main Gate (300.00 m), Lecture Hall (80.00 m)
Computer Laboratory -> Library (100.00 m), Lecture Hall (90.00 m)
Lecture Hall -> Engineering Faculty (80.00 m), Computer Laboratory (90.00 m), Cafeteria (140.00 m)
Cafeteria -> Library (120.00 m), Lecture Hall (140.00 m), Hostel (250.00 m)
Hostel -> Cafeteria (250.00 m)
7 locations, 8 roads
```

## Traversals: option 15, start at Main Gate

```text
BFS traversal: Main Gate -> Library -> Engineering Faculty -> Computer Laboratory -> Cafeteria -> Lecture Hall -> Hostel
Visited 7 of 7 locations from the starting location.
```

```text
DFS traversal: Main Gate -> Library -> Computer Laboratory -> Lecture Hall -> Engineering Faculty -> Cafeteria -> Hostel
Visited 7 of 7 locations from the starting location.
```

## Validation examples

```text
Select an option: hello
Please enter a valid number.
Select an option: 99
Choose a number from 1 to 16.
```

```text
GPA (0.00-4.00): NaN
GPA must be a finite number between 0.00 and 4.00.
GPA (0.00-4.00): 4.5
GPA must be a finite number between 0.00 and 4.00.
GPA (0.00-4.00): 3.75
```

These validation cases are also exercised by the automated console tests.
