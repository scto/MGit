package xyz.realms.mgit.tasks.repo

import xyz.realms.mgit.R
import xyz.realms.mgit.errors.NoSuchIndexPathException
import xyz.realms.mgit.database.Repo
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.errors.CorruptObjectException
import org.eclipse.jgit.errors.NoWorkTreeException
import org.eclipse.jgit.lib.FileMode

class UpdateIndexTask(repo: Repo, private val path: String, private val newMode: Int) :
    RepoOpTask(repo) {
    companion object {
        fun calculateNewMode(executable: Boolean): Int =
            if (executable) 0b111101101 else 0b110100100 // no octal literals in Kotlin, 0o755 and 0o644
    }


    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?) = updateIndex()

    private fun updateIndex(): Boolean {
        val dircache: DirCache?
        try {
            dircache = mRepo.git.repository.lockDirCache()
        } catch (e: NoWorkTreeException) {
            setException(e, R.string.error_no_worktree)
            return false
        } catch (e: CorruptObjectException) {
            setException(e, R.string.error_invalid_index)
            return false
        }

        try {
            val dirCacheEntry = dircache.getEntry(path)
            val entry = if (dirCacheEntry != null) dirCacheEntry else {
                setException(NoSuchIndexPathException(path), R.string.error_file_not_found)
                return false
            }
            val oldMode = entry.fileMode
            entry.fileMode =
                FileMode.fromBits(newMode or (oldMode.bits or 0b111111111 xor 0b111111111))

        } finally {
            dircache.unlock()
        }
        return true
    }
}
