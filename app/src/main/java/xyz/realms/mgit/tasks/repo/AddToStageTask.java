package xyz.realms.mgit.tasks.repo;

import android.app.Dialog;

import xyz.realms.mgit.R;
import xyz.realms.mgit.ui.RepoDetailActivity;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;

public class AddToStageTask extends RepoOpTask {

    public String mFilePattern;
    private AsyncTaskPostCallback mCallback;
    private final Dialog mDialog;

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
            mRepo.getGit().add().addFilepattern(mFilePattern).setRenormalize(false).call();
            //add modified/deleted files
            mRepo.getGit().add().setUpdate(true).addFilepattern(mFilePattern).setRenormalize(false).call();
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
