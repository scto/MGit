package xyz.realms.mgit.actions;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.RepoDetailActivity;

public class NewDirAction extends RepoAction {

    public NewDirAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showEditTextDialog(R.string.dialog_create_dir_title,
            R.string.dialog_create_dir_hint, R.string.label_create,
            text -> mActivity.getFilesFragment().newDir(text));
        mActivity.closeOperationDrawer();
    }
}
