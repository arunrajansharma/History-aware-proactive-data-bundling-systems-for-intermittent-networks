package stonybrook.edu.isurfer;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zipper {

    public void makeZip(ByteBuffer data, final Context ctx){
        Log.d("Context",ctx.toString());
        byte[] b = new byte[data.remaining()];
        data.get(b);

        String fileName = "Webpacket.zip";
        try{
            File f = new File(ctx.getFilesDir()+"/"+fileName);
            if(f.delete()) Log.d("Saving","deleted");
            else
                Log.d("saving","some error occured");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        Log.d("Websocket", "Data Received is " + data.toString());
        try {
            FileOutputStream fileOutputStream = null;
            fileOutputStream = ctx.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(b);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            unzip("Webpacket.zip",ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unzip(String zipFile, Context ctx) throws IOException {
        int size;
        int BUFFER_SIZE = 256;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            //read the input file with proper permissions
            File f= new File(ctx.getFilesDir()+"/"+zipFile);
            FileInputStream fin = ctx.openFileInput(zipFile);
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fin, BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if (null != parentDir) {
                            if (!parentDir.isDirectory()) {
                                parentDir.mkdirs();
                                Log.d("Unzipping",String.valueOf(parentDir));
                            }
                        }

                        // unzip the file
                        //start creating the folder and writing content to the file
                        File rootDir = new File(ctx.getFilesDir(),String.valueOf(unzipFile.getParentFile()) );
                        Log.d("Unzipping","root dir is:"+ String.valueOf(unzipFile.getParentFile()));
                        rootDir.mkdirs();
                        FileOutputStream out = new FileOutputStream(new File(rootDir,unzipFile.getName())); //openFileOutput(path, getBaseContext().MODE_PRIVATE); ////
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                fout.write(buffer, 0, size);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            Log.e("Unzipping", "Unzip exception", e);
        }
    }
}
