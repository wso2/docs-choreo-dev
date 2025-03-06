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
	"crypto/rand"
	"encoding/hex"
	"fmt"
	"time"
)

type name struct {
	seconds int64
	random  string
	prefix  string
}

func NewName(prefix string) *name {
	b := make([]byte, 2)
	rand.Read(b)

	return &name{
		seconds: time.Now().Unix(),
		random:  hex.EncodeToString(b),
		prefix:  prefix,
	}
}

func (n *name) NameWithSeparators() string {
	return fmt.Sprintf("%s%s-%d", n.prefix, n.random, n.seconds)
}

func (n *name) Name() string {
	return fmt.Sprintf("%s%s%d", n.prefix, n.random, n.seconds)
}
