package saveteam.com.ridi.presentation.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import saveteam.com.ridi.R;
import saveteam.com.ridi.firebase.FirebaseUtils;
import saveteam.com.ridi.firebase.model.ProfileFB;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {
    private Context context;
    private List<ProfileFB> profiles;
    private String uid;

    public FriendAdapter(Context context, List<ProfileFB> profiles, String uid) {
        this.context = context;
        this.profiles = profiles;
        this.uid = uid;
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_friend_where_friend, viewGroup, false);
        return new FriendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FriendHolder friendHolder, int i) {
        final ProfileFB profileFB = profiles.get(i);

        FirebaseUtils.downloadImageFile(profileFB.getUid(), new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Glide.with(context).load(bitmap)
                                .apply(RequestOptions.circleCropTransform())
                                .thumbnail(0.5f)
                                .into(friendHolder.iv_avatar);
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Glide.with(context).load(R.drawable.default_user)
                                .apply(RequestOptions.circleCropTransform())
                                .thumbnail(0.5f)
                                .into(friendHolder.iv_avatar);
                    }
                });

        friendHolder.tv_name.setText(profileFB.getFirstName() +" " + profileFB.getLastName());
        friendHolder.tv_phone.setText(profileFB.getPhone());

        friendHolder.layout_item_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = uid.hashCode() + profileFB.getUid().hashCode() + "";

                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("data", roomId);
                chatIntent.putExtra("profile", profileFB );
                context.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public class FriendHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_avatar_where_item_friend)
        ImageView iv_avatar;
        @BindView(R.id.tv_name_where_item_friend)
        TextView tv_name;
        @BindView(R.id.tv_phone_where_item_friend)
        TextView tv_phone;

        @BindView(R.id.layout_where_item_friend)
        LinearLayout layout_item_friend;

        public FriendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
