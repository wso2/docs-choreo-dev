#! /bin/sh

check_and_exit() {
    local exit_status=$1
    local err_msg=$2

    if [ $exit_status -eq 1 ]; then
        echo "[ERROR] $err_msg"
        exit 1
    fi
}

set -e

mdspell  -n -a --en-us docs/**/*.md -d dictionary/en_US-large
mdspell  -n -a --en-us docs/**/**/*.md -d dictionary/en_US-large
mdspell  -n -a --en-us mkdocs.yml -d dictionary/en_US-large
mdspell  -n -a --en-us theme/material/templates/home-page.html -d dictionary/en_US-large

find docs/** -type f -name '*.md'  | xargs -L1 markdown-link-check -c ./markdown-link-check-config.json  --quiet
exit_status="$?"
check_and_exit "$exit_status" "deadlinks found!"

find docs/**/* -type f -name '*.md' | xargs -L1 markdown-link-check -c ./markdown-link-check-config.json  --quiet
exit_status="$?"
check_and_exit "$exit_status" "deadlinks found!"

mkdocs build -c
exit_status="$?"
check_and_exit "$exit_status" "build failed!"
