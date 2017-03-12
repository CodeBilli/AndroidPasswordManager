package codebilli.passwordmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Color;
import android.telecom.Call;
import android.text.InputType;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Iterator;
import codebilli.passwordmanager.R;

public class DetailsActivity extends AppCompatActivity {

    public void confirmDelete(String str)
    {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(str);
        dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                (DetailsActivity.this).deleteAndRecreate();
                Toast.makeText(DetailsActivity.this, "Record Deleted Successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        dlgAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
    protected void addRows(String sSiteName) throws Exception {

        TableLayout layout = (TableLayout) findViewById(R.id.details);
        JSONObject jsonObj = new JSONObject(Helper.loadFromInternalStorage(this));

        // Getting JSON Array node
        JSONArray siteData = jsonObj.getJSONArray("SiteData");
        boolean bFound = false;
        for (int i = 0; i < siteData.length(); i++) {
            JSONObject c = siteData.getJSONObject(i);
            Iterator<String> iKeys = c.keys();
            while (iKeys.hasNext()) {
                String key = iKeys.next();
                if (key.equalsIgnoreCase(sSiteName)) {
                    bFound = true;

                    JSONObject bankData = (JSONObject) c.get(key);

                    // Get all the items with this JSON and display
                    Iterator<String> bankDataElements = bankData.keys();
                    int j = 0;
                    while (bankDataElements.hasNext()) {
                        String bankDataElement = bankDataElements.next();
                        TableRow row = new TableRow(this);
                        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
                        row.setLayoutParams(lp);
                        row.setMinimumHeight(100);

                        TextView tv = new TextView(this);
                        tv.setText(bankDataElement);
                        TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
                        tv.setLayoutParams(lp1);
                        tv.setId(Helper.generateViewId());
                        tv.setMinHeight(100);
                        tv.setGravity(Gravity.CENTER_VERTICAL);
                        tv.setTextSize(20);
                        tv.setPadding(30, 0, 0, 0);
                        tv.setTextColor(Color.BLACK);

                        TextView tv1 = new TextView(this);
                        if (bankDataElement.equals("Password")) {
                            String ColValue = bankData.getString(bankDataElement);
                            byte[] decodedPwd = Base64.decode(ColValue.getBytes(), Base64.DEFAULT);
                            tv1.setText(new String(decodedPwd));
                        }
                        else {
                            tv1.setText(bankData.getString(bankDataElement));
                        }
                        tv1.setLayoutParams(lp1);
                        tv1.setId(Helper.generateViewId());
                        tv1.setTextSize(20);
                        tv1.setMinHeight(100);

                        row.addView(tv);
                        row.addView(tv1);

                        layout.addView(row, j++);
                    }
                }
            }
            if (bFound)
                break;
        }
    }

    void deleteAndRecreate() {

    }

    void deleteRecord() {
        Bundle bundle = getIntent().getExtras();
        String sSiteName = bundle.getString("SiteName");

        confirmDelete("Do you want to delete?");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle bundle = getIntent().getExtras();
        String sSiteName = bundle.getString("SiteName");

        try {
            addRows(sSiteName);
        } catch (Exception e) {
            Toast.makeText(this,"Exception maga while Adding!", Toast.LENGTH_SHORT).show();
        }

        TableLayout layout = (TableLayout) findViewById(R.id.details);
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);

            if (child instanceof TableRow) {
                if (i % 2 == 0) {
                    TableRow row = (TableRow) child;
                    row.setBackgroundColor(Color.rgb(200, 200, 200));
                }
            }
        }

        final Context ctx = this;
        Button deleteButton = (Button) findViewById(R.id.deleteSite);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DetailsActivity)ctx).deleteRecord();
            }
        });

    }
}
