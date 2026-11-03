package com.hashem.island;

import android.app.*;
import android.content.*;
import android.graphics.PixelFormat;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;

/** User-started floating island. Android overlay permission is explicitly required. */
public class IslandOverlayService extends Service {
    private WindowManager wm;
    private View island;
    private WindowManager.LayoutParams params;
    private boolean expanded=false;
    private final Handler handler=new Handler(Looper.getMainLooper());
    private Runnable hideTask;
    private TextView detail;
    private TextView label;
    private int displaySeconds=3;
    private final int gold=Color.rgb(217,180,91);
    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        Notification n=new Notification.Builder(this,"island_service")
          .setSmallIcon(android.R.drawable.ic_dialog_info)
          .setContentTitle("HASHEM Island is active")
          .setContentText("الجزيرة العائمة تعمل — اضغط لإيقافها")
          .setOngoing(true)
          .setContentIntent(PendingIntent.getService(this,1,new Intent(this,IslandOverlayService.class).setAction("STOP"),PendingIntent.FLAG_IMMUTABLE))
          .build();
        startForeground(71,n);
        if(Settings.canDrawOverlays(this)) attachIsland(); else stopSelf();
    }
    private void createChannel() {
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel c=new NotificationChannel("island_service","HASHEM Island",NotificationManager.IMPORTANCE_LOW);
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).createNotificationChannel(c);
        }
    }
    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    private void attachIsland() {
        wm=(WindowManager)getSystemService(WINDOW_SERVICE);
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setGravity(Gravity.CENTER);
        box.setPadding(dp(18),dp(10),dp(18),dp(10)); box.setBackgroundResource(R.drawable.island_bg);
        label=new TextView(this); label.setText("♛ HASHEM ISLAND"); label.setTextColor(gold); label.setTextSize(13); label.setTypeface(Typeface.DEFAULT_BOLD);
        box.addView(label);
        detail=new TextView(this); detail.setText("HASHEM ISLAND"); detail.setTextColor(Color.WHITE); detail.setTextSize(11); detail.setGravity(Gravity.CENTER);
        box.addView(detail);
        params=new WindowManager.LayoutParams(dp(190),dp(54),Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE,
          WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);
        params.gravity=Gravity.TOP|Gravity.CENTER_HORIZONTAL; params.y=dp(28);
        box.setOnClickListener(v->{
            expanded=!expanded;
            detail.setText(expanded?"الجزيرة نشطة • HASHEM":"اضغط لعرض الجزيرة");
            params.width=dp(expanded?260:190); params.height=dp(expanded?92:54);
            wm.updateViewLayout(box,params);
        });
        box.setOnTouchListener(new View.OnTouchListener(){
            float downX,downY; int startX,startY;
            public boolean onTouch(View v,MotionEvent e){
                if(e.getAction()==MotionEvent.ACTION_DOWN){downX=e.getRawX();downY=e.getRawY();startX=params.x;startY=params.y;return false;}
                if(e.getAction()==MotionEvent.ACTION_MOVE){
                    params.x=startX+(int)(e.getRawX()-downX); params.y=Math.max(0,startY+(int)(e.getRawY()-downY));
                    try{wm.updateViewLayout(box,params);}catch(Exception ignored){} return true;
                } return false;
            }
        });
        island=box;
        island.setVisibility(View.GONE);
        try{wm.addView(island,params);}catch(Exception e){stopSelf();}
    }
    @Override public int onStartCommand(Intent intent,int flags,int startId){
        if(intent!=null && "STOP".equals(intent.getAction())) { stopSelf(); return START_NOT_STICKY; }
        if(intent!=null && "SHOW_EVENT".equals(intent.getAction())) {
            String title=intent.getStringExtra("title"); String text=intent.getStringExtra("text");
            showEvent(title,text);
        }
        return START_NOT_STICKY;
    }
    private void showEvent(String title,String text){
        if(island==null)return;
        label.setText(title==null||title.trim().isEmpty()?"♛ HASHEM ISLAND":title);
        detail.setText(text==null||text.trim().isEmpty()?"إشعار جديد":text);
        expanded=true; params.width=dp(260); params.height=dp(76);
        island.setVisibility(View.VISIBLE);
        try{wm.updateViewLayout(island,params);}catch(Exception ignored){}
        if(hideTask!=null)handler.removeCallbacks(hideTask);
        hideTask=()->{if(island!=null){island.setVisibility(View.GONE);expanded=false;}};
        handler.postDelayed(hideTask,displaySeconds*1000L);
    }
    @Override public void onDestroy(){if(hideTask!=null)handler.removeCallbacks(hideTask);if(wm!=null&&island!=null)try{wm.removeView(island);}catch(Exception ignored){} super.onDestroy();}
    @Override public IBinder onBind(Intent intent){return null;}
}
