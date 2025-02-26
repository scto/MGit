package xyz.realms.mgit.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import xyz.realms.mgit.ui.utils.BasicFunctions;

/**
 * Manage entries in the persisted database tracking local repo metadata.
 */
public class RepoDbManager {

    private static final Map<String, Set<RepoDbObserver>> mObservers = new HashMap<>();
    private static RepoDbManager mInstance;
    private final SQLiteDatabase mWritableDB;
    private final SQLiteDatabase mReadableDB;


    private RepoDbManager(Context context) {
        RepoDbHelper mDbHelper = new RepoDbHelper(context);
        mWritableDB = mDbHelper.getWritableDatabase();
        mReadableDB = mDbHelper.getReadableDatabase();
    }

    private static RepoDbManager getInstance() {
        if (mInstance == null) {
            mInstance = new RepoDbManager(BasicFunctions.getActiveActivity());
        }
        return mInstance;
    }

    public static void registerDbObserver(String table, RepoDbObserver observer) {
        Set<RepoDbObserver> set = mObservers.computeIfAbsent(table, k -> new HashSet<>());
        set.add(observer);
    }

    public static void unregisterDbObserver(String table, RepoDbObserver observer) {
        Set<RepoDbObserver> set = mObservers.get(table);
        if (set == null) return;
        set.remove(observer);
    }

    public static void notifyObservers(String table) {
        Set<RepoDbObserver> set = mObservers.get(table);
        if (set == null) return;
        for (RepoDbObserver observer : set) {
            observer.notifyChanged();
        }
    }

    public static void persistCredentials(long repoId, String username, String password) {
        ContentValues values = new ContentValues();
        if (username != null && password != null) {
            values.put(RepoContract.RepoEntry.COLUMN_NAME_USERNAME, username);
            values.put(RepoContract.RepoEntry.COLUMN_NAME_PASSWORD, password);
            relateRepoWithCredential(createCredential(username, password), String.valueOf(repoId));
        } else {
            values.put(RepoContract.RepoEntry.COLUMN_NAME_USERNAME, "");
            values.put(RepoContract.RepoEntry.COLUMN_NAME_PASSWORD, "");
        }
        updateRepo(repoId, values);
    }

    public static Cursor searchRepo(String query) {
        return getInstance()._searchRepo(query);
    }

    public static Cursor queryAllRepo() {
        return getInstance()._queryAllRepo();
    }

    public static Cursor getRepoById(long id) {
        return getInstance()._getRepoById(id);
    }

    public static long importRepo(String localPath, String status) {
        return createRepo(localPath, "", status);
    }

    public static void setLocalPath(long repoId, String path) {
        ContentValues values = new ContentValues();
        values.put(RepoContract.RepoEntry.COLUMN_NAME_LOCAL_PATH, path);
        updateRepo(repoId, values);
    }

    public static long createRepo(String localPath, String remoteURL, String status) {
        ContentValues values = new ContentValues();
        values.put(RepoContract.RepoEntry.COLUMN_NAME_LOCAL_PATH, localPath);
        values.put(RepoContract.RepoEntry.COLUMN_NAME_REMOTE_URL, remoteURL);
        values.put(RepoContract.RepoEntry.COLUMN_NAME_REPO_STATUS, status);
        long id = getInstance().mWritableDB.insert(RepoContract.RepoEntry.TABLE_NAME, null, values);
        notifyObservers(RepoContract.RepoEntry.TABLE_NAME);
        return id;
    }

    public static void updateRepo(long id, ContentValues values) {
        String whereClause = RepoContract.RepoEntry._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        getInstance().mWritableDB.update(RepoContract.RepoEntry.TABLE_NAME, values, whereClause,
            whereArgs);
        notifyObservers(RepoContract.RepoEntry.TABLE_NAME);
    }

    public static void deleteRepo(long id) {
        getInstance()._deleteRepo(id);
    }

