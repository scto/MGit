package xyz.realms.mgit.actions;

import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.ui.explorer.RepoDetailActivity;

public class SyncRepoAction extends RepoAction {
    public SyncRepoAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        /*
         * 本地有修改，远程无修改 -> add暂存、commit提交、push推送
         * 本地有修改、远程有修改 -> 暂存、拉取、恢复暂存、提交、推送
         * 本地无修改，远程有修改 -> 拉取
         * 本地无修改，远程无修改 -> 提示全部最新
         * AddAllAction、CommitAction
         */
        try {
            // 获取当前仓库的状态
            Status status = mRepo.getGit().status().call();
            // 检查是否有更改
            if (status.hasUncommittedChanges()) {
                AddAllAction addAllAction = new AddAllAction(mRepo, mActivity);
                addAllAction.execute();
            } else {
                mActivity.showToastMessage(R.string.alert_uncommitted_changes);
            }
        } catch (StopTaskException | GitAPIException e) {
            throw new RuntimeException(e);
        }
    }
}
