package com.xinglan.mgit.activities.delegate.actions;

import com.xinglan.android.activities.SheimiFragmentActivity.OnEditTextDialogClicked;
import me.xinglan.mgit.R;
import com.xinglan.mgit.activities.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;

public class NewDirAction extends RepoAction {

    public NewDirAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showEditTextDialog(R.string.dialog_create_dir_title,
                R.string.dialog_create_dir_hint, R.string.label_create,
                new OnEditTextDialogClicked() {
                    @Override
                    public void onClicked(String text) {
                        mActivity.getFilesFragment().newDir(text);
                    }
                });
        mActivity.closeOperationDrawer();
    }
}
