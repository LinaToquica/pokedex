package lina.pokeapp;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StreamDownloadTask;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class PokemonSingle extends AppCompatActivity implements SensorEventListener {
    private String post_key=null;
    private ImageView image;
    private TextView nombre,tipo,genero,id_pok;
    private Button almacenar;
    private DatabaseReference database;
    private DatabaseReference databaseAtrapados;
    private ImageButton pokebolabt;
    private boolean pokedex=false;
    private FirebaseAuth mAuth;


    /**
     * Constants for sensors
     */
    private static final float SHAKE_THRESHOLD = 1.1f;
    private static final int SHAKE_WAIT_TIME_MS = 250;
    private static final float ROTATION_THRESHOLD = 2.0f;
    private static final int ROTATION_WAIT_TIME_MS = 100;


    /**
     * Sensors
     */
    private SensorManager mSensorManager;
    private Sensor mSensorAcc;
    private Sensor mSensorGyr;
    private long mShakeTime = 0;
    private long mRotationTime = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_single);

        post_key = getIntent().getExtras().getString("blog_id");

        image = (ImageView)findViewById(R.id.image_pk);
        nombre  = (TextView)findViewById(R.id.pokemon_nombre_1);
        tipo  = (TextView)findViewById(R.id.tipo);
        genero  = (TextView)findViewById(R.id.genero);
        id_pok  = (TextView)findViewById(R.id.id);
        pokebolabt = (ImageButton)findViewById(R.id.pokebolabt2);

        database = FirebaseDatabase.getInstance().getReference().child("Pokemones").child(post_key);
        databaseAtrapados = FirebaseDatabase.getInstance().getReference().child("Atrapados");
        mAuth = FirebaseAuth.getInstance();

        database.keepSynced(true);
        databaseAtrapados.keepSynced(true);

        // Get the sensors to use
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGyr = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        setatrapado(post_key);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String sub_nombre =(String) dataSnapshot.child("nombre").getValue();
                long sub_id =(long) dataSnapshot.child("id").getValue();
                String sub_imagen =(String) dataSnapshot.child("imagen").getValue();
                long sub_renderrate =(long) dataSnapshot.child("gender_rate").getValue();
                List<String> sub_tipo = (List<String>) dataSnapshot.child("tipo").getValue();

                Log.i("tipo",sub_tipo.toString());
                float rata= Float.parseFloat(String.valueOf(sub_renderrate));

                float femenino = (rata/8)*100;
                float masculino = 100-femenino;
                AssetManager am = getApplicationContext().getAssets();

                Typeface typeface = Typeface.createFromAsset(am,
                        String.format(Locale.US, "fonts/%s", "Pokemon Solid.ttf"));

                nombre.setTypeface(typeface);

                nombre.setText(sub_nombre);

                id_pok.setText(("Id: "+ sub_id+""));
                genero.setText("Masculino: "+ masculino +"% Femenino: "+femenino+"%");

                String tip = sub_tipo.toString().replace("[","");
                tip = tip.replace("]","");
                tipo.setText(tip);

                Picasso.with(PokemonSingle.this).load(sub_imagen).into(image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pokebolabt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pokedex=true;


                databaseAtrapados.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(pokedex){
                            if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                                databaseAtrapados.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                setatrapado(post_key);
                                pokedex = false;
                            }
                            else{
                                databaseAtrapados.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                setatrapado(post_key);
                                pokedex= false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    public void setatrapado(final String post_key) {

        databaseAtrapados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                    pokebolabt.setImageResource(R.mipmap.pokebolaroja);


                }else{
                    pokebolabt.setImageResource(R.mipmap.pokebola);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId()==R.id.logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener((SensorEventListener) this, mSensorAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener((SensorEventListener) this, mSensorGyr, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {



        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            detectShake(event);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            detectRotation(event);
        }

    }

    private void detectShake(SensorEvent event) {

        long now = System.currentTimeMillis();

        if ((now - mShakeTime) > SHAKE_WAIT_TIME_MS) {
            mShakeTime = now;

            float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
            float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
            float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement
            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            // Change background color if gForce exceeds threshold;
            // otherwise, reset the color
            if (gForce > SHAKE_THRESHOLD) {
                pokedex=true;


                databaseAtrapados.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(pokedex){
                            if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                              //  databaseAtrapados.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                               // setatrapado(post_key);
                                //pokedex = false;
                            }
                            else{
                                databaseAtrapados.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                Toast.makeText(PokemonSingle.this,"Pokemon capturado",Toast.LENGTH_SHORT).show();
                                setatrapado(post_key);

                                pokedex= false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
    }

    private void detectRotation(SensorEvent event) {
        long now = System.currentTimeMillis();

        if ((now - mRotationTime) > ROTATION_WAIT_TIME_MS) {
            mRotationTime = now;

            // Change background color if rate of rotation around any
            // axis and in any direction exceeds threshold;
            // otherwise, reset the color
            if (Math.abs(event.values[0]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[1]) > ROTATION_THRESHOLD ||
                    Math.abs(event.values[2]) > ROTATION_THRESHOLD) {


                pokedex=true;


                databaseAtrapados.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(pokedex){
                            if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                              //  databaseAtrapados.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                               // setatrapado(post_key);
                                //pokedex = false;
                            }
                            else{
                                databaseAtrapados.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
                                setatrapado(post_key);
                                Toast.makeText(PokemonSingle.this,"Pokemon capturado",Toast.LENGTH_SHORT).show();
                                pokedex= false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
