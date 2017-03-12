package codebilli.passwordmanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Context;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;

import codebilli.passwordmanager.R;

public class AddActivity extends AppCompatActivity {

    protected void storeData(String json) {
        try {
            FileOutputStream fos = openFileOutput("PasswordData.json", Context.MODE_PRIVATE);
            fos.write(json.getBytes());
            fos.close();
        }
        catch(Exception e) {

        }
    }

    protected void addRecord() {
        EditText userIdField = (EditText) findViewById(R.id.userId);
        String userId = userIdField.getText().toString();

        EditText pwdField = (EditText) findViewById(R.id.password);
        String pwd = pwdField.getText().toString();

        EditText accNoField = (EditText) findViewById(R.id.accNo);
        String accNo = accNoField.getText().toString();

        EditText siteNameField = (EditText) findViewById(R.id.siteName);
        String siteName = siteNameField.getText().toString();

        try {
            String dataFromStorage = Helper.loadFromInternalStorage(this);
            JSONObject jsonObj = null;
            JSONArray siteData = null;
            if (dataFromStorage == null) {
                jsonObj = new JSONObject();
                siteData = new JSONArray();
            } else {
                jsonObj = new JSONObject(dataFromStorage);
                siteData = jsonObj.getJSONArray("SiteData");
            }

            // create a new JSON object out of the new data...
            JSONObject jo = new JSONObject();
            jo.put("UserId", userId);
            jo.put("Password", Base64.encodeToString(pwd.getBytes(), Base64.DEFAULT));
            jo.put("Account No", accNo);

            JSONObject arrayRecJO = new JSONObject();
            arrayRecJO.put(siteName, jo);

            siteData.put(arrayRecJO);

            JSONObject newJson = new JSONObject();
            newJson.put("SiteData", siteData);

            storeData(newJson.toString());

            String result = Helper.loadFromInternalStorage(this);
            if (result != null) {
                Toast.makeText(this, "Record added successfully.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        final Context ctx = this;
        Button addButton = (Button) findViewById(R.id.addDetails);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AddActivity)ctx).addRecord();
            }
        });
    }
}
