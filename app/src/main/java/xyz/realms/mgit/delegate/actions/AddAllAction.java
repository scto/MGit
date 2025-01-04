package xyz.realms.mgit.delegate.actions;

import xyz.realms.mgit.ui.RepoDetailActivity;
import xyz.realms.mgit.database.models.Repo;
import xyz.realms.mgit.tasks.repo.AddToStageTask;

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
