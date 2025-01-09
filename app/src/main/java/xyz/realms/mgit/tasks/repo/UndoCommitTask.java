package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.api.ResetCommand;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class UndoCommitTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final MGitAsyncPostCallBack mCallback;

    public UndoCommitTask(Repo repo, MGitAsyncPostCallBack callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mCallback = callback;
        setSuccessMsg(R.string.success_undo);
    }

    @Override
    public boolean doInBackground(Void... params) {
        return undo();
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

    public boolean undo() {
        try {
            mRepo.getGit().getRepository().writeMergeCommitMsg(null);
            mRepo.getGit().getRepository().writeMergeHeads(null);
            mRepo.getGit().reset().setRef("HEAD~").setMode(ResetCommand.ResetType.SOFT).call();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
