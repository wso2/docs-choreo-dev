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

package name

import (
	"fmt"
	"strings"
	"testing"
	"time"
)

func TestNewName(t *testing.T) {
	testCases := []struct {
		name   string
		prefix string
	}{
		{
			name:   "should create name with empty prefix",
			prefix: "",
		},
		{
			name:   "should create name with prefix",
			prefix: "test-",
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			// Act
			result := NewName(tc.prefix)

			// Assert
			if result.prefix != tc.prefix {
				t.Errorf("Expected prefix %q, got %q", tc.prefix, result.prefix)
			}

			// Random string should be 4 chars long (2 bytes as hex)
			if len(result.random) != 4 {
				t.Errorf("Expected random part to be 4 chars long, got %d chars: %q",
					len(result.random), result.random)
			}

			// Timestamp should be around current time
			now := time.Now().Unix()
			timeDiff := now - result.seconds
			if timeDiff < 0 || timeDiff > 1 {
				t.Errorf("Expected timestamp to be close to current time, got difference of %d seconds", timeDiff)
			}
		})
	}
}

func TestNameWithSeparators(t *testing.T) {
	testCases := []struct {
		name   string
		n      *name
		format string
	}{
		{
			name:   "should format name with separators and prefix",
			n:      &name{seconds: 1234567890, random: "abcd", prefix: "test-"},
			format: "test-abcd-1234567890",
		},
		{
			name:   "should format name with separators and empty prefix",
			n:      &name{seconds: 1234567890, random: "abcd", prefix: ""},
			format: "abcd-1234567890",
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			// Act
			result := tc.n.NameWithSeparators()

			// Assert
			if result != tc.format {
				t.Errorf("Expected format %q, got %q", tc.format, result)
			}
		})
	}
}

func TestName(t *testing.T) {
	testCases := []struct {
		name   string
		n      *name
		format string
	}{
		{
			name:   "should format name without separators and with prefix",
			n:      &name{seconds: 1234567890, random: "abcd", prefix: "test-"},
			format: "test-abcd1234567890",
		},
		{
			name:   "should format name without separators and with empty prefix",
			n:      &name{seconds: 1234567890, random: "abcd", prefix: ""},
			format: "abcd1234567890",
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			// Act
			result := tc.n.Name()

			// Assert
			if result != tc.format {
				t.Errorf("Expected format %q, got %q", tc.format, result)
			}
		})
	}
}

func TestIntegration(t *testing.T) {
	// Test that a newly created name produces expected format
	prefix := "auto-"
	n := NewName(prefix)

	withSeparators := n.NameWithSeparators()
	without := n.Name()

	// Check NameWithSeparators format
	expected := fmt.Sprintf("%s%s-%d", n.prefix, n.random, n.seconds)
	if withSeparators != expected {
		t.Errorf("NameWithSeparators(): expected %q, got %q", expected, withSeparators)
	}

	// Check Name format
	expected = fmt.Sprintf("%s%s%d", n.prefix, n.random, n.seconds)
	if without != expected {
		t.Errorf("Name(): expected %q, got %q", expected, without)
	}

	// Check that NameWithSeparators has a hyphen before timestamp
	if !strings.Contains(withSeparators, "-"+fmt.Sprintf("%d", n.seconds)) {
		t.Errorf("Expected NameWithSeparators to have hyphen before timestamp, got %q", withSeparators)
	}

	// Check that Name does not have a hyphen before timestamp
	if strings.Contains(without, "-"+fmt.Sprintf("%d", n.seconds)) {
		t.Errorf("Expected Name to not have hyphen before timestamp, got %q", without)
	}
}
