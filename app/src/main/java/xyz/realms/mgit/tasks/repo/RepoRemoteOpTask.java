package xyz.realms.mgit.tasks.repo;

import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.fragments.SheimiFragmentActivity;

/**
 * Super class for Tasks that operate on a git remote
 */

public abstract class RepoRemoteOpTask extends RepoOpTask implements SheimiFragmentActivity.OnPasswordEntered {


    public RepoRemoteOpTask(Repo repo) {
        super(repo);
    }

    public RepoRemoteOpTask(Repo repo, boolean parallel) {
        super(repo, parallel);
    }

    @Override
    public void onClicked(String username, String password, boolean savePassword) {
        mRepo.setAndAddAccount(username, password);
        if (savePassword) {
            mRepo.saveCredentials();
        }
        mRepo.removeTask(this);
        getNewTask().executeTask();
    }

    @Override
    public void onCanceled() {

    }

    public abstract RepoRemoteOpTask getNewTask();
}
