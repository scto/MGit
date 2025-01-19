package xyz.realms.mgit.actions;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

public class DeleteAction extends RepoAction {

    public DeleteAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.showMessageDialog(R.string.dialog_delete_repo_title,
            R.string.dialog_delete_repo_msg, R.string.label_delete, (dialogInterface, i) -> {
            mRepo.deleteRepo(true);
            mActivity.finish();
        });
        mActivity.closeOperationDrawer();
    }
}
