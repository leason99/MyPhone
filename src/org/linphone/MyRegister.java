package org.linphone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.linphone.assistant.LoginFragment;

import java.io.IOError;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by leason on 2016/4/21.,
 */
public class MyRegister extends Fragment implements View.OnClickListener {
    private EditText username, password, email;
    private Button register;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_register, container, false);
        username = (EditText) view.findViewById(R.id.username);
        password = (EditText) view.findViewById(R.id.password);
        email = (EditText) view.findViewById(R.id.email);
        register = (Button) view.findViewById(R.id.register);
        register.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                String param = new Uri.Builder()
                        .appendQueryParameter("account", username.getText().toString())
                        .appendQueryParameter("password", password.getText().toString())
                        .appendQueryParameter("mail", email.getText().toString())
                        .appendQueryParameter("add", "TRUE")
                        .build()
                        .getEncodedQuery();
                try {
                    URL url = new URL("http://162.243.4.139/register.php");
                    Register register = new Register(param, url);
                    register.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    private class Register extends AsyncTask<Void, ProgressDialog, String> {
        String param;
        URL RegisterUrl;
        boolean NetError =false;
        int ErrorCode;

        Register(String params, URL url) {
            RegisterUrl = url;
            param = params;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpURLConnection connection = (HttpURLConnection) RegisterUrl.openConnection();
                connection.setRequestMethod("POST");

                connection.setDoOutput(true);
                OutputStream dataoutput = connection.getOutputStream();
                dataoutput.write(param.getBytes());

                switch(ErrorCode=connection.getResponseCode()){
                    case 200:
                        dataoutput.flush();
                        dataoutput.close();
                        break;
                    default:
                        NetError=true;
                        break;
                }


            } catch (Exception e) {

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
          if(NetError)
            Toast.makeText(getActivity(), "server error"+ String.valueOf(ErrorCode),Toast.LENGTH_LONG).show();

            else {new AlertDialog.Builder(getActivity())
                    .setTitle("attention")
                    .setMessage("Pleas open open e-mail we send to launch your account ")
                    .setPositiveButton("OK",null)
                    .show();}
            MyLogin.instance().getFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new LoginFragment()).commit();
            super.onPostExecute(s);

        }
    }
}
