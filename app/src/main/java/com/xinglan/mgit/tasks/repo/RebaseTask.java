package com.xinglan.mgit.tasks.repo;

import com.xinglan.mgit.R;
import com.xinglan.mgit.database.models.Repo;
import com.xinglan.mgit.common.exceptions.StopTaskException;

public class RebaseTask extends RepoOpTask {

    public String mUpstream;
    private final AsyncTaskPostCallback mCallback;

    public RebaseTask(Repo repo, String upstream, AsyncTaskPostCallback callback) {
        super(repo);
        mUpstream = upstream;
        mCallback = callback;
        setSuccessMsg(R.string.success_rebase);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return rebase();
    }

    protected void onPostExecute(Boolean isSuccess) {
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
