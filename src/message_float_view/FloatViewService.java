package message_float_view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;

import org.linphone.ChatFragment;
import org.linphone.LinphoneActivity;
import org.linphone.R;

import java.util.ServiceConfigurationError;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;

/**
 * Created by leason on 2016/4/22.
 */
public class FloatViewService extends Service implements FloatingViewListener {
    //   FloatingViewManager mfloatingViewManager;
    //  PopupWindow popupWindow;

    /*
    @Override
      public  int onStartCommand(Intent intent, int flags,int startID){

        final ImageView iconView= new ImageView(this);

      iconView.setImageResource(R.drawable.chat_start_body_over);
       // icon.setOnClickListener();
        final DisplayMetrics metrics= new DisplayMetrics();
        mfloatingViewManager =new FloatingViewManager(this,this);
        mfloatingViewManager.setFixedTrashIconImage(R.drawable.clean_field_default);
      mfloatingViewManager.setActionTrashIconImage(R.drawable.delete);
        final FloatingViewManager.Options options =new FloatingViewManager.Options();
        options.shape= FloatingViewManager.SHAPE_CIRCLE;
        options.overMargin=(int) (16*metrics.density);
        mfloatingViewManager.addViewToWindow(iconView,options);
        return 1;
      }*/
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

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        final LayoutInflater inflater = LayoutInflater.from(this);


        final ImageView iconView = new ImageView(this);
        sipUri = intent.getExtras().getString("sipUri");
        iconView.setImageResource(R.mipmap.ic_launcher);
        //final View test = floatview.findViewById(R.id.test);
        iconView.setOnClickListener(
                new View.OnClickListener() {
                    //    iconView.setOnClickListener(
                    //    new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LayoutInflater layoutInflater = (LayoutInflater) getApplication().getSystemService(LAYOUT_INFLATER_SERVICE);

                        ChatFragment fragment = LinphoneActivity.instance().floatViewDisplayChat(sipUri);
                        View popupView = layoutInflater.inflate(R.layout.chat, null);

                        //pwindo = new PopupWindow(fragment.chatroomview);
                        pwindo = new PopupWindow(popupView);
                        //beginTransaction().add(R.id.flaotchatroom,fragment).commit();
                        pwindo.setWidth(600);
                        pwindo.setHeight(600);
                        pwindo.showAsDropDown(iconView, 100, -100);
                        pwindo.update();
                        pwindo.isShowing();
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
        return 1;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onFinishFloatingView() {
        stopSelf();
    }
}

