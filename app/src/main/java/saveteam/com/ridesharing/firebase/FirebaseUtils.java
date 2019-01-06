package saveteam.com.ridesharing.firebase;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class FirebaseUtils {

    public static void uploadImageFile(String fileName, ImageView imageView, OnSuccessListener successListener ,OnFailureListener failureListener) {
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        StorageReference imgRef = storage.child("images/" + fileName + ".jpg");

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(failureListener).addOnSuccessListener(successListener);
    }

    public static void downloadImageFile(String fileName, OnSuccessListener successListener,OnFailureListener failureListener ) {
        StorageReference httpsReference = FirebaseStorage.getInstance().getReference()
                .child("images/"+ fileName +".jpg");
        httpsReference.getBytes(1024*1024)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
