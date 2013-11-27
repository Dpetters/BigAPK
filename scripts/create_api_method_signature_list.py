#!/usr/bin/python

import argparse
import re

def main(**kwargs):
    f = open(kwargs['input'])
    o = open(kwargs['output'], 'w')
    ccoi = None
    for l in f:
        l = l.strip()
        m = re.match('^.*(class|interface)\s(?P<coi>([a-zA-Z_$][a-zA-Z0-9_$]*\.)*[a-zA-Z_$][a-zA-Z0-9_$]*).*{$', l)
        if m != None:
            ccoi = m.group('coi')
        else:
            l = re.sub(r'^(public )?(abstract )?(static )?(native )?(synchronized )?(volatile )?', '', l)
            l = re.sub(r', ', ',', l)
            l = l[:-1]
            o.write("<" + ccoi + ": " + l + ">\n")
    f.close()
    o.close()


if __name__=='__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('input', type=str)
    parser.add_argument('output', type=str)
    args = parser.parse_args()
    main(**vars(args));
