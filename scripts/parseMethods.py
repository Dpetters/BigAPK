
#!/usr/bin/python

import argparse
import re

def main(**kwargs):
    f = open(kwargs['input'])
    o = open(kwargs['output'], 'w')
    for l in f:
	n = ""
	s = l.split(':')
	if len(s[-1].lstrip().split(' '))==1:
		o.write( str(s[0])+": void <init>(" + str(s[-1].lstrip().split("(")[1]))
	else:
		o.write(l)
		


if __name__=='__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('input', type=str)
    parser.add_argument('output', type=str)
    args = parser.parse_args()
    main(**vars(args));
