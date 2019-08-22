package com.anik.example.tourmate.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.anik.example.tourmate.ModelClass.Moment;
import com.anik.example.tourmate.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImagePagerAdapter extends PagerAdapter {

    private ArrayList<Moment> momentArrayList;
    private Context context;

    public ImagePagerAdapter(ArrayList<Moment> momentArrayList, Context context) {
        this.momentArrayList = momentArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return momentArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Moment moment = momentArrayList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.single_image_fullscreen_layout,null,false);
        ImageView imageView = view.findViewById(R.id.fullScreenImageVIew);

        Picasso.with(context).load(moment.getImageURL()).fit().into(imageView);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view,0);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);

        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
