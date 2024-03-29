package com.mrfu.http.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class RoundImageView extends ImageViewEx {
	public RoundImageView(Context context) {  
        super(context);  
        // TODO Auto-generated constructor stub  
    }  
  
    public RoundImageView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
    }  
  
    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
    }  
  
    @Override  
    protected void onDraw(Canvas canvas) {  

    	Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		Paint paint = new Paint(1);
		paint.setColor(Color.BLACK);
    	c.drawRoundRect(
                new RectF(0.0F, 0.0F, getWidth(), getHeight()),
                getWidth()/2, getHeight()/2, paint);
    	
    	Paint paint0 = new Paint();
    	new PorterDuffXfermode(
    			PorterDuff.Mode.DST_IN);
    	paint0.setFilterBitmap(false);
    			paint0.setXfermode(new PorterDuffXfermode(
				PorterDuff.Mode.DST_IN));
    	try{
			Drawable drawable = getDrawable();
			if (drawable != null) {
				int saveCount = canvas.saveLayer(0.0F, 0.0F, getWidth(),
						getHeight(), null, 31);
				drawable.setBounds(0, 0, getWidth(), getHeight());
				drawable.draw(canvas);
				canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint0);
				canvas.restoreToCount(saveCount);
			}
		}catch(Exception e){
			e.printStackTrace();
		}catch(Throwable t){
			t.printStackTrace();
		}
    }  
}
