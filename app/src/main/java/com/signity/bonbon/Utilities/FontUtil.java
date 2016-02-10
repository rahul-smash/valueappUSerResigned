package com.signity.bonbon.Utilities;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

public class FontUtil {

    public static final String FONT_ROBOTO_REGULAR = "fonts/Roboto-Regular.ttf";
    public static final String FONT_ROBOTO_BOLD = "fonts/Roboto-Bold.ttf";
    public static final String ICON_SET = "fonts/icon_set.ttf";


    private static final Map<String, Typeface> typefaces = new HashMap<String, Typeface>();

    public static Typeface getTypeface(Context ctx, String fontName) {
        Typeface typeface = typefaces.get(fontName);
        if (typeface == null) {
            typeface = Typeface.createFromAsset(ctx.getAssets(), fontName);
            typefaces.put(fontName, typeface);
        }
        return typeface;
    }
}
