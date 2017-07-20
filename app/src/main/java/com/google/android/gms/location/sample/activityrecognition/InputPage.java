package com.google.android.gms.location.sample.activityrecognition;


        import android.app.AlertDialog;
        import android.content.Intent;
        import android.database.Cursor;

        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ScrollView;
        import android.widget.Toast;

        import com.jjoe64.graphview.GraphView;
        import com.jjoe64.graphview.series.BarGraphSeries;
        import com.jjoe64.graphview.series.DataPoint;

        import java.util.ArrayList;
        import java.util.List;


public class InputPage extends ActionBarActivity {
    final static String LOGG = "InputPage";
    DatabaseHelper myDb;
    EditText editName, editSurname, editWeight, editCarMake, editElCons, editTextId;
    Button btnAddData, btnViewAll, btnDelete, btnGraph, btnDistance;


    Button btnviewUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_page);
        myDb = new DatabaseHelper(this);

        // EditText
        editName = (EditText)findViewById(R.id.editTextName);
        editSurname = (EditText)findViewById(R.id.editTextSurname);
        editWeight = (EditText)findViewById(R.id.editTextWeight);
        editCarMake = (EditText)findViewById(R.id.editTextCarMake);
        editElCons = (EditText)findViewById(R.id.editTextElCons);
        // må bruke i delete:
        // editTextId = (EditText)findViewById(R.id.editText_id);

        // Btn
        btnAddData = (Button)findViewById(R.id.btnAdd);
        btnViewAll = (Button)findViewById(R.id.btnViewAll);
        btnviewUpdate = (Button)findViewById(R.id.btnUpdate);
        btnDelete = (Button)findViewById(R.id.btnDelete);
        btnGraph = (Button)findViewById(R.id.btnGraph);
        btnDistance = (Button)findViewById(R.id.btnDistance);

        // Methods
        AddData();
        viewAll();
        UpdateData();
        DeleteData();
        displayGraph();

    }


    public void DeleteData() {
        btnDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer deletedRows = myDb.deleteData(editName.getText().toString());
                        if(deletedRows > 0)
                            Toast.makeText(InputPage.this,"Data Deleted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(InputPage.this,"Data not Deleted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public void UpdateData() {
        btnviewUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isUpdate = myDb.updateData(
                                editName.getText().toString(),
                                editSurname.getText().toString(),
                                editWeight.getText().toString(),
                                editCarMake.getText().toString(),
                                editElCons.getText().toString());
                        if(isUpdate == true)
                            Toast.makeText(InputPage.this,"Data Update",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(InputPage.this,"Data not Updated",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    public  void AddData() {
        btnAddData.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isInserted = myDb.insertData(editName.getText().toString(),
                                editSurname.getText().toString(),
                                editWeight.getText().toString(),
                                editCarMake.getText().toString(),
                                editElCons.getText().toString());
                        if(isInserted == true)
                            Toast.makeText(InputPage.this,"Data Inserted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(InputPage.this,"Data not Inserted",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void viewAll() {
        btnViewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("Name: "+ res.getString(0)+"\n");
                            buffer.append("Surname: "+ res.getString(1)+"\n");
                            buffer.append("Weight: "+ res.getString(2)+"\n");
                            buffer.append("Car make: "+ res.getString(3)+"\n");
                            buffer.append("El. cons.: "+ res.getString(4)+"\n\n");
                        }

                        // Show all data
                        showMessage("Data",buffer.toString());
                    }
                }
        );
    }
    /**
     * This is a method description written by Magnus
     * @param title is the title of the message
     *
     * @see google
     */
    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void displayGraph(){

        btnGraph.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor dist = myDb.getDistance();
                        if(dist.getCount() == 0) {
                            // show message
                            showMessage("Error","Nothing found");
                            return;
                        }

                        // Getting the datapoints for the graph
                        GraphPoints gp = new GraphPoints(dist);
                        double maxY = gp.getMaxY();
                        DataPoint[] dp = gp.generatePoints();

                        setContentView(R.layout.graph_test);


                        StackBarChart stackBarChart = (StackBarChart) findViewById(R.id.chart);


                        List<ChartData> value = new ArrayList<>();

                        Float[] value1 = {2f,3f,6f,5f,4f,4f,6f };
                        Float[] value2 = {1f,1f,1f,1f,1f,1f,9f };
                        Float[] value3 = {3f,5f,7f,9f,4f,4f,6f };

                        String barColor1 = "#00ff00";
                        String barColor2 = "#4f8714";
                        String barColor3 = "#875c14";

                        String labelText1 = "Walking";
                        String labelText2 = "Cycling";
                        String labelText3 = "Driving";


                        value.add(new ChartData(value1, labelText1, barColor1));
                        value.add(new ChartData(value2, labelText2, barColor2));
                        value.add(new ChartData(value3, labelText3, barColor3));

                        List<String> h_lables = new ArrayList<>();
                        h_lables.add("sun");
                        h_lables.add("mon");
                        h_lables.add("tue");
                        h_lables.add("wed");
                        h_lables.add("thurs");
                        h_lables.add("fri");
                        h_lables.add("sat");

                        stackBarChart.setHorizontal_label(h_lables);
                        stackBarChart.setBarIndent(50);
                        Log.i(LOGG, "før setData");

                        stackBarChart.setData(value);

                       // stackBarChart.setDescription("Travel distance");


                        StackedBarLable labelOrganizer = (StackedBarLable) findViewById(R.id.labelStackedBar);
                        // Set color on labels
                        labelOrganizer.setColorLabels(barColor1);
                        labelOrganizer.setColorLabels(barColor2);
                        labelOrganizer.setColorLabels(barColor3);


                        // Set label text
                        labelOrganizer.setLabelText(labelText1);
                        labelOrganizer.setLabelText(labelText2);
                        labelOrganizer.setLabelText(labelText3);

                        Yaxis yaxsis = (Yaxis) findViewById(R.id.y_axis);
                        yaxsis.setBorder(60);
                        yaxsis.setFirstValueSet(value);
                        yaxsis.setFirstValueSet(value);



                    }
                }

        );

       // Yaxis yaxis = (Yaxis) findViewById(R.id.y_axis);
       // yaxis.setValues("Jepp");
        //Log.i(LOGG, holder.getValue());
    }



    public void startDistance(View view) {
        Log.i("Still in InputPage", "Inside the startDistance");
        Intent intent = new Intent(this, DistanceTracking.class);
        startActivity(intent);

    }



/* TODO: Not needed I think. Have to something to remove title?
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

}