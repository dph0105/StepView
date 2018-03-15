package com.dpal.stepview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dpal
 * @date 2018/2/23
 */

public class VerticalStepView extends View {

    public static final int TEXT_LOCATION_RIGHT = 0;//文字在右
    public static final int TEXT_LOCATION_LEFT = 1;//文字在左

    public static final int STEP_HEIGHT_MODE_AVERAGE = 0;
    public static final int STEP_HEIGHT_MODE_FIXED = 1;

    /**
     * 步骤的数量
     */
    private int count;//步骤点的个数
    /**
     * 当前进度
     */
    private int currentVal = 1;
    /**
     * 普通的步骤点的图标
     */
    private Drawable normalDrawable;

    /**
     * 正在进行中的步骤点的图标
     */
    private Drawable ongoingDrawable;

    /**
     * 已经完成的步骤点的图标
     */
    private Drawable completedDrawable;
    /**
     * 步骤条的线条宽度
     */
    private int lineWidth;

    /**
     *普通的步骤点的图标的宽高
     */
    private int normalPointWidth = 0;

    /**
     * 正在进行中的步骤点的图标的宽高
     */
    private int ongoingPointWidth = 0;

    /**
     * 已经完成的步骤点的图标的宽高
     */
    private int completedPointWidth = 0;

    /**
     * 普通的默认的线条的颜色
     */
    private int lineNormalColor;
    /**
     * 已经完成的步骤的线条的颜色
     */
    private int lineCompletedColor;
    /**
     * 说明文字的字体大小
     */
    private int descTextSize;//文字字体大小
    /**
     * 普通的默认的文字的颜色
     */
    private int descNormalTextColor;
    /**
     * 正在进行中的步骤的文字颜色
     */
    private int descOngoingTextColor;
    /**
     * 已经完成的步骤的文字颜色
     */
    private int descCompletedTextColor;
    /**
     * 说明文字与步骤条之间的距离
     */
    private int distanceFromText;
    /**
     * 如果是垂直方向时，0表示在右边，1表示在左边
     */
    private int textLocation;
    /**
     * 每个步骤的说明
     */
    private List<String> descriptions = new ArrayList<>();

    private List<StaticLayout> staticLayouts = new ArrayList<>();

    /**
     * 每个步骤项的高度；
     */
    private int stepHeight = 100;

    /**
     * 步骤项与步骤项之间的间隔，默认为0
     */
    private int stepInterval = 10;
    /**
     * 线条的Paint
     */
    private Paint linePaint = new Paint();

    /**
     * 步骤条的宽度
     */
    private int barWidth;

    /**
     * view的宽度
     */
    private int widthSize;
    /**
     * 说明文字的宽度
     */
    public int textWidth;
    /**
     * 步骤项宽高的计算方式
     * STEP_HEIGHT_MODE_AVERAGE:View的宽度均分
     * STEP_HEIGHT_MODE_FIXED:自己设置的宽度，这种模式下，必须要设置步骤项的宽度，否则报错
     */
    public int stepHeightMode;

    public VerticalStepView(Context context) {
        this(context,null);
    }

