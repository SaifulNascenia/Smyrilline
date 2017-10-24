package com.mcp.smyrilline.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.widget.Toast;
import com.mcp.smyrilline.R;
import com.soundcloud.android.crop.Crop;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Mario Velasco Casquero
 * Date: 08/09/2015
 */
public class ImagePicker {

    // To work with Crop class, we set to following request codes (MUST for crop action)
    public static final int REQUEST_PICK_IMAGE = Crop.REQUEST_PICK;
    public static final int REQUEST_CROP_IMAGE = Crop.REQUEST_CROP;

    private static final String TAG = "ImagePicker";
    private static final String TEMP_IMAGE_NAME = "tempImage";
    private static final String CROPPED_IMAGE_NAME = "croppedImage";

    private static boolean isCamera;
    private static Uri selectedImage;

    public static void pickImage(Activity activity) {
        Intent intent = getPickImageIntent(activity);
        activity.startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    private static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        // option choose existing image (gallery, photos, etc.)
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // option take camera photo
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));

        // add to list
        intentList = addIntentToList(context, intentList, pickIntent);
        intentList = addIntentToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                context.getString(R.string.select_image_from));
            chooserIntent
                .putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentToList(Context context, List<Intent> list,
        Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
            Log.d(TAG, "Intent: " + intent.getAction() + " package: " + packageName);
        }
        return list;
    }

    public static void beginCrop(Activity activity, int resultCode, Intent imageReturnedIntent) {
        Uri selectedImage = getImageUri(activity, resultCode, imageReturnedIntent);
        if (selectedImage != null) {
//            Uri destination = Uri
//                .fromFile(new File(activity.getExternalCacheDir(), CROPPED_IMAGE_NAME));
            Uri destination = AppUtils.getOutputMediaFileUri(AppUtils.UPLOAD_MEDIA_TYPE_IMAGE);
            if (destination == null) {
                // could not create file to save, do nothing
                AlertDialog.Builder builder = new Builder(activity);
                builder.setTitle(activity.getString(R.string.error))
                    .setMessage(activity.getString(R.string.failed_to_select_image))
                    .setNegativeButton(activity.getString(R.string.ok), new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
            } else {
                Crop.of(selectedImage, destination)
                    .withMaxSize(AppUtils.AVATAR_SIZE_PX, AppUtils.AVATAR_SIZE_PX)
                    .asSquare().start(activity);
            }
        }
    }

    private static Uri getImageUri(Context context, int resultCode, Intent imageReturnedIntent) {
        Log.d(TAG, "getImageUri, resultCode: " + resultCode);
        File imageFile = getTempFile(context);
        if (resultCode == Activity.RESULT_OK) {
            isCamera = (imageReturnedIntent == null ||
                imageReturnedIntent.getData() == null ||
                imageReturnedIntent.getData().equals(Uri.fromFile(imageFile)));
            if (isCamera) {     /** CAMERA **/
                selectedImage = Uri.fromFile(imageFile);
            } else {            /** ALBUM **/
                selectedImage = imageReturnedIntent.getData();
            }
            Log.d(TAG, "selectedImage: " + selectedImage);
        }
        return selectedImage;
    }

    public static Bitmap getImageCropped(Context context, int resultCode, Intent result,
        ResizeType resizeType, int size) {
        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            Uri croppedImageUri = Crop.getOutput(result);
            bitmap = getImageResized(context, croppedImageUri, resizeType, size);
            int rotation = getRotation(context, selectedImage, isCamera);
            bitmap = rotate(bitmap, rotation);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }

    public static Uri getImageCroppedPath(Context context, int resultCode, Intent result,
        ResizeType resizeType, int size) {
        Bitmap bitmap = null;
        Uri croppedImageUri = null;
        if (resultCode == Activity.RESULT_OK) {
            croppedImageUri = Crop.getOutput(result);
            /*bitmap = getImageResized(context, croppedImageUri, resizeType, size);
            int rotation = getRotation(context, selectedImage, isCamera);
            bitmap = rotate(bitmap, rotation);

            // save converted bitmap to same location
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(new File(croppedImageUri.getPath()));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                // bitmap is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                Log.e(AppUtils.TAG, "ImagePicker saving cropped image error! -> " + e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    Log.e(AppUtils.TAG, "ImagePicker saving cropped image error! -> " + e);
                }
            }*/
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(context, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
        return croppedImageUri;
    }

    private static File getTempFile(Context context) {
        File imageFile = new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

    /**
     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
     **/
    private static Bitmap getImageResized(Context context, Uri selectedImage,
        ResizeType resizeType, int size) {
        Bitmap bm;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            Log.d(TAG, "Resizer: sample " + sampleSizes[i] + ", new bitmap width=" +
                bm.getWidth() + " height=" + bm.getHeight());
            i++;
        } while (bm.getWidth() < size && i < sampleSizes.length);
        if (resizeType == ResizeType.FIXED_SIZE) {
            bm = Bitmap.createScaledBitmap(bm, size, size, true);
        }
        Log.d(TAG, "Resizer: finalSize, width=" + bm.getWidth() + " height=" + bm.getHeight());
        return bm;
    }

    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        Bitmap actuallyUsableBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
            if (fileDescriptor != null) {
                actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
                    fileDescriptor.getFileDescriptor(), null, options);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return actuallyUsableBitmap;
    }

    private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
        int rotation;
        if (isCamera) {
            rotation = getRotationFromCamera(context, imageUri);
        } else {
            rotation = getRotationFromGallery(context, imageUri);
        }
        Log.d(TAG, "Image rotation: " + rotation);
        return rotation;
    }

    private static int getRotationFromCamera(Context context, Uri imageFile) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageFile, null);
            ExifInterface exif = new ExifInterface(imageFile.getPath());
            int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static int getRotationFromGallery(Context context, Uri imageUri) {
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
        if (cursor == null)
            return 0;

        cursor.moveToFirst();

        int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
        return cursor.getInt(orientationColumnIndex);
    }

    private static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap bmOut = Bitmap
                .createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return bmOut;
        }
        return bm;
    }


    public enum ResizeType {
        MIN_QUALITY, FIXED_SIZE
    }
}