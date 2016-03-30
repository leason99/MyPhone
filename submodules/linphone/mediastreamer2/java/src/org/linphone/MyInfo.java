package org.linphone;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.linphone.core.LinphoneProxyConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leason on 2016/3/25.
 */
public class MyInfo extends Fragment{

    String[]  sideMenuItems ;
   RelativeLayout  sideMenuContent ;
    ListView   sideMenuItemList;
     View view;
    ListView accountsList;
    RelativeLayout  defaultAccount ,quitLayout;
    LayoutInflater mInflate;
    static MyInfo  instance;
    public static  MyInfo instance() {
        if (instance != null)
            return instance;
        throw new RuntimeException("MyInfoActivity not instantiated yet");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     super.onCreateView(inflater, container, savedInstanceState);
        mInflate=inflater;

         view = inflater.inflate(R.layout.my_info, container, false);
        initSideMenu();
        return view;
    }
    @Override
    public void onResume(){
        super.onResume();
        LinphoneActivity.instance().selectMenu(FragmentsAvailable.myInfo);
    }
    public void initSideMenu() {

        sideMenuItems = getResources().getStringArray(R.array.side_menu_item);
        sideMenuContent = (RelativeLayout) view.findViewById(R.id.side_menu_content);
        sideMenuItemList = (ListView) view.findViewById(R.id.item_list);

        sideMenuItemList.setAdapter(new ArrayAdapter<String>(LinphoneActivity.instance(), R.layout.side_menu_item_cell, sideMenuItems));
        sideMenuItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (sideMenuItemList.getAdapter().getItem(i).toString().equals("Settings")) {
                    LinphoneActivity.instance().displaySettings();
                }
                if (sideMenuItemList.getAdapter().getItem(i).toString().equals("About")) {
                    LinphoneActivity.instance().displayAbout();
                }
                if (sideMenuItemList.getAdapter().getItem(i).toString().equals("Assistant")) {
                    LinphoneActivity.instance().displayAssistant();
                }
            }
        });
        initAccounts();
        quitLayout = (RelativeLayout) view.findViewById(R.id.side_menu_quit);
        quitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinphoneActivity.instance().quit();
            }
        });
    }
    private void initAccounts() {
        accountsList = (ListView) view.findViewById(R.id.accounts_list);
        defaultAccount = (RelativeLayout)view.findViewById(R.id.default_account);

       refreshAccounts();
    }

    private void displayMainAccount() {
        defaultAccount.setVisibility(View.VISIBLE);
        ImageView status = (ImageView) defaultAccount.findViewById(R.id.main_account_status);
        TextView address = (TextView) defaultAccount.findViewById(R.id.main_account_address);
        TextView displayName = (TextView) defaultAccount.findViewById(R.id.main_account_display_name);
        LinphoneProxyConfig proxy = LinphoneManager.getLc().getDefaultProxyConfig();
        if (proxy == null) {
            displayName.setText(getString(R.string.no_account));
            status.setVisibility(View.GONE);
            address.setText("");
       //     statusFragment.resetAccountStatus();

            defaultAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinphoneActivity.instance().displayAccountSettings(0);

                }
            });
        }
        else {
            address.setText(proxy.getAddress().asStringUriOnly());
            displayName.setText(LinphoneUtils.getAddressDisplayName(proxy.getAddress()));
       //     status.setImageResource(getStatusIconResource(proxy.getState()));
            status.setVisibility(View.VISIBLE);

            defaultAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinphoneActivity.instance().displayAccountSettings(LinphonePreferences.instance().getDefaultAccountIndex());
                }
            });
        }
    }

    public void refreshAccounts() {
        if (LinphoneManager.getLc().getProxyConfigList().length > 1) {
            accountsList.setVisibility(View.VISIBLE);
            accountsList.setAdapter(new AccountsListAdapter());
            accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int position = Integer.parseInt(view.getTag().toString());
                    LinphoneActivity.instance().displayAccountSettings(position);

                }
            });
        } else {
            accountsList.setVisibility(View.GONE);
        }
        displayMainAccount();
    }
    class AccountsListAdapter extends BaseAdapter {
        List<LinphoneProxyConfig> proxy_list;

        AccountsListAdapter() {
            proxy_list = new ArrayList<LinphoneProxyConfig>();
            refresh();
        }

        public void refresh() {
            proxy_list = new ArrayList<LinphoneProxyConfig>();
            for (LinphoneProxyConfig proxyConfig : LinphoneManager.getLc().getProxyConfigList()) {
                if (proxyConfig != LinphoneManager.getLc().getDefaultProxyConfig()) {
                    proxy_list.add(proxyConfig);
                }
            }
        }

        public int getCount() {
            if (proxy_list != null) {
                return proxy_list.size();
            } else {
                return 0;
            }
        }

        public Object getItem(int position) {
            return proxy_list.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = null;
            LinphoneProxyConfig lpc = (LinphoneProxyConfig) getItem(position);
            if (convertView != null) {
                view = convertView;
            } else {
                view = mInflate.inflate(R.layout.side_menu_account_cell, parent, false);
            }

            ImageView status = (ImageView) view.findViewById(R.id.account_status);
            TextView address = (TextView) view.findViewById(R.id.account_address);
            String sipAddress = lpc.getAddress().asStringUriOnly();

            address.setText(sipAddress);

            int nbAccounts = LinphonePreferences.instance().getAccountCount();
            int accountIndex = 0;

            for (int i = 0; i < nbAccounts; i++) {
                String username = LinphonePreferences.instance().getAccountUsername(i);
                String domain = LinphonePreferences.instance().getAccountDomain(i);
                String id = "sip:" + username + "@" + domain;
                if (id.equals(sipAddress)) {
                    accountIndex = i;
                    view.setTag(accountIndex);
                    break;
                }
            }
       //     status.setImageResource(getStatusIconResource(lpc.getState()));
            return view;
        }
    }
}
