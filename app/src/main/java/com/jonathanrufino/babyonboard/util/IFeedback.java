package com.jonathanrufino.babyonboard.util;

import android.support.v4.app.Fragment;

public interface IFeedback {

    void showToast(String message);

    void showProgress();

    void hideProgress();

    void displayFragment(Fragment fragment);

    void notifyIPChanged();
}
