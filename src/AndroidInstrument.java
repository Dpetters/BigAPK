import java.io.File;
import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;


public class AndroidInstrument {
    
    public static void main(String[] args) {
        
        String appName = "";
        for(int i = 0; i < args.length; ++i) {
            String arg = args[i];
            // is this the arg with the app name?
            if(arg.contains(".apk")) {
                // get file name from path
                appName = new File(arg).getName();
                // get rid of the .apk
                appName = appName.substring(0, appName.length()-4);
            }
        }
        if(appName.equals("")){
            System.out.println("Couldn't figure out app name, exiting.");
            System.exit(1);
        } else {
            System.out.println(appName);
        }

        //prefer Android APK files// -src-prec apk
        Options.v().set_src_prec(Options.src_prec_apk);
        
        // resolve the PrintStream and System soot-classes
        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);

        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {

            @Override
            protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
                final PatchingChain<Unit> units = b.getUnits();
                
                //important to use snapshotIterator here
                for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
                    final Unit u = iter.next();
                    u.apply(new AbstractStmtSwitch() {
                        
                        public void caseInvokeStmt(InvokeStmt stmt) {
                            InvokeExpr invokeExpr = stmt.getInvokeExpr();
                            if(invokeExpr.getMethod().getName().equals("onDraw")) {
                            }
                        }
                        
                    });
                }
            }


        }));
        
        soot.Main.main(args);
    }
}
