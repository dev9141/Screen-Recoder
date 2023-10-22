
package com.manddprojectconsultant.screencam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.manddprojectconsultant.screencam.R;

import pl.droidsonroids.gif.GifImageView;
public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater inflater;

    public SliderAdapter(Context mContext) {
        this.context = mContext;
    }

    int image[] = {
            R.drawable.dashboard,
            R.drawable.videorecorder,
            R.drawable.shake,





    };


    int description[] =
            {
                    R.string.HomePagedescription,
                    R.string.Videoscreenshortdescription,
                    R.string.screenshortdescriptioon,
            };

    @Override
    public int getCount() {
        return description.length;
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

        ImageView imageView = view.findViewById(R.id.slider_image);
        TextView tvdescription = view.findViewById(R.id.slider_desc);


        imageView.setImageResource(image[position]);
        tvdescription.setHint(description[position]);
        //Toast.makeText(context, "Position is: "+image[position], Toast.LENGTH_SHORT).show();
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
