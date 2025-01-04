package xyz.realms.mgit.delegate.actions;

import xyz.realms.mgit.ui.SheimiFragmentActivity.OnEditTextDialogClicked;
import xyz.realms.mgit.R;
import xyz.realms.mgit.ui.RepoDetailActivity;
import xyz.realms.mgit.database.models.Repo;
import xyz.realms.mgit.tasks.SheimiAsyncTask.AsyncTaskPostCallback;
import xyz.realms.mgit.tasks.repo.CherryPickTask;

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
