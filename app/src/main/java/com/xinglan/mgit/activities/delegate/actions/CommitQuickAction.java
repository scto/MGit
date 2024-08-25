package com.xinglan.mgit.activities.delegate.actions;

import com.xinglan.mgit.activities.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;

public class CommitQuickAction extends RepoAction {

    public CommitQuickAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        RepoAction stageAll = new AddAllAction(mRepo,mActivity);
        stageAll.execute();
        RepoAction commit = new CommitAction(mRepo,mActivity);
        commit.execute();
    }

}


