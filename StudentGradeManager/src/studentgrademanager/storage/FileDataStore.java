/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package studentgrademanager.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class FileDataStore {

    public void save(Object data, String filePath)
            throws IOException {

        if (data == null) {
            throw new IllegalArgumentException(
                    "Du lieu khong duoc null.");
        }

        File file = new File(filePath);
        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (ObjectOutputStream out =
                new ObjectOutputStream(
                        new FileOutputStream(file))) {

            out.writeObject(data);
        }
    }

    public Object load(String filePath)
            throws IOException, ClassNotFoundException {

        File file = new File(filePath);

        if (!file.exists()) {
            throw new IOException(
                    "Khong tim thay file: " + filePath);
        }

        try (ObjectInputStream in =
                new ObjectInputStream(
                        new FileInputStream(file))) {

            return in.readObject();
        }
    }

    public boolean exists(String filePath) {
        return new File(filePath).exists();
    }
}
