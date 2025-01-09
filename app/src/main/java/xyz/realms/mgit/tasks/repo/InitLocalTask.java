package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.api.Git;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.database.RepoContract;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class InitLocalTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    public InitLocalTask(Repo repo) {
        super(repo);
        mGitAsyncCallBack = this;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public boolean doInBackground(Void... params) {
        boolean result = init();
        if (!result) {
            mRepo.deleteRepoSync();
            return false;
        }
        return true;
    }

    @Override
    public void onProgressUpdate(String... progress) {

    }

    @Override
    public void onPostExecute(Boolean isSuccess) {
        super.onPostExecute(isSuccess);
        if (isSuccess) {
            mRepo.updateLatestCommitInfo();
            mRepo.updateStatus(RepoContract.REPO_STATUS_NULL);
        }
    }

    public boolean init() {
        try {
            Git.init().setDirectory(mRepo.getDir()).call();
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
