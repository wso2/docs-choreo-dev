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

package config

import (
	"regexp"
	"testing"
)

func TestDefinitionOrder(t *testing.T) {
	begin := Definition(_begin_def_)
	end := Definition(_end_def_)

	if Definition.String(begin-1) != "Definition(-1)" {
		t.Errorf("Definition const %s is defined before %s", Definition.String(begin-1), Definition.String(begin))
	}

	// As enum values are added the exact value of end will change
	// So we need to do a regex match
	matched, err := regexp.MatchString("Definition\\(\\d+\\)", Definition.String(end+1))

	if err != nil || !matched {
		t.Errorf("Definition const %s is defined after %s", Definition.String(end+1), Definition.String(end))
	}
}

func TestOptionalDefinitionOrder(t *testing.T) {
	begin := OptionalDefinition(_begin_def_)
	end := OptionalDefinition(_end_def_)

	if OptionalDefinition.String(begin-1) != "OptionalDefinition(-1)" {
		t.Errorf("OptionalDefinition const %s is defined before %s", OptionalDefinition.String(begin-1), OptionalDefinition.String(begin))
	}

	// As enum values are added the exact value of end will change
	// So we need to do a regex match
	matched, _ := regexp.MatchString("OptionalDefinition\\(\\d+\\)", OptionalDefinition.String(end+1))

	if !matched {
		t.Errorf("OptionalDefinition const %s is defined after %s", OptionalDefinition.String(end+1), OptionalDefinition.String(end))
	}
}
