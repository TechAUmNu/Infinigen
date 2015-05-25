package newUtility;

import java.io.File;

public class OSValidator {

    private static String OS = System.getProperty("os.name").toLowerCase();


    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }

    public static boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
    public static String getOS(){
        if (isWindows()) {
            return "win";
        } else if (isMac()) {
            return "osx";
        } else if (isUnix()) {
            return "uni";
        } else if (isSolaris()) {
            return "sol";
        } else {
            return "err";
        }
    }
    
    public static void setCorrectNativesLocation(){
    	if (isWindows()) {
    		System.setProperty("org.lwjgl.librarypath", new File("natives/windows").getAbsolutePath());
        } else if (isMac()) {
        	System.setProperty("org.lwjgl.librarypath", new File("natives/macosx").getAbsolutePath());
        } else if (isUnix()) {
        	System.setProperty("org.lwjgl.librarypath", new File("natives/linux").getAbsolutePath());
        } else if (isSolaris()) {
        	System.setProperty("org.lwjgl.librarypath", new File("natives/solaris").getAbsolutePath());
        } else {
            System.exit(-1);
        }
    }

}