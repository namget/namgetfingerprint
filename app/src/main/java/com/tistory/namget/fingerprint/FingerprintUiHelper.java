/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.tistory.namget.fingerprint;

import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Small helper class to manage text/icon around fingerprint authentication UI.
 */
public class FingerprintUiHelper extends FingerprintManagerCompat.AuthenticationCallback {

    private static final long ERROR_TIMEOUT_MILLIS = 1600;
    private static final long SUCCESS_DELAY_MILLIS = 1300;

    private ImageView mIcon;
    private TextView mErrorTextView;
    private Callback mCallback;
    private android.support.v4.os.CancellationSignal mCancellationSignal;

    private boolean mSelfCancelled;
    private Context context;



    /**
     * Constructor for {@link FingerprintUiHelper}.
     */
    FingerprintUiHelper(Context context, ImageView icon, TextView errorTextView, Callback callback) {
        this.context = context;
        this.mIcon = icon;
        this.mErrorTextView = errorTextView;
        this.mCallback = callback;
    }


    public void startListening(FingerprintManagerCompat.CryptoObject cryptoObject) {
        if (!Util.isFingerprintAuthAvailable(context)) {
            return;
        }
        mCancellationSignal = new android.support.v4.os.CancellationSignal();
        mSelfCancelled = false;
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        Util.getFingerprintManagerCompat(context)
                .authenticate(cryptoObject, 0 /* flags */, mCancellationSignal, this, null);
        mIcon.setImageResource(R.drawable.ic_fp_40px);
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mSelfCancelled = true;
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        if (!mSelfCancelled) {
            showError(errString);
            mIcon.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError();
                }
            }, ERROR_TIMEOUT_MILLIS);
        }
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        showError(helpString);
    }

    @Override
    public void onAuthenticationFailed() {
        showError(mIcon.getResources().getString(
                R.string.fingerprint_not_recognized));
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mIcon.setImageResource(R.drawable.ic_fingerprint_success);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.success_color));
        mErrorTextView.setText(
                mErrorTextView.getResources().getString(R.string.fingerprint_success));
        mIcon.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCallback.onAuthenticated();
            }
        }, SUCCESS_DELAY_MILLIS);
    }

    private void showError(CharSequence error) {
        mIcon.setImageResource(R.drawable.ic_fingerprint_error);
        mErrorTextView.setText(error);
        mErrorTextView.setTextColor(
                mErrorTextView.getResources().getColor(R.color.warning_color));
        mErrorTextView.removeCallbacks(mResetErrorTextRunnable);
        mErrorTextView.postDelayed(mResetErrorTextRunnable, ERROR_TIMEOUT_MILLIS);
    }

    private Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            mErrorTextView.setTextColor(
                    mErrorTextView.getResources().getColor(R.color.hint_color));
            mErrorTextView.setText(
                    mErrorTextView.getResources().getString(R.string.fingerprint_hint));
            mIcon.setImageResource(R.drawable.ic_fp_40px);
        }
    };

    public interface Callback {

        void onAuthenticated();

        void onError();
    }
}
