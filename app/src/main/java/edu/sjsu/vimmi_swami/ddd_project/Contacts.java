package edu.sjsu.vimmi_swami.ddd_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by VimmiRao on 10/16/2017.
 */

public class Contacts extends Activity {

    private String contactNumber1=null,contactNumber2=null,contactNumber3=null;
    private Button buttonPickContact1,buttonPickContact2,buttonPickContact3,submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        buttonPickContact1 = (Button)findViewById(R.id.pickcontact1);
        buttonPickContact2 = (Button)findViewById(R.id.pickcontact2);
        buttonPickContact3 = (Button)findViewById(R.id.pickcontact3);
        submit = (Button)findViewById(R.id.submit);

        buttonPickContact1.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });

        buttonPickContact2.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 2);
            }
        });

        buttonPickContact3.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 3);
            }
        });

        submit.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                if (contactNumber1==null||contactNumber2==null||contactNumber3==null){
                    Toast.makeText(Contacts.this, "Please choose 3 contacts!", Toast.LENGTH_SHORT).show();
                }
                else if(contactNumber1.equals(contactNumber2) || contactNumber1.equals(contactNumber3)|| contactNumber2.equals(contactNumber3)){
                    Toast.makeText(Contacts.this, "Please choose different contacts!", Toast.LENGTH_SHORT).show();

                }
                else{
                        String FILENAME = "contacts.txt";

                        File file = new File(getApplicationContext().getCacheDir(), FILENAME);
                        if(!file.exists()) {
                            try {
                                file.createNewFile();
                            }catch(Exception e){
                                Log.d("ERROR CREATNG FILE-Con",e.getMessage());
                            }
                        }
                        try {
                            Writer mwriter = new BufferedWriter(new FileWriter(file));
                            String contacts= contactNumber1+","+contactNumber2+","+contactNumber3;
                            mwriter.write(contacts);
                            mwriter.close();
                        } catch (IOException e) {
                            Log.d("Contacts Storage", e.getMessage() + e.getLocalizedMessage() + e.getCause());
                        }
                        Intent i=new Intent(Contacts.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
                Uri contactData = data.getData();
                Cursor cursor =   getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(requestCode == 1) {
                    Log.d("contact1:", number);
                    contactNumber1 = number;
                    buttonPickContact1.setText("Contact 1 Chosen!");
                    buttonPickContact1.setBackgroundColor(Color.parseColor("#ff99cc00"));
                } else if (requestCode == 2) {
                    Log.d("contact2:", number);
                    contactNumber2 = number;
                    buttonPickContact2.setText("Contact 2 Chosen!");
                    buttonPickContact2.setBackgroundColor(Color.parseColor("#ff99cc00"));
                } else if (requestCode == 3) {
                    Log.d("contact3:", number);
                    contactNumber3 = number;
                    buttonPickContact3.setText("Contact 3 Chosen!");
                    buttonPickContact3.setBackgroundColor(Color.parseColor("#ff99cc00"));
                }
        }
    }
}

