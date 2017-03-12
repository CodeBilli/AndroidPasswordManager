package codebilli.passwordmanager;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Context ctx = this;
        Button addButton = (Button) findViewById(R.id.login);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validate the passcode...
                EditText ed = (EditText) findViewById(R.id.passcode);
                String p = (String) ed.getText().toString();
                if (p.equals("bajrangi$007")) {
                    Intent intent = new Intent(ctx, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(Login.this, "Wrong Passcode. Try again!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onResume() {
        EditText ed = (EditText) findViewById(R.id.passcode);
        ed.setText("");
        ed.requestFocus();
        super.onResume();
    }
}
