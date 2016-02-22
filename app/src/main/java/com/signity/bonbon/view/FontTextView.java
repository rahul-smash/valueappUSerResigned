package com.signity.bonbon.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.signity.bonbon.Utilities.FontUtil;

public class FontTextView extends TextView {


    public FontTextView(Context context) {
        super(context);
        if (isInEditMode()) {
            return;
        }
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        parseAttributes(context, attrs);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        setTypeface(FontUtil.getTypeface(context, FontUtil.ICON_SET));
    }

}
