package xyz.realms.mgit.tasks.repo;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class CheckoutFileTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final MGitAsyncCallBack mCallback;
    private final String mPath;

    public CheckoutFileTask(Repo repo, String path, MGitAsyncCallBack callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mCallback = callback;
        mPath = path;
        setSuccessMsg(R.string.success_checkout_file);
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public boolean doInBackground(Void... params) {
        return checkout();
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

    private boolean checkout() {
        try {
            mRepo.getGit().checkout().addPath(mPath).call();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
