package com.liaowei.music.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class CircleView extends ImageView {

    private final Paint mPaint;
    private final Matrix mMatrix;
    private final Context mContext;

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true); // 设置画笔抗锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mMatrix = new Matrix();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 禁用硬件加速
        mContext = context;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        float radius = Math.min(width, height) / 2f; // 50

        // 确保图片位于中心
        float left = (width - radius * 2) / 2f; // -50
        float top = (height - radius * 2) / 2f; // 0
        float right = left + radius * 2; // -50
        float bottom = top + radius * 2; // 0

        // 使用Oval来定义圆形区域
        @SuppressLint("DrawAllocation") RectF oval = new RectF(left, top, right, bottom);

        // 清除画布
        canvas.drawColor(0x00000000);

        // 绘制圆形
        mPaint.setShader(createBitmapShader());
        canvas.drawOval(oval, mPaint);
    }

    private BitmapShader createBitmapShader(){
        Bitmap bitmap = drawableToBitmap(getDrawable());
        return new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    private Bitmap drawableToBitmap(Drawable drawable){
        if (drawable == null) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
