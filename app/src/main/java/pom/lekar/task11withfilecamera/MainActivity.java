package pom.lekar.task11withfilecamera;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    //константы(флаги) для определения активности
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    private Button btnSelect;

    private ImageView ivImage;

    private String userChooserTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivImage = (ImageView) findViewById(R.id.iv_image);
        btnSelect = (Button) findViewById(R.id.btn_select_photo);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }

    private void selectImage() {
        //массив для диалога заполнения диалога
        final CharSequence[] items = { "Take photo", "Choose From Library", "Cancel"};
//создание билдера для иницлизации диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        //задаем тему для  диалога
        builder.setTitle("Add photo!");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if(items[item].equals("Take photo"))
                {
                    //хз
                    userChooserTask = "Take Photo";

                    //метод запускающий интент с камерой
                    cameraIntent();

                }else if (items[item].equals("Choose From Library")){
                    //метод запускающий интент с запросом к галереи
                    galleryIntent();

                } else if (items[item].equals("Cancel"))
                {
                    //закрыть диалог
                    dialog.dismiss();
                }
            }
        });
        // показать созданый диалог
        builder.show();

    }

    private void galleryIntent(){
        //создание интента
        Intent intent = new Intent();
        //заем  тип контента в нашем случае все изображения любого типа
        intent.setType("image/*");
        //
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Activity.RESULT_OK)
        {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
        }else if (requestCode == REQUEST_CAMERA)
        {
            onGalleryImageResult(data);
        }
    }

    private void onGalleryImageResult(Intent data)
    {
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

        File destination = new File (Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        ivImage.setImageBitmap(bitmap);
    }

    private void onSelectFromGalleryResult(Intent data){

        Bitmap bm = null;
        if (data != null){
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        ivImage.setImageBitmap(bm);
    }
}