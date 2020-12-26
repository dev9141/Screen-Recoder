package com.manddprojectconsultant.screenrecorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;
public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SliderAdapter(Context mContext) {
        this.context = mContext;
    }

    int gif[] = {
            R.drawable.settingsvideo,
            R.drawable.videorecoder,
            R.drawable.screenshortforrecosing,
    };
    int tittle[] = {
            R.string.HomePage,
            R.string.Videoscreenshort,
            R.string.screenshort,
    };
    int description[] =
            {
                    R.string.HomePagedescription,
                    R.string.Videoscreenshortdescription,
                    R.string.screenshortdescriptioon,
            };

    @Override
    public int getCount() {
        return tittle.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.welcome_page_1, container, false);
        // find view for init

        GifImageView gifImageView = view.findViewById(R.id.slider_image);
        TextView tvtitle = view.findViewById(R.id.slider_heading);
        TextView tvdescription = view.findViewById(R.id.slider_desc);


        gifImageView.setImageResource(gif[position]);
        tvtitle.setText(tittle[position]);
        tvdescription.setHint(description[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
