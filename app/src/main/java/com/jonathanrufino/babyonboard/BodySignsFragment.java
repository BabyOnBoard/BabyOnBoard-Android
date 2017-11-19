package com.jonathanrufino.babyonboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BodySignsFragment  extends Fragment {

    @SuppressWarnings("unused")
    public static final String TAG  = BodySignsFragment.class.getSimpleName();

    private WebView streamingWV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bodysigns, container, false);

        streamingWV = view.findViewById(R.id.wv_streaming);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        streamingWV.setWebViewClient(new WebViewClient());
        streamingWV.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        streamingWV.loadUrl("http://0.0.0.0:8081");
    }
}
