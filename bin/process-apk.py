<target name="run" depends="compile">                                                                                
        <java classname="AndroidInstrument">                                                                             
            <arg value="-android-jars"/>                                                                                 
            <arg value="android-platforms"/>                                                                             
            <arg value="-allow-phantom-refs"/>                                                                           
            <arg value="-process-dir"/>                                                                                  
            <arg value="apps/apks/AlZakat.apk"/>                                                                         
            <classpath>                                                                                                  
                <pathelement path="lib/soot.jar"/>                                                                       
                <pathelement path="lib/"/>                                                                               
                <pathelement path="classes/"/>                                                                           
            </classpath>                                                                                                 
        </java>
