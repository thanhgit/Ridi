package saveteam.com.ridesharing.firebase;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import saveteam.com.ridesharing.firebase.model.ConfirmFB;
import saveteam.com.ridesharing.firebase.model.ConfirmListFB;

public class FirebaseUtils {

    /**
     * working at storage firebase
     */
    public static void uploadImageFile(String fileName, ImageView imageView, OnSuccessListener successListener ,OnFailureListener failureListener) {
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storage.child("images/" + fileName + ".jpg");

        if (((BitmapDrawable)imageView.getDrawable()).getBitmap() != null) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imgRef.putBytes(data);
            uploadTask.addOnFailureListener(failureListener).addOnSuccessListener(successListener);
        }
    }

    public static void downloadImageFile(String fileName, OnSuccessListener successListener,OnFailureListener failureListener ) {
        StorageReference httpsReference = FirebaseStorage.getInstance().getReference()
                .child("images/"+ fileName +".jpg");
        httpsReference.getBytes(1024*1024)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * working at conformtrips
     */

    public interface PutConfirmListener {
        void success();
        void fail();
    }

    public static void putConfirm(ConfirmListFB confirmFB, final PutConfirmListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(ConfirmListFB.DB_IN_FB);
        ref.child(confirmFB.getUid()).setValue(confirmFB).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.success();
                } else {
                    listener.fail();
                }
            }
        });
    }

    public interface GetConfirmListener {
        void success(ConfirmListFB confirm);
        void fail();
    }

    public static void getConfirm(String uid, final GetConfirmListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(ConfirmListFB.DB_IN_FB);
        ref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ConfirmListFB confirmFB = dataSnapshot.getValue(ConfirmListFB.class);
                if (confirmFB != null) {
                    listener.success(confirmFB);
                } else {
                    listener.fail();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.fail();
            }
        });
    }
}
