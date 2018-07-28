#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys

file_path="./test_log.txt"
if len(sys.argv) > 1:
	file_path=sys.argv[1].strip()
submission_name=""
if len(sys.argv) > 2:
	submission_name=sys.argv[2].replace("/", "").strip()

result_line="Tests run: 0, Failures: 0, Errors: 0, Skipped: 0"

with open(file_path, 'r') as log_file:
	for line in log_file:
		line=line.strip()
		if "Tests run:" in line:
			result_line=line

result_line=result_line.replace(",", " ")

test_result_numbers = [int(s) for s in result_line.split() if s.isdigit()]

print '"'+submission_name+'"',
for n in test_result_numbers:
	print ",", n,
