package com.mcp.smyrilline.fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog.Builder;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.mcp.smyrilline.R;
import com.mcp.smyrilline.model.messaging.ChatUserClientModel;
import com.mcp.smyrilline.rest.VolleySingleton;
import com.mcp.smyrilline.util.AppUtils;
import com.mcp.smyrilline.util.ImagePicker;
import com.mcp.smyrilline.util.ImagePicker.ResizeType;
import com.mcp.smyrilline.util.McpApplication;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by raqib on 1/5/16.
 */
public class ProfileDialogFragment extends DialogFragment implements OnClickListener {

    public static final String KEY_USER_DATA = "user_data";
    public static final int AVATAR_SIZE = 300;        // min pixels
    private boolean mReturningWithResult = false;
    private Uri croppedFileUri; // file url to store image/video

    private Button btnCancel, btnSave;
    private EditText editName;
    private EditText editDescription;
    private ImageButton btnCapturePhoto, btnEditDescription;
    private CircleImageView imgProfilePic;
    private String profileName, profileDescription, profilePicUrl;
    private TextView uploadTxtPercentage;
    private ProgressBar uploadProgressBar;
    private Drawable originalEditNameBackground;
    private Drawable originalEditDescriptionBackground;

    public static ProfileDialogFragment newInstance(ChatUserClientModel user) {
        ProfileDialogFragment fragment = new ProfileDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_USER_DATA, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialog_profile, container, false);

        // set the messaging_toolbar as actionbar
//        ((DrawerActivity) getActivity()).setToolbarAndToggle((Toolbar) rootView.findViewById(R.id.messaging_toolbar));
//        getActivity().setTitle(R.string.inbox);  // Set title

        // Get data from BulletinAdapter
        Bundle extras = getArguments();
        ChatUserClientModel chatUser = extras.getParcelable(KEY_USER_DATA);

        if (extras != null) {
            editName = (EditText) rootView.findViewById(R.id.editName);
            editDescription = (EditText) rootView.findViewById(R.id.editDescription);
            imgProfilePic = (CircleImageView) rootView.findViewById(R.id.imgProfilePic);
            uploadTxtPercentage = (TextView) rootView.findViewById(R.id.uploadTxtPercentage);
            uploadProgressBar = (ProgressBar) rootView.findViewById(R.id.uploadProgressBar);

            // set current data
            profileName =
                chatUser.getName() == null ? "" : chatUser.getName();
            profileDescription = chatUser.getDescription() == null ? "" : chatUser.getDescription();
            profilePicUrl = chatUser.getImageUrl();
            editName.setText(profileName);
            editName.setSelection(editName.getText().length());
            editDescription.setText(profileDescription);

            // set max character limit for name & description
            editName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                getResources().getInteger(R.integer.profile_user_name_max_char))});
            editDescription.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                getResources().getInteger(R.integer.profile_description_max_char))});

            // set max lines of description to 3
            editDescription.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (((EditText) v).getLineCount() >= 3)
                            return true;
                    }
                    return false;
                }
            });

            // set alert on max limit exceed

            editName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    int max_limit = getResources()
                        .getInteger(R.integer.profile_user_name_max_char);
                    if (s.length() > max_limit) {
                        Toast.makeText(getContext(), String
                            .format(getString(R.string.user_name_cannot_exceed_more_than),
                                max_limit), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            editDescription.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    int max_limit = getResources()
                        .getInteger(R.integer.profile_description_max_char);
                    if (s.length() > max_limit) {
                        Toast.makeText(getContext(), String
                            .format(getString(R.string.description_cannot_exceed_more_than),
                                max_limit), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            editName.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
//                        AppUtils.disableInputText(editName,
//                            ContextCompat.getDrawable(getContext(), R.color.transparent));
                    }
                }
            });

            editDescription.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
