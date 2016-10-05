package com.example.demoapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineConfig;
import com.freshdesk.hotline.HotlineUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.w3c.dom.Text;

public class DemoActivity extends AppCompatActivity {

    private static final String USER_PREF = "USER_PREF";
    private static final String KEY_NAME = "keyName";
    private static final String KEY_EMAIL = "keyEmail";
    private static final String KEY_PHONE = "keyPhone";
    public static final String EMPTY_STRING = "";

    private SharedPreferences sharedPreferences;
    private Hotline hotlineInstance;
    private Button btnShowConversations, btnShowFAQs;
    private EditText name, email, phone;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 4329;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        sharedPreferences = getSharedPreferences(USER_PREF, MODE_PRIVATE);
        setTitle("Joe Chat Demo");
        //init
        HotlineConfig hotlineConfig=new HotlineConfig("4ea6aa9d-46e8-45b1-b8c5-3d47d723454c","76034f94-7a32-4b21-8b05-d5ad3d7a8517");
        Hotline.getInstance(getApplicationContext()).init(hotlineConfig);

        btnShowFAQs = (Button) findViewById(R.id.btnShowFAQs);
        btnShowConversations = (Button) findViewById(R.id.btnShowConversations);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);

        populateSavedUser();

        btnShowFAQs.setOnClickListener(viewClickListener);
        btnShowConversations.setOnClickListener(viewClickListener);

        if(checkPlayServices(this)) {
            Intent intent = new Intent(this, MyGcmRegistrationService.class);
            startService(intent);
        }
    }

    View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick (View v) {
            if (verifyForm())
            {
                updateUser();
                saveUser();
                if(v.getId() == R.id.btnShowFAQs) {

                    Hotline.showFAQs(DemoActivity.this);

                } else if(v.getId() == R.id.btnShowConversations) {

                    Hotline.showConversations(DemoActivity.this);

                }
            }
        }
    };

    private void populateSavedUser()
    {
        name.setText(sharedPreferences.getString(KEY_NAME, EMPTY_STRING));
        email.setText(sharedPreferences.getString(KEY_EMAIL, EMPTY_STRING));
        phone.setText(sharedPreferences.getString(KEY_PHONE, EMPTY_STRING));
    }

    private void saveUser()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name.getText().toString());
        editor.putString(KEY_EMAIL, email.getText().toString());
        editor.putString(KEY_PHONE, phone.getText().toString());
        editor.commit();
    }

    private void updateUser()
    {
        HotlineUser user = Hotline.getInstance(getApplicationContext()).getUser();
        user.setName(name.getText().toString()).setEmail(email.getText().toString())
                .setPhone("+91", phone.getText().toString()).setExternalId(email.getText().toString());
        Hotline.getInstance(getApplicationContext()).updateUser(user);
    }

    private boolean verifyForm()
    {
        if (TextUtils.isEmpty(name.getText()))
        {
            name.setError("Please enter name");
            return false;
        }
        if (TextUtils.isEmpty(phone.getText()))
        {
            phone.setError("Please enter phone");
            return false;
        }
        if (TextUtils.isEmpty(email.getText()))
        {
            email.setError("Please enter email");
            return false;
        }
        return true;
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices(Activity activityContext) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activityContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activityContext, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("demoapp", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

}
