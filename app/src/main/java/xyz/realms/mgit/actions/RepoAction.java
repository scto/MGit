package xyz.realms.mgit.actions;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.RepoDetailActivity;

public abstract class RepoAction {

    protected Repo mRepo;
    protected RepoDetailActivity mActivity;

    public RepoAction(Repo repo, RepoDetailActivity activity) {
        mRepo = repo;
        mActivity = activity;
    }

    public abstract void execute();
}
