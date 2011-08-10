package com.dumptruckman.bartersigns.util;

import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileMgmt {

    /**
     * Pass a file and it will return it's contents as a string.
     * @param file File to read.
     * @return Contents of file.  String will be empty in case of any errors.
     */
    public static String convertFileToString(File file) {
        if (file != null && file.exists() && file.canRead() && !file.isDirectory()) {
	        Writer writer = new StringWriter();
	        InputStream is = null;

	        char[] buffer = new char[1024];
	        try {
                is = new FileInputStream(file);
	            Reader reader = new BufferedReader(
	                    new InputStreamReader(is, "UTF-8"));
	            int n;
	            while ((n = reader.read(buffer)) != -1) {
	                writer.write(buffer, 0, n);
	            }
	        } catch (IOException e)
			{
			    System.out.println("Exception ");
			} finally {
                if (is != null) {
                    try {
	                    is.close();
                    } catch (IOException ignore) {}
                }
	        }
	        return writer.toString();
	    } else {
	        return "";
	    }
    }

    /**
     * Writes the contents of a string to a file.
     * @param source String to write.
     * @param file File to write to.
     * @return True on success.
     * @throws IOException
     */
    public static boolean stringToFile(String source, File file) throws IOException {

        try {

			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");

		    //BufferedWriter out = new BufferedWriter(new FileWriter(FileName));

		    source.replaceAll("\n", System.getProperty("line.separator"));

		    out.write(source);
		    out.close();
		    return true;

		}
		catch (IOException e)
		{
		    System.out.println("Exception ");
		    return false;
		}
    }
}
