package xyz.realms.mgit.tasks.repo;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class RebaseTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final MGitAsyncPostCallBack mCallback;
    public String mUpstream;

    public RebaseTask(Repo repo, String upstream, MGitAsyncPostCallBack callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mUpstream = upstream;
        mCallback = callback;
        setSuccessMsg(R.string.success_rebase);
    }

    @Override
    public boolean doInBackground(Void... params) {
        return rebase();
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

    public boolean rebase() {
        try {
            mRepo.getGit().rebase().setUpstream(mUpstream).call();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
