package toxz.me.whizz.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Carlos on 4/16/2014.
 */
@Entity
public class Note {

    public static final int LOW_IMPORTANCE = 2;
    public static final int NORMAL_IMPORTANCE = 1;
    public static final int HIGH_IMPORTANCE = 0;
    public static final int NO_IMPORTANCE = -1;

    private int mImportance = NO_IMPORTANCE;
    @Id
    private long ID = -1;
    private String mContent = "";
    private long mCreatedTime = 0;
    private boolean isFinished = false;
    private long mFinishedTime = 0;
    private long mDeadline = 0;
    private boolean isNotice = true;
    String mImagesPath;

    @Generated(hash = 2058219551)
    public Note(int mImportance, long ID, String mContent, long mCreatedTime,
                boolean isFinished, long mFinishedTime, long mDeadline,
                boolean isNotice, String mImagesPath) {
        this.mImportance = mImportance;
        this.ID = ID;
        this.mContent = mContent;
        this.mCreatedTime = mCreatedTime;
        this.isFinished = isFinished;
        this.mFinishedTime = mFinishedTime;
        this.mDeadline = mDeadline;
        this.isNotice = isNotice;
        this.mImagesPath = mImagesPath;
    }

    @Generated(hash = 1272611929)
    public Note() {
    }

    public long getID() {
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

    @Nullable
    public List<String> getImagesPath() {
        if (mImagesPath == null) {
            return Collections.emptyList();
        }

        final String[] s = TextUtils.split(mImagesPath, "_\uD83D\uDE02_");
        return Arrays.asList(s);
    }

    public void setImagesPath(@NonNull List<String> list) {
        final String s = TextUtils.join("_\uD83D\uDE02_", list);
        this.mImagesPath = s;
    }

    public void commit(DatabaseHelper helper) {
        helper.saveNote(this);
    }

    public void delete(DatabaseHelper helper) {
        helper.deleteNote(this);
    }

    public int getMImportance() {
        return this.mImportance;
    }

    public void setMImportance(int mImportance) {
        this.mImportance = mImportance;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getMContent() {
        return this.mContent;
    }

    public void setMContent(String mContent) {
        this.mContent = mContent;
    }

    public long getMCreatedTime() {
        return this.mCreatedTime;
    }

    public void setMCreatedTime(long mCreatedTime) {
        this.mCreatedTime = mCreatedTime;
    }

    public boolean getIsFinished() {
        return this.isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public long getMFinishedTime() {
        return this.mFinishedTime;
    }

    public void setMFinishedTime(long mFinishedTime) {
        this.mFinishedTime = mFinishedTime;
    }

    public long getMDeadline() {
        return this.mDeadline;
    }

    public void setMDeadline(long mDeadline) {
        this.mDeadline = mDeadline;
    }

    public boolean getIsNotice() {
        return this.isNotice;
    }

    public void setIsNotice(boolean isNotice) {
        this.isNotice = isNotice;
    }

    public String getMImagesPath() {
        return this.mImagesPath;
    }

    public void setMImagesPath(String mImagesPath) {
        this.mImagesPath = mImagesPath;
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

        public Builder setImagesPath(List<String> list) {
            if (list != null && list.size() > 0) {
                mCreated.setImagesPath(list);
            }
            return this;
        }

        public Builder setImagesPathString(String s) {
            mCreated.mImagesPath = s;
            return this;
        }

        public Note create() {
            return mCreated;
        }

    }
}
