package com.wolfoxstudio.chatvara;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.GroupieViewHolder;

import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_contacts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView rv = findViewById(R.id.recycler);

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        FetchUsers();
    }

    private void FetchUsers(){
        FirebaseFirestore.getInstance().collection("/users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            Log.e("Test", error.getMessage(), error);
                            return;
                        }
                        List<DocumentSnapshot> docs = value.getDocuments();
                        for (DocumentSnapshot doc: docs){
                            User user = doc.toObject(User.class);
                            Log.d("Test", user.getUsername());
                            adapter.add(new UserItem(user));
                        }
                    }
                });
    }

    private class UserItem extends Item<GroupieViewHolder> {

        private final User user;

        private UserItem(User user){
            this.user = user;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            TextView txtUserName = viewHolder.itemView.findViewById(R.id.contact);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.avatar);

            txtUserName.setText(user.getUsername());

            Picasso.get()
                    .load(user.getProfileUrl())
                    .into(imgPhoto);
        }

        @Override
        public int getLayout() {
            return R.layout.item_user;
        }
    }
}