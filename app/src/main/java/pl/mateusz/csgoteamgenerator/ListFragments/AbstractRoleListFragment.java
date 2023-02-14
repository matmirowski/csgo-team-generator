package pl.mateusz.csgoteamgenerator.ListFragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import pl.mateusz.csgoteamgenerator.MyDatabaseHelper;
import pl.mateusz.csgoteamgenerator.Role;

public abstract class AbstractRoleListFragment extends ListFragment {
    SQLiteDatabase db;
    Cursor cursor;

    protected class SetupAdapterTask extends AsyncTask<Role, Void, SimpleCursorAdapter> {

        @Override
        protected SimpleCursorAdapter doInBackground(Role... role) {
            SQLiteOpenHelper helper = new MyDatabaseHelper(getActivity());
            try {
                db = helper.getReadableDatabase();
                cursor = db.query("PLAYERS",
                        new String[]{"_id", "NAME"}, "ROLE = ?",
                        new String[]{role[0].toString()},
                        null, null, null);
                return new SimpleCursorAdapter(getActivity(),
                        android.R.layout.simple_list_item_1,
                        cursor,
                        new String[]{"NAME"},
                        new int[] {android.R.id.text1},
                        0);
            } catch (SQLiteException e) {
                Log.e("ERR", "Error while setting up adapter in SniperFragment", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(SimpleCursorAdapter adapter) {
            if (adapter != null) {
                setListAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), "Database unavailable", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        db.close();
    }
}
