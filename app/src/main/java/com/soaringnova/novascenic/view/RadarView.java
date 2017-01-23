package com.soaringnova.novascenic.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Neb on 2016/11/22.
 */
public class RadarView extends View implements SensorEventListener {

    private Context mContext;
    private Paint mPaint;
    private int mPointCount = 0;// 圆点总数
    private List<String> mPointArray = new ArrayList<String>();// 存放Point
    private int mWidth, mHeight;// 宽高
    int mOutWidth;// 外圆宽度(w/4/5*2=w/10)
    int mCx, mCy;// x、y轴中心点
    int mOutsideRadius, mInsideRadius;// 外、内圆半径
    BitmapFactory.Options options = new BitmapFactory.Options();
    Canvas canvas1;
    private SensorManager sensorManager;
    private double angle;
    private int maxDistance = 1000;

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RadarView(Context context) {
        super(context);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        mPaint = new Paint();
        this.mContext = context;
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor_orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        registerListener(sensor_orientation);
    }


    /**
     * 测量视图及其内容,以确定所测量的宽度和高度(测量获取控件尺寸).
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取控件区域宽高
        if (mWidth == 0 || mHeight == 0) {
            final int minimumWidth = getSuggestedMinimumWidth();
            final int minimumHeight = getSuggestedMinimumHeight();
            mWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
            mHeight = resolveMeasured(heightMeasureSpec, minimumHeight);
            mCx = mWidth / 2;
            mCy = mHeight / 2;
            // 获取外圆宽度
            mOutWidth = mWidth / 10;
            // 计算内、外半径
            mOutsideRadius = mWidth / 2;// 外圆的半径
            mInsideRadius = (mWidth - mOutWidth) / 4 / 2;// 内圆的半径
        }
    }

    /**
     * 绘制视图--从外部向内部绘制
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas1 == null) {
            canvas1 = canvas;
        }
        //坐标点数为0时绘制一次即可，减少资源占用
        //绘制圆线
        drawCircle(canvas);
        canvas.save();
        canvas.restore();
        // 绘制点
        mPaint.setAlpha(255);
        drwaPoint(canvas);

    }

    /**
     * 绘制扇形区域
     *
     * @param canvas
     */
    private void drawFan(Canvas canvas) {
       /* RectF oval2 = new RectF(60, 100, 200, 240);// 设置个新的长方形，扫描测量
        canvas.drawArc(oval2, 270 - 22.5f, 45, true, mPaint);*/
    }


    /**
     * 绘制圆形
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {

        //绘制背景圆形
        mPaint.setAntiAlias(true);// 设置抗锯齿
        mPaint.setStyle(Style.FILL);// 设置填充样式
        mPaint.setColor(0x000000);
        mPaint.setAlpha(150); //设置不透明度  0~255
        mPaint.setDither(true);//平滑绘制
        // 绘制外围圆线
        canvas.drawCircle(mCx, mCy, mInsideRadius * 4 + 2, mPaint);
        // 绘制内部的三条圆线
        mPaint.setStrokeWidth(3); //更改画笔粗细
        mPaint.setColor(0xffffff);
        mPaint.setStyle(Style.STROKE);
        mPaint.setAlpha(150);
        canvas.drawCircle(mCx, mCy, mInsideRadius * 4, mPaint);
        canvas.drawCircle(mCx, mCy, mInsideRadius * 2.66f, mPaint);
        canvas.drawCircle(mCx, mCy, mInsideRadius * 1.33f, mPaint);
    }

    /**
     * 绘制点
     */
    private void drwaPoint(Canvas canvas) {
        if (mPointCount > 0) {// 当圆点总数>0时,进入下一层判断
            //绘制坐标点
            for (int i = 0; i < mPointArray.size(); i++) {
                String[] result = mPointArray.get(i).split("/");
                canvas.drawCircle(Integer.parseInt(result[0]), Integer.parseInt(result[1]), 2, mPaint);
                this.invalidate();
            }
        }
    }

    /**
     * 根据坐标绘制点
     *
     * @param poi  poi坐标
     * @param self 自身坐标
     */
    public void addPoint(LatLonPoint poi, LatLonPoint self) {
        //需要做转换
        //mPointArray.add(x + "/" + y);
        mPointCount++;
    }

    /**
     * 获取宽高
     *
     * @return int
     */
    private int resolveMeasured(int measureSpec, int desired) {
        int result = 0;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.min(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = specSize;
        }
        return result;
    }


