package xyz.realms.mgit.delegate.actions;

import xyz.realms.mgit.ui.SheimiFragmentActivity.OnEditTextDialogClicked;
import xyz.realms.mgit.R;
import xyz.realms.mgit.ui.RepoDetailActivity;
import xyz.realms.mgit.database.models.Repo;

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
