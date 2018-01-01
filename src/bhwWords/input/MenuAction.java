package bhwWords.input;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import bhwWords.batch.WordExport;
import bhwWords.dict.model.DictInstall;
import bhwWords.provider.WordDbHelper;
import bhwWords.provider.WordProvider;

import com.bhw1899.bhwwords.R;

public class MenuAction {
    public static final String INTENT_TEST = "bhw1899.action.test";
    public static final String INTENT_INPUT = "bhw1899.action.input";
    public static final String INTENT_EDIT = "bhw1899.action.edit";
    public static final String INTENT_WORD_EDIT = "bhw1899.action.wordedit";

    public static final int MISID_DICT_INSTALL_COMPLETE = 442;

    public static void onOptionsItemSelected(Context context, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.gotest:
            Intent intentgotest = new Intent(INTENT_TEST);
            context.startActivity(intentgotest);
            break;
        case R.id.goinput:
            Intent intentgoinput = new Intent(INTENT_INPUT);
            context.startActivity(intentgoinput);
            break;
        case R.id.goedit:
            Intent intentgoedit = new Intent(INTENT_EDIT);
            context.startActivity(intentgoedit);
            break;
        case R.id.exportlatest:
            new WordExport(context).export(WordExport.LATEST_3_DAYS);
            break;
        case R.id.exportall:
            new WordExport(context).export(WordExport.ALL);
            break;
        case R.id.importadd:
            // new WordImport(context).importAdd(false);
            Intent intent1 = new Intent();
            intent1.setAction("bhw1899.action.general_progress");
            context.startActivity(intent1);
            break;
        case R.id.importall:
            // new WordImport(context).importAll();
            break;
        case R.id.install_dict:
            Intent intent = new Intent();
            intent.setAction("bhw1899.action.dict_install");
            context.startActivity(intent);
            // initDictInBackgroundThread(context);
            break;
        case R.id.debug:
            // Toast.makeText(context, "no debug at present!",
            // Toast.LENGTH_LONG).show();
            // debug6_clearWords(context);
            // debug7_setAllFail(context);
            // debug(context);
            // debug2_delreplicatedWord(context);
            // debug3_refreshTableData(context);
            // debug4_refreshTableData(context);
            break;
        default:
            break;
        }
    }

    public static void debug7_setAllFail(Context context) {
        String conditionAll = "_id >= 0";
        ContentValues values = new ContentValues();
        values.put(WordDbHelper.TABLE_WORD.EN_PASS, 1);
        values.put(WordDbHelper.TABLE_WORD.CN_PASS, 1);
        int row = context.getContentResolver().update(WordProvider.CONTENT_URI, values,
                conditionAll, null);
        Toast.makeText(context, "debug7_setAllFail comlete. affect row:" + row, Toast.LENGTH_LONG)
                .show();
    }

    public static void debug6_clearWords(Context context) {
        String conditionAll = "_id >= 0";
        context.getContentResolver().delete(WordProvider.CONTENT_URI, conditionAll, null);
        context.getContentResolver().delete(WordProvider.DATA_URI, conditionAll, null);
        Toast.makeText(context, "debug6_clearWords comlete.", Toast.LENGTH_LONG).show();
    }

    public static void debug5_uninstallDict(Context context) {
        new DictInstall(context).uninstallDict();
        Toast.makeText(context, "uninstall dict comlete.", Toast.LENGTH_LONG).show();
    }

    public static void debug4_refreshTableData(Context context) {
        ContentValues values = new ContentValues();
        values.put(WordDbHelper.TABLE_WORD.EN_PASS, 0);
        values.put(WordDbHelper.TABLE_WORD.CN_PASS, -1);
        String sel1 = WordDbHelper.TABLE_WORD.DIRECTION + " = 0";
        int row = context.getContentResolver().update(WordProvider.CONTENT_URI, values, sel1, null);

        ContentValues values2 = new ContentValues();
        values2.put(WordDbHelper.TABLE_WORD.EN_PASS, -1);
        values2.put(WordDbHelper.TABLE_WORD.CN_PASS, 0);
        String sel2 = WordDbHelper.TABLE_WORD.DIRECTION + " = 1";
        int row2 = context.getContentResolver().update(WordProvider.CONTENT_URI, values2, sel2,
                null);

        ContentValues values3 = new ContentValues();
        values3.put(WordDbHelper.TABLE_WORD.EN_PASS, 0);
        values3.put(WordDbHelper.TABLE_WORD.CN_PASS, 0);
        String sel3 = WordDbHelper.TABLE_WORD.DIRECTION + " = 2";
        int row3 = context.getContentResolver().update(WordProvider.CONTENT_URI, values3, sel3,
                null);

        Toast.makeText(context, "affect " + row + "/" + row2 + "/" + row3 + " rows.",
                Toast.LENGTH_LONG).show();
    }

    public static void debug3_refreshTableData(Context context) {
        ContentValues values = new ContentValues();
        values.put(WordDbHelper.TABLE_WORD.EN_PASS, 0);
        values.put(WordDbHelper.TABLE_WORD.CN_PASS, 0);
        values.put(WordDbHelper.TABLE_WORD.REVIEW_FLAG, 0);
        int row = context.getContentResolver().update(WordProvider.CONTENT_URI, values, null, null);
        Toast.makeText(context, "affect " + row + " rows.", Toast.LENGTH_LONG).show();
    }

    public static void debug2_delreplicatedWord(Context context) {
        int mod = context.getContentResolver().delete(WordProvider.CONTENT_URI, "_id >= 607", null);
        Toast.makeText(context, "del records " + mod, Toast.LENGTH_LONG).show();
    }

    public static void debug(Context context) {
        Cursor cursor = context.getContentResolver().query(WordProvider.CONTENT_URI, null, null,
                null, null);
        cursor.moveToPosition(-1);
        int s = 0;
        int f = 0;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD._ID));
            int succ = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.EN_PASS));
            int fail = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.CN_PASS));
            int sum = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.REVIEW_FLAG));
            int dir = cursor.getInt(cursor.getColumnIndex(WordDbHelper.TABLE_WORD.DIRECTION));
            int ex1, ex2;
            if (dir == 2) {
                ex1 = succ;
                ex2 = sum;
            } else if (dir == 1) {
                ex1 = succ;
                ex2 = sum;
                succ = 100;
                sum = 100;
                fail = 0;
            } else if (dir == 0) {
                ex1 = 100;
                ex2 = 100;
            } else {
                f++;
                continue;
            }
            ContentValues values = new ContentValues();
            values.put(WordDbHelper.TABLE_WORD.EN_PASS, succ);
            values.put(WordDbHelper.TABLE_WORD.CN_PASS, fail);
            values.put(WordDbHelper.TABLE_WORD.REVIEW_FLAG, sum);
            values.put(WordDbHelper.TABLE_WORD.EXT1, ex1);
            values.put(WordDbHelper.TABLE_WORD.EXT2, ex2);
            Uri uri = ContentUris.withAppendedId(WordProvider.CONTENT_URI, id);
            int ret = context.getContentResolver().update(uri, values, null, null);
            if (ret > 0) {
                s++;
            } else {
                f++;
            }
        }
        String msg = "debug: update ex1,ex2, succeed: " + s + ", failed: " + f;
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void initSearchbar(Context context, Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchView == null) {
            return;
        }
        SearchManager searchManager = (SearchManager) context
                .getSystemService(Context.SEARCH_SERVICE);
        ComponentName cn = new ComponentName(context, EditActivity.class);
        SearchableInfo info = searchManager.getSearchableInfo(cn);
        if (info == null) {
            Log.e("SearchableInfo", "Fail to get search info.");
        }
        searchView.setSearchableInfo(info);
    }
}
