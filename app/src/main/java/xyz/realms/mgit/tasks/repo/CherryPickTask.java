package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.lib.ObjectId;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class CherryPickTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final MGitPostCallBack mCallback;
    public String mCommitStr;

    public CherryPickTask(Repo repo, String commit, MGitPostCallBack callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mCommitStr = commit;
        mCallback = callback;
        setSuccessMsg(R.string.success_cherry_pick);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public boolean doInBackground(Void... params) {
        return cherrypick();
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

    public boolean cherrypick() {
        try {
            ObjectId commit = mRepo.getGit().getRepository().resolve(mCommitStr);
            mRepo.getGit().cherryPick().include(commit).call();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }


}
