/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.imageencryption;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 *
 * @author David Harrop
 */
public class Encrypter {
    private File outputfile;
    
    
    boolean areHashesEqual(File file, String keyHash) throws FileNotFoundException, IOException{        
        BufferedInputStream fileReader=new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
        //reading key hash from file
        StringBuffer keyHashFromFile=new StringBuffer(128);
        for(int i=0; i<128; i++)
        {
            keyHashFromFile.append((char)fileReader.read());
        }
                
        //verifying both hashes
        System.out.println("keyHashFromFile.to string()= "+keyHashFromFile);
        System.out.println("keyHash= "+keyHash);
        fileReader.close();
        if(keyHashFromFile.toString().equals(keyHash))
        {
            return true;
        }
        return false;
    }
    
    private byte[] getHashInBytes(String key) throws NoSuchAlgorithmException{
        byte[] keyHash;
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
                keyHash = md.digest(key.getBytes());
                StringBuilder sb = new StringBuilder();
                for(int i=0; i< keyHash.length ;i++)
                {
                    sb.append(Integer.toString((keyHash[i] & 0xff) + 0x100, 16).substring(1));
                }
                String hashOfPassword = sb.toString();
                System.out.println("hashOfPassword length= "+hashOfPassword.length());
                System.out.println("hashOfPassword = " +hashOfPassword);
                return hashOfPassword.getBytes();                
    }
    
    private String getHashInString(String key) throws NoSuchAlgorithmException{
        byte[] keyHash;
        final MessageDigest md = MessageDigest.getInstance("SHA-512");
                keyHash = md.digest(key.getBytes());
                StringBuilder sb = new StringBuilder();
                for(int i=0; i< keyHash.length ;i++)
                {
                    sb.append(Integer.toString((keyHash[i] & 0xff) + 0x100, 16).substring(1));
                }
                String hashOfPassword = sb.toString();
                System.out.println("hashOfPassword length= "+hashOfPassword.length());
                System.out.println("hashOfPassword = " +hashOfPassword);
                return hashOfPassword;
    }
    
    public void encrypt(File file, String password){
        byte[] keyHash;
        double percentageOfFileCopied=0;
        if(!file.isDirectory())
        {
            try
            {
                keyHash=getHashInBytes(password);
                
                outputfile=new File(file.getAbsolutePath().concat(".crypt"));
                if(outputfile.exists())
                {
                    outputfile.delete();
                    outputfile=new File(file.getAbsolutePath().concat(".crypt"));
                }
                
                BufferedInputStream fileReader=new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                FileOutputStream fileWriter=new FileOutputStream (outputfile, true);
                
                //writing key hash to file
                fileWriter.write(keyHash, 0, 128); 
                
                //encrypting content & writing
                byte[] buffer = new byte[262144];
                int bufferSize=buffer.length;
                int pwSize=password.length();
                while(fileReader.available()>0)
                {
                    int bytesCopied=fileReader.read(buffer);
                    for(int i=0,keyCounter=0; i<bufferSize; i++, keyCounter%=pwSize )
                    {
                        buffer[i]+=password.toCharArray()[keyCounter];
                    }
                    
                    fileWriter.write(buffer, 0, bytesCopied);
                    long fileLength=file.length();
                    percentageOfFileCopied+= (((double)bytesCopied/fileLength)*100);
                    
                    System.out.println("Output file length= "+outputfile.length());
                }
                fileReader.close();
                fileWriter.close();                
            } 
            catch (NoSuchAlgorithmException e)
            {
                new ExceptionUI("NoSuchAlgorithmException!", "Fatal Error 01", e).setVisible(true);
                Logger.getLogger(Encrypter.class.getName()).log(Level.SEVERE, null, e);
            }
            catch (SecurityException e)
            {
                new ExceptionUI("Security Error!", file+": action not permitted", e).setVisible(true);
            }
            catch (FileNotFoundException e)
            {
                new ExceptionUI("File Not Found!", file+": not found!", e).setVisible(true);
            }
            catch (IOException e)
            {
                new ExceptionUI("Cannot Read or Write file!", file+" can not be read or written!", e).setVisible(true);
            }
            catch (Exception e)
            {
                 new ExceptionUI("Unexpected System Error!", "Fatal Error 02", e).setVisible(true);
            }
            
        }
    }
}
