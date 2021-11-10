#! /bin/sh
set -e
mdspell  -n -a -x --en-us docs/**/*.md -d dictionary/en_US-large
find . -type f -name '*.md' ! -path '/docs/**/*' | xargs -L1 markdown-link-check 
mkdocs serve
