package com.xinglan.mgit.activities.delegate.actions;

import com.xinglan.mgit.activities.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;
import com.xinglan.mgit.tasks.SheimiAsyncTask;
import com.xinglan.mgit.tasks.repo.AddToStageTask;


public class CommitQuickAction extends RepoAction {

    public CommitQuickAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);

    }

    @Override
    public void execute() {
        AddToStageTask stageAndCommit = new AddToStageTask(mRepo, ".", isSuccess -> {
            RepoAction commit = new CommitAction(mRepo, mActivity);
            commit.execute();
        });
        stageAndCommit.executeTask();
    }

}




