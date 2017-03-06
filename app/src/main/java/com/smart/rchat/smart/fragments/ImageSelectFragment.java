package com.smart.rchat.smart.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart.rchat.smart.R;
import com.smart.rchat.smart.util.AppUtil;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

/**
 * Created by nishant on 22.02.17.
 */

public class ImageSelectFragment extends DialogFragment implements View.OnClickListener {

    private BitMapFetchListener bitMapFetchListener;

    public static final String TAG = ImageSelectFragment.class.getSimpleName();

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int PICK_IMAGE = 2;

    public static ImageSelectFragment getInstane(FragmentManager fragmentManager) {
        ImageSelectFragment imageSelectFragment = (ImageSelectFragment) fragmentManager.findFragmentByTag(TAG);
        if (imageSelectFragment == null) {
            imageSelectFragment = new ImageSelectFragment();
            imageSelectFragment.setArguments(new Bundle());
            fragmentManager.beginTransaction().add(imageSelectFragment, TAG).commit();
        }
        return imageSelectFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BitMapFetchListener) {
            bitMapFetchListener = (BitMapFetchListener) context;
        } else {
            throw new IllegalStateException("Please implement bitmapfetch listenetr");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            setShowsDialog(false);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image_select, null, false);
        view.findViewById(R.id.tvCamera).setOnClickListener(this);
        view.findViewById(R.id.tvGallery).setOnClickListener(this);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    public void getCameraImage() {
        Intent takePictureIntent = new Intent(ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
             ImageSelectFragment.this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void getStoredImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        getIntent.putExtra("return-data", true);
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        pickIntent.putExtra("return-data", true);
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        ImageSelectFragment.this.startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            bitMapFetchListener.onBitMapFetched((Bitmap) extras.get("data"));
        }
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            bitMapFetchListener.onBitMapFetched(AppUtil.getBitmapFromUri(data.getData(), getActivity()));
        }
        if(getDialog()  != null) {
            getDialog().dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCamera) {
            getCameraImage();
        } else {
            getStoredImage();
        }
    }

    public interface BitMapFetchListener {
        void onBitMapFetched(Bitmap bitmap);
    }

}
