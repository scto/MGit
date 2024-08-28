package com.xinglan.mgit.delegate.actions;

import android.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.databinding.DataBindingUtil;

import com.xinglan.mgit.R;
import com.xinglan.mgit.ui.RepoDetailActivity;
import com.xinglan.mgit.database.models.GitConfig;
import com.xinglan.mgit.database.models.Repo;
import com.xinglan.mgit.databinding.DialogRepoConfigBinding;
import com.xinglan.mgit.common.exceptions.StopTaskException;

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