    public VerticalStepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VerticalStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        linePaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerticalStepView, defStyleAttr, 0);
        count = a.getInt(R.styleable.HorizontalStepView_count, 1);
        if (count<2){
            //点数必须大于等于2，否则抛出异常
            throw new IllegalArgumentException("Step count cant be less than 2!");
        }
        if (currentVal+1>count){
            throw new IllegalArgumentException("CurrentVal must be less than count!");
        }

        normalDrawable = a.getDrawable(R.styleable.VerticalStepView_normal_point);
        ongoingDrawable = a.getDrawable(R.styleable.VerticalStepView_ongoing_point);
        completedDrawable = a.getDrawable(R.styleable.VerticalStepView_completed_point);
        lineWidth = a.getDimensionPixelSize(R.styleable.VerticalStepView_line_width, 16);
        lineNormalColor = a.getColor(R.styleable.VerticalStepView_line_normal_color, Color.parseColor("#ff5566"));
        lineCompletedColor = a.getColor(R.styleable.VerticalStepView_line_completed_color, Color.parseColor("#ff0000"));
        descTextSize = a.getDimensionPixelSize(R.styleable.VerticalStepView_desc_textSize, 18);
        descNormalTextColor = a.getColor(R.styleable.VerticalStepView_desc_normal_textColor, Color.BLACK);
        descOngoingTextColor = a.getColor(R.styleable.VerticalStepView_desc_ongoing_textColor, Color.BLACK);
        descCompletedTextColor = a.getColor(R.styleable.VerticalStepView_desc_completed_textColor, Color.BLACK);
        distanceFromText = a.getDimensionPixelSize(R.styleable.VerticalStepView_distance_from_text, 10);
        textLocation = a.getInt(R.styleable.VerticalStepView_vertical_text_location,TEXT_LOCATION_RIGHT);
        ongoingPointWidth = a.getDimensionPixelSize(R.styleable.VerticalStepView_ongoing_point_width,ongoingPointWidth);
        completedPointWidth = a.getDimensionPixelSize(R.styleable.VerticalStepView_completed_point_width,completedPointWidth);
        normalPointWidth = a.getDimensionPixelSize(R.styleable.VerticalStepView_normal_point_width,normalPointWidth);
        stepHeight = a.getDimensionPixelSize(R.styleable.VerticalStepView_step_height,stepHeight);

        stepHeightMode = a.getInt(R.styleable.VerticalStepView_stepHeightMode, STEP_HEIGHT_MODE_AVERAGE);
        a.recycle();

        linePaint.setStrokeWidth(lineWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthSize = getWidthSize(widthMeasureSpec);
        int heightSize = getHeightSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画连线
        //画已完成的线
        int startX = getPaddingStart()+barWidth/2;
        int stopX = getPaddingStart()+barWidth/2;
        if (textLocation==TEXT_LOCATION_LEFT){
            startX += textWidth+distanceFromText;
            stopX += textWidth + distanceFromText;
        }

        int startY = getPaddingTop()+stepHeight/2;
        int stopY = startY+stepHeight*currentVal+stepInterval*currentVal;

        linePaint.setColor(lineCompletedColor);
        canvas.drawLine(startX,startY,stopX,stopY,linePaint);
        //画未完成的线
        linePaint.setColor(lineNormalColor);
        canvas.drawLine(stopX,stopY,stopX,stepHeight*count+(count-1)*stepInterval-stepHeight/2,linePaint);

        //画步骤点
        int top = getPaddingTop();
        for (int i=0;i<count;i++){
            if (i==currentVal){
                drawStepPoint(canvas,ongoingDrawable,top,ongoingPointWidth);
//                drawStepPointBitmap(canvas,ongoingBitmap,top,ongoingPointWidth);
            }else if (i<currentVal){
                drawStepPoint(canvas,completedDrawable,top,ongoingPointWidth);
//                drawStepPointBitmap(canvas,completedBitmap,top,completedPointWidth);
            }else if (i>currentVal){
                drawStepPoint(canvas,normalDrawable,top,ongoingPointWidth);
//                drawStepPointBitmap(canvas,normalBitmap,top,normalPointWidth);
            }
            top = top + stepHeight + stepInterval;
        }

        //画文字
        canvas.save();
        canvas.translate(textLocation==TEXT_LOCATION_RIGHT?barWidth+distanceFromText+getPaddingStart():getPaddingStart(),getPaddingTop()+stepHeight/2);

        for(int i=0;i<staticLayouts.size();i++){
            int dy = 0;
            if (i==currentVal){
                dy = ongoingPointWidth/2;
            }else if (i<currentVal){
                dy = completedPointWidth/2;
            }else if (i>currentVal){
                dy = normalPointWidth/2;
            }
            canvas.translate(0,-dy);
            staticLayouts.get(i).draw(canvas);
            canvas.translate(0,dy);
            canvas.translate(0,stepHeight+stepInterval);
        }
        canvas.restore();


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    private int getWidthSize(int measureSpec){
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int trueSize = 0;
        barWidth = getBarWidth();
        textWidth = setMultiTextStaticLayouts(size-getPaddingStart()-getPaddingEnd()-barWidth-distanceFromText);
        switch (mode){
            case MeasureSpec.UNSPECIFIED://测量模式是不作限制，一般是在ScrollView中
            case MeasureSpec.AT_MOST://如果测量模式是当前尺寸能取的最大值，当wrap-content时
//              //这时候size是父View的size，因为是wrap-content，我们应该测量自己的大小
//              //获取View的宽度
                trueSize = getPaddingStart() + getPaddingEnd() + barWidth + distanceFromText + textWidth;
                break;
            case MeasureSpec.EXACTLY://如果测量模式是精确值，也就是固定的大小
                trueSize = size;//这时候size是我们xml设置的值
                break;
        }
        return trueSize;
    }


    private int getHeightSize(int measureSpec){
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int trueSize = 0;
        switch (mode){
            case MeasureSpec.UNSPECIFIED://测量模式是不作限制，我们这里不做处理
            case MeasureSpec.AT_MOST://如果测量模式是当前尺寸能取的最大值，当wrap-content时
                //这时候size是父View的size，因为是wrap-content，我们应该测量自己的大小
                //获取View的高度
                trueSize = count*stepHeight+(count-1)*stepInterval+getPaddingTop()+getPaddingBottom();
                break;
            case MeasureSpec.EXACTLY://如果测量模式是精确值，也就是固定的大小
                if (stepHeightMode==STEP_HEIGHT_MODE_AVERAGE){
                    stepHeight = (size-stepInterval*(count-1))/count;//这时候每个步骤项的宽度，为总宽度除以步骤的数量
                }
                trueSize = size;//这时候size是我们xml设置的值

                break;
        }
        return trueSize;
    }


    private void drawStepPoint(Canvas canvas,Drawable drawable,int top,int setWidth){
        int destLeft = textLocation==TEXT_LOCATION_LEFT?getPaddingStart()+textWidth+distanceFromText:getPaddingStart();
        int destTop;
        int destRight;
        int destBottom;

        if (setWidth==0){
            //如果没有设置图片的宽高
            if (drawable.getIntrinsicHeight()>=stepHeight){
                destTop = top;
                destBottom = destTop+ stepHeight;
            }else {
                destTop = top +stepHeight/2 - drawable.getIntrinsicHeight()/2;
                destBottom = destTop+drawable.getIntrinsicHeight();
            }
            if (drawable.getIntrinsicWidth()>stepHeight){
                destRight = destLeft+stepHeight;
            }else {
                destLeft += barWidth/2 - drawable.getIntrinsicWidth()/2;
                destRight = destLeft+drawable.getIntrinsicHeight();
            }
        }else {
            if (setWidth>=stepHeight){
                destTop = top;
                destRight = destLeft+stepHeight;
                destBottom = destTop+stepHeight;
            }else {
                destLeft = barWidth/2-setWidth/2;
                destRight = destLeft+setWidth;
                destTop = top+stepHeight/2-setWidth/2;
                destBottom = destTop + setWidth;
            }
        }

        drawable.setBounds(destLeft,destTop,destRight,destBottom);
        drawable.draw(canvas);
    }



    //获得步骤条的宽度
    private int getBarWidth(){
        //在这里获得各个图片的宽度
        if (normalDrawable!=null){
            normalPointWidth = normalPointWidth==0?normalDrawable.getIntrinsicWidth():normalPointWidth;
        }
        if (ongoingDrawable!=null){
            ongoingPointWidth = ongoingPointWidth==0?ongoingDrawable.getIntrinsicWidth():ongoingPointWidth;
        }
        if (completedDrawable!=null){
            completedPointWidth = completedPointWidth==0?completedDrawable.getIntrinsicWidth():completedPointWidth;
        }
        int max = getMax(normalPointWidth, ongoingPointWidth, completedPointWidth, lineWidth);
        return max>stepHeight?stepHeight:max;
    }


    //得到多个int值中的最大值
    private int getMax(int ...sizes){
        int temp = 0;
        for (int size : sizes){
            if (size>temp){
                temp = size;
            }
        }
        return temp;
    }

    //设置多个Text的StaticLayout
    private int setMultiTextStaticLayouts(int size){
        int temp = 0;
        staticLayouts.clear();
        for (int i=0;i<descriptions.size();i++){
            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(descTextSize);
            if (i==currentVal){
                textPaint.setColor(descOngoingTextColor);
            }else if (i>currentVal){
                textPaint.setColor(descNormalTextColor);
            }else if (i<currentVal){
                textPaint.setColor(descCompletedTextColor);
            }
            int width = (int) Math.ceil(StaticLayout.getDesiredWidth(descriptions.get(i), textPaint));
            if (width>size){
                width = size;
            }
            if (width>temp){
                temp = width;
            }
            StaticLayout staticLayout = new StaticLayout(descriptions.get(i),textPaint, width, Layout.Alignment.ALIGN_NORMAL,1,0,true);
            staticLayouts.add(staticLayout);
        }
        return temp;
    }



    //设置说明文字
    public void setDescriptions(List<String> descs){
        descriptions.clear();
        descriptions.addAll(descs);
        invalidate();
        requestLayout();
    }

    //设置说明文字
    public void setDescriptions(String[] descs){
        descriptions.clear();
        setDescriptions(Arrays.asList(descs));
    }
    //设置普通步骤点的图标的宽高
    public void setNormalPointWidth(int dp){
        normalPointWidth = dp2px(getContext(),dp);
        requestLayout();
        invalidate();
    }

    //设置正在进行步骤点的图标的宽高
    public void setOngoingPointWidth(int dp){
        ongoingPointWidth = dp2px(getContext(),dp);
        requestLayout();
        invalidate();
    }

    //设置已经完成的步骤点的图标的宽高
    public void setCompletedPointWidth(int dp){
        completedPointWidth = dp2px(getContext(),dp);
        requestLayout();
        invalidate();
    }

    private int dp2px(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * value + 0.5f);
    }

    private int sp2px(Context context, int value) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scaledDensity + 0.5f);
    }
}
