package com.hashem.island;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.provider.Settings;
import android.net.Uri;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
 private LinearLayout root; private TextView state;
 private final int gold=Color.rgb(217,180,91);
 private TextView label(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setPadding(0,8,0,8);return t;}
 private Button btn(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setTextColor(gold);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(27,26,25)));b.setOnClickListener(v->r.run());return b;}
 @Override public void onCreate(Bundle b){super.onCreate(b);render();}
 private void render(){
  root=new LinearLayout(this);root.setOrientation(1);root.setPadding(22,28,22,24);root.setBackgroundColor(Color.rgb(8,9,11));
  ScrollView sc=new ScrollView(this);sc.addView(root);
  TextView brand=label("♛ HASHEM",28,gold);brand.setTypeface(Typeface.DEFAULT_BOLD);root.addView(brand);
  root.addView(label("ISLAND OS PRO • ENHANCED 2.0",12,Color.LTGRAY));
  TextView card=label("HASHEM ISLAND\\nالجزيرة العائمة",18,Color.WHITE);card.setGravity(Gravity.CENTER);card.setBackgroundResource(R.drawable.island_bg);card.setPadding(18,24,18,24);root.addView(card);
  state=label("الحالة: الجزيرة غير مفعلة",13,Color.GRAY);root.addView(state);
  root.addView(btn("1 — منح إذن الظهور فوق التطبيقات",()->overlayPermission()));
  root.addView(btn("2 — تشغيل الجزيرة العائمة",()->startIsland()));
  root.addView(btn("إيقاف الجزيرة",()->stopService(new Intent(this,IslandOverlayService.class))));
  root.addView(btn("إعدادات الوصول للإشعارات",()->startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))));
  root.addView(btn("الثيمات",()->themes()));
  root.addView(btn("حول التطبيق",()->new AlertDialog.Builder(this).setTitle("HASHEM ISLAND OS PRO 2.0").setMessage("نسخة Enhanced: الجزيرة العائمة الأساسية قيد التطوير.").setPositiveButton("حسناً",null).show()));
  setContentView(sc);
 }
 private void overlayPermission(){
  if(Build.VERSION.SDK_INT>=23&&!Settings.canDrawOverlays(this)){
   Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:"+getPackageName()));startActivity(i);
  } else state.setText("إذن الظهور فوق التطبيقات ممنوح.");
 }
 private void startIsland(){
  if(Build.VERSION.SDK_INT>=23&&!Settings.canDrawOverlays(this)){overlayPermission();state.setText("امنح الإذن ثم اضغط تشغيل الجزيرة مجدداً.");return;}
  try{if(Build.VERSION.SDK_INT>=26)startForegroundService(new Intent(this,IslandOverlayService.class));else startService(new Intent(this,IslandOverlayService.class));state.setText("تم طلب تشغيل الجزيرة العائمة.");}
  catch(Exception e){state.setText("تعذر التشغيل: "+e.getMessage());}
 }
 private void themes(){
  String[] a={"Gold Pro","Neon Blue","Purple","Emerald","Silver","Pure Black","Red Cyber","Custom Theme"};
  new AlertDialog.Builder(this).setTitle("الثيمات").setItems(a,(d,w)->state.setText("تم اختيار "+a[w]+" — ربط ألوان الثيم قيد التطوير.")).show();
 }
}