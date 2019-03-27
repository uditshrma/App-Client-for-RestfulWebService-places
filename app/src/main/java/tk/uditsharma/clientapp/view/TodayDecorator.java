package tk.uditsharma.clientapp.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import tk.uditsharma.clientapp.R;

public class TodayDecorator implements DayViewDecorator {

    private Drawable highlightDrawable;

    public TodayDecorator( Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            highlightDrawable = ContextCompat.getDrawable(context, R.drawable.circlebackground);
        } else {
            highlightDrawable = context.getResources().getDrawable(R.drawable.circlebackground);
        }
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(CalendarDay.today());
    }

    @Override
    public void decorate(DayViewFacade view) {

        view.setBackgroundDrawable(highlightDrawable);
        view.addSpan(new ForegroundColorSpan(Color.GRAY));
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.1f));

    }
}
