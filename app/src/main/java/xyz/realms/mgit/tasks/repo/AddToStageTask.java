package xyz.realms.mgit.tasks.repo;

import android.app.Dialog;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;
import xyz.realms.mgit.ui.RepoDetailActivity;

public class AddToStageTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final Dialog mDialog;
    public String mFilePattern;

    public AddToStageTask(Repo repo, String filePattern, RepoDetailActivity activity) {
        super(repo);
        mGitAsyncCallBack = this;
        mFilePattern = filePattern;
        mDialog = activity.showProgressDialog();
        setSuccessMsg(R.string.success_add_to_stage);
    }

    @Override
    public boolean doInBackground(Void... params) {
        return addToStage();
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onProgressUpdate(String... progress) {
    }

    @Override
    public void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
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
