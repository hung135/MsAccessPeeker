/**
 * 
 */
package com.hung135;

import java.io.File;
import java.io.FileFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author hung
 *
 */
class test{
	int t=0;
	test(int tt){
		t=tt;
	}
	
	
}

class maxin {

	public static void print_columns(String filePath) throws SQLException {
		try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);

			try (ResultSet rsMD = conn.getMetaData().getColumns(null, null, null, null)) {

				int i = 0;
				while (rsMD.next()) {
					String colName = rsMD.getString("COLUMN_NAME");
					String tblName = rsMD.getString("TABLE_NAME");
					String dataType = rsMD.getString("DATA_TYPE");
					System.out.println(tblName + ",\t" + colName + ",\t" + dataType);
					i++;
				}
				System.out.println("Total Columns: " + i);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<String> getAccessFiles(List<String> dirs,int depth) throws InterruptedException {

		List<String> fileList = new ArrayList<>();

		List<String> subDirs = new ArrayList<>();

		for (String directory : dirs) {
			System.out.println("Looking in :"+directory+"--------");
			File folder = new File(directory);
			if (directory.toString().equals("/Users/nguyenhu/Desktop/census/2010_AIANSF_MSAccessShell")) {
				//System.out.println("ohhh");

				//TimeUnit.SECONDS.sleep(5);
			}
			File[] files = folder.listFiles(new FileFilter() {
				private final FileNameExtensionFilter filter = new FileNameExtensionFilter("MsAccess files", "mdb",
						"accdb");

				public boolean accept(File file) {
					if (file.toString().contains("/hsqldb-2.4.1")) {
						return false;
					}
					if (file.toString().contains("/DbVisualizer")) {
						return false;
					}
					if (file.toString().contains("/commons-lang")) {
						return false;
					}
					return filter.accept(file);
				}

			});
			
			
			
			for (File i : files) {
				if (i.isFile()) {
					// System.out.println(i);
					fileList.add(i.toString());
					System.out.println("Access File Found: \n\t" + i + "\n\t" + directory);
				} else {

					subDirs.add(i.toString());
					System.out.println("Adding toRecurse: "+i.toString() );

				}

			}

			if (subDirs.size() > 0 && depth<3) {
				List<String> subFiles = getAccessFiles(subDirs,depth+1);

				for (String i : subFiles) {
					fileList.add(i.toString());
				}
			}

		}
		return fileList;
	}

	public static void main(String[] args) throws SQLException, InterruptedException {
		List<String> dirs = new ArrayList<>();

		dirs.add("/Users/nguyenhu/Desktop/");
		 

		List<String> files = getAccessFiles(dirs,0);
		// print_columns(filePath);

		for (String i : files) {
			int ii=1;
			//System.out.println(i);
		}

	}

}

public class MsAccessPeeker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("test");

	}

}