    /**
     * 监听方向传感器的X值，按中心点逆旋转RadarView
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        this.setRotation(360 - event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 提供注册监听方法
     */
    private void registerListener(Sensor sensor_orientation) {
        sensorManager.registerListener(this, sensor_orientation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /**
     * 暴露移除监听的方法，在Activity的销毁或失去焦点时调用
     */
    public void unregisterListenter() {
        sensorManager.unregisterListener(this);
    }

    /**
     * 根据POI绘制点
     *
     * @param poi        poi
     * @param myLocation 自身坐标
     */
    public void addPoint(PoiItem poi, LatLonPoint myLocation) {
        //得到两点坐标
        double x1 = (poi.getLatLonPoint().getLongitude());
        double x2 = (poi.getLatLonPoint().getLatitude());
        double y1 = (myLocation.getLongitude());
        double y2 = (myLocation.getLatitude());
        System.out.println("  poi点：" + x1 + "==" + x2 + "==\n 中心点：" + y1 + "==" + y2 + "");
        MyLatLng A = new MyLatLng(x1, x2);
        MyLatLng B = new MyLatLng(y1, y2);
        System.out.println("角度：==" + RadarView.getAngle(A, B));
        int distens = poi.getDistance();        //得到POI与当前位置距离
        angle = RadarView.getAngle(A, B);        //得到角度
        //angle1 = angle%90;
        int z = (int) (distens * Math.sin(Math.toRadians(angle + 90)));        //纵向距离
        int h = (int) (distens * Math.cos(Math.toRadians(angle + 90)));        //横向距离
        System.out.println("纵向距离：=" + z + "横向距离：=" + h + "--------------");
        //得到纵横距离后根据比例绘制到雷达上
        int x = (int) (mCx + (h * (1f / (float) maxDistance) * 0.85f) * mCx);
        int y = (int) (mCy + (z * (1f / (float) maxDistance) * 0.85f) * mCy);
        System.out.println(mCx + ":::" + mCy + x + "=x" + y + "=y" + poi.getTitle() + (h * 0.001) * mCy);
        mPointArray.add(x + "/" + y);
        mPointCount++;
        //刷新视图
        invalidate();

    }

    /**
     * 求B点经纬度
     *
     * @param A        已知点的经纬度，
     * @param distance AB两地的距离  单位km
     * @param angle    AB连线与正北方向的夹角（0~360）
     * @return B点的经纬度
     */
    public static MyLatLng getMyLatLng(MyLatLng A, double distance, double angle) {

        double dx = distance * 1000 * Math.sin(Math.toRadians(angle));
        double dy = distance * 1000 * Math.cos(Math.toRadians(angle));

        double bjd = (dx / A.Ed + A.m_RadLo) * 180. / Math.PI;
        double bwd = (dy / A.Ec + A.m_RadLa) * 180. / Math.PI;
        return new MyLatLng(bjd, bwd);
    }

    /**
     * 获取AB连线与正北方向的角度
     *
     * @param A A点的经纬度
     * @param B B点的经纬度
     * @return AB连线与正北方向的角度（0~360）
     */
    public static double getAngle(MyLatLng A, MyLatLng B) {
        double dx = (B.m_RadLo - A.m_RadLo) * A.Ed;
        double dy = (B.m_RadLa - A.m_RadLa) * A.Ec;
        double angle = 0.0;
        angle = Math.atan(Math.abs(dx / dy)) * 180. / Math.PI;
        double dLo = B.m_Longitude - A.m_Longitude;
        double dLa = B.m_Latitude - A.m_Latitude;
        if (dLo > 0 && dLa <= 0) {
            angle = (90. - angle) + 90;
        } else if (dLo <= 0 && dLa < 0) {
            angle = angle + 180.;
        } else if (dLo < 0 && dLa >= 0) {
            angle = (90. - angle) + 270;
        }
        return angle;
    }

    /**
     * 通过传入最远距离动态的控制显示比例
     *
     * @param distance
     */
    public void setMaxDistance(int distance) {
        maxDistance = distance;
    }

    public static class MyLatLng {
        double Rc = 6378137;
        double Rj = 6356725;
        double m_LoDeg, m_LoMin, m_LoSec;
        double m_LaDeg, m_LaMin, m_LaSec;
        double m_Longitude, m_Latitude;
        double m_RadLo, m_RadLa;
        double Ec;
        double Ed;

        public MyLatLng(double longitude, double latitude) {
            m_LoDeg = (int) longitude;
            m_LoMin = (int) ((longitude - m_LoDeg) * 60);
            m_LoSec = (longitude - m_LoDeg - m_LoMin / 60.) * 3600;
            m_LaDeg = (int) latitude;
            m_LaMin = (int) ((latitude - m_LaDeg) * 60);
            m_LaSec = (latitude - m_LaDeg - m_LaMin / 60.) * 3600;
            m_Longitude = longitude;
            m_Latitude = latitude;
            m_RadLo = longitude * Math.PI / 180.;
            m_RadLa = latitude * Math.PI / 180.;
            Ec = Rj + (Rc - Rj) * (90. - m_Latitude) / 90.;
            Ed = Ec * Math.cos(m_RadLa);
        }
    }

    /**
     * 清空poi点
     */
    public void clearPOI() {
        mPointArray.clear();
        mPointCount = 0;
    }
}