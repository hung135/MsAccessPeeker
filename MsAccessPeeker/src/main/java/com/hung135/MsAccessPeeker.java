/**
 * 
 */
package com.hung135;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * @author hung
 *
 */
class utils {
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
		
		if (outLogFile.exists()) {
			
			if (outLogFile != null) {
				row.add("File: " + filename);
				row.add("Hash: " + hash.toString());
				row.add("LastModified: " + (new Date(f.lastModified())));
				writeLogFile(outLogFile, row);
			}
		}
		else {
			
			row.add("File,Hash,LastModified");
			
			writeLogFile(outLogFile, row);
			
			
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
					if (file.toString().contains("/.py/")) {
						return false;
					}
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
		for (String name : ns.<String>getList("outfile")) {
			System.out.println(name);
			outFile = new File(name);
			
		}

		for (String name : ns.<String>getList("d")) {
			Path path = Paths.get(name);
			crawDriectory(path.toString(), outFile);

		}
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
		utils.getAccessFiles(dirs, 0, outLogFile);
		// List<String> files = utils.getAccessFiles(dirs, 0);
		// print_columns(filePath);
		/*
		 * for (String i : files) { int ii = 1; // System.out.println(i); }
		 */

	}

}
