package com.xinglan.mgit.delegate.actions;

import com.xinglan.mgit.ui.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;
import com.xinglan.mgit.tasks.repo.AddToStageTask;

public class AddAllAction extends RepoAction {

    public AddAllAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        AddToStageTask addTask = new AddToStageTask(mRepo, ".", mActivity);
        addTask.executeTask();
        mActivity.closeOperationDrawer();
    }

}
