package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.api.RebaseCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;

import timber.log.Timber;
import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class ResetCommitTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private final MGitAsyncPostCallBack mCallback;

    public ResetCommitTask(Repo repo, MGitAsyncPostCallBack callback) {
        super(repo);
        mGitAsyncCallBack = this;
        mCallback = callback;
        setSuccessMsg(R.string.success_reset);
    }

    @Override
    public boolean doInBackground(Void... params) {
        return reset();
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

    public boolean reset() {
        try {
            mRepo.getGit().getRepository().writeMergeCommitMsg(null);
            mRepo.getGit().getRepository().writeMergeHeads(null);
            try {
                // if a rebase is in-progress, need to abort it
                mRepo.getGit().rebase().setOperation(RebaseCommand.Operation.ABORT).call();
            } catch (WrongRepositoryStateException e) {
                // Ignore this, it happens if rebase --abort is called without a rebase in progress.
                Timber.i(e, "Couldn't abort rebase while reset.");
            } catch (Exception e) {
                setException(e, R.string.error_rebase_abort_failed_in_reset);
                return false;
            }
            mRepo.getGit().reset().setMode(ResetCommand.ResetType.HARD).call();
        } catch (StopTaskException e) {
            return false;
        } catch (Throwable e) {
            setException(e);
            return false;
        }
        return true;
    }
}
