package com.xinglan.mgit.tasks.repo;

import android.app.Dialog;

import com.xinglan.mgit.R;
import com.xinglan.mgit.activities.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;
import com.xinglan.mgit.exceptions.StopTaskException;

public class AddToStageTask extends RepoOpTask {

    public String mFilePattern;
    private AsyncTaskPostCallback mCallback;
    private Dialog mDialog;

    public AddToStageTask(Repo repo, String filePattern, RepoDetailActivity activity) {
        super(repo);
        mFilePattern = filePattern;
        mDialog = activity.showProgressDialog();
        setSuccessMsg(R.string.success_add_to_stage);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return addToStage();
    }

    protected void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    public boolean addToStage() {
        try {
            //   jGit hasn't a cmd direct add modified/new/deleted files, so if want to add
            //   those 3 types changed, need a combined call like below.
            //   check it: https://stackoverflow.com/a/59434085

            //add modified/new files
            mRepo.getGit().add().setUpdate(false).addFilepattern(mFilePattern).call();
            //add modified/deleted files
            mRepo.getGit().add().setUpdate(true).addFilepattern(mFilePattern).call();
            mDialog.dismiss();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
