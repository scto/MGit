package xyz.realms.mgit.delegate.actions;

import xyz.realms.mgit.ui.RepoDetailActivity;
import xyz.realms.mgit.database.models.Repo;

public abstract class RepoAction {

    protected Repo mRepo;
    protected RepoDetailActivity mActivity;

    public RepoAction(Repo repo, RepoDetailActivity activity) {
        mRepo = repo;
        mActivity = activity;
    }

    public abstract void execute();
}
