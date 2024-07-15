package com.example.prm392_product_sale.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm392_product_sale.R;
import com.example.prm392_product_sale.adapter.ReviewAdapter;
import com.example.prm392_product_sale.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends Fragment {
    private RecyclerView recyclerReview;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviews = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.review_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerReview = view.findViewById(R.id.recyclerReview);
        reviewAdapter = new ReviewAdapter(reviews);
        recyclerReview.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerReview.setAdapter(reviewAdapter);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        if (reviewAdapter != null) {
            reviewAdapter.setReviews(reviews);
        }
    }
}