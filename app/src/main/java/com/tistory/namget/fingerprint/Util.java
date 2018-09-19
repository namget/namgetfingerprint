package com.tistory.namget.fingerprint;

import android.content.Context;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

/**
 * Created by JaeWooLee on 2017-11-10.
 */

public class Util {

    public static boolean isFingerprintAuthAvailable(Context context) {
        FingerprintManagerCompat mFingerprintManager;
        mFingerprintManager = FingerprintManagerCompat.from(context);
        if (mFingerprintManager.isHardwareDetected() && mFingerprintManager.hasEnrolledFingerprints()) {
            return true;
        } else {
            return false;
        }
    }
    public static FingerprintManagerCompat getFingerprintManagerCompat(Context context){
        FingerprintManagerCompat mFingerprintManager;
        mFingerprintManager = FingerprintManagerCompat.from(context);
        return mFingerprintManager;
    }

}
