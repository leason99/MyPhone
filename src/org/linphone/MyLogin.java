package org.linphone;

/*import android.support.v7.app.AppCompatActivity;*/

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.linphone.LinphonePreferences.AccountBuilder;
import org.linphone.assistant.AssistantFragmentsEnum;
import org.linphone.assistant.CreateAccountActivationFragment;
import org.linphone.assistant.CreateAccountFragment;
import org.linphone.assistant.EchoCancellerCalibrationFragment;
import org.linphone.assistant.LinphoneLoginFragment;
import org.linphone.assistant.LoginFragment;
import org.linphone.assistant.RemoteProvisioningFragment;
import org.linphone.assistant.WelcomeFragment;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAddress.TransportType;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;

/**
 * @author Sylvain Berfini
 */
public class MyLogin extends Activity implements OnClickListener {
    private static MyLogin instance;
    //  private ImageView back, cancel;
    private AssistantFragmentsEnum currentFragment;
    private AssistantFragmentsEnum firstFragment;
    private Fragment fragment;
    private LinphonePreferences mPrefs;
    private boolean accountCreated = false;
    private LinphoneCoreListenerBase mListener;
    private LinphoneAddress address;
    private StatusFragment status;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.isTablet) && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (getResources().getBoolean(R.bool.orientation_portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.my_login);
        initUI();
        displayLoginGeneric();
        /**  firstFragment =  AssistantFragmentsEnum.LOGIN ;

         if (findViewById(R.id.fragmentContainer) != null) {
         if (savedInstanceState == null) {
         display(firstFragment);
         } else {
         currentFragment = (AssistantFragmentsEnum) savedInstanceState.getSerializable("CurrentFragment");
         }
         }*/
        mPrefs = LinphonePreferences.instance();
        mListener = new LinphoneCoreListenerBase() {
            @Override
            public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
                if (accountCreated) {
                    if (address != null && address.asString().equals(cfg.getIdentity())) {
                        if (state == RegistrationState.RegistrationOk) {
                            if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
                                launchEchoCancellerCalibration(true);
                            }
                        } else if (state == RegistrationState.RegistrationFailed) {
                            //showDialog(cfg);
                            Toast.makeText(MyLogin.this, getString(R.string.first_launch_bad_login_password), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        };
        instance = this;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {

            AlertDialog.Builder exit = new AlertDialog.Builder(this);
            exit.setTitle("exit");
            exit.setMessage("Are you sure you want to exit ");
            exit.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LinphoneActivity.instance().quit();
                   // FloatViewService.instance().onFinishFloatingView();
                    MyLogin.this.finish();
                    System.exit(1);
                }
            });
            exit.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            exit.show();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.addListener(mListener);
        }
    }

    @Override
    protected void onPause() {
        LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.removeListener(mListener);
        }

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("CurrentFragment", currentFragment);
        super.onSaveInstanceState(outState);
    }

    public static MyLogin instance() {
        return instance;
    }

    public void updateStatusFragment(StatusFragment fragment) {
        status = fragment;
    }

    private void initUI() {

    }

    private void changeFragment(Fragment newFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, newFragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.cancel) {
            LinphonePreferences.instance().firstLaunchSuccessful();
            if (getResources().getBoolean(R.bool.setup_cancel_move_to_back)) {
                moveTaskToBack(true);
            } else {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        } else if (id == R.id.back) {
            onBackPressed();
        }
    }
