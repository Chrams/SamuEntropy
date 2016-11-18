/*
 * NeuronAnimActivity.java
 *
 * Norbiron Game
 * This is a case study for creating sprites for SamuEntropy/Brainboard.
 *
 * Copyright (C) 2016, Dr. Bátfai Norbert
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Ez a program szabad szoftver; terjeszthető illetve módosítható a
 * Free Software Foundation által kiadott GNU General Public License
 * dokumentumában leírtak; akár a licenc 3-as, akár (tetszőleges) későbbi
 * változata szerint.
 *
 * Ez a program abban a reményben kerül közreadásra, hogy hasznos lesz,
 * de minden egyéb GARANCIA NÉLKÜL, az ELADHATÓSÁGRA vagy VALAMELY CÉLRA
 * VALÓ ALKALMAZHATÓSÁGRA való származtatott garanciát is beleértve.
 * További részleteket a GNU General Public License tartalmaz.
 *
 * A felhasználónak a programmal együtt meg kell kapnia a GNU General
 * Public License egy példányát; ha mégsem kapta meg, akkor
 * tekintse meg a <http://www.gnu.org/licenses/> oldalon.
 *
 * Version history:
 *
 * 0.0.1, 2013.szept.29.
 */
package batfai.samuentropy.brainboard7;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 *
 * @author nbatfai
 */
public class NeuronGameActivity extends android.app.Activity {

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    private NorbironSurfaceView nSurfaceView;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference(mUser.getUid());



    ValueEventListener dbListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            GenericTypeIndicator<java.util.ArrayList<NeuronBox>> t = new GenericTypeIndicator<ArrayList<NeuronBox>>() {};

            java.util.ArrayList<NeuronBox> nb = dataSnapshot.getValue(t);

            if(nb != null){
                nSurfaceView.nodeBoxes.clear();
                for(NeuronBox n : nb){
                    nSurfaceView.addNode(n.getType(),n.getX(),n.getY());
                }
            }


        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    @Override
    public void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        nSurfaceView = (NorbironSurfaceView)findViewById(R.id.norbironSurfaceView);

        database.addListenerForSingleValueEvent(dbListener);

        Button btn = (Button)findViewById(R.id.build_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Context context;
                context = getApplicationContext();
                android.content.Intent i = new android.content.Intent(context, NodeActivity.class);
                //context.startActivity(i);
                startActivityForResult(i,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                nSurfaceView.spawnNode(data.getExtras());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        database.setValue(nSurfaceView.nodeBoxes);
    }
}
