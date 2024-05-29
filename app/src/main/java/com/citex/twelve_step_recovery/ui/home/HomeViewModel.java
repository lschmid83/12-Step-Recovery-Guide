package com.citex.twelve_step_recovery.ui.home;

import android.text.SpannableString;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mSobrietyDate;
    private final MutableLiveData<String> mTimeSoberLabel;
    private final MutableLiveData<SpannableString> mTimeSober;
    private final MutableLiveData<String> mStep;
    private final MutableLiveData<String> mStepDescription;

    public HomeViewModel() {
        mSobrietyDate = new MutableLiveData<>();
        mTimeSoberLabel = new MutableLiveData<>();
        mTimeSober = new MutableLiveData<>();
        mStep = new MutableLiveData<>();
        mStepDescription = new MutableLiveData<>();
    }

    public LiveData<String> getSobrietyDate() {
        return mSobrietyDate;
    }

    public void setSobrietyDate(String sobrietyDate) {
        mSobrietyDate.setValue(sobrietyDate);
    }

    public LiveData<String> getTimeSoberLabel() {
        return mTimeSoberLabel;
    }

    public void setTimeSoberLabel(String timeSoberLabel) {
        mTimeSoberLabel.setValue(timeSoberLabel);
    }

    public LiveData<SpannableString> getTimeSober() {
        return mTimeSober;
    }

    public void setTimeSober(SpannableString timeSober) {
        mTimeSober.setValue(timeSober);
    }

    public LiveData<String> getStep() {
        return mStep;
    }

    public void setStep(String step) { mStep.setValue(step); }

    public LiveData<String> getStepDescription() {
        return mStepDescription;
    }

    public void setStepDescription(String stepDescription) { mStepDescription.setValue(stepDescription); }
}