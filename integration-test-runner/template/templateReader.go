/*
 * Copyright © 2025 WSO2 LLC. (http://www.wso2.com).
 *
 * This software is the property of WSO2 LLC and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package template

import (
	"bytes"
	"embed"
	"fmt"
	"os"
	"strings"
	"text/template"
)

var requestTemplates = make(map[string]*template.Template)
var responseTemplates = make(map[string]*template.Template)

//go:embed request/*
var requestTemplatesFS embed.FS

//go:embed response/*
var responseTemplatesFS embed.FS

func PopulateRequestTemplate(name string, model any) (*bytes.Buffer, error) {
	tmpl, ok := requestTemplates[name]

	if !ok {
		return nil, fmt.Errorf("request template for '%s' not found", name)
	}

	return renderTemplate(tmpl, model)
}

func PopulateResponseTemplate(name string, model any) (*bytes.Buffer, error) {
	tmpl, ok := responseTemplates[name]

	if !ok {
		return nil, fmt.Errorf("response template for '%s' not found", name)
	}

	return renderTemplate(tmpl, model)
}

func LoadTemplates() error {
	err := loadTemplates(requestTemplatesFS, "request", requestTemplates)
	if err != nil {
		return err
	}

	err = loadTemplates(responseTemplatesFS, "response", responseTemplates)
	if err != nil {
		return err
	}

	return nil
}

func renderTemplate(tmpl *template.Template, model any) (*bytes.Buffer, error) {
	var buf bytes.Buffer
	err := tmpl.Execute(&buf, model)

	if err != nil {
		return nil, fmt.Errorf("failed to render template: %w", err)
	}

	return &buf, nil
}

func loadTemplates(fs embed.FS, dir string, templateStore map[string]*template.Template) error {
	files, err := fs.ReadDir(dir)

	if err != nil {
		return fmt.Errorf("failed to read directory %s in loadTemplates(): %w", dir, err)
	}

	for _, f := range files {
		if f.IsDir() {
			continue
		}

		name := f.Name()

		withoutExtension, found := strings.CutSuffix(name, ".tmpl")

		if found {
			tmpl, err := template.ParseFS(fs, dir+string(os.PathSeparator)+name)
			if err != nil {
				return fmt.Errorf("failed to parse temaplate %s/%s in loadTemplates(): %w", dir, name, err)
			}

			templateStore[withoutExtension] = tmpl

		} else {
			return fmt.Errorf("template file %s has invalid extension. files should end with *.tmpl", name)
		}
	}

	return nil
}
