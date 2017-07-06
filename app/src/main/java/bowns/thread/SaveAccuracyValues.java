package bowns.thread;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveAccuracyValues extends Thread {

    private Context context;

    private Bundle paraPack;

    private FileOutputStream fos_internal;
    private FileOutputStream fos_external;

    private final static String fName = "accValuesRecord.txt";

    public SaveAccuracyValues(Context context, Bundle paraPack) {

        this.context = context;
        this.paraPack = paraPack;

        try {
            this.fos_internal = this.context.openFileOutput(
                    this.fName, this.context.MODE_APPEND);

            File eFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    this.fName);
            if (!this.isSdcardWritable())
                Toast.makeText(this.context, "sdcard isn't writable!", Toast.LENGTH_LONG).show();
            else {
                if (!eFile.getParentFile().exists()) eFile.getParentFile().mkdirs();
                if (eFile.exists()) eFile.delete();

                this.fos_external = new FileOutputStream(eFile);
            }
        }
        catch(FileNotFoundException e) {
            Toast.makeText(this.context, "file not found :(", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void run() {

        while(true){
            try{
                /* save the acc values */
                StringBuilder sb = new StringBuilder();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS");
                sb.append(sdf.format(new Date())); sb.append("\t");
                sb.append(paraPack.getString("X-VALUE")); sb.append("\t");
                sb.append(paraPack.getString("Y-VALUE")); sb.append("\t");
                sb.append(paraPack.getString("Z-VALUE")); sb.append("\n");

                try {
                    /* save under internal storage */
                    this.fos_internal.write(sb.toString().getBytes());

                    /*
                    Toast.makeText(this.context,
                            "saving at " + this.context.getFileStreamPath(AccelerometerListener.fName),
                            Toast.LENGTH_LONG).show();
                    */

                    /* save under external storage */
                    this.fos_external.write(sb.toString().getBytes());

                    /*
                    Toast.makeText(this.context,
                            "saving at " + this.eFile.toString(), Toast.LENGTH_LONG).show();
                    */
                }
                catch(IOException e) {
                    Toast.makeText(this.context,
                            "saving file error :(", Toast.LENGTH_LONG).show();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private boolean isSdcardWritable() {

        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) return true;
        else return false;
    }

    @Override
    public void finalize() {

        try {
            this.fos_internal.close();
            this.fos_external.close();
        }
        catch(IOException e) {
            Toast.makeText(this.context, "close file exception :(", Toast.LENGTH_LONG).show();
        }
    }
}
