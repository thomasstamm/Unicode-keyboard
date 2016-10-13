/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TablesGUI;

import TablesLaunch.Main;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Thomas
 */
public class IOManager_Modified_UTF8 {
    /**
     * There are currently 2 values: Mode = "UTF16" and Mode = "Modified UTF8"
     * The default value is set to "UTF16".
     * The value can be set and changed via the Mode button
     */
    static String Mode = "UTF16"; //default, value can be set via mode button

    public static void SaveFile(String AttachedFile) {

        JOptionPane.showMessageDialog(null, "Saving: " + AttachedFile, "alert", JOptionPane.ERROR_MESSAGE);

        DataOutputStream out = null;

        try {
            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(AttachedFile)));
            MyDefaultTableModel MyDefaultTableModel = Main.MainScreen.MyDefaultTableModel;

            for (int i = 0; i < MyDefaultTableModel.getRowCount(); i++) {
                for (int j = 0; j < MyDefaultTableModel.getColumnCount(); j++) {

                    String s;
                    if (MyDefaultTableModel.getValueAt(i, j) != null) {
                        s = MyDefaultTableModel.getValueAt(i, j).toString();
                        writeNextChar(out, s);
//                        out.writeUTF(s);
                    } else {
                        writeNextChar(out, "\u0000");
//                        out.writeUTF("\u0000"); //evade NullPointeExceptiontion;
//                        out.writeUTF(null);     // out.writeUTF(null) causes a NullPointerException
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOManager_Modified_UTF8.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(IOManager_Modified_UTF8.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(IOManager_Modified_UTF8.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void LoadFile(String SelectedFile) {

        DataInputStream in = null;
        MyDefaultTableModel MyDefaultTableModel = Main.MainScreen.MyDefaultTableModel;

        MyDefaultTableModel.initializeTable();

        try {

            in = new DataInputStream(new BufferedInputStream(new FileInputStream(SelectedFile)));

            while (true) {
                String s;
                s = readNextChar(in);
                if (s != null) {
                    MyDefaultTableModel.addObjectEndofTable(s);
                } else {
                    MyDefaultTableModel.addObjectEndofTable(null);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOManager_Modified_UTF8.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EOFException ex) { // EOFException is used to exit the while loop

        } catch (IOException | NullPointerException ex) {
            Logger.getLogger(IOManager_Modified_UTF8.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in != null) {  // evading derefencing possible null pointer
                    in.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(IOManager_Modified_UTF8.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static String readNextChar(DataInputStream in) throws 
            IOException, EOFException, UTFDataFormatException {
        switch (Mode) {
            case "Modified UTF8":
                System.out.println("readNextChar Modified UTF8");
                return in.readUTF();
            case "UTF16":
                System.out.println("readNextChar UTF16");
                StringBuilder sb = new StringBuilder();

                while (true) {
                    char c = in.readChar();
                    System.out.print(c);
                    if (c != FlowManager.SEPERATOR) {
                        /* \u0002 sign used as separator*/
                        sb.append(c);
                    } else {
                        return sb.toString();
                    }                  
                }
            default:
                return null;
        }
    }

    private static void writeNextChar(DataOutputStream out, String s)
            throws IOException {
        switch (Mode) {
            case "Modified UTF8":
                System.out.println("writeNextChar Modified UTF8");
                out.writeUTF(s);
            case "UTF16":
                System.out.println("writeNextChar UTF16");
                out.writeChars(s);
                out.writeChar(FlowManager.SEPERATOR); //Remark: writeChar takes an integr as input
        }

    }

}
