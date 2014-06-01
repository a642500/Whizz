package com.unique.whizzdo.data;

import android.net.Uri;
import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Carlos on 4/16/2014.
 */
public class Note {

    public static final int LOW_IMPORTANCE = 2;
    public static final int NORMAL_IMPORTANCE = 1;
    public static final int HIGH_IMPORTANCE = 0;
    public static final int NO_IMPORTANCE = -1;
    private int mImportance = NO_IMPORTANCE;
    private int ID = -1;
    private String mContent = null;
    private long mCreatedTime = 0;
    private boolean isFinished = false;
    private long mFinishedTime = 0;
    private long mDeadline = 0;
    private boolean isNotice = true;
    private ArrayList<String> mImagesPaths = new ArrayList<String>();
    //TODO  继续，完成build和数据库方法

    public int getID() {
        return ID;
    }

    public Note resetID() {
        this.ID = -1;
        return this;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public long getCreatedTime() {
        return mCreatedTime;
    }

    public void setCreatedTime(long mCreatedTime) {
        this.mCreatedTime = mCreatedTime;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public long getFinishedTime() {
        return mFinishedTime;
    }

    public void setFinishedTime(long mFinishedTime) {
        this.mFinishedTime = mFinishedTime;
    }

    public long getDeadline() {
        return mDeadline;
    }

    public void setDeadline(long mDeadline) {
        this.mDeadline = mDeadline;
    }

    public boolean isNotice() {
        return isNotice;
    }

    public void setNotice(boolean isNotice) {
        this.isNotice = isNotice;
    }


    public int getImportance() {
        return mImportance;
    }

    public void setImportance(int importance) {
        this.mImportance = importance;
    }

    public ArrayList<String> getImagesPaths() {
        return mImagesPaths;
    }

    public void commit(DatabaseHelper helper) {
        helper.saveNote(this);
    }

    public void delete(DatabaseHelper helper) {
        helper.deleteNote(this);
    }

    public static class Builder {
        private Note mCreated;

        public Builder() {
            mCreated = new Note();
        }

        public Builder setID(int id) {
            mCreated.ID = id;
            return this;
        }

        public Builder setContent(String mContent) {
            mCreated.mContent = mContent;
            return this;
        }

        public Builder setCreatedTime(long mCreatedTime) {
            mCreated.mCreatedTime = mCreatedTime;
            return this;
        }

        public Builder setFinished(boolean isFinished) {
            mCreated.isFinished = isFinished;
            return this;
        }

        public Builder setFinishedTime(long mFinishedTime) {
            mCreated.mFinishedTime = mFinishedTime;
            return this;
        }

        public Builder setDeadline(long mDeadline) {
            mCreated.mDeadline = mDeadline;
            return this;
        }

        public Builder setNotice(boolean isNotice) {
            mCreated.isNotice = isNotice;
            return this;
        }

        public Builder setImportance(int importance) {
            mCreated.mImportance = importance;
            return this;
        }

        public Builder setImagePaths(ArrayList<String> list) {
            if (list != null) {
                mCreated.mImagesPaths = list;
            }
            return this;
        }



        public Note create() {
            return mCreated;
        }

    }
}
