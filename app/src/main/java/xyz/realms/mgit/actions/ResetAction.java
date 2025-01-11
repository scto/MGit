package xyz.realms.mgit.actions;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.tasks.repo.ResetCommitTask;
import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

public class ResetAction extends RepoAction {

    public ResetAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showMessageDialog(R.string.dialog_reset_commit_title,
            R.string.dialog_reset_commit_msg, R.string.action_reset,
            (dialogInterface, i) -> reset());
        mActivity.closeOperationDrawer();
    }

    public void reset() {
        ResetCommitTask resetTask = new ResetCommitTask(mRepo, isSuccess -> mActivity.reset());
        resetTask.executeTask();
    }
}
