package wqyap762.rprqs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Date;


public class ItemActivity extends ActionBarActivity {

    public static final String DEFAULT = "N/A";
    ImageView itemImage;
    TextView foodNameText, descriptionText, basicPriceText, totalPriceText;
    EditText quantityText;
    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);

        itemImage = (ImageView) findViewById(R.id.itemImage);
        foodNameText = (TextView) findViewById(R.id.foodNameText);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        basicPriceText = (TextView) findViewById(R.id.basicPriceText);
        totalPriceText = (TextView) findViewById(R.id.totalPriceText);
        quantityText = (EditText) findViewById(R.id.quantityText);

        new downloadImage().execute();
        getMenu();

        totalPriceCalculated();

        // customer order
        Button orderButton = (Button) findViewById(R.id.orderButton);
        orderButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(final View v) {
                        if (TextUtils.isEmpty(quantityText.getText().toString())) {
                            quantityText.setError("Please enter quantity.");
                            return;
                        } else {
                            final AlertDialog.Builder orderConfirm = new AlertDialog.Builder(ItemActivity.this);

                            // setting dialog title
                            orderConfirm.setTitle("Confirm Order");

                            // setting dialog message
                            orderConfirm.setMessage("Do you want to continue?");

                            // setting icon to dialog
                            //orderConfirm.setIcon(R.drawable.save);

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
                        }
                    }
                }
        );
    }

    public class downloadImage extends AsyncTask<Void, Void, Bitmap> {
        Intent intent = getIntent();
        String menu_id = intent.getStringExtra("menu_id");

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL imageURL = new URL("http://rprqs.16mb.com/image/" + menu_id + ".jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                return bitmap;
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

    public void getMenu() {
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
                        String basic_price = jsonResponse.getString("basic_price");

                        foodNameText.setText(food_name);
                        descriptionText.setText(description);
                        basicPriceText.setText(basic_price);
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

    public void totalPriceCalculated() {
        quantityText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(quantityText.getText().toString())) {
                    Double basicPrice = Double.parseDouble(basicPriceText.getText().toString());
                    Double quantity = Double.parseDouble(quantityText.getText().toString());
                    totalPriceText.setText(String.valueOf(basicPrice * quantity));
                } else {
                    quantityText.setError("Please enter quantity.");
                    return;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void orderButtonClicked (View v) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        final Double total_price = Double.parseDouble(totalPriceText.getText().toString());
        final int quantity = Integer.parseInt(quantityText.getText().toString());
        final String payment_status = "Unpaid";
        final String hpno = sharedPreferences.getString("hpno", DEFAULT);
        final String menu_id = intent.getStringExtra("menu_id");
        final Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ordered_on = simpleDateFormat.format(now);


        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        orderDone();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ItemActivity.this);
                        builder.setMessage("Order Failed")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        OrderRequest orderRequest = new OrderRequest(total_price, quantity, payment_status, hpno, menu_id, ordered_on, responseListener);
        RequestQueue queue = Volley.newRequestQueue(ItemActivity.this);
        queue.add(orderRequest);
    }

    public void orderDone() {
        AlertDialog.Builder orderDone = new AlertDialog.Builder(ItemActivity.this);

        // setting dialog title
        orderDone.setTitle("Order Successfully!");

        // setting dialog message
        orderDone.setMessage("Your order has been placed successfully.");

        // setting icon to dialog
        //orderConfirm.setIcon(R.drawable.save);

        // setting positive "Okay" button
        orderDone.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user press Proceed button. Write logic here
                goToViewOrderActivity();
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

        return super.onOptionsItemSelected(item);
    }

    public void goToViewOrderActivity() {
        Intent intent = new Intent(this, ViewOrderActivity.class);
        startActivity(intent);
    }
}
