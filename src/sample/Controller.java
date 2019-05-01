package sample;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;


import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;


public class Controller {


    public File file = null;
    KeyGenerator keyGenerator = null;
    public String key = "WOW";
    public String todo = "nothing";
    SecretKey secretKey = null;
    static Cipher cipher;

    @FXML Label TargetPane;

    @FXML
    public void initialize()
    {
        TargetPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()){
                    event.acceptTransferModes(TransferMode.COPY);
                }
                else{
                    event.consume();
                }
            }
        });

        TargetPane.setOnDragDropped(new EventHandler<DragEvent>() {

            String[] t_ext = null;
            String ext = "";
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if(db.hasFiles()){
                    success=true;
                    String filePath = null;
                    for (File f:db.getFiles()){
                        filePath=f.getAbsolutePath();

                        file = new File(filePath);

                        t_ext = file.getName().split("\\.");;
                        ext = t_ext[t_ext.length-1];
                    }
                }

                //TODO Du yor now de wey?
                event.setDropCompleted(success);
                event.consume();
                if (ext.compareTo("enc")!=0) {
                    TargetPane.setText("Выбран файл для шифровки: "+file.getAbsolutePath());
                    todo="change";
                    Main.dialog.show();
                }
                else {
                    TargetPane.setText("Выбран файл для дешифровки: "+file.getAbsolutePath());
                    todo="unchange";
                    Main.dialog.show();
                }
            }
        });

        TargetPane.setOnDragEntered(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != TargetPane && event.getDragboard().hasFiles())
                {
                    TargetPane.setText("Да, именно сюда");
                    TargetPane.setStyle("-fx-border-width: 4;" +
                            "-fx-border-style: segments(15,15,15,15) line-cap butt;"+
                            "-fx-border-color: Grey;" );
                }
                event.consume();
            }
        });

        TargetPane.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {

                TargetPane.setStyle("-fx-border-width: 0;");
                event.consume();
            }
        });
        System.out.println("Initialization complete.");
    }

    private void cipher_init() throws Exception {
        keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        //secretKey = keyGenerator.generateKey();

        byte[] t_key = key.getBytes("UTF-8");
        t_key = MessageDigest.getInstance("SHA-1").digest(t_key);
        t_key = Arrays.copyOf(t_key, 16);

        secretKey = new SecretKeySpec(t_key,"AES");
        cipher = Cipher.getInstance("AES"); //don't change

    }

    private static byte[] encrypt(byte[] plainData, SecretKey secretKey) throws Exception{

        byte[] encrypted;
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        encrypted = cipher.doFinal(plainData);
        Base64.Encoder encoder = Base64.getEncoder();

        return encoder.encode(encrypted);
    }
    private static String encrypt(String plainText, SecretKey secretKey) throws Exception{

        byte[] plainTextByte = plainText.getBytes();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedByte = cipher.doFinal(plainTextByte);
        Base64.Encoder encoder = Base64.getEncoder();
        String encryptedText = encoder.encodeToString(encryptedByte);

        return encryptedText;
    }
    private static byte[] decrypt(byte[] encData, SecretKey secretKey) {

        byte[] decrypted = null;
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            decrypted = cipher.doFinal(decoder.decode(encData));

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка дешифровки");
            alert.setHeaderText("Внимение! Ошибка при попытке дешифровать файл!");
            alert.setContentText("Возможно, Вы ввели неправильную ключевую фразу(пароль)");
            alert.showAndWait();
        }

        return decrypted;
    }
    private static String decrypt(String encryptedText, SecretKey secretKey) throws Exception{
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }

    public void ChangeFile() {
        FileOutputStream fout = null;
        RandomAccessFile f = null;
        try {
            fout = new FileOutputStream(file.getParent() + "\\" + file.getName() + ".enc");
            f = new RandomAccessFile(file, "r");
            byte[] b = new byte[(int) f.length()];
            f.readFully(b);

            cipher_init();
            b = encrypt(b, secretKey);
            fout.write(b);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fout.flush();
                fout.close();
                f.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public void UnChangeFile()
    {
        FileOutputStream fout = null;
        RandomAccessFile f = null;
        try {
            fout = new FileOutputStream(file.getParent()+"\\"+file.getName().replaceAll(".enc",""));
            f = new RandomAccessFile(file,"r");
            byte[] b = new byte[(int)f.length()];
            f.readFully(b);

            cipher_init();
            b = decrypt(b,secretKey);
            fout.write(b);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        try {
            fout.flush();
            fout.close();
            f.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
}
