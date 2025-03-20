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

package logger

import (
	"log"
	"os"
)

type TestLogger struct {
	filepath string
	name     string
}

func NewTestLogger(name string, filepath string) *TestLogger {
	return &TestLogger{
		filepath: filepath,
		name:     name,
	}
}

func (l *TestLogger) Errorf(format string, v ...interface{}) {
	l.write(format, v...)
}

func (l *TestLogger) Warnf(format string, v ...interface{}) {
	l.write(format, v...)
}

func (l *TestLogger) Debugf(format string, v ...interface{}) {
	l.write(format, v...)
}

func (l *TestLogger) write(format string, v ...interface{}) {
	f, err := os.OpenFile(l.filepath, os.O_APPEND|os.O_RDWR|os.O_CREATE, 0644)
	if err != nil {
		log.Panic(err)
	}

	defer f.Close()

	logFile := log.New(f, l.name, log.LstdFlags)
	logFile.Printf(format, v...)
}
