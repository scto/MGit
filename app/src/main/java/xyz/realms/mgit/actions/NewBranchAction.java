package xyz.realms.mgit.actions;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.tasks.SheimiAsyncTask;
import xyz.realms.mgit.tasks.repo.CheckoutTask;
import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

/**
 * Created by liscju - piotr.listkiewicz@gmail.com on 2015-03-15.
 */
public class NewBranchAction extends RepoAction {
    public NewBranchAction(Repo mRepo, RepoDetailActivity mActivity) {
        super(mRepo, mActivity);
    }

    @Override
    public void execute() {
        mActivity.showEditTextDialog(R.string.dialog_create_branch_title,
            R.string.dialog_create_branch_hint, R.string.label_create, branchName -> {
            CheckoutTask checkoutTask = new CheckoutTask(mRepo, null, branchName,
                new ActivityResetPostCallback(branchName));
            checkoutTask.executeTask();
        });
        mActivity.closeOperationDrawer();
    }

    private class ActivityResetPostCallback implements SheimiAsyncTask.AsyncTaskPostCallback {
        private final String mBranchName;

        public ActivityResetPostCallback(String branchName) {
            mBranchName = branchName;
        }

        @Override
        public void onPostExecute(Boolean isSuccess) {
            mActivity.reset(mBranchName);
        }
    }
}
