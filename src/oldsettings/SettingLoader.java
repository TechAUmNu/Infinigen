package oldsettings;

import java.io.File;


public class SettingLoader {
	
	
	public void test(){
		 // The DLL needs to be loaded dynamically.
        // Adapt to your way of finding the file; here we assume it is in the current directory.
        // Load different DLLs according to whether the platform is 32 bit or 64 bit.
       /* String platform = System.getProperty("sun.arch.data.model").contains("64") ? "_x64" : "";
        System.load( new File( "" ).getAbsolutePath() + "\\lib\\WinFoldersJava\\WinFoldersJava" + platform + "-1.1.dll" );
        System.out.println( "CSIDL_PERSONAL\t\t" + WinFoldersJava.getSpecialFolderPath(WinFoldersJava.CSIDL_PERSONAL) );
        System.out.println( "CSIDL_APPDATA\t" + WinFoldersJava.getSpecialFolderPath(WinFoldersJava.CSIDL_APPDATA) );
        System.out.println( "CSIDL_LOCAL_APPDATA\t" + WinFoldersJava.getSpecialFolderPath(WinFoldersJava.CSIDL_LOCAL_APPDATA) );
        System.out.println( "CSIDL_MYPICTURES\t" + WinFoldersJava.getSpecialFolderPath(WinFoldersJava.CSIDL_MYPICTURES) );
        System.out.println( "CSIDL_COMMON_APPDATA\t" + WinFoldersJava.getSpecialFolderPath(WinFoldersJava.CSIDL_COMMON_APPDATA) );
        System.out.println( "CSIDL_COMMON_DOCUMENTS\t" + WinFoldersJava.getSpecialFolderPath(WinFoldersJava.CSIDL_COMMON_DOCUMENTS) );
	 */
	}
	
	
	
	public static void main(String[] args){
		new SettingLoader().test();
	}
}
