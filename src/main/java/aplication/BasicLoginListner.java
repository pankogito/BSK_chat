package aplication;

import cipher.AsymmetricCipher;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.util.Arrays;
import java.util.LinkedList;

public class BasicLoginListner implements LoginListner{
    private static final String RSA_ALGORITHM = "RSA";


    private UserData userKeys;

    public synchronized UserData getUser() {
        return userKeys;
    }

    @Override
    public synchronized void action(String user, char[] pass) {
        String title = "Information";
        Path filePath = Path.of("hashedUsernamePassword.txt");
        Path publicPath = Path.of("public.key");
        Path privatePath = Path.of("private.key");
        Path trustedPath = Path.of("trusted/");
        try {
            if(Files.exists(filePath) &&
                    Files.exists(privatePath) &&
                    Files.exists(publicPath) &&
                    Files.exists(trustedPath)) {
                FileReader fileReader = new FileReader(filePath.toFile());
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String correctHashUsernamePassword = bufferedReader.readLine();
                String providedHashUsernamePassword = hashUsernamePassword(user + Arrays.toString(pass));

                if(correctHashUsernamePassword.equals(providedHashUsernamePassword)){

                    var trusted = new LinkedList<AsymmetricCipher>();
                    Files.list(trustedPath).forEach(path->{
                        try {
                            trusted.add(new AsymmetricCipher(Files.readAllBytes(path)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    userKeys = new UserData( new AsymmetricCipher(
                            Files.readAllBytes(publicPath),
                            Files.readAllBytes(privatePath)),
                            trusted);

                    JOptionPane.showMessageDialog(null, "Login succeeded", title, JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Login failed", title, JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else{

                    String hashUsernamePassword = hashUsernamePassword(user + Arrays.toString(pass));
                    clearPassword(pass);
                    FileWriter fileWriter = new FileWriter(filePath.toFile());
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(hashUsernamePassword);
                    bufferedWriter.close();



                    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
                    keyPairGenerator.initialize(2048); // Key size of 2048 bits

                    KeyPair keyPair = keyPairGenerator.generateKeyPair();;
                    PublicKey publicKey = keyPair.getPublic();
                    PrivateKey privateKey = keyPair.getPrivate();

                    Files.write(privatePath,privateKey.getEncoded());
                    Files.write(publicPath,publicKey.getEncoded());

                    if(!Files.exists(trustedPath))
                        Files.createDirectory(trustedPath);

                    userKeys = new UserData(
                            new AsymmetricCipher(privateKey.getEncoded(),publicKey.getEncoded()),
                            new LinkedList<>()
                    );
                    JOptionPane.showMessageDialog(null, "New user created.", title, JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private String hashUsernamePassword(String UsernamePassword) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedPassword = md.digest(UsernamePassword.getBytes());
        return Arrays.toString(hashedPassword);
    }

    private void clearPassword(char[] password){
        Arrays.fill(password, '\0');
    }
}
