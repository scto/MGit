package xyz.realms.mgit.tasks.repo;

import xyz.realms.mgit.MGitApplication;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.ui.fragments.SheimiFragmentActivity;
import xyz.realms.mgit.ui.preference.PreferenceHelper;

/**
 * Super class for Tasks that operate on a git remote
 */

public abstract class RepoRemoteOpTask extends RepoOpTask implements SheimiFragmentActivity.OnPasswordEntered {


    public RepoRemoteOpTask(Repo repo) {
        super(repo);
        mRepo.setToken(MGitApplication.getContext());
    }

    public RepoRemoteOpTask(Repo repo, boolean parallel) {
        super(repo, parallel);
        mRepo.setToken(MGitApplication.getContext());
    }

    @Override
    public void onClicked(String username, String password, boolean savePassword) {
        mRepo.setUsername(username);
        mRepo.setPassword(password);
        if (savePassword) {
            PreferenceHelper prefHelper = MGitApplication.getContext().getPrefenceHelper();
            if (prefHelper != null) {
                prefHelper.setTokenAccount(username);
                prefHelper.setTokenSecretKey(password);
            }
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
