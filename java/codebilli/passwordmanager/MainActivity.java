package codebilli.passwordmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import codebilli.passwordmanager.R;
import static android.R.drawable.ic_menu_add;
import static android.support.v4.content.FileProvider.getUriForFile;

public class MainActivity extends AppCompatActivity {
    protected Vector<Integer> m_vButtons = new Vector<Integer>();

    protected class EmailSender extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String sFileToUpload = Helper.loadFromInternalStorage(MainActivity.this);

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");

            emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"vidhyashankar99@hotmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT   , "Navneet, your exams are starting on 7th March.");

            String dir = getFilesDir().getAbsolutePath();
            File attachFile = new File(dir, "PasswordData.json");
            if (attachFile.exists()) {
                try {
                    Uri contentUri = getUriForFile(MainActivity.this, "codebilli.passwordmanager.fileprovider", attachFile);
                    emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, contentUri);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Exception - " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
            return null;
        }
    }

    protected class FileUploader extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String sFileToUpload = Helper.loadFromInternalStorage(MainActivity.this);
            HttpURLConnection conn = null;
            String boundary = "*****";
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";

            try {
                String upLoadServerUri = "http://10.20.52.52:8080/upload.php";
                URL url = new URL(upLoadServerUri);

                // Open a HTTP connection to the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", "PasswordData.json");

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + "PasswordData.json" + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // read file and write it into form...
                dos.write(sFileToUpload.getBytes(), 0, sFileToUpload.length());

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                if (serverResponseCode == 200) {
                    //alert("File Upload complete!");
                }

                // close the streams //
                dos.flush();
                dos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    protected void deleteAllRecords() {
        String dir = getFilesDir().getAbsolutePath();
        File f0 = new File(dir, "PasswordData.json");
        if (f0.exists()) {
            boolean d0 = f0.delete();

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
            for (int i = 0; i < m_vButtons.size(); i++) {
                Button btn = (Button) findViewById(m_vButtons.get(i));
                layout.removeView(btn);
            }
        }
    }

    public void confirmDelete(String str)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(str);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                (MainActivity.this).deleteAllRecords();
                Toast.makeText(MainActivity.this, "All records deleted successfully!", Toast.LENGTH_SHORT).show();
            }
        });

        dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    protected void addSites() {
        final Context ctx = this;
        try {
            String sitesData = Helper.loadFromInternalStorage(ctx);
            if (sitesData == null)
                return;

            JSONObject jsonObj = new JSONObject(sitesData);

            // Getting JSON Array node
            JSONArray siteData = jsonObj.getJSONArray("SiteData");
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
            int lastId = 0;
            for (int i = 0; i < siteData.length(); i++) {
                JSONObject c = siteData.getJSONObject(i);
                Iterator<String> iKeys = c.keys();
                while (iKeys.hasNext()) {
                    final String siteName = iKeys.next();

                    Button b = new Button(this);

                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                             RelativeLayout.LayoutParams.WRAP_CONTENT);

                    if (i == 0) {
                        params.setMargins(0, 100, 0, 0);
                    }

                    if (i != 0) {
                        params.addRule(RelativeLayout.BELOW, lastId);
                        params.setMargins(0, 10, 0, 0);
                    }
                    b.setLayoutParams(params);
                    b.setGravity(Gravity.CENTER_HORIZONTAL);

                    lastId = Helper.generateViewId();
                    b.setId(lastId);
                    b.setText(siteName);
                    b.setTextSize(20);

                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ctx, DetailsActivity.class);
                            intent.putExtra("SiteName", siteName);
                            startActivity(intent);
                        }
                    });

                    b.setOnLongClickListener(new View.OnLongClickListener(){
                        @Override
                        public boolean onLongClick(View v) {
                            // custom dialog

                            /*final Dialog dialog = new Dialog(ctx);
                            dialog.setContentView(R.layout.custom_dialog);
                            dialog.setTitle("Title...");

                            // set the custom dialog components - text, image and button
                            Button text = (Button) dialog.findViewById(R.id.deleteItem);
                            text.setOnClickListener(new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();*/
                            return true;
                        }
                    });
                    layout.addView(b);
                    m_vButtons.add(lastId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
        for (int i=0; i<m_vButtons.size(); i++) {
            Button btn = (Button) findViewById(m_vButtons.get(i));
            layout.removeView(btn);
        }
        addSites();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load data from Internal file...
        addSites();

        final Context ctx = this;
        Button addButton = (Button) findViewById(R.id.add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, AddActivity.class);
                startActivity(intent);
            }
        });

        Button deleteButton = (Button) findViewById(R.id.delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dir = getFilesDir().getAbsolutePath();
                File attachFile = new File(dir, "PasswordData.json");
                if (attachFile.exists()) {
                    confirmDelete("Do you want to delete all Records?");
                }
                else {
                    Toast.makeText(MainActivity.this, "No data to delete!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button exportButton = (Button) findViewById(R.id.export);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dir = getFilesDir().getAbsolutePath();
                File attachFile = new File(dir, "PasswordData.json");
                if (attachFile.exists()) {
                    new EmailSender().execute();
                }
                else {
                    Toast.makeText(MainActivity.this, "No data to export!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
