import au.com.bytecode.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;

import soot.BodyTransformer;

import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;

import soot.Local;

import soot.options.Options;

import soot.PackManager;

import soot.PatchingChain;

import soot.RefType;

import soot.Scene;

import soot.SootClass;

import soot.SootMethod;

import soot.Transform;

import soot.Unit;


public class AndroidInstrument {

    public static void main(String[] args) {
        final String[] librariesToFilter = {"com.google.android.apps.analytics",
                                      "android.support",
                                      "com.mobisystems",
                                      "google.common",
                                      "google.gdata",
                                      "org.apache",
                                      "org.json.simple",
                                      "com.dropbox",
                                      "com.box.androidlib",
                                      "com.sony.nfx",
                                      "com.appattach.tracking",
                                      "com.google.web.bindery",
                                      "com.google.ads",
                                      "com.pontiflex",
                                      "com.google.gwt",
                                      "org.json",
                                      "net.lingala.zip4j",
                                      "org.mortbay",
                                      "org.jsoup",
                                      "com.inmobi.androidsdk",
                                      "com.Leadbolt.AdLog",
                                      "com.adsdk",
                                      "com.adfonic",
                                      "com.google.gson",
                                      "com.amazon.inapp",
                                      "com.facebook.android",
                                      "com.bugsense",
                                      "com.commonsware.cwac",
                                      "org.xbill.DNS",
                                      "com.kenai.jbosh",
                                      "org.codehaus.jackson"};
                                      
        String appNameTmp = "";
        for(int i = 0; i < args.length; ++i) {
            String arg = args[i];
            // is this the arg with the app name?
            if(arg.contains(".apk")) {
                // get file name from path
                appNameTmp = new File(arg).getName();
                // get rid of the .apk
                appNameTmp = appNameTmp.substring(0, appNameTmp.length()-4);
            }
        }

        if(appNameTmp.equals("")){
            System.out.println("Couldn't figure out app name, exiting.");
            System.exit(1);
        } else {
            System.out.println(appNameTmp);
        }

        final String appName = appNameTmp;

        String dataRoot = "/home/dpetters/Dropbox/Mach-lrn-prj/";
        String dataFilePath = dataRoot + "/" + appName + ".data.csv";
        File dataFile = new File(dataFilePath);
        String idFilePath = dataRoot + "/" + appName + ".ids.csv";
        File idFile = new File(idFilePath);

        if(dataFile.exists() && idFile.exists()){
            System.out.println("Data for app " + appName + " already exists.");
            System.exit(0);
        }

        final List<String> apiCallSigs = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("data/api-method-signatures"));
        } catch(FileNotFoundException e) {
            System.out.println("Couldn't find api-method-signatures: " + e.toString());
            System.exit(1);
        }
        String line;
        try {
            while((line=br.readLine()) != null) {
                apiCallSigs.add(line);
            }
        } catch(IOException e) {
            System.out.println("Couldn't read from api-method signatures: " + e.toString());
            System.exit(1);
        }
        try {
            br.close();
        } catch(IOException e) {
            System.out.println("Couldn't close api-method signatures: " + e.toString());
            System.exit(1);
        }

        final List<String> adLibPackages = new ArrayList<String>();
        try {
            br = new BufferedReader(new FileReader("data/adLibPackages"));
        } catch(FileNotFoundException e) {
            System.out.println("Couldn't find api-method-signatures: " + e.toString());
            System.exit(1);
        }
        try {
            while((line=br.readLine()) != null) {
                adLibPackages.add(line);
            }
        } catch(IOException e) {
            System.out.println("Couldn't read from adLibPackages: " + e.toString());
            System.exit(1);
        }
        try {
            br.close();
        } catch(IOException e) {
            System.out.println("Couldn't close adLibPackages: " + e.toString());
            System.exit(1);
        }

        try {
            final CSVWriter dataWriter = new CSVWriter(new FileWriter(dataFilePath));
            final CSVWriter idWriter = new CSVWriter(new FileWriter(idFilePath));

            //prefer Android APK files// -src-prec apk
            Options.v().set_src_prec(Options.src_prec_apk);

            // resolve the PrintStream and System soot-classes
            Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
            Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);

            PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

                @Override
                protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                    final PatchingChain<Unit> units = b.getUnits();

                    SootMethod containingMethod = b.getMethod();
                    String containingMethodSig = containingMethod.getSignature();
                    
                    for(String library : librariesToFilter){
                        if(containingMethodSig.contains(library))
                            return;
                    }

                    for(String adLibPackage : adLibPackages){
                        if(containingMethodSig.contains(adLibPackage))
                            return;
                    }

                    final List<String> idEntries = new ArrayList<String>();
                    final List<String> dataEntries = new ArrayList<String>();

                    idEntries.add(appName + ":" + containingMethodSig);

                    //important to use snapshotIterator here
                    for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
                        final Unit u = iter.next();
                        u.apply(new AbstractStmtSwitch() {

                            public void caseInvokeStmt(InvokeStmt stmt) {
                                InvokeExpr invokeExpr = stmt.getInvokeExpr();
                                SootMethod sootMethod = invokeExpr.getMethod();
                                String methodSignature = sootMethod.getSignature();

                                // if methodSignature is in the list, add entry
                                int index = apiCallSigs.indexOf(methodSignature)+1;
                                if(index != 0) {
                                    dataEntries.add(Integer.toString(index));
                                }
                            }

                        });
                    }

                    if(dataEntries.size() > 0) {
                        idWriter.writeNext(idEntries.toArray(new String[] {}));
                        dataWriter.writeNext(dataEntries.toArray(new String[] {}));
                    }
                }


            }));

            soot.Main.main(args);

            try {
                dataWriter.close();
                idWriter.close();
            } catch(IOException e) {
                System.out.println("Couldn't close writer: " + e.toString());
            }

        } catch(Exception e) {
            System.out.println("Couldn't open writer: " + e.toString());
            System.exit(1);
        }

    }
}
