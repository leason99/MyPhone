package org.linphone;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class MyAccountPreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_count_preferences_fragment);
        Bundle bundle= getIntent().getExtras();
        Fragment fragment =new AccountPreferencesFragment();
        fragment.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.fragmentContainer, fragment).commit();
    }
}
