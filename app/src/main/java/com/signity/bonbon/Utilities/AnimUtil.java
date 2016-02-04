package com.signity.bonbon.Utilities;

import android.app.Activity;


public class AnimUtil {

    public static void slideFromLeftAnim(Activity context) {
        context.overridePendingTransition(com.signity.bonbon.R.anim.left_to_center_slide,
                com.signity.bonbon.R.anim.center_to_right_slide);
    }

    public static void slideFromRightAnim(Activity context) {
        context.overridePendingTransition(com.signity.bonbon.R.anim.right_to_center_slide,
                com.signity.bonbon.R.anim.center_to_left_slide);
    }

    public static void slideUpAnim(Activity context) {
        context.overridePendingTransition(com.signity.bonbon.R.anim.slide_up_activity,
                com.signity.bonbon.R.anim.no_change);
    }

    public static void slidDownAnim(Activity context) {
        context.overridePendingTransition(com.signity.bonbon.R.anim.no_change,
                com.signity.bonbon.R.anim.slide_down_activity);
    }

}
