package com.zdhx.androidbase.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Android on 2017/2/27.
 */

public class FaceTextView extends TextView {
    private CharSequence text;
    private Context context;

    public FaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public FaceTextView(Context context) {
        super(context);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public FaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public CharSequence getText() {
        return text == null ? "" : text;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        this.text = text;
        String cs = text.toString();
        if (cs.contains("[em_")) {
            for (int i = 1; i < 76; i++) {
               cs = cs.replaceAll("\\["+"em_"+i+"\\]","\\<img src='" + i +"'\\/\\>");
                if (!cs.contains("[em_")) {
                    break;
                }
            }
        }
        Log.e("cs",cs);
        super.setText(cs,type);
    }

    public Drawable loadImageFromAsserts(String fileName) {
        try {
            InputStream is = getContext().getResources().getAssets().open("drawable/qqface/" + fileName + ".gif");
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

