#!/usr/bin/python3

import argparse
import re

thread_prefix = "choreo-"
last_read_thread = ""

parser = argparse.ArgumentParser("log-filter")
parser.add_argument("input", help="Path to log file that needs to be filtered.", type=str)
parser.add_argument("output", help="Path to filtered output file.", type=str)
parser.add_argument("thread", help="Integer thread Id to be used to filter log", type=int)
args = parser.parse_args()

filter_str = thread_prefix + str(args.thread)

print("Log file path: " + args.input)
print("Out file path: " + args.output)
print("Log filtered by: " + filter_str)


def filter_line(line):
    matched = re.search(thread_prefix + '\\d', line)
    if matched:
        if line.find(filter_str) != -1:
            write_to_file(line)
        global last_read_thread
        last_read_thread = matched.group()
    else:
        if last_read_thread == filter_str:
            write_to_file(line)
		
def write_to_file(content):
    with open(args.output, "a") as outfile:
        outfile.write(content)


with open(args.input) as file:
    for line in file:
        filter_line(line)
	    