//                        AppUtils.disableInputText(editDescription,
//                            ContextCompat.getDrawable(getContext(), R.color.transparent));
                    }
                }
            });

            // preview profile image
            Picasso.with(getContext()).load(profilePicUrl).error(R.drawable.ic_user_profile)
                .placeholder(R.drawable.ic_user_profile)
                .into(imgProfilePic);

            // initially disable edit texts, save their original background (to use for enable)
            originalEditNameBackground = editName.getBackground();
            originalEditDescriptionBackground = editDescription.getBackground();
            if (McpApplication.instance().sharedPref()
                .getBoolean(AppUtils.PREF_PROFILE_VERIFIED, false)) {

                AppUtils.disableInputText(editName,
                    ContextCompat.getDrawable(getContext(), R.color.transparent));
                AppUtils.disableInputText(editDescription,
                    ContextCompat.getDrawable(getContext(), R.color.transparent));
            } else {
                AppUtils.enableInputText(editName, originalEditNameBackground);
                editName.requestFocus();
                AppUtils.enableInputText(editDescription, originalEditDescriptionBackground);
            }

            btnCapturePhoto = (ImageButton) rootView.findViewById(R.id.btnCapturePhoto);
            btnCapturePhoto.setOnClickListener(this);
            btnEditDescription = (ImageButton) rootView.findViewById(R.id.btnEditDescription);
            btnEditDescription.setOnClickListener(this);

            btnSave = (Button) rootView.findViewById(R.id.btnSave);
            btnSave.setOnClickListener(this);

            btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(this);
        }

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onResume() {
        // set size of dialog to layout file
        LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCapturePhoto:
                // Checking camera availability
                if (!AppUtils.isDeviceSupportCamera()) {
                    Toast.makeText(getContext(),
                        "Sorry! Your device doesn't support camera.",
                        Toast.LENGTH_LONG).show();
                } else {
                    // capture picture
                    ImagePicker.pickImage(getActivity());
                }
                break;
            case R.id.btnEditDescription:
                // enable input
                AppUtils.enableInputText(editName, originalEditNameBackground);
                editName.requestFocus();
                AppUtils.enableInputText(editDescription, originalEditDescriptionBackground);
                break;
            case R.id.btnSave:

                // check profile state and confirm save
                if (editName.getText().toString().trim().isEmpty() || editDescription.getText()
                    .toString().trim().isEmpty()) {    // check description
                    Builder builder = new Builder(getContext());
                    builder.setTitle(getString(R.string.invalid_info))
                        .setMessage(getString(R.string.name_description_cannot_be_empty))
                        .setPositiveButton(getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                        .show();
                } else {
                    // send upload to server
                    // TODO: 10/18/17
                    Builder builder = new Builder(getContext());
                    builder
                        .setTitle(getString(R.string.are_you_sure))
                        .setMessage(AppUtils
                            .getSpannedText(getString(R.string.you_can_review_profile_from)))
                        .setPositiveButton(getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // check if profile pic has been updated
                                    if (croppedFileUri != null) {
                                        // uploading the file to server (other data will also be saved there)
                                        uploadImageToServer(croppedFileUri.getPath());
                                    } else
                                        saveProfileDataAndShowAlert();

                                    // close this alert
                                    dialog.dismiss();
                                }
                            })
                        .setNegativeButton(getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                        .show();
                }
                break;
            case R.id.btnCancel:
                // save all data and show success alert
                saveProfileDataAndShowAlert();
                // close the profile view
                dismiss();
                break;
        }
    }

    private void saveProfileDataAndShowAlert() {
        profileName = editName.getText().toString().trim();
        profileDescription = editDescription.getText().toString().trim();

        // save name, description, image
        Editor editor = McpApplication.instance().sharedPref().edit();
        editor.putString(AppUtils.PREF_MY_NAME, profileName);
        editor.putString(AppUtils.PREF_MY_DESCRIPTION,
            profileDescription);
        editor.putString(AppUtils.PREF_MY_IMAGE_URL, profilePicUrl);

        // profile is verified
        editor.putBoolean(AppUtils.PREF_PROFILE_VERIFIED, true);
        editor.apply();

        // show success alert
        /*AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        alertDialogBuilder
            .setTitle(getString(R.string.success))
            .setMessage(getString(R.string.profile_saved))
            .setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {

                        // close profile dialog
                        dismiss();

                        // close alert
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();*/

        Toast.makeText(getContext(), getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
        // close profile
        dismiss();
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_PICK_IMAGE) {
            ImagePicker.beginCrop(getActivity(), resultCode, data);

        } else if (resultCode == RESULT_OK
            && requestCode == ImagePicker.REQUEST_CROP_IMAGE) {   // if result from crop
            croppedFileUri = ImagePicker.getImageCroppedPath(getContext(), resultCode, data,
                ResizeType.FIXED_SIZE, AVATAR_SIZE);
            Log.d(AppUtils.TAG, "Profile bitmap picked: " + croppedFileUri);
            //
//            launchUploadFragment(croppedFileUri);

            // preview the image
            /*BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 1;

            final Bitmap bitmap = BitmapFactory.decodeFile(croppedFileUri.getPath(), options);

            imgProfilePic.setImageBitmap(bitmap);*/

            Picasso.Builder builder = new Picasso.Builder(getContext());
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Log.e(AppUtils.TAG,
                        "Profile fragment - Picasso setting pic exception -> " + exception);
                }
            });
            builder.build().load(croppedFileUri).error(R.drawable.ic_user_profile)
                .placeholder(R.drawable.ic_user_profile).into(imgProfilePic);

        } else if (resultCode == RESULT_CANCELED) {
            // user cancelled Image capture
//            Toast.makeText(getContext(),
//                "User cancelled image capture", Toast.LENGTH_SHORT)
//                .show();

        } else {
            // failed to capture image
            Toast.makeText(getContext(),
                "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                .show();
        }
    }

