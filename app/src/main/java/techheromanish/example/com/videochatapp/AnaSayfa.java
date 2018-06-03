package techheromanish.example.com.videochatapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnaSayfa extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private String mUsername;
    private Firebase mRef;


    public String getData() {
        return mUsername;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_sayfa2);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.READ_PHONE_STATE},100);
        }



        // setupUsername();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://chatapp-b17f7.firebaseio.com/");
        UsersClass users = new UsersClass(LoginActivity.mUsername, LoginActivity.kullaniciMil);
        mRef.child("Users").child(LoginActivity.mUsername).setValue(users);
        mRef.child("Rations").child(LoginActivity.kullaniciMil).child(LoginActivity.mUsername).setValue(LoginActivity.mUsername);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            Intent inte = new Intent(AnaSayfa.this, KonusmaDinleme.class);
//            startActivity(inte);
//            // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//            //       .setAction("Action", null).show();
//        }
//    });

}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ana_sayfa, menu);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements ValueEventListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        private static final String ARG_SECTION_NUMBER = "section_number";
        ListView Kisiliste, AranankisiListe;
        Spinner ArananSpin;

        String[] milliyet = new String[]{"English", "Français", "Deutsch", "Türk"};
        String arananMil;
        int i;
        Firebase mRef;
        List<String> kisi = new ArrayList<String>();
        List<String> kisikod = new ArrayList<String>();
        List<String> Aranankisi = new ArrayList<String>();
        List<String> AranankisiKod = new ArrayList<String>();
        AnaSayfa activity;
        String musername;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;

            //  TextView textView = rootView.findViewById(R.id.section_label);
            // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            // my.userid

            activity = (AnaSayfa) getActivity();
            musername = activity.getData();

            i = getArguments().getInt(ARG_SECTION_NUMBER);
            if (i == 1) {
                rootView = inflater.inflate(R.layout.fragment_ana_sayfa2, container, false);
                Kisiliste = (ListView) rootView.findViewById(R.id.liste);
                //return rootView;
            }
            if (i == 2) {
                rootView = inflater.inflate(R.layout.fragment_ana_sayfa3, container, false);
                AranankisiListe = (ListView) rootView.findViewById(R.id.listesection2);
                ArananSpin = (Spinner) rootView.findViewById(R.id.spn_kullaniciarama);


            }

            Firebase.setAndroidContext(this.getActivity());
            mRef = new Firebase("https://chatapp-b17f7.firebaseio.com/");
            mRef.addValueEventListener(this);

            return rootView;

        }


        @Override
        public void onDataChange(final DataSnapshot dataSnapshot) {

            if (i == 1) {
                kisi.clear();
                //  kisikod.clear();
                for (DataSnapshot kullanici : dataSnapshot.child("chat").child(LoginActivity.mUsername).getChildren()) {


                    kisi.add(kullanici.getKey());
                    //    kisikod.add(kullanici.getValue(UsersClass.class).getKullaniciKodAd());

                }
                ArrayAdapter<String> veriAdaptoru = new ArrayAdapter<String>
                        (this.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, kisi);
                Kisiliste.setAdapter(veriAdaptoru);
                Kisiliste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                        Intent intent = new Intent(getContext(), MainActivity.class);

                        intent.putExtra("userid", kisi.get(position));
                        //  intent.putExtra("userkulad", kisi.get(position));
                        intent.putExtra("musername", LoginActivity.mUsername);
                        startActivity(intent);

                        getActivity().finish();


                        // Toast.makeText(view.getContext(), "Uçuyozz", Toast.LENGTH_LONG).show();
                    }
                });


            } else if (i == 2) {
                Aranankisi.clear();
                // AranankisiKod.clear();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.support_simple_spinner_dropdown_item, milliyet);
                ArananSpin.setAdapter(arrayAdapter);
                ArananSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        arananMil = parent.getSelectedItem().toString();
                        if (arananMil != null) {

                            Aranankisi.clear();
                            //    AranankisiKod.clear();
                            for (DataSnapshot Aranankullanici : dataSnapshot.child("Rations").child(arananMil).getChildren()) {


                                Aranankisi.add(Aranankullanici.getValue().toString());
                                //  AranankisiKod.add(Aranankullanici.getKey().toString());

                            }
                        }
                        ArrayAdapter<String> veriAdaptoru = new ArrayAdapter<String>
                                (view.getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, Aranankisi);
                        AranankisiListe.setAdapter(veriAdaptoru);
                        AranankisiListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                Intent intent = new Intent(getContext(), MainActivity.class);

                                intent.putExtra("userid", Aranankisi.get(position));
                                //   intent.putExtra("userkulad", Aranankisi.get(position));
                                intent.putExtra("musername", LoginActivity.mUsername);
                                startActivity(intent);


                                getActivity().finish();
                                // Toast.makeText(view.getContext(), "Uçuyozz", Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

        }

            @Override
            public void onCancelled (FirebaseError firebaseError){

            }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Sohbet";
                case 1:
                    return "Kişiler";

            }
            return null;
        }

    }
}
