package xyz.realms.mgit.actions;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.tasks.repo.AddToStageTask;
import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

public class AddAllAction extends RepoAction {

    public AddAllAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        AddToStageTask addTask = new AddToStageTask(mRepo, ".", null);
        mActivity.showToastMessage("正在暂存全部。");
        addTask.executeTask();
        mActivity.closeOperationDrawer();
    }

}
