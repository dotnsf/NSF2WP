package me.juge.nsf2wp;



public class test_nsf2wp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String server = "";
		String filepath = "dev/multilang.nsf";
//		String filepath = "dev/mydb.nsf";
//		String filepath = "dev/n_tips.nsf";
		
		try{
			String repid0 = NDJavaLib.GetRepid( server, filepath );
			System.out.println( "repid0 = " + repid0 );
			
			String repid1 = NDJavaLib.ExportDb( server, filepath, "", "" );
			System.out.println( "repid1 = " + repid1 );

//			NDJavaLib.GenerateXSLs( repid1 );
			
			int r1 = NDJavaLib.UploadFiles( repid1, "Subject" );
			System.out.println( "r1 = " + r1 );
			
			int r2 = NDJavaLib.postPostProcess( repid0 );
			System.out.println( "r2 = " + r2 );
		}catch( Exception e ){
			e.printStackTrace();
		}
	}

}
