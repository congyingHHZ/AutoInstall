package com.example.hl.test01;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class AutoInstallService extends AccessibilityService{
    private static final String TAG = "AutoInstallService:";
    //private static String PACKAGE_INSTALLER = "com.android.packageinstaller";
    private static final String PACKAGE_INSTALLER_MIUI = "com.miui.packageinstaller";
    private static final String PACKAGE_INSTALLER_MIUI_adb = "com.miui.securitycenter";

    public AutoInstallService() {
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        /*
         * 回调方法，当事件发生时会从这里进入，在这里判断需要捕获的内容，
         * 可通过下面这句log将所有事件详情打印出来，分析决定怎么过滤。
         */

        log("!!onAccessibilityEvent!!");
        //log(event.toString());
        AccessibilityNodeInfo noteInfo = event.getSource();
        log("===noteInfo!===");
        if (event.getSource() == null) {
            log("<null> event source");
            return;
        }

        AccessibilityNodeInfo rowNode = getRootInActiveWindow();
        log("===rowNode!===");
        //log(rowNode.toString());
        int eventType = event.getEventType();
        log(eventType+"");
        log(event.getPackageName().toString());
        /*
         * 在弹出安装界面时会发生 TYPE_WINDOW_STATE_CHANGED 事件，其属主
         * 是系统安装器com.android.packageinstaller
         */
        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                && (event.getPackageName().equals(PACKAGE_INSTALLER_MIUI) | event.getPackageName().equals(PACKAGE_INSTALLER_MIUI_adb))) {
            boolean r = performInstallation(event);
            log("Action Perform: " + r);
        }else if(eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
                event.getPackageName().equals(PACKAGE_INSTALLER_MIUI)){
            log("!!input TYPE_WINDOW_CONTENT_CHANGED !!");
            boolean r = performInstallation(event);
            log("Action Perform: " + r);
        }
    }
    @Override
    public void onInterrupt() {
        log("AutoInstallServiceInterrupted");
    }

    private void log(String s) {
        Log.d(TAG, s);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean performInstallation(AccessibilityEvent event) {
        List<AccessibilityNodeInfo> nodeInfoList;
        /*
         * 有的手机会弹2次，有的只弹一次，在替换安装时会出现确定按钮，
         * 为了大而全，下面定义了比较多的内容，可按需增减。
         */
        log("!!performInstallation!!");
        String[] labels = new String[]{"本次允许","允许", "确定", "继续安装", "下一步", "完成","安装"};
        for (String label : labels) {
            log(label);
            nodeInfoList = event.getSource().findAccessibilityNodeInfosByText(label);
            if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
                boolean performed = performClick(nodeInfoList);
                if (performed) return true;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean performClick(List<AccessibilityNodeInfo> nodeInfoList) {
        for (AccessibilityNodeInfo node : nodeInfoList) {
            /*
             * 这里还可以根据node的类名来过滤，大多数是button类，这里也是为了大而全，
             * 判断只要是可点击的是可用的就点。
             */

            if (node.isClickable() && node.isEnabled()) {
                return node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            else if(node.getClassName() == "android.widget.Button"){
                Log.d(TAG,"clickByNode");
                return clickByNode(node);
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public final boolean clickByNode(AccessibilityNodeInfo nodeInfo){
        if (nodeInfo == null){
            return false;
        }
//        if (nodeInfo.getClassName() != "android.widget.Button"){
//            return false;
//        }
        Rect rect = new Rect();
        nodeInfo.getBoundsInScreen(rect);
        int x = (rect.left + rect.right)/2;
        int y = (rect.top + rect.bottom)/2;

        Point point = new Point(x,y);

        GestureDescription.Builder builder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(point.x,point.y);

        builder.addStroke(new GestureDescription.StrokeDescription(path,100,50));
        //path：路径  startTime：从手势开始到开始笔画的时间
        final GestureDescription gesture = builder.build();

        return dispatchGesture(gesture,
                new GestureResultCallback(){
            @Override
            public void onCompleted(GestureDescription gestureDescription){
                super.onCompleted(gestureDescription);
            }
            @Override
            public void onCancelled(GestureDescription gestureDescription){
                super.onCancelled(gestureDescription);
            }

                },null);

    }


}