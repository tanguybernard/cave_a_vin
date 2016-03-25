package istic.fr.cavevin;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.LoaderManager;

public class MainActivity extends Activity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String ADDRESS = "address";

    private SimpleCursorAdapter dataAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayListView();

    }


    /**
     * Add wine to an other activity
     * @param v
     */
    public void addWine(View v){
        Intent regionEdit = new Intent(getBaseContext(), WineEdit.class);
        Bundle bundle = new Bundle();
        bundle.putString("mode", "add");
        regionEdit.putExtras(bundle);
        startActivity(regionEdit);
    }

    /**
     * Increase quantity of wine
     * @param v
     */
    public void addQuantity(View v){

        View parentRow = (View) v.getParent();

        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        System.out.println(position);

        Cursor cursor = (Cursor) listView.getItemAtPosition(position);

        String rowId =
                cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_ROWID));


        String data = "_id = '%" + rowId + "%'";

        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + rowId);


        Cursor c = getContentResolver().query(uri, null, data, null,null);

        Integer quantity =
                cursor.getInt(c.getColumnIndexOrThrow(WinesDb.KEY_QUANTITY));


        if(quantity!=null){
            ContentValues values = new ContentValues();
            values.put(WinesDb.KEY_QUANTITY, quantity + 1);

            getContentResolver().update(uri, values, null, null);

            View vv = listView.getChildAt(position);


            TextView tt = (TextView)vv.findViewById(R.id.quantity);

            tt.setText(Integer.toString(quantity + 1));


        }


    }

    /**
     * Decrease quantity of wine
     * @param v
     */
    public void subQuantity(View v){

        View parentRow = (View) v.getParent();

        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        System.out.println(position);

        Cursor cursor = (Cursor) listView.getItemAtPosition(position);

        String rowId =
                cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_ROWID));


        String data = "_id = '%" + rowId + "%'";

        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + rowId);


        Cursor c = getContentResolver().query(uri, null, data, null, null);


        Integer quantity =null;


                    quantity = cursor.getInt(c.getColumnIndexOrThrow(WinesDb.KEY_QUANTITY));



        if(quantity!=null && quantity>0){
            ContentValues values = new ContentValues();
            values.put(WinesDb.KEY_QUANTITY, quantity - 1);

            getContentResolver().update(uri, values, null, null);

            View vv = listView.getChildAt(position);

            TextView tt = (TextView)vv.findViewById(R.id.quantity);
            tt.setText(Integer.toString(quantity - 1));

        }

    }


    /**
     * Display map location
     * @param v
     */
    public void showMap(View v){
        Intent intent = new Intent(this,MapsActivity.class);
        View parentRow = (View) v.getParent();

        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);
        System.out.println(position);

        Cursor cursor = (Cursor) listView.getItemAtPosition(position);
        String address = cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_NAME));
        intent.putExtra(ADDRESS,address);
        startActivityForResult(intent, 0);
    }

    public void searchWine(View view){

        EditText ed = (EditText) findViewById(R.id.fillWine);
        String searchName = ed.getText().toString();


        String searchQuery = "name like '%" + searchName + "%'";


        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "");



        Cursor cursor = getContentResolver().query(uri, null, searchQuery, null, null);



        dataAdapter.changeCursor(cursor);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //Starts a new or restarts an existing Loader in this manager
        getLoaderManager().restartLoader(0, null, this);
    }

    private void displayListView() {


        // The desired columns to be bound
        String[] columns = new String[] {
                WinesDb.KEY_NAME,
                WinesDb.KEY_QUANTITY,
                WinesDb.KEY_REGION,
                WinesDb.KEY_YEAR,
                WinesDb.KEY_DETAILS,
                WinesDb.KEY_MATURATION,
                WinesDb.KEY_KIND
        };

        // the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.name,
                R.id.quantity,
                R.id.region,
                R.id.year,
                R.id.details,
                R.id.maturation,
                //R.id.kind

        };

        // create an adapter from the SimpleCursorAdapter
        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.wine_info,
                null,
                columns,
                to,
                0);




        // get reference to the ListView
        ListView listView = (ListView) findViewById(R.id.wineList);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);
        //Ensures a loader is initialized and active.
        getLoaderManager().initLoader(0, null, this);




        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view,
                                    int position, long id) {




                // Get the cursor, positioned to the corresponding row in the result set
                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                // display the selected region
                String regionName =
                        cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_NAME));
                Toast.makeText(getApplicationContext(),
                        regionName, Toast.LENGTH_SHORT).show();

                String rowId =
                        cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_ROWID));

                // starts a new Intent to update/delete a Wine
                // pass in row Id to create the Content URI for a single row
                Intent regionEdit = new Intent(getBaseContext(), WineEdit.class);
                Bundle bundle = new Bundle();
                bundle.putString("mode", "update");
                bundle.putString("rowId", rowId);
                regionEdit.putExtras(bundle);
                startActivity(regionEdit);

            }
        });

    }

    // This is called when a new Loader needs to be created.
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                WinesDb.KEY_ROWID,
                WinesDb.KEY_NAME,
                WinesDb.KEY_QUANTITY,
                WinesDb.KEY_REGION,
                WinesDb.KEY_YEAR,
                WinesDb.KEY_DETAILS,
                WinesDb.KEY_MATURATION,
                WinesDb.KEY_KIND
        };
        CursorLoader cursorLoader = new CursorLoader(this,
                MyContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        dataAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        dataAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}


