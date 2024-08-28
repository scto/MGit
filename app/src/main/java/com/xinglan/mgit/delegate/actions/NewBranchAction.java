package com.xinglan.mgit.delegate.actions;

import com.xinglan.mgit.ui.SheimiFragmentActivity;
import com.xinglan.mgit.R;
import com.xinglan.mgit.ui.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;
import com.xinglan.mgit.tasks.SheimiAsyncTask;
import com.xinglan.mgit.tasks.repo.CheckoutTask;

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
            R.string.dialog_create_branch_hint, R.string.label_create,
            new SheimiFragmentActivity.OnEditTextDialogClicked() {
                @Override
                public void onClicked(String branchName) {
                    CheckoutTask checkoutTask = new CheckoutTask(mRepo, null, branchName,
                        new ActivityResetPostCallback(branchName));
                    checkoutTask.executeTask();
                }
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
