package com.hamilton.joel.wallpaper;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by joel on 12/10/15.
 */
public class UnlockPagerFragment extends Fragment {
    private final String TAG = "UnlockPagerFragment";

    int pageNumber;
    TextView titleView;
    TextView messageView;
    ImageView unlockImageView;

    public static UnlockPagerFragment newInstance(int pageNumber) {
        Bundle args = new Bundle();
        UnlockPagerFragment fragment = new UnlockPagerFragment();
        args.putInt("pageNum", pageNumber);
        fragment.setArguments(args);

        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt("pageNum");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.unlock_fragment_boss, container, false);

        titleView = (TextView) v.findViewById(R.id.unlock_fragment_title);
        messageView = (TextView) v.findViewById(R.id.unlock_fragment_message);
        unlockImageView = (ImageView) v.findViewById(R.id.unlock_fragment_image_view);

        switch (pageNumber) {
            case 0:
                titleView.setText(getResources().getString(R.string.title0));
                unlockImageView.setImageDrawable(getResources().getDrawable(R.drawable.photo_collage));
                messageView.setText(getResources().getString(R.string.message0));
                break;
            case 1:
                titleView.setText(getResources().getString(R.string.title1));
                unlockImageView.setImageBitmap(AnalyticsApplication.getScaledBitmapFromRes(R.drawable.snowglobe, 500));
                messageView.setText(getResources().getString(R.string.message1));
                break;
            case 2:
                titleView.setText(getResources().getString(R.string.title2));
                unlockImageView.setImageBitmap(AnalyticsApplication.getScaledBitmapFromRes(R.drawable.settings, 500));
                        messageView.setText(getResources().getString(R.string.message2));
                break;
        }
        return v;
    }
}
