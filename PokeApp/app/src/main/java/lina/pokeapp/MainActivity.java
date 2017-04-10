package lina.pokeapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mPokemonList;
    private DatabaseReference mdatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mauthlistener;


    private boolean pokedex=false;

    private DatabaseReference mdatabaseuser;
    private DatabaseReference mdatabasepokedex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mPokemonList = (RecyclerView)findViewById(R.id.pokemon_list);
        mPokemonList.setHasFixedSize(true);
        mPokemonList.setLayoutManager(new LinearLayoutManager(this));

        mdatabase = FirebaseDatabase.getInstance().getReference().child("Pokemones");
        mdatabaseuser = FirebaseDatabase.getInstance().getReference().child("Users");
        mdatabasepokedex = FirebaseDatabase.getInstance().getReference().child("Atrapados");


        mdatabase.keepSynced(true);
        mdatabaseuser.keepSynced(true);
        mdatabasepokedex.keepSynced(true);


        mAuth = FirebaseAuth.getInstance();

        mauthlistener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    Intent loginintent = new Intent(MainActivity.this,LoginActivity.class);
                    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginintent);
                }
                else {
                    //checkexist();
                }

            }
        };




    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mauthlistener);

        FirebaseRecyclerAdapter<Pokemon,PokemonViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pokemon, PokemonViewHolder>(
                Pokemon.class,
                R.layout.pokemon_row,
                PokemonViewHolder.class,
                mdatabase
        ) {
            @Override
            protected void populateViewHolder(PokemonViewHolder viewHolder, Pokemon model, int position) {

                final String postkey= getRef(position).getKey().toString();
                viewHolder.setnombre(model.getNombre(),getApplicationContext());
                viewHolder.setimage(getApplicationContext(),model.getImagen());


                viewHolder.setatrapado(postkey);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(MainActivity.this,postkey,Toast.LENGTH_LONG).show();
                        Intent singleblog = new Intent(MainActivity.this,PokemonSingle.class);
                        singleblog.putExtra("blog_id", postkey);
                        startActivity(singleblog);
                    }
                });

                viewHolder.pokebolaimg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pokedex=true;


                        mdatabasepokedex.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(pokedex){
                                    if(dataSnapshot.child(postkey).hasChild(mAuth.getCurrentUser().getUid())){
                                        mdatabasepokedex.child(postkey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        pokedex = false;
                                    }
                                    else{
                                        mdatabasepokedex.child(postkey).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");
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
        };

        mPokemonList.setAdapter(firebaseRecyclerAdapter);


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


    public static class PokemonViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton pokebolaimg;
        DatabaseReference databaseatrapado;
        FirebaseAuth mAuth;
        FirebaseAuth.AuthStateListener mauthlistener;

        public PokemonViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            pokebolaimg = (ImageButton)mView.findViewById(R.id.pokebola);
            databaseatrapado= FirebaseDatabase.getInstance().getReference().child("Atrapados");

            mAuth = FirebaseAuth.getInstance();

            databaseatrapado.keepSynced(true);


        }
        public void setnombre(String name,Context context){
            TextView nombretxt  = (TextView)mView.findViewById(R.id.nombrepokemon);
            AssetManager am = context.getApplicationContext().getAssets();

            Typeface typeface = Typeface.createFromAsset(am,
                    String.format(Locale.US, "fonts/%s", "Pokemon Solid.ttf"));

            nombretxt.setTypeface(typeface);
            nombretxt.setTextColor(Color.rgb(0,0,0));


            nombretxt.setText(name);
        }
        public void setimage(final Context ctx,final  String image){
           final ImageView imagen_pok =(ImageView)mView.findViewById(R.id.imgpokemon);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imagen_pok, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(imagen_pok);

                }
            });

        }

        public void setatrapado(final String postkey) {


                        databaseatrapado.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(mAuth.getCurrentUser() != null) {
                                    if (dataSnapshot.child(postkey).hasChild(mAuth.getCurrentUser().getUid())) {
                                        pokebolaimg.setImageResource(R.mipmap.pokebolaroja);

                                    } else {
                                        pokebolaimg.setImageResource(R.mipmap.pokebola);

                                    }
                                }
                                else {
                                        Intent intent = new Intent (mView.getContext(), LoginActivity.class);
                                        mView.getContext().startActivity(intent);
                                    }
                            }
                               /* else {
                                    Intent intent = new Intent (mView.getContext(), LoginActivity.class);
                                    mView.getContext().startActivity(intent);
                                }
                            }*/

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }







    }

    private void logout() {
        mAuth.signOut();
    }


}
