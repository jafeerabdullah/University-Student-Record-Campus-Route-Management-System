#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
mode="${1:-build}"
case "$mode" in build|run|test) ;; *) echo 'Usage: sh scripts/build.sh [build|run|test] [--demo]'; exit 1 ;; esac
if [ -L out ]; then echo 'Refusing to clean a symbolic-link output directory.' >&2; exit 1; fi
project_root="$(pwd -P)"
if [ -e out ]; then
    if [ "$(cd out && pwd -P)" != "$project_root/out" ]; then
        echo 'Output directory is outside the expected project location.' >&2
        exit 1
    fi
    rm -rf -- "$project_root/out"
fi
mkdir -p out
find src -name '*.java' -print | sort > out/sources.txt
if [ "$mode" = test ]; then
    find tests -name '*.java' -print | sort >> out/sources.txt
fi
javac --release 17 -encoding UTF-8 -Xlint:all -d out @out/sources.txt
case "$mode" in
    test) java -cp out TestRunner ;;
    run) if [ "${2:-}" = '--demo' ]; then java -cp out Main --demo; else java -cp out Main; fi ;;
    build) echo 'Build successful (Java 17 target). Run: java -cp out Main --demo' ;;
esac
