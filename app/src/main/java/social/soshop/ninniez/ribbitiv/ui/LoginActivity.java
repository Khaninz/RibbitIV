package social.soshop.ninniez.ribbitiv.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import social.soshop.ninniez.ribbitiv.R;
import social.soshop.ninniez.ribbitiv.RibbitApplication;


public class LoginActivity extends ActionBarActivity {

    protected TextView mSignupTextView;

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //add indicator bar without disturbing anything in the layout.
        //MUST SET BEFORE SET CONTENT VIEW,otherwise the app will crash.
        //does't work
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);





        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mSignupTextView = (TextView) findViewById(R.id.signupText);
        mSignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //anynnomus typye of using "this" as context
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        //CODE COPY FROM SINGUP ACTIVITY
        mUsername = (EditText) findViewById(R.id.usernameField);
        mPassword = (EditText) findViewById(R.id.passwordField);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();


                //trim to delete white space.
                username = username.trim();
                password = password.trim();


                if (username.isEmpty() || password.isEmpty()){

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message);
                    builder.setTitle(R.string.login_error_title);
                    builder.setPositiveButton(android.R.string.ok,null);

                    AlertDialog dialog = builder.create();
                    dialog.show();





                } else {

                    //setProgressBarIndeterminateVisibility(true);
                    mProgressBar.setVisibility(View.VISIBLE);

                    //Loging in in Parse.com
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            //setProgressBarIndeterminateVisibility(false);
                            //parse exception will be know if ParseUser exist.
                            if (e == null) {
                                //Success!

                                //update parseinstallation afther login success
                                RibbitApplication.updateParseInstallation(parseUser);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                //ADD FLAG SO USER CANNOT PRESS BACK TO SIGN UP ACTIVITY AGAIN
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);

                            } else {
                                //Fail
                                //Show dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage());
                                builder.setTitle(R.string.login_error_title);
                                builder.setPositiveButton(android.R.string.ok, null);

                                AlertDialog dialog = builder.create();
                                dialog.show();

                            }

                        }
                    });

                }



            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
