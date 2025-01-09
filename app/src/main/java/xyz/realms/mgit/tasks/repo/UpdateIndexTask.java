package xyz.realms.mgit.tasks.repo;

import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheEntry;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.FileMode;

import java.io.IOException;

import xyz.realms.mgit.R;
import xyz.realms.mgit.database.Repo;
import xyz.realms.mgit.errors.NoSuchIndexPathException;
import xyz.realms.mgit.errors.StopTaskException;
import xyz.realms.mgit.tasks.MGitAsyncTask;

public class UpdateIndexTask extends MGitAsyncTask implements MGitAsyncTask.MGitAsyncCallBack {

    private static final int Mode_755 = 0b111101101;
    private static final int Mode_644 = 0b110100100;
    private static final int Mode_777 = 0b111111111;
    private final String path;
    private final int newMode;

    public UpdateIndexTask(Repo repo, String path, int newMode) {
        super(repo);
        mGitAsyncCallBack = this;
        this.path = path;
        this.newMode = newMode;
    }

    public static int calculateNewMode(boolean executable) {
        return executable ? Mode_755 : Mode_644; // no octal literals in Java, 0o755 and 0o644
    }

    @Override
    public boolean doInBackground(Void... params) {
        return updateIndex();
    }

    private boolean updateIndex() {
        DirCache dircache;
        try {
            dircache = mRepo.getGit().getRepository().lockDirCache();
        } catch (NoWorkTreeException e) {
            setException(e, R.string.error_no_worktree);
            return false;
        } catch (CorruptObjectException e) {
            setException(e, R.string.error_invalid_index);
            return false;
        } catch (StopTaskException | IOException e) {
            throw new RuntimeException(e);
        }

        try {
            DirCacheEntry dirCacheEntry = dircache.getEntry(path);
            if (dirCacheEntry == null) {
                setException(new NoSuchIndexPathException(path), R.string.error_file_not_found);
                return false;
            }
            int oldMode = dirCacheEntry.getFileMode().getBits();
            dirCacheEntry.setFileMode(FileMode.fromBits(newMode | (oldMode & Mode_777)));
        } finally {
            dircache.unlock();
        }
        return true;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onProgressUpdate(String... progress) {

    }

    @Override
    public void onPostExecute(Boolean isSuccess) {

    }
}
