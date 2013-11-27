import argparse
import subprocess

def main(**kawrgs):
    apk_path = kawrgs["apk"]
    p = subprocess.Popen(["java", "-cp", "bin/AndroidInstrument.jar:lib/soot.jar", "AndroidInstrument",
        "-android-jars", "android-platforms", "-allow-phantom-refs", "-process-dir", apk_path], stdout=subprocess.PIPE)
    print p.stdout.read()

if __name__=="__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('apk', type=str)
    args = parser.parse_args()
    main(**vars(args))

