/**
 * 
 */
package com.hung135;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
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

import org.apache.commons.collections4.ListUtils;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * @author hung
 *
 */
class utils {

	private static final String[] FILTER_EXTENSION = { ".exe.mdb", ".dll.mdb" };

	public static void writeLogFile(File fileName, List<String> rowData) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName.toString(), true));
		for (String row : rowData) {
			writer.append(row);
		}

		writer.close();
	}

	public static void printFileInfo(String filename, File outLogFile) throws IOException {
		HashCode hash = com.google.common.io.Files.hash(new File(filename), Hashing.md5());
		List<String> row = new ArrayList<String>();

		File f = new File(filename);
		System.out.println("\t" + hash.toString());
		System.out.println("\tLastModified: " + (new Date(f.lastModified())));

		if (outLogFile != null && outLogFile.exists()) {

			if (outLogFile != null) {
				row.add("File: " + filename);
				row.add("Hash: " + hash.toString());
				row.add("LastModified: " + (new Date(f.lastModified())));
				writeLogFile(outLogFile, row);
			}
		} else {

			row.add("File,Hash,LastModified");
			if (outLogFile != null) {
				writeLogFile(outLogFile, row);
			}

		}
	}

	public static List<String> getAccessFiles(List<String> dirs, int depth, File outLogFile)
			throws InterruptedException {

		List<String> fileList = new ArrayList<>();

		List<String> subDirs = new ArrayList<>();
		// System.out.println(subDirs.size()+"------size");

		for (String directory : dirs) {
			// System.out.println("Looking in :" + directory + "--------");
			File folder = new File(directory);

			File[] files = folder.listFiles(new FileFilter() {
				private final FileNameExtensionFilter filter = new FileNameExtensionFilter("MsAccess files", "mdb",
						"accdb");

				public boolean accept(File file) {
					boolean x = true;
					for (String i : utils.FILTER_EXTENSION) {

						if (file.toString().contains(i)) {

							x = false;
							return x;
						}
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
							utils.printFileInfo(i.toString(), outLogFile);

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
					List<String> subFiles = getAccessFiles(aSubDir, depth + 1, outLogFile);

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
	public static void print_procs(String filePath) throws SQLException {
		try {
			System.out.println(filePath);
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);
			 
			try (// ResultSet rsMD = conn.getMetaData().getColumns(null, null, null, null)
					ResultSet rsMD = conn.getMetaData().getProcedures(null, null, null)

			) {
				int i = 0;

				while (rsMD.next()) {

					System.out.println(rsMD.getString("PROCEDURE_NAME"));
					i++;

				}
				System.out.println("Total" + i);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void print_tables(String filePath) throws SQLException {
		try {
			System.out.println(filePath);
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);
			/*
			 * Each table description has the following columns:
			 * 
			 * TABLE_CAT String => table catalog (may be null) TABLE_SCHEM String => table
			 * schema (may be null) TABLE_NAME String => table name TABLE_TYPE String =>
			 * table type. Typical types are "TABLE", "VIEW", "SYSTEM TABLE",
			 * "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM". REMARKS String =>
			 * explanatory comment on the table TYPE_CAT String => the types catalog (may be
			 * null) TYPE_SCHEM String => the types schema (may be null) TYPE_NAME String =>
			 * type name (may be null) SELF_REFERENCING_COL_NAME String => name of the
			 * designated "identifier" column of a typed table (may be null) REF_GENERATION
			 * String => specifies how values in SELF_REFERENCING_COL_NAME are created.
			 * Values are "SYSTEM", "USER", "DERIVED". (may be null)
			 */
			try (// ResultSet rsMD = conn.getMetaData().getColumns(null, null, null, null)
					ResultSet rsMD = conn.getMetaData().getTables(null, null, null, null)

			) {
				int i = 0;

				while (rsMD.next()) {

					System.out.println(rsMD.getString("TABLE_NAME"));
					i++;

				}
				System.out.println("Total" + i);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void print_columns(String filePath) throws SQLException {
		try {
			System.out.println(filePath);
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			Connection conn = DriverManager.getConnection("jdbc:ucanaccess://" + filePath);

			try (ResultSet rsMD = conn.getMetaData().getColumns(null, null, null, null)) {

				int i = 0;
				int ii = 0;
				while (rsMD.next()) {
					if (i == 0) {
						System.out.print("Create table " + rsMD.getString("TABLE_NAME"));
					}
					if (ii == 3) {
						System.out.print("\n");
						ii = 0;
					}

					String colName = rsMD.getString("COLUMN_NAME");
					// String tblName = rsMD.getString("TABLE_NAME");
					String dataType = rsMD.getString("TYPE_NAME");
					System.out.print(colName + "\t as " + dataType + ", ");
					i++;
					ii++;
				}
				System.out.println("Total Columns: " + i);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void argParser(String[] args) throws InterruptedException {
		ArgumentParser parser = ArgumentParsers.newFor("java -jar MsAccessPeeker.jar").build().defaultHelp(true)
				.description("Finds all MsAccess files");
		parser.addArgument("-hash", "--crc").choices("SHA-256", "SHA-512", "SHA1", "MD5", "None").setDefault("None")
				.help("Specify hash function to run on file");
		parser.addArgument("-o", "--outfile").nargs(1).help("Output file");
		parser.addArgument("-d").nargs("*").help("Starting Directory");
		Namespace ns = null;
		try {
			ns = parser.parseArgs(args);
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
		// MessageDigest digest = null;

		// System.out.println(ns.toString());
		// System.out.print(ns.getString("d"));

		/*
		 * try { digest = MessageDigest.getInstance(ns.getString("crc")); } catch
		 * (NoSuchAlgorithmException e) {
		 * System.err.printf("Could not get instance of algorithm %s: %s",
		 * ns.getString("crc"), e.getMessage()); System.exit(1); }
		 */

		File outFile = null;
		for (String name : ListUtils.emptyIfNull(ns.<String>getList("outfile"))) {
			System.out.println(name);
			outFile = new File(name);

		}

		for (String name : ListUtils.emptyIfNull(ns.<String>getList("d"))) {
			Path path = Paths.get(name);
			crawDriectory(path.toString(), outFile);

		}
		System.out.println("Crawling Complete");
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		argParser(args);

	}

	private static void crawDriectory(String dirPath, File outLogFile) throws InterruptedException {
		List<String> dirs = new ArrayList<>();
		Path currentRelativePath = Paths.get(dirPath);
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Crawling Directory: " + s);
		dirs.add(s);
		List<String> filesFound = utils.getAccessFiles(dirs, 0, outLogFile);

		// List<String> files = utils.getAccessFiles(dirs, 0);
		// print_columns(filePath);

		for (String i : filesFound) {

			try {
				//print_columns(i);
				print_tables(i);
				print_procs(i);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println(i);
		}
	}

}
