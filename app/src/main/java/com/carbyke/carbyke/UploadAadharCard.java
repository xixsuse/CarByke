package com.carbyke.carbyke;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadAadharCard extends Fragment {

    private View view;
    private ImageView upload_license_ib;
    private ImageButton delete_ib;
    private Uri ImageFilePath;
    private Button upload_b;

    private final static String USER_PROFILES = "user_profiles";
    private final static String IMAGES = "images";
    private final static String IMAGE_NAME = "aadhar_card.jpg";
    private FirebaseAuth mAuth;
    private SpinKitView loading, loading1;
    private NumberProgressBar numberProgressBar;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public UploadAadharCard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upload_aadhar_card, container, false);

        upload_license_ib = view.findViewById(R.id.ac_upload_license_ib);
        upload_b = view.findViewById(R.id.ac_upload_b);
        loading = view.findViewById(R.id.ac_spin_kit);
        loading1 = view.findViewById(R.id.ac_spin_kit_1);
        delete_ib = view.findViewById(R.id.ac_delete_ib);
        numberProgressBar = view.findViewById(R.id.ac_number_progress);

        mAuth = FirebaseAuth.getInstance();

        delete_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                databaseReference.child(USER_PROFILES).child(uid).child(IMAGES)
                        .child("driving_license").setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        upload_license_ib.setBackgroundResource(R.drawable.ic_upload_image);
                        delete_ib.setVisibility(View.GONE);
                        upload_b.setEnabled(false);
                        upload_b.setBackgroundColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.buttonDisabledColor));
                        Picasso.with(getActivity())
                                .load(R.drawable.ic_upload_image)
                                .into(upload_license_ib);
                    }
                });
            }
        });

        upload_license_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageToUpload();
            }
        });

        upload_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadImageFileToFirebaseStorage();
            }
        });

        checkIfImageUploaded();

        return view;

    }

    private void checkIfImageUploaded() {
        show1();
        final String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        databaseReference.child(USER_PROFILES).child(uid).child(IMAGES)
                .child("aadhar_image").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (!TextUtils.isEmpty(url)) {
                    Picasso.with(getActivity())
                            .load(url)
                            .into(upload_license_ib);
                    delete_ib.setVisibility(View.VISIBLE);
                    dismiss1();
                } else {
                    upload_license_ib.setBackgroundResource(R.drawable.ic_upload_image);
                    dismiss1();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dismiss1();
            }
        });
    }


    private boolean getExtension(File file) {
        String fileName = file.getName();
        String extension;
        try {
            extension = fileName.substring(fileName.lastIndexOf("."));
            if (extension.equals(".gif")) {
                Toast.makeText(getActivity(), "gif not supported", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "something went gone, try again", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    //    select image to upload to firebase storage
    private void selectImageToUpload() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                .setOutputCompressQuality(75)
                .setInitialCropWindowPaddingRatio(0)
                .start(Objects.requireNonNull(getActivity()), this);
    }
//    select image to upload to firebase storage


    //wait for result after selecting image
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ImageFilePath = result.getUri();
                //  File actualImage = FileUtils.getFile(getActivity(), ImageFilePath);
                //if (getExtension(actualImage)){
                upload_license_ib.setBackground(null);
                Picasso.with(getActivity())
                        .load(ImageFilePath)
                        .into(upload_license_ib);
                upload_b.setEnabled(true);
                upload_b.setBackgroundColor(Objects.requireNonNull(getActivity()).getResources().getColor(R.color.lightGreen));
//                }
//                else {
//                    ImageFilePath = null;
//                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                        .title("Failed")
                        .content(error.getLocalizedMessage())
                        .contentColorRes(R.color.black)
                        .titleColor(getResources().getColor(R.color.googleRed))
                        .titleColor(getResources().getColor(R.color.black))
                        .positiveText("Try Again")
                        .positiveColorRes(R.color.black)
                        .backgroundColor(getResources().getColor(R.color.white))
                        .icon(getResources().getDrawable(R.drawable.ic_warning))
                        .show();
            }
        }
    }
    //wait for result after selecting image

    //    upload image
    public void UploadImageFileToFirebaseStorage() {

        try {
            if (ImageFilePath != null) {

                show();
                numberProgressBar.setVisibility(View.VISIBLE);
                delete_ib.setVisibility(View.GONE);
                final String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final StorageReference imageStorageReference = storageReference.child(USER_PROFILES).child(uid)
                        .child(IMAGES).child("aadhar_image").child(IMAGE_NAME);

                imageStorageReference.putFile(ImageFilePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                imageStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String url = uri.toString();

                                        databaseReference.child(USER_PROFILES).child(uid).child(IMAGES)
                                                .child("aadhar_image")
                                                .setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                ShowMessage showMessage = new ShowMessage(getActivity());
                                                showMessage.successMessage("Success", "Driving License Image Uploaded Successfully");
                                                dismiss();
                                                numberProgressBar.setVisibility(View.GONE);
                                                delete_ib.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }
                                });

                            }
                        })
                        // If something goes wrong .
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                dismiss();
                                numberProgressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
                        // On progress change upload time.
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                int total_size = (int) taskSnapshot.getTotalByteCount();
                                int uploaded = (int) taskSnapshot.getBytesTransferred();
                                int percentage_completed;
                                percentage_completed = Math.abs(uploaded * 100 / total_size);
                                numberProgressBar.setProgress(percentage_completed);
                                if (TextUtils.equals(String.valueOf(taskSnapshot.getTotalByteCount()),
                                        String.valueOf(taskSnapshot.getBytesTransferred()))) {
                                    numberProgressBar.setProgress(100);
                                }
                            }
                        });
            }

        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
        }

    }//upload image


    private void show() {
        loading.setVisibility(View.VISIBLE);
    }

    private void dismiss() {
        loading.setVisibility(View.GONE);
    }

    private void show1() {
        loading1.setVisibility(View.VISIBLE);
    }

    private void dismiss1() {
        loading1.setVisibility(View.GONE);
    }



//    end
}

