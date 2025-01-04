package xyz.realms.mgit.tasks.repo;

import xyz.realms.mgit.ui.SheimiFragmentActivity;
import xyz.realms.android.preference.PreferenceHelper;
import xyz.realms.android.MGitApplication;
import xyz.realms.mgit.database.models.Repo;

/**
 * Super class for Tasks that operate on a git remote
 */

public abstract class RepoRemoteOpTask extends RepoOpTask implements SheimiFragmentActivity.OnPasswordEntered {


    public RepoRemoteOpTask(Repo repo) {
        super(repo);
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
