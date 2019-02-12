package saveteam.com.ridi.presentation.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MessageHolder> {

    private Context context;
    private List<MessageFB> messages;
    private String uid;
    private ProfileFB profile;

    public ConversationAdapter(Context context, List<MessageFB> messages, String uid, ProfileFB profile) {
        this.context = context;
        this.messages = messages;
        this.uid = uid;
        this.profile = profile;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item_message_where_conversation, viewGroup, false);

        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageHolder messageHolder, int i) {
        MessageFB messageFB = messages.get(i);

        if (uid.equals(messageFB.getUid())) {
            messageHolder.layout_me.setVisibility(View.VISIBLE);
            messageHolder.layout_you.setVisibility(View.GONE);
            messageHolder.tv_my_message.setText(messageFB.getMessage());
        } else {
            messageHolder.layout_you.setVisibility(View.VISIBLE);
            messageHolder.layout_me.setVisibility(View.GONE);

            messageHolder.tv_name.setText(profile.getFirstName() + " " + profile.getLastName());
            messageHolder.tv_your_message.setText(messageFB.getMessage());

            FirebaseUtils.downloadImageFile(messageFB.getUid(), new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            Glide.with(context).load(bitmap)
                                    .apply(RequestOptions.circleCropTransform())
                                    .thumbnail(0.5f)
                                    .into(messageHolder.iv_avatar);
                        }
                    },
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Glide.with(context).load(R.drawable.default_user)
                                    .apply(RequestOptions.circleCropTransform())
                                    .thumbnail(0.5f)
                                    .into(messageHolder.iv_avatar);
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_me_where_conversation)
        RelativeLayout layout_me;
        @BindView(R.id.layout_you_where_conversation)
        RelativeLayout layout_you;

        @BindView(R.id.tv_name_where_conversation)
        TextView tv_name;
        @BindView(R.id.tv_your_message_where_conversation)
        TextView tv_your_message;
        @BindView(R.id.iv_avatar_where_conversation)
        ImageView iv_avatar;

        @BindView(R.id.tv_my_message_where_conversation)
        TextView tv_my_message;

        public MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
