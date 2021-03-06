package wqyap762.rprqs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ItemActivity extends AppCompatActivity {

    ImageView itemImage;
    TextView foodNameText, descriptionText, basicPriceText, totalPriceText, quantityText;
    Button decreaseButton, increaseButton;
    private ProgressBar spinner;
    int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        if (spinner != null) {
            spinner.setVisibility(View.VISIBLE);
        }

        itemImage = (ImageView) findViewById(R.id.itemImage);
        foodNameText = (TextView) findViewById(R.id.foodNameText);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        basicPriceText = (TextView) findViewById(R.id.basicPriceText);
        totalPriceText = (TextView) findViewById(R.id.totalPriceText);
        quantityText = (TextView) findViewById(R.id.quantityText);
        decreaseButton = (Button) findViewById(R.id.decreaseButton);
        increaseButton = (Button) findViewById(R.id.increaseButton);

        decreaseButton.setEnabled(false);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!=null && networkInfo.isConnected()) {
            new downloadImage().execute();
            getItemInformation();
        } else {
            android.support.v7.app.AlertDialog.Builder networkNotFound = new android.support.v7.app.AlertDialog.Builder(ItemActivity.this);
            networkNotFound.setTitle("Network Error");
            networkNotFound.setMessage("Please check your internet connection.");
            networkNotFound.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NavUtils.navigateUpFromSameTask(ItemActivity.this);
                }
            });
            networkNotFound.show();
        }
        totalPriceCalculated();
        operatingHours();
    }

    private void operatingHours() {
        Calendar now = Calendar.getInstance();

        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        Date time = parseDate(hour + ":" + minute);
        String compareStartTime = "10:00";
        Date startTime = parseDate(compareStartTime);
        String compareEndTime = "21:00";
        Date endTime = parseDate(compareEndTime);

        // customer order
        Button orderButton = (Button) findViewById(R.id.orderButton);

        if (startTime.before(time) && endTime.after(time)) {
            assert orderButton != null;
            orderButton.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(final View v) {
                            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                            if (networkInfo!=null && networkInfo.isConnected()) {
                                final AlertDialog.Builder orderConfirm = new AlertDialog.Builder(ItemActivity.this);

                                // setting dialog title
                                orderConfirm.setTitle("Confirm Order");

                                // setting dialog message
                                orderConfirm.setMessage("Do you want to continue?");

                                // setting positive "Proceed" button
                                orderConfirm.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // user press Proceed button. Write logic here
                                        spinner.setVisibility(View.VISIBLE);
                                        orderButtonClicked(v);
                                    }
                                });

                                // setting neutral "Cancel" button
                                orderConfirm.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // user press Cancel button. Write login here
                                        dialog.dismiss();
                                    }
                                });

                                orderConfirm.show();
                            } else {
                                final android.support.v7.app.AlertDialog.Builder networkNotFound = new android.support.v7.app.AlertDialog.Builder(ItemActivity.this);
                                networkNotFound.setTitle("Network Error");
                                networkNotFound.setMessage("Please check your internet connection.");
                                networkNotFound.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                networkNotFound.show();
                            }
                        }
                    }
            );
        } else {
            if (orderButton != null) {
                orderButton.setEnabled(false);
            }
        }
    }

    private Date parseDate(String date) {
        final String inputFormat = "HH:mm";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.getDefault());
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public class downloadImage extends AsyncTask<Void, Void, Bitmap> {
        Intent intent = getIntent();
        String menu_id = intent.getStringExtra("menu_id");

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL imageURL = new URL("http://188.166.178.66/image/" + menu_id + ".jpg");
                return BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                itemImage.setImageBitmap(bitmap);

                spinner.setVisibility(View.GONE);
            }
        }
    }

    public void getItemInformation() {
        Intent intent = getIntent();
        String menu_id = intent.getStringExtra("menu_id");

        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        String food_name = jsonResponse.getString("food_name");
                        String description = jsonResponse.getString("description");
                        double basic_price = jsonResponse.getDouble("basic_price");

                        foodNameText.setText(food_name);
                        descriptionText.setText(description);
                        basicPriceText.setText(String.format("%.2f", basic_price));
                        totalPriceText.setText(String.format("%.2f", basic_price));
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this);
                        builder.setMessage("Item Retrieve Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        ItemRequest menuRequest = new ItemRequest(menu_id, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ItemActivity.this);
        queue.add(menuRequest);
    }

    public void increaseInteger(View view) {
        decreaseButton.setEnabled(true);
        quantity = quantity + 1;
        if (quantity >= 10) {
            if (increaseButton != null) {
                increaseButton.setEnabled(false);
            }
            displayQuantity(quantity);
        } else {
            displayQuantity(quantity);
        }
    }

    public void decreaseInteger(View view) {
        increaseButton.setEnabled(true);
        quantity = quantity - 1;
        if (quantity <= 1) {
            if (decreaseButton != null) {
                decreaseButton.setEnabled(false);
            }
            displayQuantity(quantity);
        } else {
            displayQuantity(quantity);
        }
    }

    private void displayQuantity(int number) {
        TextView displayQuantity = (TextView) findViewById(R.id.quantityText);
        assert displayQuantity != null;
        displayQuantity.setText(String.valueOf(number));
    }

    public void totalPriceCalculated() {
        quantityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Double basicPrice = Double.parseDouble(basicPriceText.getText().toString());
                Double quantity = Double.parseDouble(quantityText.getText().toString());
                totalPriceText.setText(String.format("%.2f", basicPrice * quantity));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void orderButtonClicked (View v) {
        Intent intent = getIntent();
        final Double total_price = Double.parseDouble(totalPriceText.getText().toString());
        final int quantity = Integer.parseInt(quantityText.getText().toString());
        final String payment_status = "Unpaid";
        final String hpno = SaveSharedPreferences.getPrefHpno(ItemActivity.this);
        final String menu_id = intent.getStringExtra("menu_id");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String ordered_on = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.MINUTE, 5);
        String ready_on = simpleDateFormat.format(calendar.getTime());


        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        orderDone();
                        spinner.setVisibility(View.GONE);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this);
                        builder.setMessage("Order Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                        spinner.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        OrderRequest orderRequest = new OrderRequest(total_price, quantity, payment_status, hpno, menu_id, ordered_on, ready_on, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ItemActivity.this);
        queue.add(orderRequest);
    }

    public void orderDone() {
        AlertDialog.Builder orderDone = new AlertDialog.Builder(ItemActivity.this);

        // setting dialog title
        orderDone.setTitle("Ordered Successfully!");

        // setting dialog message
        orderDone.setMessage("Your order has been placed successfully.");

        // setting icon to dialog
        //orderConfirm.setIcon(R.drawable.save);

        // setting positive "Okay" button
        orderDone.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user press Proceed button. Write logic here
                goToTrackOrderActivity();
            }
        });
        orderDone.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToTrackOrderActivity() {
        Intent intent = new Intent(this, TrackOrderActivity.class);
        startActivity(intent);
    }
}
