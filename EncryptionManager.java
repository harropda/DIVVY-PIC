/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.imageencryption;

import javax.swing.SwingWorker;
import java.io.File;

/**
 *
 * @author David Harrop
 */
public class EncryptionManager extends SwingWorker <Boolean,Boolean>{
    File[] fileList;
    String password;
    Encrypter encrypter;
    
    public EncryptionManager(File[] fileList, String password)
    {
        this.fileList = fileList;
        this.password = password;
        encrypter = new Encrypter();
    }
    
    @Override
    protected Boolean doInBackground() 
    {
        try
        {
            encrypt();
        }
        catch (Exception e)
        {
              new ExceptionUI("Unexpected System Error!", "Fatal Error 03", e).setVisible(true);         
        }
        finally
        {
            return true;
        }
    }
    
    private void encrypt()
    {
        for(File file:this.fileList){
            encrypt(this.fileList);
        }
    }

    private void encrypt(File[] fileList)
    {
         for(File file:this.fileList)
        {
            if(!file.isDirectory() && file.exists())
            {
                encrypter.encrypt(file, this.password);
            } 
        }
    }
}
