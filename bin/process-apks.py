import argparse
import os
import subprocess

def main(**kawrgs):
    d = kawrgs["dir"]
    for app in os.listdir(d):
        p = subprocess.Popen(["java", "-cp", "bin/AndroidInstrument.jar:lib/apk-parser-1.1.jar:lib/soot.jar:lib/opencsv-2.3.jar", "AndroidInstrument",
            "-android-jars", "android-platforms", "-allow-phantom-refs", "-output-format", "n", "-process-dir", d+"/"+app], stdout=subprocess.PIPE)
        print "Processing " + app
        print p.stdout.read()
        p.communicate()

if __name__=="__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('dir', type=str)
    args = parser.parse_args()
    main(**vars(args))

