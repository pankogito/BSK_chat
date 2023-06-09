package aplication;

import javax.swing.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class BasicLoginListner implements LoginListner{
    @Override
    public void action(String user, char[] pass) {
        String title = "Information";
        String filePath = "hashedUsernamePassword.txt";
        File file = new File(filePath);
        try {
            if(file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String correctHashUsernamePassword = bufferedReader.readLine();
                String providedHashUsernamePassword = hashUsernamePassword(user + Arrays.toString(pass));

                if(correctHashUsernamePassword.equals(providedHashUsernamePassword)){
                    JOptionPane.showMessageDialog(null, "Login succeeded", title, JOptionPane.INFORMATION_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "Login failed", title, JOptionPane.INFORMATION_MESSAGE);
                }
            }
            else{

                    String hashUsernamePassword = hashUsernamePassword(user + Arrays.toString(pass));
                    clearPassword(pass);
                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(hashUsernamePassword);
                    bufferedWriter.close();

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
