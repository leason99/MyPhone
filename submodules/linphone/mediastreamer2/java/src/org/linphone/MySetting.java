package org.linphone;


import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

public class MySetting extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_setting);
        Intent intent=getIntent();

        getFragmentManager().beginTransaction().add(R.id.fragmentContainer,new SettingsFragment()).commit();

    }
}
