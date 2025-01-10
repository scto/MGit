package xyz.realms.mgit.actions;

import android.content.DialogInterface;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.tasks.SheimiAsyncTask.AsyncTaskPostCallback;
import xyz.realms.mgit.tasks.repo.ResetCommitTask;
import xyz.realms.mgit.ui.RepoDetailActivity;

public class ResetAction extends RepoAction {

    public ResetAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showMessageDialog(R.string.dialog_reset_commit_title,
            R.string.dialog_reset_commit_msg, R.string.action_reset,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    reset();
                }
            });
        mActivity.closeOperationDrawer();
    }

    public void reset() {
        ResetCommitTask resetTask = new ResetCommitTask(mRepo,
            new AsyncTaskPostCallback() {
                @Override
                public void onPostExecute(Boolean isSuccess) {
                    mActivity.reset();
                }
            });
        resetTask.executeTask();
    }
}
