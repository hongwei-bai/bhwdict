package bhwWords.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileOperation {
    public void write(File file, String buffer) {
        write(file, buffer, true);
    }

    public void write(File file, String buffer, boolean append) {
        OutputStream outstream;
        try {
            outstream = new FileOutputStream(file, append);
            OutputStreamWriter out = new OutputStreamWriter(outstream);
            out.write(buffer);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read(File file) {
        String bufferout = "";
        try {
            InputStream instream = new FileInputStream(file);
            if (instream != null) {
                InputStreamReader inputreader = new InputStreamReader(instream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                // read each line
                while ((line = buffreader.readLine()) != null) {
                    bufferout += line + "\n";
                }
                instream.close();
            }
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bufferout;
    }
}
