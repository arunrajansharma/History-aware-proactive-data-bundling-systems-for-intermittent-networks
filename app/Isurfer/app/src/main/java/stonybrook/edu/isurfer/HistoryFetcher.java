package stonybrook.edu.isurfer;
import android.content.Context;
import android.database.Cursor;
import android.provider.Browser;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class HistoryFetcher {

    public ArrayList<JSONObject> fetchHistory(Context ctx) {
        ArrayList<JSONObject> json = new ArrayList<>();
        String[] projection = new String[] {Browser.BookmarkColumns.TITLE, Browser.BookmarkColumns.URL,Browser.BookmarkColumns.DATE,
                Browser.BookmarkColumns.VISITS};

        Cursor mCur = ctx.getContentResolver().query(android.provider.Browser.BOOKMARKS_URI,
                projection, Browser.BookmarkColumns.BOOKMARK + " = 0", null, Browser.BookmarkColumns.VISITS + " DESC"
        );

        mCur.moveToFirst();
        int titleIdx = mCur.getColumnIndex(Browser.BookmarkColumns.TITLE);
        int urlIdx = mCur.getColumnIndex(Browser.BookmarkColumns.URL);
        int dateIdx = mCur.getColumnIndex(Browser.BookmarkColumns.DATE);
        int visitsIdx = mCur.getColumnIndex(Browser.BookmarkColumns.VISITS);

        while (mCur.isAfterLast() == false) {
            try {
                Date date = new Date(Long.parseLong(mCur.getString(dateIdx)));
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                format.setTimeZone(TimeZone.getTimeZone("EST"));
                String formatted = format.format(date);
                JSONObject current = new JSONObject();
                current.put("title", mCur.getString(titleIdx));
                current.put("url", mCur.getString(urlIdx));
                current.put("datetime", formatted);
                current.put("visits", mCur.getString(visitsIdx));
                json.add(current);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            mCur.moveToNext();
        }
        return json;
    }
}
