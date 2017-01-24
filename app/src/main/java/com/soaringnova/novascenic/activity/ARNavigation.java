package com.soaringnova.novascenic.activity;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.soaringnova.novascenic.R;
import com.soaringnova.novascenic.adapter.PoiListAdapter;
import com.soaringnova.novascenic.utils.ToastUtil;
import com.soaringnova.novascenic.view.FloatView;
import com.soaringnova.novascenic.view.RadarView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Be on 2017/1/23.
 * AR导航界面
 */

public class ARNavigation extends BaseActivity
        implements PoiSearch.OnPoiSearchListener, SensorEventListener, AMapLocationListener {
    //成员变量
    @BindView(R.id.dl)
    DrawerLayout dl;
    @BindView(R.id.rightContent)
    LinearLayout rightContent;
    @BindView(R.id.poiList)
    RecyclerView poiList;
    private FrameLayout poiContent;
    private SurfaceView surfaceView;
    private Camera camera;
    private boolean preview = false;
    private ArrayList<FloatView> floatViewList = new ArrayList<>(); //存放悬浮View的集合
    private ArrayList<Integer> angle = new ArrayList<>();  //存放返回poi点的角度
    private ArrayList<PoiItem> pois;    //存放poi集合
    private RelativeLayout activity_arnavigation;
    private TextView editQuery;
    private TextView currentAddress;
    private MapView mapView;
    private AMap aMap;
    private SensorManager sensorManager;
    private Sensor magneticSensor;//传感器
    private LinearLayout.LayoutParams mParams;
    private Marker marker;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private LatLonPoint centerpoint;//= new LatLonPoint(30.230867, 120.189743);
    private RadarView radarView;
    private float x;
    private float y;
    private float z;
    private float fixedZ;
    private float fixedX1;
    private float tempx;
    //记录控件高
    private int viewHeight = 130;
    //存放屏幕宽高
    private int width;
    private int height;
    private AMapLocationClient aMapLocationClient;
    private AMapLocationClientOption mLocationOption;
    private SensorManager sensorManager2;
    private Sensor sensor2;
    private TextView xyz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arnavigation);
        ButterKnife.bind(this);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().addCallback(new SurfaceViewCallback());
        sensorManager2 = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor2 = sensorManager2.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        //初始化定位
        initLocator();
        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
        //初始化AMap对象&注册监听
        init();

    }

    /**
     * 初始化定位系统
     */
    private void initLocator() {
        aMapLocationClient = new AMapLocationClient(this);
        aMapLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(10000);
        //给定位客户端对象设置定位参数
        aMapLocationClient.setLocationOption(mLocationOption);
        //启动定位
        aMapLocationClient.startLocation();
    }

    /**
     * 标注当前位置
     *
     * @param centerpoint
     */
    private void showMarker(LatLng centerpoint) {
        marker = aMap.addMarker(new MarkerOptions()
                .position(centerpoint)
                .title("单位")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true));
        marker.showInfoWindow();// 设置默认显示一个infowinfow
    }

    /**
     * 初始化AMap对象&注册监听
     */
    private void init() {
        activity_arnavigation = (RelativeLayout) findViewById(R.id.activity_arnavigation);
        poiContent = (FrameLayout) findViewById(R.id.poiContent);   //悬浮view容器
        //将poicontent右移400像素以校准
        //poiContent.setTranslationX(8640);
        dl.setOnClickListener(new onClickListenr());
        rightContent.setOnClickListener(new onClickListenr());
        radarView = (RadarView) findViewById(R.id.radarView);
        radarView.setOnClickListener(new onClickListenr());
        //editQuery = (EditText) findViewById(R.id.editQuery);
        currentAddress = (TextView) findViewById(R.id.currentAddress);
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
            mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            //activity_arnavigation.addView(mapView, mParams);
        }
        //得到屏幕宽高
        WindowManager wm = this.getWindowManager();
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
        System.out.println(width + "22222:" + height);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //注册传感器
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        poiContent.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                for (FloatView floatView : floatViewList) {
                    floatView.setSelectedState(false);
                    floatView.setAlpha(0.8f);
                }
            }
        });
        dl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }


    /**
     * * 开始进行poi搜索
     */
    protected void doSearchQuery(String keyWord) {
        //aMap.clear();
        //清空悬浮view
        for (FloatView floatView : floatViewList) {
            poiContent.removeView(floatView);
        }
        floatViewList.clear();
        angle.clear();
        int currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "杭州市");
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(centerpoint, 1000));//设置周边搜索的中心点以及半径
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        pois = poiResult.getPois();
        aMap.clear();// 清理之前的图标
        radarView.clearPOI();//清空poi
        if (poiResult == null || poiResult.getPois().size() == 0) {
            Toast.makeText(ARNavigation.this, "没有结果，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
         //设置布局管理器
        poiList.setLayoutManager(layoutManager);
        //设置为垂直布局，这也是默认的
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        poiList.setAdapter(new PoiListAdapter(this, pois));
        //设置最远距离
        radarView.setMaxDistance(pois.get(pois.size() - 1).getDistance());
        for (final PoiItem poi : pois) {
            System.out.println("距离：   " + poi.getDirection() + poi.getDistance() + "==============");
            double x1 = (poi.getLatLonPoint().getLongitude());
            double x2 = (poi.getLatLonPoint().getLatitude());
            double y1 = (centerpoint.getLongitude());
            double y2 = (centerpoint.getLatitude());
            System.out.println("  poi点：" + x1 + "==" + x2 + "==\n 中心点：" + y1 + "==" + y2 + "");
            RadarView.MyLatLng A = new RadarView.MyLatLng(x1, x2);
            RadarView.MyLatLng B = new RadarView.MyLatLng(y1, y2);
            int poiAngle = (int) RadarView.getAngle(A, B);
            System.out.println("角度：==" + poiAngle);
            radarView.addPoint(poi, centerpoint);
            System.out.println("==============================================================================");
            //初始化悬浮view位置，根据角度设置left， 并把角度20度之内的poi实现层叠
            int left = (int) ((((float) (360 - RadarView.getAngle(A, B)) / (float) 360)) * -width * 6) + 2160;
            int top = getTop(poiAngle) - 500;
            System.out.println("left" + left + "top:" + top);
            FloatView floatView = new FloatView(this, poi, left, top);
            //floatView.floatview.setOnClickListener(new FloatOnclickListener());
            floatView.setOnCheckedListener(new CheckedListener());
            System.out.println("tttttt" + poi.getTypeCode() + poi.getTypeDes());
            angle.add(poiAngle);
            floatViewList.add(floatView);
            poiContent.addView(floatView);
            System.out.println("评分：" + poi.getPoiExtension().getmRating());

        }

    }

    /**
     * 计算控件的top属性
     *
     * @param angle1
     * @return
     */
    private int getTop(int angle1) {
        int top = 0;
        int count = 0;
        for (int i = floatViewList.size() - 1; i >= 0; i--) {
            if (Math.abs(angle1 - angle.get(i)) < 20) {
                count++;
                top = count * viewHeight;
            }
        }
        return top;
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        System.out.println(poiItem.getAdName() + "=====");
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        radarView.unregisterListenter();
        //sensorManager.unregisterListener(this, magneticSensor);
        mapView.onDestroy();
        //mLocationClient.stopLocation();//停止定位
        aMapLocationClient.onDestroy();//销毁定位客户端。
        //销毁定位客户端之后，若要重新开启定位请重新New一个AMapLocationClient对象。
    }

    public void searchPOI(View view) {
        String keyWord = editQuery.getText().toString();
        if (TextUtils.isEmpty(keyWord)) {
            Toast.makeText(ARNavigation.this, "不能为空", Toast.LENGTH_SHORT).show();
        } else {
            doSearchQuery(keyWord);
        }
    }

    /**
     * ARCode#######################################################################################################
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        z = event.values[2];
        y = event.values[1];
        x = event.values[0];
        double fixedX = x;
        if (y <= 0 && z >= 0) {                                  // 第一象限
            if (y == 0) {
                fixedX += 90;
            } else {
                float radio = Math.abs(z / y);
                fixedX += Math.atan(radio) * 180 / Math.PI;
            }
        } else if (y >= 0 && z >= 0) {                             // 第二象限
            if (y == 0) {
                fixedX += 90;
            } else {
                float radio = Math.abs(z / y);
                fixedX += 90 - Math.atan(radio) * 180 / Math.PI + 90;
            }
        } else if (y >= 0 && z <= 0) {                             // 第三现象
            if (y == 0) {
                fixedX += 90 + 180;
            } else {
                float radio = Math.abs(z / y);
                fixedX += Math.atan(radio) * 180 / Math.PI + 180;
            }
        } else {                                              // 第四现象
            float radio = Math.abs(z / y);
            fixedX += 90 - Math.atan(radio) * 180 / Math.PI + 270;
        }
        while (fixedX > 360) fixedX -= 360;
        while (fixedX < 0) fixedX += 360;
       /* double targetX = event.values[0] + Math.cos(y / 180 * Math.PI) * z + (y > 0 ? y / 0.5 : 0);
        if (targetX > 360) targetX -= 360;*/
        //x = (float) (event.values[0] + Math.cos(y / 180 * Math.PI) * z + (y > 0 ? y / 0.5 : 0));
        //if (x > 360) x -= 360;
        fixedZ = (180 - (z + 90)) + 270;
        double temY = -(Math.cos(z / 180 * Math.PI) * (-Math.abs(y)));
        double banlanceZ = Math.abs(z);
        double fixedY = temY / 90 * height - 0.5f * height;
        double fixed_Y_L = temY / 90 * width + width / 3;
        //System.out.println(fixedY + ":::::" + fixed_Y_L + "：：：：" + banlanceZ);
        fixedY = banlanceZ / 90 * fixed_Y_L + (90 - banlanceZ) / 90 * fixedY;
        //手机倒置的情况
        if (y > 0) {
            fixedZ = 90 + 90 - fixedZ;
        }
        //fixedY=y;
        //修正Z
        //System.out.println(z + "----");
        //改变所有悬浮view的位置
        if (floatViewList.size() > 0) {
            for (int i = 0; i < floatViewList.size(); i++) {
                //做修正拼接处理,解决X指向360和0度的交界处引起的显示异常，
                if (fixedX < angle.get(i)) {
                    tempx = (float) (fixedX + 360);
                } else {
                    tempx = (float) fixedX;
                }
                //float fixedX = (tempx / (360 - 22.5f)) * 1080 - 540;
                //floatViewList.get(i).setTranslationX((1080 - fixedX * 8) + 4320);
                fixedX1 = (width - ((tempx / (360 - 22.5f)) * width - width / 2) * 6) + width * 6;
                float zz = 90 - Math.abs(event.values[2]);
              /*  if (Math.abs(z)<80) {
                    floatViewList.get(i).setTranslationY(fixedY * zz / 80f);
                }*/
                floatViewList.get(i).setTranslationY((float) fixedY);
                //      System.out.println(fixedY);
                floatViewList.get(i).setRotation(fixedZ);
                poiContent.setRotation(360 - fixedZ);
               /* if (Math.abs((360 - fixedZ)) > 80 && Math.abs((360 - fixedZ)) < 90) {
                    return;
                }*/
                floatViewList.get(i).setTranslationX(fixedX1);
            }
        }
/*        xyz.setText("X:" + fixedX + "\n" +
                "Y:" + y + "\n" +
                "Z:" + (360 - fixedZ) + "\n" +
                "x:" + event.values[0]
        );*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //获取当前位置经纬度

        if (centerpoint == null) {
            double latitude = aMapLocation.getLatitude();
            double Longitude = aMapLocation.getLongitude();
            //centerpoint = new LatLng(latitude, Longitude);
            centerpoint = new LatLonPoint(latitude, Longitude);
            //第一次搜索初始化控件高度
            doSearchQuery("超市");

            //得到当前地址信息
            currentAddress.setText(aMapLocation.getStreet() + "区域");
            System.out.println("位置发生改变：" + aMapLocation.getAddress() + "街道：" +
                    aMapLocation.getStreet() + "地区：" + aMapLocation.getDistrict() + "城市编码" +
                    aMapLocation.getCityCode() + "地区编码：" +
                    aMapLocation.getAdCode());
        }
       /*     } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
                Toast.makeText(this, "定位失败", Toast.LENGTH_LONG).show();
            }
        }*/
    }

    /**
     * 返回控件高度
     *
     * @param view
     * @return
     */
    public int getHW(final View view) {

        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                viewHeight = view.getHeight();
            }
        });
        int height = viewHeight;
        return height;
    }

    private class SurfaceViewCallback implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            camera = Camera.open();
            //修正角度
            camera.setDisplayOrientation(90);
            try {
                //显示在SurfaceView上
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Camera.Parameters parameters = camera.getParameters();
        /* 每秒从摄像头捕获5帧画面， */
            parameters.setPreviewFrameRate(5);
        /* 照片质量 */
            parameters.set("jpeg-quality", 85);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            //camera.setParameters(parameters);
        /* 将参数对象赋予到 camera 对象上 */
            camera.startPreview();
            preview = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
            /* 若摄像头正在工作，先停止它 */
                if (preview) {
                    camera.stopPreview();
                    preview = false;
                }
                camera.setPreviewCallback(null);
                camera.release();
            }
        }
    }


    /**
     * 悬浮view的点击逻辑
     */
    class FloatOnclickListener implements View.OnClickListener {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            if (((FloatView) ((v.getParent()))).getChildAt(0) == v) {
                Toast.makeText(ARNavigation.this, "ssss", Toast.LENGTH_SHORT).show();

            }
            if (((FloatView) ((v.getParent()))).getSelectedState()) {
                for (FloatView floatView : floatViewList) {
                    floatView.setSelectedState(false);
                    floatView.setAlpha(0.8f);
                }
                return;
            }
            for (FloatView floatView : floatViewList) {
                if ((((v.getParent()))) == floatView) {
                    floatView.setSelectedState(true);
                } else {
                    floatView.setSelectedState(false);
                }
            }
        }
    }

    /**
     * 自定义单击事件
     */
    class onClickListenr implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.dl:
                    ToastUtil.show(ARNavigation.this, "clickdl");
                    break;
                case R.id.radarView:
                    ToastUtil.show(ARNavigation.this, "showlist");
                    dl.openDrawer(Gravity.RIGHT);
                    break;
                case R.id.rightContent:
                    ToastUtil.show(ARNavigation.this, "list");
                    break;
            }
        }
    }

    //自定义FloatView监听
    class CheckedListener implements FloatView.OnCheckedListener {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onCehcked(PoiItem poiItem, FloatView floatView) {
            Toast.makeText(ARNavigation.this, "执行回调" + poiItem.getTitle(), Toast.LENGTH_SHORT).show();
            if (poiItem.getPhotos().size() > 0) {
                Picasso.with(ARNavigation.this).load(poiItem.getPhotos().get(0).getUrl()).into(floatView.getPoiImg());
            }
            //如果floatview已经被选中，点击时释放所有floatview
            if (floatView.getSelectedState()) {
                for (FloatView f : floatViewList) {
                    f.setSelectedState(false);
                    f.setAlpha(0.8f);
                }
                return;
            }
            for (FloatView floatView1 : floatViewList) {
                if (floatView == floatView1) {
                    floatView1.setSelectedState(true);

                } else {
                    floatView1.setSelectedState(false);
                }
            }
        }
    }
}

