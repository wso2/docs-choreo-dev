# AGENTS.md

## Project Overview

WSO2 Developer Platform (formerly Choreo) documentation site, built with **MkDocs** and the **Material for MkDocs** theme. The site uses a **dual-perspective documentation model**:

- `en/developer-docs/` — Developer view (application developers)
- `en/pe-docs/` — Platform Engineer view (platform operators/admins)

Each perspective has its own `mkdocs.yml`, navigation structure, and output directory. Changes may need to go into one or both perspectives depending on scope.

Published at: https://wso2.com/choreo/docs/

## Build & Development Commands

### Setup

```bash
cd en/
pip3 install -r requirements.txt
npm install -g markdown-spellcheck@1.3.1
npm install -g markdown-link-check@3.10.3
```

### Build and validate (spell-check + link-check + build)

```bash
cd en/
./serve.sh
```

### Serve locally for iterative development

```bash
# From en/developer-docs/ or en/pe-docs/
mkdocs serve                # Standard serve at localhost:8000
mkdocs serve --dirtyreload  # Faster reload (requires strict: false in mkdocs.yml)
```

### Build only (no validation)

```bash
mkdocs build --config-file en/developer-docs/mkdocs.yml
mkdocs build --config-file en/pe-docs/mkdocs.yml
```

## Architecture

### Dual-Perspective Model

Both perspectives share a custom theme (`en/theme/material/`) but have independent:
- `mkdocs.yml` config files with separate `nav` sections and `site_dir` outputs
- `docs/` directories with their own markdown content and assets
- Redirect maps (developer-docs has 50+ redirects; pe-docs has minimal)

The `mkdocs.yml` files contain `extra` metadata fields (`developer_docs_path`, `pe_docs_path`, `show_perspective_dropdown`) that drive the perspective selector UI.

### Key Directories

- `en/theme/material/` — Custom Material theme overrides (shared by both perspectives): base templates, home page template, custom CSS/JS
- `en/dictionary/` — Custom spell-check dictionary (`en_US-large`)
- `.github/workflows/ci.yml` — GitHub Actions: builds both doc sites, deploys to `gh-pages-v2` branch
- `.azure/` — Azure DevOps pipelines: production (`main` branch) and dev (`dev` branch) deployments to Azure Storage + CDN

### Content Structure Within Each Perspective

Markdown files live under `docs/` in each perspective directory. Assets (images, CSS, JS) are duplicated per perspective under `docs/assets/`. Navigation order is defined in the `nav` section of each `mkdocs.yml`.

### MkDocs Plugins in Use

`search`, `include-markdown` (file inclusion/reuse), `glightbox` (image lightbox), `open-in-new-tab`, `markdownextradata`, `redirects`, `minify`

### Markdown Extensions

Admonitions, definition lists, footnotes, PyMdown Extensions (tabs, task lists, superfences, snippets), code highlighting via Pygments, attribute lists.

## Contribution Workflow

1. Work on the `PE` branch (this is the main branch)
2. Determine if the change applies to developer-docs, pe-docs, or both
3. Run `./serve.sh` from `en/` to validate spelling and links before submitting
4. PR template has checkboxes for which perspective(s) are affected and tested
5. Before PR: ensure `strict: true` in mkdocs.yml (set to `false` only during local drafting)

## Important Conventions

- Page structure changes must update the redirect maps in `mkdocs.yml` to preserve existing URLs
- The `extra` section in `mkdocs.yml` defines product naming variables (`product_name`, `short_name`, `cli_root_name`) used across docs
- Style/tone guidelines are in the GitHub wiki: "Choreo Documentation Guidelines and Best Practices"
