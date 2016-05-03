package message_float_view;

import android.app.Fragment;
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
import org.linphone.R;

import java.util.ArrayList;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;
/**
 * Created by leason on 2016/4/22.
 */
public class FloatViewService extends Service implements FloatingViewListener {
    static FloatViewService instance;

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

    FloatingViewManager mFloatingViewManager;
    PopupWindow pwindo;
    String sipUri;
    TextView unreadMessages;
    View iconView;
    static ArrayList<String> showing_sip;
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final LayoutInflater inflater = LayoutInflater.from(this);
        sipUri = intent.getExtras().getString("sipUri");
     //   final ImageView iconView = new ImageView(this);
      if( showing_sip==null) {
           showing_sip = new ArrayList<String>();
       }
        //if (showing_sip.contains(sipUri))
        if(true){

            showing_sip.add(sipUri);

            iconView = inflater.inflate(R.layout.flaot_chat_icon, null);
            unreadMessages = (TextView) iconView.findViewById(R.id.unreadMessages);

            //   iconView.setImageResource(R.mipmap.ic_launcher);
            //final View test = floatview.findViewById(R.id.test);
            iconView.setOnClickListener(
                    new View.OnClickListener() {
                        //    iconView.setOnClickListener(
                        //    new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            //LayoutInflater layoutInflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);
                            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
                            Fragment fragment = LinphoneActivity.instance().floatViewDisplayChat(sipUri);
                            View chatroom = layoutInflater.inflate(R.layout.chat, null);

                            // View popupView = layoutInflater.inflate(R.layout.flaot_chat_room, null);
                            // popupView.setMinimumHeight(500);
                            //popupView.setMinimumWidth(500);
                            // popupView.add
                            //pwindo = new PopupWindow(fragment.chatroomview);
                            //   pwindo = new PopupWindow(popupView,800,600);
                            // pwindo = new PopupWindow(popupView);
                            chatroom.setMinimumHeight(500);
                            chatroom.setMinimumWidth(500);
                            pwindo = new PopupWindow(chatroom);

                            //beginTransaction().add(R.id.flaotchatroom,fragment).commit();
                            pwindo.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                            pwindo.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                            pwindo.showAsDropDown(iconView, 10, -10);
                            pwindo.update();
                            pwindo.isShowing();

                            pwindo.update();
                        }
                    }
            );

            final DisplayMetrics metrics = new DisplayMetrics();
            mFloatingViewManager = new FloatingViewManager(this, this);
            mFloatingViewManager.setFixedTrashIconImage(R.drawable.clean_field_default);
            mFloatingViewManager.setActionTrashIconImage(R.drawable.delete);
            final FloatingViewManager.Options options = new FloatingViewManager.Options();
            options.shape = FloatingViewManager.SHAPE_CIRCLE;
            options.overMargin = (int) (16 * metrics.density);
            //   WindowManager winma= (WindowManager) getSystemService(WINDOW_SERVICE);
            //  winma.addView(floatview);
            mFloatingViewManager.addViewToWindow(iconView, options);
        }
     /*  new FloatingView(getApplication());
        //unreadmessage
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
        }*/
        return 1;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onFinishFloatingView() {

      /*  new FloatingView(());*/
        stopSelf();
    }
}

