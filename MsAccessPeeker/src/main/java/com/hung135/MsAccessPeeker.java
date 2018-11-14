/**
 * 
 */
package com.hung135;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * @author hung
 *
 */
class utils {
	public static void printFileInfo(String filename) throws IOException {
		HashCode hash = com.google.common.io.Files.hash(new File(filename), Hashing.md5());
		System.out.println("\t"+ hash.toString());
		
		File f = new File(filename);
		System.out.println("\tLastModified: "+ (new Date(f.lastModified())));
 
		
	}
	
	public static List<String> getAccessFiles(List<String> dirs, int depth) throws InterruptedException {

		List<String> fileList = new ArrayList<>();

		List<String> subDirs = new ArrayList<>();
		// System.out.println(subDirs.size()+"------size");

		for (String directory : dirs) {
			// System.out.println("Looking in :" + directory + "--------");
			File folder = new File(directory);
			if (directory.toString().equals("/Users/nguyenhu/Desktop/census/2010_AIANSF_MSAccessShell")) {
				// System.out.println("ohhh");

				// TimeUnit.SECONDS.sleep(5);
			}
			File[] files = folder.listFiles(new FileFilter() {
				private final FileNameExtensionFilter filter = new FileNameExtensionFilter("MsAccess files", "mdb",
						"accdb");

				public boolean accept(File file) {
					boolean x = true;
					if (file.toString().contains("/hsqldb-2.4.1")) {
						return false;
					}
					if (file.toString().contains("/DbVisualizer")) {
						return false;
					}
					if (file.toString().contains("/commons-lang")) {
						return false;
					}
					x = filter.accept(file);
					return x;
				}

			});

			if (files != null) {

				for (File i : files) {

					if (i.isFile()) {
						// System.out.println(i);
						fileList.add(i.toString());
						System.out.println("Access File Found: \n\t" + i);

						try {
						utils.printFileInfo(i.toString());
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						
						subDirs.add(i.toString());
						// System.out.println("Adding toRecurse: " + i.toString());

					}

				}
			}
			// System.out.println(subDirs.size() + "------size");

			if (subDirs.size() > 0 && depth < 6) {

				for (String aDir : subDirs) {
					List<String> aSubDir = new ArrayList<>();
					aSubDir.add(aDir);
					// System.out.println(subDirs.size() + "------size---Recursing\n\t\t" +
					// subDirs.toString());
					List<String> subFiles = getAccessFiles(aSubDir, depth + 1);

					for (String i : subFiles) {
						fileList.add(i.toString());
					}
				}
			}

		}

		return fileList;
	}

}

public class MsAccessPeeker {
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

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		List<String> dirs = new ArrayList<>();
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
 	
	 
		
		dirs.add(s);
		 

		List<String> files = utils.getAccessFiles(dirs, 0);
		// print_columns(filePath);

		for (String i : files) {
			int ii = 1;
			// System.out.println(i);
		}

	}

}
