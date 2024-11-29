package com.xinglan.mgit.delegate.actions;

import com.xinglan.mgit.ui.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;

public abstract class RepoAction {

    protected Repo mRepo;
    protected RepoDetailActivity mActivity;

    public RepoAction(Repo repo, RepoDetailActivity activity) {
        mRepo = repo;
        mActivity = activity;
    }

    public abstract void execute();
}
