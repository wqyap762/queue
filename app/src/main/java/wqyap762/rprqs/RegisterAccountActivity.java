package wqyap762.rprqs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class RegisterAccountActivity extends Activity {

    EditText nameText, usernameText, passwordText, confirmPasswordText, hpnoText;
    String name, username, password, hpno, user_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        nameText = (EditText) findViewById(R.id.nameText);
        usernameText = (EditText) findViewById(R.id.usernameText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        confirmPasswordText = (EditText) findViewById(R.id.confirmPasswordText);
        hpnoText = (EditText) findViewById(R.id.hpnoText);

        // register account button
        final Button registerAccountButton = (Button) findViewById(R.id.registerAccountButton);
        registerAccountButton.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(final View v) {

                        // check empty fields
                        // String uname = dbHandler.searchUsername(usernameText.getText().toString());
                        /*if (usernameText.getText().toString().equals(uname)) {
                            usernameText.setError("This username is unavailable.");
                            return;
                        } else*/ if (TextUtils.isEmpty(nameText.getText().toString())) {
                            nameText.setError("Please enter your full name.");
                            return;
                        } else if (TextUtils.isEmpty(usernameText.getText().toString())) {
                            usernameText.setError("Please enter username.");
                            return;
                        } else if (TextUtils.isEmpty(passwordText.getText().toString())) {
                            passwordText.setError("Please enter password.");
                            return;
                        } else if (TextUtils.isEmpty(confirmPasswordText.getText().toString())) {
                            confirmPasswordText.setError("Please confirm your password.");
                            return;
                        } else if (TextUtils.isEmpty(hpnoText.getText().toString())) {
                            hpnoText.setError("Please enter your phone number.");
                            return;
                        }

                        // username < 8 char
                        if (usernameText.getText().toString().length() > 0 && usernameText.getText().toString().length() < 8) {
                            usernameText.setError("Username must be at least 8 characters!");
                            return;
                        }

                        // password > 8 char and mix
                        if (!isValidPassword(passwordText.getText().toString().trim())) {
                            passwordText.setError("Minimum 8 characters, at least 1 Uppercase Alphabet, 1 Lowercase Alphabet and 1 Number");
                            passwordText.setText("");
                            confirmPasswordText.setText("");
                            return;
                        }

                        // phone number should be in format
                        if (hpnoText.getText().toString().length() < 10) {
                            hpnoText.setError("Phone number must be at least 10 number!");
                            return;
                        }

                        // if all fields filled
                        if (!TextUtils.isEmpty(nameText.getText().toString()) && !TextUtils.isEmpty(usernameText.getText().toString()) && !TextUtils.isEmpty(passwordText.getText().toString()) && !TextUtils.isEmpty(confirmPasswordText.getText().toString()) && !TextUtils.isEmpty(hpnoText.getText().toString())) {
                            if (passwordText.getText().toString().equals(confirmPasswordText.getText().toString())/* && !usernameText.getText().toString().equals(uname)*/) {
                                AlertDialog.Builder orderConfirm = new AlertDialog.Builder(RegisterAccountActivity.this);

                                // setting dialog title
                                orderConfirm.setTitle("Confirm Register");

                                // setting dialog message
                                orderConfirm.setMessage("Do you want to continue?");

                                // setting icon to dialog
                                //orderConfirm.setIcon(R.drawable.save);

                                // setting positive "Proceed" button
                                orderConfirm.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // user press Proceed button. Write logic here
                                        registerButtonClicked(v);

                                        AlertDialog.Builder registrationDone = new AlertDialog.Builder(RegisterAccountActivity.this);

                                        // setting dialog title
                                        registrationDone.setTitle("Register Successfully!");

                                        // setting dialog message
                                        registrationDone.setMessage("Your account has been registered successfully.");

                                        // setting icon to dialog
                                        //orderConfirm.setIcon(R.drawable.save);

                                        // setting positive "Okay" button
                                        registrationDone.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // user press Proceed button. Write logic here
                                                goToLoginActivity();
                                            }
                                        });
                                        registrationDone.show();
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
                                passwordText.setText("");
                                confirmPasswordText.setText("");
                                confirmPasswordText.setError("Your password does not match!");
                            }
                        }
                    }
                }
        );
    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public void registerButtonClicked(View v) {
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        name = nameText.getText().toString();
        hpno = hpnoText.getText().toString();
        user_state = "2";
        String method = "register";
        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method, username, password, name, hpno, user_state);
        finish();
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
