package saveteam.com.ridesharing.presentation.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import saveteam.com.ridesharing.R;
import saveteam.com.ridesharing.firebase.model.ProfileFB;
import saveteam.com.ridesharing.utils.activity.ActivityUtils;
import saveteam.com.ridesharing.utils.activity.SharedRefUtils;

public class ChatActivity extends AppCompatActivity {
    @BindView(R.id.rv_conversation_where_chat)
    RecyclerView rv_conversation;
    @BindView(R.id.txt_messaging_where_chat)
    EditText txt_messaging;

    List<MessageFB> messages;

    ConversationFB conversationFB;
    String uid = "";
    String roomId = "";
    ProfileFB profile = null;

    ConversationAdapter conversationAdapter;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        roomId = getIntent().getStringExtra("data");
        profile = (ProfileFB) getIntent().getSerializableExtra("profile");

        uid = SharedRefUtils.getUid(this);
        conversationFB = new ConversationFB();
        conversationFB.setRoomid(roomId);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        messages = new ArrayList<>();

        conversationAdapter = new ConversationAdapter(this, messages, uid, profile);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rv_conversation.setLayoutManager(layoutManager);
        rv_conversation.setAdapter(conversationAdapter);

        FirebaseDatabase.getInstance().getReference("channels").child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ConversationFB conversationFB = dataSnapshot.getValue(ConversationFB.class);
                if (conversationFB != null) {
                    messages.clear();
                    messages.addAll(conversationFB.getMessages());
                    conversationAdapter.notifyDataSetChanged();
                    rv_conversation.smoothScrollToPosition(messages.size() > 5 ? messages.size() - 1 : 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        GetRoomTask getRoomTask = new GetRoomTask(this, conversationFB, new GetRoomTask.GetConversationListener() {
            @Override
            public void done(ConversationFB conversation) {
                conversationFB = conversation;
                messages.clear();
                messages.addAll(conversationFB.getMessages());
                conversationAdapter.notifyDataSetChanged();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }

            @Override
            public void fail() {
                CreateRoomTask createRoomTask = new CreateRoomTask(ChatActivity.this, conversationFB, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ActivityUtils.displayToast(ChatActivity.this, "Create room successfully");
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });

                createRoomTask.execute();
            }
        });
        getRoomTask.execute();
    }

    @OnClick(R.id.iv_send_message_where_chat)
    public void clickSend(View view) {
        if (!txt_messaging.getText().toString().trim().equals("")) {
            final MessageFB msg = new MessageFB(uid, roomId, txt_messaging.getText().toString(), ActivityUtils.getNow());
            messages.add(msg);
            conversationFB.getMessages().add(msg);
            CreateRoomTask createRoomTask = new CreateRoomTask(this, conversationFB, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    ActivityUtils.displayToast(ChatActivity.this, "Add message successfully");
                }
            });
            createRoomTask.execute();
            conversationAdapter.notifyDataSetChanged();
            txt_messaging.setText("");
        }

        ActivityUtils.hideKeyboard(this, txt_messaging);
    }

    private static class CreateRoomTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private ConversationFB conversationFB;
        private OnCompleteListener<Void> listener;

        public CreateRoomTask(Context context, ConversationFB conversationFB, OnCompleteListener<Void> listener) {
            this.context = context;
            this.conversationFB = conversationFB;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("channels");
            dbRef.child(conversationFB.getRoomid()).setValue(conversationFB).addOnCompleteListener(listener);
            return null;
        }
    }

    private static class GetRoomTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private ConversationFB conversationFB;
        private GetConversationListener listener;

        public interface GetConversationListener {
            void done(ConversationFB conversationFB);
            void fail();
        }

        public GetRoomTask(Context context, ConversationFB conversationFB, GetConversationListener listener) {
            this.context = context;
            this.conversationFB = conversationFB;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("channels");
            dbRef.child(conversationFB.getRoomid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ConversationFB conversationFB = dataSnapshot.getValue(ConversationFB.class);
                    if (conversationFB != null) {
                        listener.done(conversationFB);
                    } else {
                        listener.fail();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.fail();
                }
            });
            return null;
        }
    }
}
