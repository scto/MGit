package xyz.realms.mgit.actions;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

public class DiffAction extends RepoAction {

    public DiffAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        mActivity.enterDiffActionMode();
        mActivity.closeOperationDrawer();
    }
}
