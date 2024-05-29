package com.citex.twelve_step_recovery.ui.home.thought_for_the_day;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ThoughtForTheDayViewModel extends ViewModel {

    private final MutableLiveData<String> mDate;
    private final MutableLiveData<String> mThought;
    private final MutableLiveData<String> mMeditation;
    private final MutableLiveData<String> mPrayer;
    private final MutableLiveData<String> mCopyright;

    public ThoughtForTheDayViewModel() {
        mDate = new MutableLiveData<>();
        mThought = new MutableLiveData<>();
        mMeditation = new MutableLiveData<>();
        mPrayer = new MutableLiveData<>();
        mCopyright = new MutableLiveData<>();
    }

    public void setDate(String date) { mDate.setValue(date); }

    public LiveData<String> getDate() {
        return mDate;
    }

    public void setThought(String thought) {
        mThought.setValue(thought);
    }

    public LiveData<String> getThought() {
        return mThought;
    }

    public void setMeditation(String meditation) {
        mMeditation.setValue(meditation);
    }

    public LiveData<String> getMeditation() {
        return mMeditation;
    }

    public void setPrayer(String prayer) { mPrayer.setValue(prayer);  }

    public LiveData<String> getPrayer() {
        return mPrayer;
    }

    public void setCopyright(String copyright) { mCopyright.setValue(copyright);  }

    public LiveData<String> getCopyright() {
        return mCopyright;
    }

}
