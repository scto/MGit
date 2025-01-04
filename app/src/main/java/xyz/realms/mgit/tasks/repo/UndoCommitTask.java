package xyz.realms.mgit.tasks.repo;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.models.Repo;
import xyz.realms.mgit.common.exceptions.StopTaskException;

import org.eclipse.jgit.api.ResetCommand;

public class UndoCommitTask extends RepoOpTask {

    private final AsyncTaskPostCallback mCallback;

    public UndoCommitTask(Repo repo, AsyncTaskPostCallback callback) {
        super(repo);
        mCallback = callback;
        setSuccessMsg(R.string.success_undo);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return undo();
    }

    protected void onPostExecute(Boolean isSuccess) {
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