    public static long createCredential(String token_account, String token_secret) {
        long credentialId = -1;
        try (Cursor cursor = getInstance()._queryAllCredential()) {
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    int columnTokenAccountIndex = cursor.getColumnIndex(RepoContract.RepoCredential.COLUMN_TOKEN_ACCOUNT);
                    int columnCredentialIdIndex = cursor.getColumnIndex(RepoContract.RepoCredential._ID);
                    String curr_token_account = cursor.getString(columnTokenAccountIndex);
                    if (token_account.equals(curr_token_account)) {
                        credentialId = cursor.getInt(columnCredentialIdIndex);
                        break;
                    }
                    cursor.moveToNext();
                }
            }
            if (credentialId == -1) {
                ContentValues values = new ContentValues();
                values.put(RepoContract.RepoCredential.COLUMN_TOKEN_ACCOUNT, token_account);
                values.put(RepoContract.RepoCredential.COLUMN_TOKEN_SECRET, token_secret);
                credentialId = getInstance()
                    .mWritableDB
                    .insert(RepoContract.RepoCredential.TABLE_NAME, null, values);
            }
        } catch (Exception ignored) {
        }
        return credentialId;
    }

    public static void relateRepoWithCredential(long id, String repo) {
        Cursor credential = getCredentialById(id);
        if (credential == null || !credential.moveToFirst()) {
            return; // Credential not found or cursor is empty
        }
        int columnIndex = credential.getColumnIndex(RepoContract.RepoCredential.COLUMN_REL_REPO);
        String relRepoString = credential.getString(columnIndex);
        if (relRepoString == null) return;
        String[] in_db_rel = relRepoString.split(",");
        HashSet<String> rel_list = new HashSet<>(Arrays.asList(in_db_rel));
        rel_list.add(repo);
        String new_rel = String.join(",", rel_list);
        ContentValues values = new ContentValues();
        values.put(RepoContract.RepoCredential.COLUMN_REL_REPO, new_rel);
        updateCredential(id, values);
    }

    public static void unrelateRepoWithCredential(long id, String repo) {
        Cursor credential = getCredentialById(id);
        if (credential == null || !credential.moveToFirst()) {
            return; // Credential not found or cursor is empty
        }
        int columnIndex = credential.getColumnIndex(RepoContract.RepoCredential.COLUMN_REL_REPO);
        String relRepoString = credential.getString(columnIndex);
        if (relRepoString == null) return;
        String[] in_db_rel = relRepoString.split(",");
        HashSet<String> rel_list = new HashSet<>(Arrays.asList(in_db_rel));
        if (!rel_list.contains(repo)) return;
        rel_list.remove(repo);
        String new_rel = String.join(",", rel_list);
        ContentValues values = new ContentValues();
        values.put(RepoContract.RepoCredential.COLUMN_REL_REPO, new_rel);
        updateCredential(id, values);
    }

    public static void updateCredential(long id, ContentValues values) {
        String whereClause = RepoContract.RepoCredential._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        getInstance().mWritableDB.update(RepoContract.RepoCredential.TABLE_NAME, values,
            whereClause, whereArgs);
    }

    public static Cursor getCredentialById(long id) {
        return getInstance()._getCredentialById(id);
    }

    public static Cursor queryAllCredential() {
        return getInstance()._queryAllCredential();
    }

    public static void deleteCredential(long id) {
        getInstance()._deleteCredential(id);
    }

    private Cursor _searchRepo(String query) {
        String whereClause =
            RepoContract.RepoEntry.COLUMN_NAME_LOCAL_PATH
                + " LIKE ? OR " + RepoContract.RepoEntry.COLUMN_NAME_REMOTE_URL
                + " LIKE ? OR " + RepoContract.RepoEntry.COLUMN_NAME_LATEST_COMMITTER_UNAME
                + " LIKE ? OR " + RepoContract.RepoEntry.COLUMN_NAME_LATEST_COMMIT_MSG
                + " LIKE ?"
            ;
        query = "%" + query + "%";
        String[] whereArgs = {query, query, query, query};
        return mReadableDB.query(true, RepoContract.RepoEntry.TABLE_NAME,
            RepoContract.RepoEntry.ALL_COLUMNS, whereClause, whereArgs, null, null, null, null);
    }

    private Cursor _queryAllRepo() {
        return mReadableDB.query(true, RepoContract.RepoEntry.TABLE_NAME,
            RepoContract.RepoEntry.ALL_COLUMNS, null, null, null, null, null, null);
    }

    private Cursor _getRepoById(long id) {
        String whereClause = RepoContract.RepoEntry._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = mReadableDB.query(true, RepoContract.RepoEntry.TABLE_NAME,
            RepoContract.RepoEntry.ALL_COLUMNS, whereClause, whereArgs, null, null, null, null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    private void _deleteRepo(long id) {
        String whereClause = RepoContract.RepoEntry._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        mWritableDB.delete(RepoContract.RepoEntry.TABLE_NAME, whereClause, whereArgs);
        notifyObservers(RepoContract.RepoEntry.TABLE_NAME);
    }

    private Cursor _getCredentialById(long id) {
        String whereClause = RepoContract.RepoCredential._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        Cursor cursor = mReadableDB.query(true, RepoContract.RepoCredential.TABLE_NAME,
            RepoContract.RepoCredential.ALL_COLUMNS, whereClause, whereArgs, null, null, null,
            null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    private Cursor _queryAllCredential() {
        return mReadableDB.query(true, RepoContract.RepoCredential.TABLE_NAME,
            RepoContract.RepoEntry.ALL_COLUMNS, null, null, null, null, null, null);
    }

    private void _deleteCredential(long id) {
        String whereClause = RepoContract.RepoCredential._ID + " = ?";
        String[] whereArgs = {String.valueOf(id)};
        mWritableDB.delete(RepoContract.RepoCredential.TABLE_NAME, whereClause, whereArgs);
    }

    public interface RepoDbObserver {
        void notifyChanged();
    }

}
