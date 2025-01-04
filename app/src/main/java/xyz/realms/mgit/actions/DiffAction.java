package xyz.realms.mgit.actions;

import xyz.realms.mgit.ui.RepoDetailActivity;
import xyz.realms.mgit.database.Repo;

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
