package messagefloatview;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.linphone.LinphoneActivity;
import org.linphone.LinphoneManager;
import org.linphone.R;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.mediastream.Log;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

/**
 * Created by leason on 2016/4/22.
 */
public class FloatViewService extends Service implements FloatingViewListener {
    static FloatViewService instance = null;
    public static boolean isReady() {
        return instance != null;
    }

    /**
     * @throws RuntimeException service not instantiated
     */
    public static FloatViewService instance() {
        if (isReady()) return instance;
        throw new RuntimeException("FloatViewService not instantiated yet");

    }

    public FloatingViewManager mFloatingViewManager;
    PopupWindow pwindo;
    TextView unreadMessages;
    View iconView;
    FloatingViewManager.Options options;
    LayoutInflater inflater;



    //    static ArrayList<String> showing_sip;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        instance=this;
        inflater = LayoutInflater.from(this);

        final DisplayMetrics metrics = new DisplayMetrics();
        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.clean_field_default);
        mFloatingViewManager.setActionTrashIconImage(R.drawable.delete);
        options = new FloatingViewManager.Options();
        options.shape = FloatingViewManager.SHAPE_CIRCLE;
        options.overMargin = (int) (16 * metrics.density);
        return 1;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void addfloatvideo(final String sipUri,View view){
        iconView= view;
       // iconView = inflater.inflate(R.layout.video, null);
        mFloatingViewManager.addViewToWindow(iconView, options, sipUri);
    }
    public void addFloatChatRoom(final String sipUri) {
        iconView = inflater.inflate(R.layout.flaot_chat_icon, null);
        unreadMessages = (TextView) iconView.findViewById(R.id.unreadMessages);
        LinphoneAddress address;
        int unreadMessagesCount = 0;
        try {
            address = LinphoneCoreFactory.instance().createLinphoneAddress(sipUri);
            LinphoneChatRoom chatRoom = LinphoneManager.getLc().getChatRoom(address);
            unreadMessagesCount   = chatRoom.getUnreadMessagesCount();
        } catch (LinphoneCoreException e) {
            Log.e("Chat view cannot parse address",e);
        }
        if (unreadMessagesCount > 0) {
            unreadMessages.setVisibility(View.VISIBLE);
            unreadMessages.setText(String.valueOf(unreadMessagesCount));
            if(unreadMessagesCount > 99){
                unreadMessages.setTextSize(12);
            }
        } else {
            unreadMessages.setVisibility(View.GONE);
        }
        //   iconView.setImageResource(R.mipmap.ic_launcher);
        //final View test = floatview.findViewById(R.id.test);
        iconView.setOnClickListener(
                new View.OnClickListener() {
                    //    iconView.setOnClickListener(
                    //    new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(pwindo==null){
                            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                            View chatroom = layoutInflater.inflate(R.layout.chat, null);
                            LinphoneActivity.instance().floatViewDisplayChat(sipUri);
                            chatroom.setMinimumHeight(500);
                            chatroom.setMinimumWidth(500);
                            pwindo = new PopupWindow(chatroom);
                        }//LayoutInflater layoutInflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
                       // LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                      //  Fragment fragment = LinphoneActivity.instance().floatViewDisplayChat(sipUri);
                      //  View chatroom = layoutInflater.inflate(R.layout.chat, null);
                        // View popupView = layoutInflater.inflate(R.layout.flaot_chat_room, null);
                        // popupView.setMinimumHeight(500);
                        //popupView.setMinimumWidth(500);
                        // popupView.add
                        //pwindo = new PopupWindow(fragment.chatroomview);
                        //   pwindo = new PopupWindow(popupView,800,600);
                        // pwindo = new PopupWindow(popupView);

                      if (!pwindo.isShowing()){
                        //beginTransaction().add(R.id.flaotchatroom,fragment).commit();
                        pwindo.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                        pwindo.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                        pwindo.showAsDropDown(iconView, 10, -10);
                    }
                        else {
                            pwindo.dismiss();
                        }
                    }
                }
        );

        mFloatingViewManager.addViewToWindow(iconView, options, sipUri);
    }

    @Override
    public void onFinishFloatingView() {

      /*  new FloatingView(());*/
        stopSelf();
    }
}