/*
    @Override
    public void onBackPressed() {
        if (currentFragment == firstFragment) {
            LinphonePreferences.instance().firstLaunchSuccessful();
            if (getResources().getBoolean(R.bool.setup_cancel_move_to_back)) {
                moveTaskToBack(true);
            } else {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        } else if (currentFragment == AssistantFragmentsEnum.LOGIN
                || currentFragment == AssistantFragmentsEnum.LINPHONE_LOGIN
                || currentFragment == AssistantFragmentsEnum.CREATE_ACCOUNT
                || currentFragment == AssistantFragmentsEnum.REMOTE_PROVISIONING) {
            WelcomeFragment fragment = new WelcomeFragment();
            changeFragment(fragment);
            currentFragment = AssistantFragmentsEnum.WELCOME;

        } else if (currentFragment == AssistantFragmentsEnum.WELCOME) {
            finish();
        }
    }*/

    private void launchEchoCancellerCalibration(boolean sendEcCalibrationResult) {
        boolean needsEchoCalibration = LinphoneManager.getLc().needsEchoCalibration();
        if (needsEchoCalibration && mPrefs.isFirstLaunch()) {
            mPrefs.setAccountEnabled(mPrefs.getAccountCount() - 1, false); //We'll enable it after the echo calibration
            EchoCancellerCalibrationFragment fragment = new EchoCancellerCalibrationFragment();
            fragment.enableEcCalibrationResultSending(sendEcCalibrationResult);
            changeFragment(fragment);
            currentFragment = AssistantFragmentsEnum.ECHO_CANCELLER_CALIBRATION;
        } else {
            success();
        }
    }

    private void logIn(String username, String password, String displayName, String domain, boolean sendEcCalibrationResult) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        saveCreatedAccount(username, password, displayName, domain);

        if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
            launchEchoCancellerCalibration(sendEcCalibrationResult);
        }
    }

    public void checkAccount(String username, String password, String displayName, String domain) {
        saveCreatedAccount(username, password, displayName, domain);
    }

    public void linphoneLogIn(String username, String password, String displayName, boolean validate) {
        if (validate) {
            checkAccount(username, password, displayName, getString(R.string.default_domain));
        } else {
            logIn(username, password, displayName, getString(R.string.default_domain), true);
        }
    }

    public void genericLogIn(String username, String password, String displayName, String domain) {
        logIn(username, password, displayName, domain, false);
    }

    private void display(AssistantFragmentsEnum fragment) {
        switch (fragment) {
            case WELCOME:
                displayMenu();
                break;
            case LINPHONE_LOGIN:
                displayLoginLinphone();
                break;
            default:
                throw new IllegalStateException("Can't handle " + fragment);
        }
    }

    public void displayMenu() {
        fragment = new WelcomeFragment();
        changeFragment(fragment);
        currentFragment = AssistantFragmentsEnum.WELCOME;

    }

    public void displayLoginGeneric() {
        fragment = new LoginFragment();
        changeFragment(fragment);
        currentFragment = AssistantFragmentsEnum.LOGIN;

    }

    public void displayLoginLinphone() {
        fragment = new LinphoneLoginFragment();

        //LinphoneManager.getInstance().loadConfig(R.raw.config_linphone_account);
        //LinphoneManager.getInstance().resetLinphoneCore(this);


        changeFragment(fragment);
        currentFragment = AssistantFragmentsEnum.LINPHONE_LOGIN;

    }

    public void displayCreateAccount() {
        fragment = new CreateAccountFragment();
        changeFragment(fragment);
        currentFragment = AssistantFragmentsEnum.CREATE_ACCOUNT;

    }

    public void displayRemoteProvisioning() {
        fragment = new RemoteProvisioningFragment();
        changeFragment(fragment);
        currentFragment = AssistantFragmentsEnum.REMOTE_PROVISIONING;

    }

    public void retryLogin(LinphoneProxyConfig proxy, String password) {
        LinphoneAuthInfo info = LinphoneManager.getLc().findAuthInfo(LinphoneUtils.getUsernameFromAddress(proxy.getIdentity()), proxy.getRealm(), proxy.getDomain());
        if (info != null) {
            info.setPassword(password);
            LinphoneManager.getLc().addAuthInfo(info);
        }
        LinphoneManager.getLc().refreshRegisters();
    }

    public void saveCreatedAccount(String username, String password, String displayName, String domain) {
        if (accountCreated)
            return;

        if (username.startsWith("sip:")) {
            username = username.substring(4);
        }

        if (username.contains("@"))
            username = username.split("@")[0];

        if (domain.startsWith("sip:")) {
            domain = domain.substring(4);
        }

        String identity = "sip:" + username + "@" + domain;
        try {
            address = LinphoneCoreFactory.instance().createLinphoneAddress(identity);
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }

        if (displayName != null && !displayName.equals("")) {
            address.setDisplayName(displayName);
        }

        boolean isMainAccountLinphoneDotOrg = domain.equals(getString(R.string.default_domain));
        boolean useLinphoneDotOrgCustomPorts = getResources().getBoolean(R.bool.use_linphone_server_ports);
        AccountBuilder builder = new AccountBuilder(LinphoneManager.getLc())
                .setUsername(username)
                .setDomain(domain)
                .setDisplayName(displayName)
                .setPassword(password);

        if (isMainAccountLinphoneDotOrg && useLinphoneDotOrgCustomPorts) {
            if (getResources().getBoolean(R.bool.disable_all_security_features_for_markets)) {
                builder.setProxy(domain + ":5228")
                        .setTransport(TransportType.LinphoneTransportTcp);
            } else {
                builder.setProxy(domain + ":5223")
                        .setTransport(TransportType.LinphoneTransportTls);
            }

            builder.setExpires("604800")
                    .setAvpfEnabled(true)
                    .setAvpfRRInterval(3)
                    .setQualityReportingCollector("sip:voip-metrics@sip.linphone.org")
                    .setQualityReportingEnabled(true)
                    .setQualityReportingInterval(180)
                    .setRealm("sip.linphone.org");


            mPrefs.setStunServer(getString(R.string.default_stun));
            mPrefs.setIceEnabled(true);
        } else {
            String forcedProxy = "";
            if (!TextUtils.isEmpty(forcedProxy)) {
                builder.setProxy(forcedProxy)
                        .setOutboundProxyEnabled(true)
                        .setAvpfRRInterval(5);
            }
        }

        if (getResources().getBoolean(R.bool.enable_push_id)) {
            String regId = mPrefs.getPushNotificationRegistrationID();
            String appId = getString(R.string.push_sender_id);
            if (regId != null && mPrefs.isPushNotificationEnabled()) {
                String contactInfos = "app-id=" + appId + ";pn-type=google;pn-tok=" + regId;
                builder.setContactParameters(contactInfos);
            }
        }

        try {
            builder.saveNewAccount();
            accountCreated = true;
        } catch (LinphoneCoreException e) {
            e.printStackTrace();
        }
    }

    public void displayWizardConfirm(String username) {
        CreateAccountActivationFragment fragment = new CreateAccountActivationFragment();

        Bundle extras = new Bundle();
        extras.putString("Username", username);
        fragment.setArguments(extras);
        changeFragment(fragment);

        currentFragment = AssistantFragmentsEnum.CREATE_ACCOUNT_ACTIVATION;

    }

    public void isAccountVerified(String username) {
        Toast.makeText(this, getString(R.string.setup_account_validated), Toast.LENGTH_LONG).show();
        LinphoneManager.getLcIfManagerNotDestroyedOrNull().refreshRegisters();
        launchEchoCancellerCalibration(true);
    }

    public void isEchoCalibrationFinished() {
        mPrefs.setAccountEnabled(mPrefs.getAccountCount() - 1, true);
        success();
    }

    public Dialog displayWrongPasswordDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Drawable d = new ColorDrawable(getResources().getColor(R.color.colorC));
        d.setAlpha(200);
        dialog.setContentView(R.layout.input_dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(d);

        TextView customText = (TextView) dialog.findViewById(R.id.customText);
        customText.setText(getString(R.string.error_bad_credentials));
        return dialog;
    }

    public void showDialog(final LinphoneProxyConfig proxy) {
        final Dialog dialog = displayWrongPasswordDialog();

        Button retry = (Button) dialog.findViewById(R.id.retry);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        retry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = ((EditText) dialog.findViewById(R.id.password)).getText().toString();
                retryLogin(proxy, newPassword);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                success();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void success() {
        mPrefs.firstLaunchSuccessful();
        LinphoneActivity.instance().isNewProxyConfig();
        setResult(Activity.RESULT_OK);
        finish();
    }
}
