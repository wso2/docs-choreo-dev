#! /bin/sh
set -e
mdspell  -n -a --en-us docs/**/*.md -d dictionary/en_US-large
mdspell  -n -a --en-us mkdocs.yml -d dictionary/en_US-large
mdspell  -n -a --en-us theme/material/templates/home-page.html -d dictionary/en_US-large
find docs/** -type f -name '*.md' ! -path 'docs/**/*' | xargs -L1 markdown-link-check 
find docs/**/* -type f -name '*.md' ! -path 'docs/**/*' | xargs -L1 markdown-link-check 
mkdocs serve
