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

package matcher

import (
	"encoding/json"
	"fmt"
)

type Result struct {
	Match     bool
	ErrorMsgs []string
}

const beginExpected = "==================== Begin Expected ====================\n"
const endExpected = "==================== End Expected ====================\n"
const beginActual = "==================== Begin Actual ====================\n"
const endActual = "==================== End Actual ====================\n"

func JsonMatch(expected []byte, actual []byte) (*Result, error) {
	expectedMap := make(map[string]interface{})
	actualMap := make(map[string]interface{})

	err := json.Unmarshal(expected, &expectedMap)

	if err != nil {
		return nil, err
	}

	err = json.Unmarshal(actual, &actualMap)

	if err != nil {
		return nil, err
	}

	// We could have chosen to do a deep equal comparison here, but we chose to do a manual comparison
	// because we want to ignore certain fields in the actual response that may have been added later that
	// are not being used, but whose precesense would cause the comparison to fail.
	// return reflect.DeepEqual(expectedMap, actualMap), nil

	result := &Result{
		Match:     true,
		ErrorMsgs: make([]string, 0, len(expectedMap)),
	}

	compareValues(expectedMap, actualMap, result)

	return result, nil
}

func compareValues(expectedMap map[string]interface{}, actualMap map[string]interface{}, result *Result) error {
	for key, value := range expectedMap {
		actualValue, ok := actualMap[key]

		if !ok {
			result.Match = false
			result.ErrorMsgs = append(result.ErrorMsgs, "Key "+key+" not found in actual response")
		} else {
			switch value.(type) {
			case float64, bool, nil: // Non string single value types
				if value != actualValue {
					result.Match = false
					result.ErrorMsgs = append(result.ErrorMsgs,
						fmt.Sprintf("Expected values '%v' does not match actual value '%v'", value, actualValue))
				}
			case []interface{}: // Array types
				ok, err := compareArrays(value.([]interface{}), actualValue.([]interface{}))

				if err != nil {
					return err
				}

				if !ok {
					result.Match = false
					indentedValue, _ := json.MarshalIndent(value, "", "    ")
					indentedActualValue, _ := json.MarshalIndent(actualValue, "", "    ")
					result.ErrorMsgs = append(result.ErrorMsgs,
						fmt.Sprintf("Arrays do not match %s '%s' %s %s '%s' %s",
							beginExpected, string(indentedValue), endExpected,
							beginActual, string(indentedActualValue), endActual))
				}
			case string:
				if value != "@ignore@" && value != actualValue {
					result.Match = false
					result.ErrorMsgs = append(result.ErrorMsgs,
						fmt.Sprintf("Expected values '%s' does not match actual value '%s'", value, actualValue))
				}
			case map[string]interface{}: // Object type
				ok, err := isObjectContains(value.(map[string]interface{}), actualValue.(map[string]interface{}))

				if err != nil {
					return err
				}

				if !ok {
					result.Match = false
					indentedValue, _ := json.MarshalIndent(value, "", "    ")
					indentedActualValue, _ := json.MarshalIndent(actualValue, "", "    ")
					result.ErrorMsgs = append(result.ErrorMsgs,
						fmt.Sprintf("Objects do not match\n %s '%s' %s %s '%s' %s",
							beginExpected, string(indentedValue), endExpected,
							beginActual, string(indentedActualValue), endActual))
				}
			default:
				return fmt.Errorf("unhandled data type %T detected for value %v", value, value)
			}

		}
	}

	return nil
}

func compareArrays(array1 []interface{}, array2 []interface{}) (bool, error) {
	if len(array1) != len(array2) {
		return false, nil
	}

	var isFound = make([]bool, len(array1))

	for i, v1 := range array1 {
		for _, v2 := range array2 {
			switch v1.(type) {
			case map[string]interface{}: // Object type
				var err error
				isFound[i], err = isObjectContains(v1.(map[string]interface{}), v2.(map[string]interface{}))
				if err != nil {
					return false, err
				}
			case float64, bool, nil:
				isFound[i] = v1 == v2
			case string:
				if v1 != "@ignore@" && v1 != v2 {
					isFound[i] = false
				} else {
					isFound[i] = true
				}
			case []interface{}: // Array types
				var err error
				isFound[i], err = compareArrays(v1.([]interface{}), v2.([]interface{}))
				if err != nil {
					return false, err
				}
			default:
				return false, fmt.Errorf("unhandled data type %T detected for value %v", v1, v1)
			}

			if isFound[i] {
				break
			}
		}
	}

	for _, ok := range isFound {
		if !ok {
			return false, nil
		}
	}

	return true, nil
}

func isObjectContains(subObject map[string]interface{}, target map[string]interface{}) (bool, error) {
	for k1, v1 := range subObject {
		v2, ok := target[k1]

		if !ok {
			return false, nil
		} else {
			switch v1.(type) {
			case map[string]interface{}: // Object type
				ok, err := isObjectContains(v1.(map[string]interface{}), v2.(map[string]interface{}))

				if err != nil || !ok {
					return ok, err
				}
			case float64, bool, nil:
				if v1 != v2 {
					return false, nil
				}
			case string:
				if v1 != "@ignore@" && v1 != v2 {
					return false, nil
				}
			case []interface{}: // Array types
				ok, err := compareArrays(v1.([]interface{}), v2.([]interface{}))

				if err != nil || !ok {
					return ok, err
				}
			default:
				return false, fmt.Errorf("unhandled data type %T detected for value %v", v1, v1)
			}
		}
	}

	return true, nil
}
