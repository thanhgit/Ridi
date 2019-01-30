package saveteam.com.ridesharing.utils.activity;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class AnimationUtils {
    public static void slideToRight(View view){
        TranslateAnimation animate = new TranslateAnimation(0,view.getWidth(),0,0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        // view.setVisibility(View.GONE);
    }

    public static void slideToLeft(View view){
        // TranslateAnimation animate = new TranslateAnimation(0,-view.getWidth(),0,0);
        TranslateAnimation animate = new TranslateAnimation(0,0,0,0);
        animate.setDuration(1500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        // view.setVisibility(View.GONE);
    }

    public static void slideToBottom(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

    public static void slideToTop(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,-view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }

}
