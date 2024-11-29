package com.xinglan.mgit.delegate.actions;

import com.xinglan.mgit.ui.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;

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
