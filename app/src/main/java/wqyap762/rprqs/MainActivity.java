package wqyap762.rprqs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Handler;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()) {
            // automatic move to another interface
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (SaveSharedPreferences.getPrefHpno(MainActivity.this).length() == 0) {
                        final Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                        MainActivity.this.startActivity(mainIntent);
                        MainActivity.this.finish();
                    } else {
                        final Intent mainIntent = new Intent(MainActivity.this, CustomerMainActivity.class);
                        MainActivity.this.startActivity(mainIntent);
                        MainActivity.this.finish();
                    }
                }
            }, 2500);
        } else {
            AlertDialog.Builder networkNotFound = new AlertDialog.Builder(MainActivity.this);
            networkNotFound.setTitle("Network Error");
            networkNotFound.setMessage("No Internet Connection.");
            networkNotFound.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveTaskToBack(true);
                }
            });
            networkNotFound.show();
        }
    }
}
