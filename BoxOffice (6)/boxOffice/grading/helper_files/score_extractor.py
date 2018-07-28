#!/usr/bin/python
# -*- coding: UTF-8 -*-

import xml.etree.ElementTree as parser
import sys
import os.path

def get_tescase_weights(weights_file_name):
    weights = {}
    with open(weights_file_name) as f:
        for line in f.readlines():
            if ',' in line:
                tname, tweight = line.split(',')
                tname = tname.strip()
                tweight = int(tweight)
                weights[tname]=tweight
    return weights;

    #weights = {}
    #weights['testBestSeatAlmostFull'     ] = 1
    #weights['testBestSeatFull'           ] = 1
    #weights['testBestSeatHalfFull'       ] = 1
    #weights['testBestSeatEmpty'          ] = 1
    #weights['testBestSeatDouble'         ] = 1
    #weights['testContextSwitch'          ] = 3
    #weights['testPrintTicket'            ] = 3
    #weights['testTransactionLogOrder'    ] = 3
    #weights['testTransactionLogUnique'   ] = 3
    #return weights;


def get_failed_tests(tree):
    result = []
    for t in tree.findall('testcase'):
        if len(t.findall('error')) + len(t.findall('failure')) > 0:
            result.append(t.attrib['name'])
    return result

def calculate_grade(weights, failures):
    total = 0
    loss = 0
    for w in weights:
        total += weights[w]
    for f in failures:
        loss += weights[f]
    return total - loss

def main(argv):
    if len(argv) < 4:
        print 'too few arguments'
        exit(1)
    path = argv[1]
    submission_name=argv[2].replace('/', '').strip()
    weights_file_name = argv[3]

    if os.path.exists(path):
        tree = parser.parse(path).getroot()
        weights = get_tescase_weights(weights_file_name)
        ftests = get_failed_tests(tree)
        if len(ftests) is 1 and (ftests[0]=='assignment6.A6AllTest' or ftests[0]=='assignment6.A6SampleTest'):
            grade = 0
        else:
            grade = calculate_grade(weights, ftests)
        print '"'+submission_name+'", ', grade, ', ""'
    else:
        print '"'+submission_name+'", ', 0, ', "compile_error"'

main(sys.argv)
