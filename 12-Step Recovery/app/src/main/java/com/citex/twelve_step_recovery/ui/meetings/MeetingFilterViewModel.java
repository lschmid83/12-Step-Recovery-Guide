package com.citex.twelve_step_recovery.ui.meetings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MeetingFilterViewModel extends ViewModel {

    private final MutableLiveData<Boolean> mProgramAA;
    private final MutableLiveData<Boolean> mProgramCA;
    private final MutableLiveData<Boolean> mProgramNA;
    private final MutableLiveData<Boolean> mProgramOA;
    private final MutableLiveData<String> mWeekday;
    private final MutableLiveData<String> mSearchRange;
    private final MutableLiveData<String> mSearchUnits;
    private final MutableLiveData<Boolean> mWheelchairAccessibility;

    public MeetingFilterViewModel() {
        mProgramAA = new MutableLiveData<Boolean>();
        mProgramCA = new MutableLiveData<Boolean>();
        mProgramNA = new MutableLiveData<Boolean>();
        mProgramOA = new MutableLiveData<Boolean>();
        mWeekday = new MutableLiveData<String>();
        mSearchRange = new MutableLiveData<String>();
        mSearchUnits = new MutableLiveData<String>();
        mWheelchairAccessibility = new MutableLiveData<Boolean>();
    }

    public LiveData<Boolean> getProgramAA() {
        return mProgramAA;
    }

    public void setProgramAA(Boolean isChecked) { mProgramAA.setValue(isChecked); }

    public LiveData<Boolean> getProgramCA() {
        return mProgramCA;
    }

    public void setProgramCA(Boolean isChecked) { mProgramCA.setValue(isChecked); }

    public LiveData<Boolean> getProgramNA() {
        return mProgramNA;
    }

    public void setProgramNA(Boolean isChecked) { mProgramNA.setValue(isChecked); }

    public LiveData<Boolean> getProgramOA() {
        return mProgramOA;
    }

    public void setProgramOA(Boolean isChecked) { mProgramOA.setValue(isChecked); }

    public LiveData<String> getWeekday() {
        return mWeekday;
    }

    public void setWeekday(String weekday) { mWeekday.setValue(weekday); }

    public LiveData<String> getSearchRange() {
        return mSearchRange;
    }

    public void setSearchRange(String searchRange) { mSearchRange.setValue(searchRange); }

    public LiveData<String> getSearchUnits() {
        return mSearchUnits;
    }

    public void setSearchUnits(String searchUnits) { mSearchUnits.setValue(searchUnits); }

    public LiveData<Boolean> getWheelchairAccessibility() { return mWheelchairAccessibility;   }

    public void setWheelchairAccessibility(Boolean accessibility) { mWheelchairAccessibility.setValue(accessibility); }
}
