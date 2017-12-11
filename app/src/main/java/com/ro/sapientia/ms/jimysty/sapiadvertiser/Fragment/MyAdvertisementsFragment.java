package com.ro.sapientia.ms.jimysty.sapiadvertiser.Fragment;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.Adapter.MyRecyclerViewAdapter;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.StaticMethods;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.activity.ListScreen;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by Drako on 20-Nov-17.
 */

public class MyAdvertisementsFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener, MyRecyclerViewAdapter.LongItemClickListener{

    private static final String TAG = "MyAdvertisementsFrag";

    private static final int RESET_STATE_DELAY_MILLIS = 300;



    private MyRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    private FloatingActionButton add;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";

    protected RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mCurrentLayoutManagerType;

    private FirebaseUser currentFirebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public View rootView;
    public MyAdvertisementsFragment() {
        // Required empty public constructor
    }

    private String searchTitle = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_advertisements, container, false);

        // set up the RecyclerView
        recyclerView = rootView.findViewById(R.id.rv_list_advertisements);
        recyclerView.setItemAnimator(new SlideInUpAnimator());

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        // database references
        database = FirebaseDatabase.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if (currentFirebaseUser != null) {
            myRef = database.getReference(currentFirebaseUser.getUid());

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    loadAdvertisementsFromDatabase(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.d(TAG, error.getMessage());
                }
            });
        }
        else{
            ArrayList<String> profilPictureList = new ArrayList<>();

            ArrayList<String> titleList = new ArrayList<>();
            ArrayList<String> descriptionList = new ArrayList<>();
            ArrayList<String> imagesList = new ArrayList<>();
            ArrayList<String> id = new ArrayList<>();

            profilPictureList.add("");
            titleList.add("This will be you advertisement's title but before you have to...");
            descriptionList.add("And this will be you advertisement description but before you have to...");
            imagesList.add("https://firebasestorage.googleapis.com/v0/b/sapiadvertiser-a59e8.appspot.com/o/SignIn.jpg?alt=media&token=5ca20486-0547-45c1-a93d-2de792528aa5");
            id.add("");
            adapter = new MyRecyclerViewAdapter(rootView.getContext(), profilPictureList, titleList, descriptionList, imagesList, id);
            adapter.setClickListener(MyAdvertisementsFragment.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        add = rootView.findViewById(R.id.fbt_add_event);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFirebaseUser != null){
                    StaticMethods.goToCreateNewAdvertisementActivity(getActivity());
                    //StaticMethods.goToProfile(getActivity());
                } else{
                    dialogIfUserWantToLogin();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 ||dy<0 && add.isShown())
                    add.hide();
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    add.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        return rootView;
    }
    public void setSearchText(Bundle msg){
        if (msg == null){
            searchTitle = null;
        }
        else {
            searchTitle = msg.getString("Search");
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
    private void dialogIfUserWantToLogin(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set title
        alertDialogBuilder.setTitle("Click Yes to Log In");
        // set dialog message
        alertDialogBuilder
                .setMessage("To add a new advertisement you have to be logged in!")
                .setCancelable(false)
                .setPositiveButton("Go to login",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        StaticMethods.goToLoginScreenActivity(getActivity());
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Stay here",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        adapter.isClickable = true;
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    private void loadAdvertisementsFromDatabase(DataSnapshot dataSnapshot) {

        ArrayList<String> profilPictureList = new ArrayList<>();
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> descriptionList = new ArrayList<>();
        ArrayList<String> imagesList = new ArrayList<>();
        ArrayList<String> id = new ArrayList<>();
        try {
            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                if ( searchTitle == null) {

                    String profilPicture = (String) messageSnapshot.child("googleUser").child("image").getValue();
                    String title = (String) messageSnapshot.child("title").getValue();
                    String description = (String) messageSnapshot.child("description").getValue();
                    String sId = (String) messageSnapshot.child("id").getValue();

                    ArrayList<String> images = (ArrayList<String>) messageSnapshot.child("images").getValue();

                    if (sId != null) {
                        profilPictureList.add(profilPicture);
                        titleList.add(title);
                        descriptionList.add(description);
                        imagesList.add(images.get(0));
                        id.add(sId);
                    }
                }
                else{
                    String profilPicture = (String) messageSnapshot.child("googleUser").child("image").getValue();
                    String title = (String) messageSnapshot.child("title").getValue();

                    String description = (String) messageSnapshot.child("description").getValue();
                    String sId = (String) messageSnapshot.child("id").getValue();

                    ArrayList<String> images = (ArrayList<String>) messageSnapshot.child("images").getValue();

                    if ((sId != null) && (title.toLowerCase().contains(searchTitle.toLowerCase()))) {
                        profilPictureList.add(profilPicture);
                        titleList.add(title);
                        descriptionList.add(description);
                        imagesList.add(images.get(0));
                        id.add(sId);
                    }
                }
            }
            adapter = new MyRecyclerViewAdapter(rootView.getContext(), profilPictureList, titleList, descriptionList, imagesList, id);
            adapter.setClickListener(MyAdvertisementsFragment.this);
            adapter.setLongClickListener(MyAdvertisementsFragment.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.d(TAG , e.getMessage());
        }
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (adapter.isClickable) {
            adapter.isClickable = false;
            if (currentFirebaseUser != null) {
                //if (adapter.isClickable) {
                    //adapter.isClickable = false;
                    view.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.button_click_animation));
                    StaticMethods.goToTellAboutAdvertisementActivity(rootView.getContext(), adapter.getItem(position));
                //}
            }
            else {
                view.startAnimation(AnimationUtils.loadAnimation(rootView.getContext(), R.anim.button_click_animation));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogIfUserWantToLogin();
                    }
                }, RESET_STATE_DELAY_MILLIS);
            }
        }
    }
    @Override
    public boolean onLongItemClick(View view, final int position) {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        if (currentFirebaseUser != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            // set title
            alertDialogBuilder.setTitle("Delete Advertisement");
            // set dialog message
            alertDialogBuilder
                    .setMessage("Are you sure you want to delete this advertisement?")
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, close
                            // current activity
                            database = FirebaseDatabase.getInstance();
                            database.getReference(currentFirebaseUser.getUid()).child(adapter.getItem(position)).removeValue();
                            database.getReference("Advertisements").child(adapter.getItem(position)).removeValue();
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.detach(MyAdvertisementsFragment.this).attach(MyAdvertisementsFragment.this).commit();
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            adapter.isClickable = true;
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.isClickable = true;
        }
    }
}
