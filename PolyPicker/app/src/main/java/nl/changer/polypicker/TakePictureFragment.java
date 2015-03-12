package nl.changer.polypicker;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.changer.polypicker.model.Image;

/**
 * Created by kevin on 3/12/15.
 */
public class TakePictureFragment extends Fragment {

    private static int REQUEST_CODE_TAKE_PICTURE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.take_picture_fragment_layout, container, false);
        setupView(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            if (data.getExtras() != null) {
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, getPhotoFilename(), null);
                Uri contentUri = Uri.parse(path);
                ((ImagePickerActivity) getActivity()).addImage(getImageFromContentUri(contentUri));
            }
        }
    }

    public Image getImageFromContentUri(Uri contentUri) {
        String[] cols = {MediaStore.Images.Media.DATA, MediaStore.Images.ImageColumns.ORIENTATION};

        // can post image
        Cursor cursor = getActivity().getContentResolver().query(contentUri, cols, null, null, null);

        Uri uri = null;
        int orientation = -1;

        try {
            if (cursor.moveToFirst()) {
                uri = Uri.parse(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return new Image(uri, orientation);
    }

    private String getPhotoFilename() {
        String timeStamp = new SimpleDateFormat("yyyy-M-Mdd_HH-mm-ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        return imageFileName;
    }

    private void setupView(View view) {
        ImageButton button = (ImageButton)view.findViewById(R.id.takePictureButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
                }
            }
        });
    }


}
