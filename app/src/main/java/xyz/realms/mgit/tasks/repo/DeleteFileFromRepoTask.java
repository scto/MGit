package xyz.realms.mgit.tasks.repo;

import java.io.File;

import xyz.realms.android.utils.FsUtils;
import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class DeleteFileFromRepoTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final DeleteOperationType mOperationType;
    public String mFilePattern;
    public MGitAsyncPostCallBack mCallback;

    public DeleteFileFromRepoTask(Repo repo, String filepattern,
                                  DeleteOperationType deleteOperationType,
                                  MGitAsyncPostCallBack callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mFilePattern = filepattern;
        mCallback = callback;
        mOperationType = deleteOperationType;
        setSuccessMsg(R.string.success_remove_file);
    }

    @Override
    public boolean doInBackground(Void... params) {
        return removeFile();
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
        if (mCallback != null) {
            mCallback.onPostExecute(isSuccess);
        }
    }

    public boolean removeFile() {
        try {
            switch (mOperationType) {
                case DELETE:
                    File fileToDelete = FsUtils.joinPath(mRepo.getDir(), mFilePattern);
                    FsUtils.deleteFile(fileToDelete);
                    break;
                case REMOVE_CACHED:
                    mRepo.getGit().rm().setCached(true).addFilepattern(mFilePattern).call();
                    break;
                case REMOVE_FORCE:
                    mRepo.getGit().rm().addFilepattern(mFilePattern).call();
                    break;
            }
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }

    /**
     * Created by lee on 2015-01-30.
     */
    public enum DeleteOperationType {
        DELETE, REMOVE_CACHED, REMOVE_FORCE
    }
}
