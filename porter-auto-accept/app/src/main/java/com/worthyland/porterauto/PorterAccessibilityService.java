package com.worthyland.porterauto;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.*;
import java.util.regex.*;

public class PorterAccessibilityService extends AccessibilityService {
    static final String PORTER="com.theporter.android.driverapp";
    long lastAccept=0;
    @Override public void onAccessibilityEvent(AccessibilityEvent ev){
        if(ev==null || ev.getPackageName()==null || !PORTER.contentEquals(ev.getPackageName())) return;
        if(!getSharedPreferences("rules",0).getBoolean("auto",false)) return;
        if(System.currentTimeMillis()-lastAccept<3500) return;
        AccessibilityNodeInfo root=getRootInActiveWindow(); if(root==null)return;
        String text=treeText(root); if(text.length()<5)return;
        Double fare=find(text,"(?:fare|₹|rs\\.?)[^0-9]{0,12}(\\d+(?:\\.\\d+)?)");
        Double pickup=find(text,"(?:pickup|pick up)[^0-9]{0,20}(\\d+(?:\\.\\d+)?)\\s*km");
        Double drop=find(text,"(?:drop|destination)[^0-9]{0,20}(\\d+(?:\\.\\d+)?)\\s*km");
        double minFare=num("fare",300), maxPickup=num("pickup",5), minDrop=num("drop",0);
        if(fare!=null && fare<minFare)return;
        if(pickup!=null && pickup>maxPickup)return;
        if(drop!=null && drop<minDrop)return;
        String areas=getSharedPreferences("rules",0).getString("areas","").trim();
        if(!areas.isEmpty()){ String low=text.toLowerCase(Locale.US); boolean hit=false; for(String a:areas.split(",")) if(!a.trim().isEmpty()&&low.contains(a.trim().toLowerCase(Locale.US))){hit=true;break;} if(!hit)return; }
        if(!containsAccept(text))return;
        if(swipeAccept(root)) lastAccept=System.currentTimeMillis();
    }
    double num(String k,double d){try{return Double.parseDouble(getSharedPreferences("rules",0).getString(k,""+d));}catch(Exception e){return d;}}
    boolean containsAccept(String s){String l=s.toLowerCase(Locale.US);return l.contains("accept")||l.contains("book now")||l.contains("confirm trip");}
    boolean swipeAccept(AccessibilityNodeInfo root){
        AccessibilityNodeInfo target=findAcceptNode(root); Rect r=new Rect(); if(target!=null)target.getBoundsInScreen(r);
        int width=getResources().getDisplayMetrics().widthPixels,height=getResources().getDisplayMetrics().heightPixels;
        int y,startX,endX;
        if(target!=null&&r.width()>180&&r.height()>40){y=r.centerY();startX=r.left+Math.max(12,r.width()/12);endX=r.right-Math.max(12,r.width()/12);}
        else{y=(int)(height*0.86f);startX=(int)(width*0.12f);endX=(int)(width*0.88f);}
        if(endX<=startX||y<=0||y>=height)return false;
        Path path=new Path();path.moveTo(startX,y);path.lineTo(endX,y);
        GestureDescription.StrokeDescription stroke=new GestureDescription.StrokeDescription(path,0,650);
        return dispatchGesture(new GestureDescription.Builder().addStroke(stroke).build(),null,null);
    }
    AccessibilityNodeInfo findAcceptNode(AccessibilityNodeInfo n){
        if(n==null)return null; CharSequence tx=n.getText(),cd=n.getContentDescription();
        String a=((tx==null?"":tx.toString())+" "+(cd==null?"":cd.toString())).toLowerCase(Locale.US);
        if((a.contains("slide")&&a.contains("accept"))||a.contains("swipe to accept")||a.contains("accept ride")||a.equals("accept"))return n;
        for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo f=findAcceptNode(n.getChild(i));if(f!=null)return f;} return null;
    }
    String treeText(AccessibilityNodeInfo n){StringBuilder s=new StringBuilder();walk(n,s);return s.toString();}
    void walk(AccessibilityNodeInfo n,StringBuilder s){if(n==null)return;if(n.getText()!=null)s.append(' ').append(n.getText());if(n.getContentDescription()!=null)s.append(' ').append(n.getContentDescription());for(int i=0;i<n.getChildCount();i++)walk(n.getChild(i),s);}
    Double find(String s,String r){Matcher m=Pattern.compile(r,Pattern.CASE_INSENSITIVE).matcher(s);return m.find()?Double.valueOf(m.group(1)):null;}
    @Override public void onInterrupt(){}
}
