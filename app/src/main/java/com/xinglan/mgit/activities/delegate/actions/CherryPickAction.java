package com.xinglan.mgit.activities.delegate.actions;

import com.xinglan.android.activities.SheimiFragmentActivity.OnEditTextDialogClicked;
import com.xinglan.mgit.R;
import com.xinglan.mgit.activities.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;
import com.xinglan.mgit.tasks.SheimiAsyncTask.AsyncTaskPostCallback;
import com.xinglan.mgit.tasks.repo.CherryPickTask;

public class CherryPickAction extends RepoAction {

    public CherryPickAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showEditTextDialog(R.string.dialog_cherrypick_title,
            R.string.dialog_cherrypick_msg_hint,
            R.string.dialog_label_cherrypick,
            new OnEditTextDialogClicked() {
                @Override
                public void onClicked(String text) {
                    cherrypick(text);
                }
            });
        mActivity.closeOperationDrawer();
    }

    public void cherrypick(String commit) {
        CherryPickTask task = new CherryPickTask(mRepo, commit, new AsyncTaskPostCallback() {
            @Override
            public void onPostExecute(Boolean isSuccess) {
                mActivity.reset();
            }
        });
        task.executeTask();
    }

}
