package xyz.realms.mgit.actions;

import java.io.IOException;

import timber.log.Timber;
import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

public class NewFileAction extends RepoAction {

    public NewFileAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showEditTextDialog(R.string.dialog_create_file_title,
            R.string.dialog_create_file_hint, R.string.label_create, text -> {
            try {
                mActivity.getFilesFragment().newFile(text);
            } catch (IOException e) {
                Timber.e(e);
                mActivity.showMessageDialog(R.string.dialog_error_title,
                    mActivity.getString(R.string.error_something_wrong));
            }
        });
        mActivity.closeOperationDrawer();
    }
}
