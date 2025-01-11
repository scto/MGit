package xyz.realms.mgit.actions;

import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.repo.AddToStageTask;
import xyz.realms.mgit.tasks.repo.CheckoutFileTask;
import xyz.realms.mgit.tasks.repo.RebaseTask;
import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

public class SyncRepoAction extends RepoAction {
    public SyncRepoAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        /*
         * 本地有修改，远程无修改 -> add暂存、commit提交、push推送
         * 本地无修改，远程有修改 -> 拉取
         * 本地无修改，远程无修改 -> 提示全部最新
         * AddAllAction、CommitAction
         * 本地有修改、远程有修改，极易产生冲突。暂存、拉取、在恢复暂存的时候有些文件版本不一样了必然导致冲突。
         */
        try {
            // 获取当前仓库的状态
            Status status = mRepo.getGit().status().call();
            // 检查是否有更改
            if (status.hasUncommittedChanges()) {
//                不能通过action进行链式调用，即使不能重用action的代码。
//                只能通过task进行链式调用。
//                AddToStageTask addTask = new AddToStageTask(mRepo, ".", (isAddToStageTaskSuccess) -> {
//                    new CheckoutFileTask(mRepo, null, (isCheckoutFileTaskSuccess) -> {
//                        new RebaseTask(mRepo, "true", (isRebaseTaskSuccess) -> {
//                            new RebaseTask(mRepo, "true", null).execute();
//                        }).execute();
//                    }).execute();
//                });
//                addTask.executeTask();
            } else {
                mActivity.showToastMessage(R.string.alert_uncommitted_changes);
            }
        } catch (StopTaskException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
