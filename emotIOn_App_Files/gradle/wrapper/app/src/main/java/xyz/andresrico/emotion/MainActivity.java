package xyz.andresrico.emotion;

//Imported libraries.
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

//declaration of public MAIN class.
public class MainActivity extends AppCompatActivity {

    //TextView txt1; //Debugging variable for printing received data.
    DatagramSocket socket; //Socket used to send UDP packages.
    String msg, fname; //Variable for storing data received by UDP and for storing selected file name.
    CharSequence oldMsg="a"; //Variable used for checking that message is not the same as last one.
    boolean errorReceive = false; //Flags errors in UDP communication.
    Integer updateCount=0; //Counter variable for timeout.
    String current_affect = ",1"; //String variable used to indicate current user state. Starts with neutral affect.
    Button Neut, HH, HL, LH, LL,connec_ind; //Button variables used for button control on UI.

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Runs once when app is opened.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        //Declare text variables for displaying packets. Comment out if not needed.
        //txt1 = (TextView) findViewById(R.id.debbug_text);
        connec_ind = (Button) findViewById(R.id.connect_indicator);

        fname = "emotIOn_data_file.txt"; //Declaration of file name.

        Neut = (Button) findViewById(R.id.N);
        HH = (Button) findViewById(R.id.HH);
        LH = (Button) findViewById(R.id.LH);
        LL = (Button) findViewById(R.id.LL);
        HL = (Button) findViewById(R.id.HL);

        HH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
        Neut.setBackgroundColor(Color.parseColor("#FFE5E6DC"));
        LL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
        HL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
        LH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
        connec_ind.setBackgroundColor(Color.parseColor("#FFE7373A"));

        //Start ASync Class
        new receiveUDP().execute();

        HH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HH.setBackgroundColor(Color.parseColor("#FFE9E494"));
                Neut.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                LL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                HL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                LH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                current_affect = ",2";
            }
        });

        LL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                Neut.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                LL.setBackgroundColor(Color.parseColor("#FF94CAE9"));
                HL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                LH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                current_affect = ",4";
            }
        });

        HL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                Neut.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                LL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                HL.setBackgroundColor(Color.parseColor("#FFD4A6E7"));
                LH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                current_affect = ",5";
            }
        });

        LH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                Neut.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                LL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                HL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                LH.setBackgroundColor(Color.parseColor("#FFEA5A5A"));
                current_affect = ",3";
            }
        });

        Neut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                Neut.setBackgroundColor(Color.parseColor("#FFE5E6DC"));
                LL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                HL.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                LH.setBackgroundColor(Color.parseColor("#FFCCCCCC"));
                current_affect = ",1";
            }
        });

    }

    public class receiveUDP extends AsyncTask <Void, CharSequence, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            //Check to see if new packet is received.
            while (true) {
                try {
                        Thread.sleep(1500);
                    }
                    catch (InterruptedException e) {

                    }
                try {
                    if (socket == null) {
                        socket = new DatagramSocket(2905);
                        socket.setBroadcast(true);
                    }

                    byte[] buf = new byte[2500];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    //Change packet data to string.
                    msg = new String(packet.getData(), 0, packet.getLength());
                    connec_ind.setBackgroundColor(Color.parseColor("#FF75ED5F"));


                }
                catch (Exception e) {
                    errorReceive = true;
                    e.printStackTrace();
                    connec_ind.setBackgroundColor(Color.parseColor("#FFE7373A"));
                }

                publishProgress(msg.concat(current_affect));
            }
        }


        protected void onProgressUpdate(CharSequence...progress) {
            updateCount++;
            // If no errors, and if new message is different than old message
            // Then change the text field to show new message.
            if(!(errorReceive)){
                if(!(oldMsg.equals(progress[0]))){
                    //txt1.setText(progress[0]);
                    saveTextAsFile(fname, msg.concat(current_affect));
                    oldMsg = progress[0];
                }

            }
        }
    }

    private void saveTextAsFile (String filename, String content) {
        String fileName = filename;

        //Create file
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);

        //Write to file
        try {
            FileOutputStream fos = new FileOutputStream(file, true);
            fos.write('\n');
            fos.write(content.getBytes());
            fos.close();
            //Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //Toast.makeText(this, "File not found!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Error saving!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
