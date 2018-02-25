package misao.edu.soapws;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransport;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    ListView listView;
    String resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        spinner = findViewById( R.id.sp1 );
        listView = findViewById( R.id.lv1 );
//        button = findViewById( R.id.btn1 );

    }

    class MyTask extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] objects) {
         loadData();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute( o );
            parseXML();
        }
    }


    public void getCities(View view)
    {

        MyTask myTask = new MyTask();
        myTask.execute();


    }

    public void loadData()
    {
        String wsdl_url = "http://www.webservicex.com/globalweather.asmx?WSDL";
        String soap_action = "http://www.webserviceX.NET/GetCitiesByCountry";
        String name_space = "http://www.webserviceX.NET";
        String method_name = "GetCitiesByCountry";

        //soap読み込む:NEW → Module →　import.JAR →　.jarのファイルを読み込む
        //Open Module settings →　dependencies →　+ →　Module dependencies
        SoapObject soapObject = new SoapObject( name_space,method_name );
        soapObject.addProperty( "CountryName",spinner.getSelectedItem().toString() );

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11 );
        envelope.dotNet = true;

        envelope.setOutputSoapObject(soapObject);

        HttpTransportSE httpTransportSE = new HttpTransportSE(wsdl_url);



        try {
            httpTransportSE.call(soap_action,envelope);

            SoapObject obj = (SoapObject) envelope.bodyIn;
            resp = obj.getProperty(0).toString();
            Toast.makeText( this, "hgggggggggghhgh", Toast.LENGTH_SHORT ).show();

            System.out.println( resp );

            SoapObject result = (SoapObject) envelope.getResponse();

        }
        catch (Exception e)
        {
            e.printStackTrace();

        }
    }

    public void parseXML()
    {
        try
        {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        parser.parse( IOUtils.toInputStream( resp) ,new DefaultHandler(){

            ArrayList<String> list = new ArrayList<String>(  );
            String msg ="";

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                super.characters( ch, start, length );
                msg = new String( ch,start,length );
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                super.endElement( uri, localName, qName );

                if(qName.equals( "City" ))
                {
                    list.add( msg );
                }
                if(qName.equals( "NewDataSet" ))
                {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>( MainActivity.this,android.R.layout.simple_list_item_single_choice,list );
                    listView.setAdapter( adapter );
                }
            }
        } );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



}


