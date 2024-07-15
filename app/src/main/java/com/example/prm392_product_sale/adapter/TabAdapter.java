package com.example.prm392_product_sale.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.prm392_product_sale.ui.fragment.DescriptionFragment;
import com.example.prm392_product_sale.ui.fragment.ReviewFragment;
import com.example.prm392_product_sale.ui.fragment.SoldFragment;


public class TabAdapter extends FragmentPagerAdapter {

    private DescriptionFragment descriptionFragment;
    private ReviewFragment reviewFragment;

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        descriptionFragment = new DescriptionFragment();
        reviewFragment = new ReviewFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return descriptionFragment;
            case 1:
                return reviewFragment;
            case 2:
                return new SoldFragment();
            default:
                return descriptionFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public DescriptionFragment getDescriptionFragment() {
        return descriptionFragment;
    }

    public ReviewFragment getReviewFragment() {
        return reviewFragment;
    }
}