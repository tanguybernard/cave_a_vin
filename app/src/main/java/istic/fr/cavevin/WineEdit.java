package istic.fr.cavevin;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class WineEdit extends Activity implements View.OnClickListener {

    private Spinner regionList;
    private Spinner kindList;
    private Button save, delete;
    private String mode;
    private EditText year, name, quantity, details, maturation;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_page);

        // get the values passed to the activity from the calling activity
        // determine the mode - add, update or delete
        if (this.getIntent().getExtras() != null){
            Bundle bundle = this.getIntent().getExtras();
            mode = bundle.getString("mode");
        }

        // get references to the buttons and attach listeners
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);


        name = (EditText) findViewById(R.id.name);
        quantity = (EditText) findViewById(R.id.quantity);
        year = (EditText) findViewById(R.id.year);
        details = (EditText) findViewById(R.id.details);
        maturation = (EditText) findViewById(R.id.maturation);


        // create a dropdown for users to select various regions
        regionList = (Spinner) findViewById(R.id.regionList);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.region_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionList.setAdapter(adapter);


        kindList = (Spinner) findViewById(R.id.kindList);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.kind_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kindList.setAdapter(adapter2);

        // if in add mode disable the delete option
        if(mode.trim().equalsIgnoreCase("add")){
            delete.setEnabled(false);
        }
        // get the rowId for the specific country
        else{
            Bundle bundle = this.getIntent().getExtras();
            id = bundle.getString("rowId");
            loadCountryInfo();
        }

    }


    public boolean wineAlreadyExist(View v,String name, String year){
        View parentRow = (View) v.getParent();



        String searchQuery = "name ='"+name+"'"+" and "+ "year ='" + year + "'";




        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "");



        Cursor cr = getContentResolver().query(uri, null, searchQuery, null, "");

        String num=null;
        if(cr != null && cr.moveToFirst()){
            num = cr.getString(cr.getColumnIndex("year"));
            System.out.println(num);
        }
        cr.close();





        if(num!=null){
            return true;

        }

        return false;
    }

    public void onClick(View v) {

        // get values from the spinner and the input text fields
        String myRegion = regionList.getSelectedItem().toString();
        String myKind = kindList.getSelectedItem().toString();
        String myName = name.getText().toString();
        String myQuantity = quantity.getText().toString();
        String myDetails = details.getText().toString();
        String myMaturation = maturation.getText().toString();
        String myYear = year.getText().toString();



        // check for blanks
        if(myName.trim().equalsIgnoreCase("")){
            Toast.makeText(getBaseContext(), "ENTRER le nom du vin", Toast.LENGTH_LONG).show();
            return;
        }

        // check for blanks
        if(myYear.trim().equalsIgnoreCase("")){
            Toast.makeText(getBaseContext(), "ENTRER l'année du vin", Toast.LENGTH_LONG).show();
            return;
        }

        System.out.println(mode);

        // check for non existing wines
        if(wineAlreadyExist(v,myName,myYear)){
            if(!mode.trim().equalsIgnoreCase("update")) {
                Toast.makeText(getBaseContext(), "Le vin est déjà dans la base", Toast.LENGTH_LONG).show();
                return;
            }
        }




        switch (v.getId()) {
            case R.id.save:
                ContentValues values = new ContentValues();
                values.put(WinesDb.KEY_NAME, myName);
                values.put(WinesDb.KEY_QUANTITY, myQuantity);
                values.put(WinesDb.KEY_REGION, myRegion);
                values.put(WinesDb.KEY_YEAR, myYear);
                values.put(WinesDb.KEY_DETAILS, myDetails);
                values.put(WinesDb.KEY_MATURATION, myMaturation);
                values.put(WinesDb.KEY_KIND, myKind);

                // insert a record
                if(mode.trim().equalsIgnoreCase("add")){
                    getContentResolver().insert(MyContentProvider.CONTENT_URI, values);
                }
                // update a record
                else {
                    Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + id);
                    getContentResolver().update(uri, values, null, null);
                }
                finish();
                break;

            case R.id.delete:
                // delete a record
                Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + id);
                getContentResolver().delete(uri, null, null);
                finish();
                break;

            // More buttons go here (if any) ...

        }
    }

    // based on the rowId get all information from the Content Provider
    // about that country
    private void loadCountryInfo(){

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
        Uri uri = Uri.parse(MyContentProvider.CONTENT_URI + "/" + id);
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String myName = cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_NAME));
            String myRegion = cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_REGION));
            String myQuantity = cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_QUANTITY));
            String myYear = cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_YEAR));
            String myMaturation = cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_MATURATION));
            String myDetails = cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_DETAILS));
            String myKind = cursor.getString(cursor.getColumnIndexOrThrow(WinesDb.KEY_KIND));

            name.setText(myName);
            quantity.setText(myQuantity);
            maturation.setText(myMaturation);
            year.setText(myYear);
            details.setText(myDetails);
            regionList.setSelection(getIndex(regionList, myRegion));
            kindList.setSelection(getIndex(kindList, myKind));
        }


    }

    // this sets the spinner selection based on the value
    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

}
