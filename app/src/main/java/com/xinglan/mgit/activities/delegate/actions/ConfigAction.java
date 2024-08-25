package com.xinglan.mgit.activities.delegate.actions;

import android.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import android.view.LayoutInflater;

import com.xinglan.mgit.exceptions.StopTaskException;

import me.xinglan.mgit.R;
import com.xinglan.mgit.activities.RepoDetailActivity;
import com.xinglan.mgit.database.models.GitConfig;
import com.xinglan.mgit.database.models.Repo;

import me.xinglan.mgit.databinding.DialogRepoConfigBinding;
import timber.log.Timber;

/**
 * Action to display configuration for a Repo
 */
public class ConfigAction extends RepoAction {


    public ConfigAction(Repo repo, RepoDetailActivity activity) {
        super(repo, activity);
    }

    @Override
    public void execute() {

        try {
            DialogRepoConfigBinding binding = DataBindingUtil.inflate(LayoutInflater.from(mActivity), R.layout.dialog_repo_config, null, false);
            GitConfig gitConfig = new GitConfig(mRepo);
            binding.setViewModel(gitConfig);

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setView(binding.getRoot())
                .setNeutralButton(R.string.label_done, null)
                .create().show();

        } catch (StopTaskException e) {
            //FIXME: show error to user
            Timber.e(e);
        }
    }

}
