# Mini In-Memory File System

## Build

```powershell
javac --release 8 *.java
```

## Run

```powershell
type input.txt | java Main
```

## Supported commands

- `MKDIR <absPath>`
- `TOUCH <absPath> <size>`
- `LS <absPath>`
- `INFO <absPath>`
- `FIND <absPath> <name>`
- `RM <absPath>`
- `LINK <srcAbsPath> <dstAbsPath>`

All paths are normalized before execution.