/*
    @Override
    public void onStart() {
        super.onStart();

        // register eventbus
        McpApplication.registerWithEventBus(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        // unregister eventbus
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onImageUpdated(ProfileImageUpdate profileImageUpdate) {
        if (profileImageUpdate.imageUrl != null && !profileImageUpdate.imageUrl.trim().isEmpty()) {
            profilePicUrl = profileImageUpdate.imageUrl;

            Picasso.Builder builder = new Picasso.Builder(getContext());
            builder.listener(new Picasso.Listener()
            {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
                {
                    Log.e(AppUtils.TAG, "Profile fragment - Picasso setting pic exception -> "+exception);
                }
            });
            builder.build().load(profilePicUrl).into(imgProfilePic);
        }else{
            AppUtils.showAlertDialog(getContext(), getString(R.string.invalid_image));
        }
    }
*/

    private void uploadImageToServer(String filePath) {
        uploadTxtPercentage.setVisibility(View.VISIBLE);
        uploadProgressBar.setVisibility(View.VISIBLE);

        String imageUploadUrl =
            getString(R.string.url_signalr_root) + "/chat/api/v1/profileimageupload";
        SimpleMultiPartRequest simpleMultiPartRequest = new SimpleMultiPartRequest(
            Request.Method.POST, imageUploadUrl,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String jsonResponse) {
                    Log.d(AppUtils.TAG, "Upload image response -> " + jsonResponse);

                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        profilePicUrl = AppUtils.getStringFromJsonObject(jsonObject, "imageUrl");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // save all data and show success alert
                    saveProfileDataAndShowAlert();
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // hide upload progress and percent txt
                uploadProgressBar.setVisibility(View.GONE);
                uploadTxtPercentage.setVisibility(View.GONE);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder
                    .setTitle(getString(R.string.image_upload_failed))
                    .setMessage(getString(R.string.please_try_again))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                // save all data and show success alert
                                saveProfileDataAndShowAlert();

                                // do nothing
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };

        simpleMultiPartRequest.addFile("image", filePath);
        simpleMultiPartRequest.addMultipartParam("deviceId", "text/plain", AppUtils.getDeviceId());
        simpleMultiPartRequest.setFixedStreamingMode(true);
        simpleMultiPartRequest.setRetryPolicy(new DefaultRetryPolicy(
            5000,   // wait for 3 sec
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // no retry
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        simpleMultiPartRequest.setOnProgressListener(new Response.ProgressListener() {
            @Override
            public void onProgress(final long transferredBytes, final long totalSize) {
                final int percentage = (int) ((transferredBytes / ((float) totalSize)) * 100);

                // update UI in main thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // updating progress bar value
                        uploadProgressBar.setProgress(percentage);
                        // updating percentage value
                        uploadTxtPercentage
                            .setText(getString(R.string.uploading_image) + " " + percentage + "%");
//                txtPercentage.setText(bytes2String(transferredBytes) + "/" + bytes2String(totalSize));
                    }
                });


            }
        });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(simpleMultiPartRequest);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) (getActivity()
            .getSystemService(Activity.INPUT_METHOD_SERVICE));
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
