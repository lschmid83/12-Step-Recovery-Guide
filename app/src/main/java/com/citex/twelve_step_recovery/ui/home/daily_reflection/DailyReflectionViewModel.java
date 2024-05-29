package com.citex.twelve_step_recovery.ui.home.daily_reflection;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DailyReflectionViewModel extends ViewModel {

    private final MutableLiveData<String> mDate;
    private final MutableLiveData<String> mHeaderTitle;
    private final MutableLiveData<String> mHeaderContent;
    private final MutableLiveData<String> mContentTitle;
    private final MutableLiveData<String> mContent;
    private final MutableLiveData<String> mCopyright;

    public DailyReflectionViewModel() {
        mDate = new MutableLiveData<>();
        mHeaderTitle = new MutableLiveData<>();
        mHeaderContent = new MutableLiveData<>();
        mContentTitle = new MutableLiveData<>();
        mContent = new MutableLiveData<>();
        mCopyright = new MutableLiveData<>();
    }

    public void setDate(String date) {
        mDate.setValue(date);
    }

    public LiveData<String> getDate() {
        return mDate;
    }

    public void setHeaderTitle(String headerTitle) {
        mHeaderTitle.setValue(headerTitle);
    }

    public LiveData<String> getHeaderTitle() {
        return mHeaderTitle;
    }

    public void setHeaderContent(String headerContent) {
        mHeaderContent.setValue(headerContent);
    }

    public LiveData<String> getHeaderContent() {
        return mHeaderContent;
    }

    public void setContentTitle(String contentTitle) {
        mContentTitle.setValue(contentTitle);
    }

    public LiveData<String> getContentTitle() {
        return mContentTitle;
    }

    public void setContent(String content) {
        mContent.setValue(content);
    }

    public LiveData<String> getContent() {
        return mContent;
    }

    public void setCopyright(String copyright) {
        mCopyright.setValue(copyright);
    }

    public LiveData<String> getCopyright() {
        return mCopyright;
    }

}
