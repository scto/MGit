package com.xinglan.mgit.activities.delegate.actions;

import android.content.Context;

import com.xinglan.mgit.activities.RepoDetailActivity;
import com.xinglan.mgit.database.models.Repo;
import com.xinglan.mgit.tasks.repo.AddToStageTask;


public class CommitQuickAction extends RepoAction {
    public CommitQuickAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {
        /*
         * index.lock的作用：防止在当前运行的git过程以外去更改本地存储库的资源。
         * 避免了多个git进程同时进行操作更改导致问题！
         *
         * 每当你运行一个git进程时，git就会在.git目录创建一个index.lock文件。
         * 例如，在当前的git仓库里运行git add .来stage本地的修改点，git就会在git add执行的时候创建index.lock文件，
         * 命令执行结束后，删除该文件。
         *
         * 如果某个进程退出/结束的时候除了问题，可能会导致index.lock文件没有被清除掉，此时，你需要将index.lock文件
         * 手动移除
         *
         * https://blog.csdn.net/u010682774/article/details/115725354
         */
        AddToStageTask stageAndCommit = new AddToStageTask(mRepo, ".",mActivity, isSuccess -> {
            RepoAction commit = new CommitAction(mRepo, mActivity);
            commit.execute();
        });
        stageAndCommit.executeTask();
    }

}




