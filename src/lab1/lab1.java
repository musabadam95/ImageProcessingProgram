/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab1;

import java.io.*;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.imageio.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.Random;

public class lab1 extends Component implements ActionListener {

    public static JTextField green;
    public static JTextField red;

    public static JTextField blue;
    public static JComboBox plusRed;
    public static JComboBox plusGreen;

    public static JComboBox plusBlue;
    DecimalFormat df2 = new DecimalFormat("0.000");
    public static JFrame f;
    public static JPanel ba;
    public static int count;
    public static ArrayList<BufferedImage> imageHistory = new ArrayList<BufferedImage>();
    public static JButton undoBtn;
    public static JButton rawBtn;
    //************************************
    // List of the options(Original, Negative); correspond to the cases:
    //************************************
    String descs[] = {
        "Original",
        "Negative", "Logs", "Power", "LUT", "bitplane", "histogram", "average", "sobelx", "weightedAverage", "4Lapi", "8Lapi", "4LapiEnhance", "8LapiEnhance", "roberts", "sobelY", "median", "Min", "Max", "MidPoint", "addNoise", "SimpleThreshold", "automatedThreshold", "ROI", "Undo",
        "addition",
        "subtraction", "multiplication", "division", "NOT", "AND", "OR", "XOR"};

    int opIndex;  //option index for 
    int lastOp;

    private BufferedImage bi, biFiltered;   // the input image saved as bi;//
    int w, h;

    public void filterImage() {

        if (opIndex == lastOp) {
            return;
        }

        lastOp = opIndex;
        switch (opIndex) {
            case 0:
                bi = imageHistory.get(0);
                /* original */
                return;
            case 1:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = ImageNegative(bi);
                /* Image Negative */
                return;
            case 2:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = logsTrans(bi);
                return;
            case 3:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = powerTrans(bi);
                return;
            case 4:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = LUTGen(bi);
                return;
            case 5:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = BitPlaneGen(bi);
                return;
            case 6:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = getHistogram(bi);
                return;
            case 7:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = imagesmooth(bi, "average");
                return;
            case 8:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = imagesharper(bi, "sobelx");
                return;
            case 9:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = imagesmooth(bi, "WeightedAverage");
                break;
            case 10:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = imagesharper(bi, "4Lapi");
                break;
            case 11:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = imagesharper(bi, "8Lapi");
                break;
            case 12:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = imagesharper(bi, "4LapiEnhance");
                break;
            case 13:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = imagesharper(bi, "8LapiEnhance");
                break;
            case 14://give new method
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = imageRobel(bi);
                break;
            case 15:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = imagesharper(bi, "sobely");
                break;
            case 16:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = statisticFilter(bi, 4);
                break;
            case 17:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = statisticFilter(bi, 0);
                break;
            case 18:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = statisticFilter(bi, 8);
                break;
            case 19:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = statisticFilterMidpoint(bi);
                break;

            case 20:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = addNoise(bi);
                break;
            case 21:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = simpleThresh(bi);
                break;
            case 22:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = complexThresh(bi);
                break;
            case 23:
                imageHistory.add(convertToBimage(convertToArray(bi)));
                bi = roiImaging(bi);
                break;
        }

    }

    public BufferedImage rawimage() {
        JFileChooser chooser = new JFileChooser();
        int choice = chooser.showOpenDialog(chooser);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File chosenFile = chooser.getSelectedFile();
        String filePath = chosenFile.getAbsolutePath();
        Path imagePath = Paths.get(filePath);
        try {
            byte[] imageBytes = Files.readAllBytes(imagePath);

            int[] imageInts = new int[imageBytes.length];

            int width = w = 512;
            int height = 512;
            int[][][] result = new int[width][height][4];

            int counterX = 0;
            int counterY = 0;
            for (int i = 0; i < imageBytes.length; i++) {
                result[counterX][counterY][0] = (int) (imageBytes[i]) & 0xff;
                result[counterX][counterY][1] = (int) (imageBytes[i]) & 0xff;
                result[counterX][counterY][2] = (int) (imageBytes[i]) & 0xff;
                result[counterX][counterY][3] = (int) (imageBytes[i]) & 0xff;

                counterX++;
                if (counterX == width) {
                    counterX = 0;
                    counterY++;
                }
            }

            BufferedImage biOG = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    int a = result[row][col][0];
                    int r = result[row][col][1];
                    int g = result[row][col][2];
                    int b = result[row][col][3];

                    int p = (a << 24) | (r << 16) | (g << 8) | b;
                    biOG.setRGB(row, col, p);
                    bi.setRGB(row, col, p);

                }
            }
            w = biOG.getWidth(null);
            h = biOG.getHeight(null);
            System.out.println(biOG.getType());
            if (biOG.getType() != BufferedImage.TYPE_INT_RGB && bi.getType() != BufferedImage.TYPE_INT_RGB) {
                BufferedImage bi2Original = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                Graphics bigOriginal = bi2Original.getGraphics();
                Graphics big = bi2.getGraphics();

                bigOriginal.drawImage(biOG, 0, 0, null);
                big.drawImage(bi, 0, 0, null);

                BufferedImage biFilteredOG = biOG = bi2Original;
                return biFiltered = bi = bi2;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public lab1() {
        try {
            JFileChooser chooser = new JFileChooser();
            int choice = chooser.showOpenDialog(chooser);
            if (choice != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File chosenFile = chooser.getSelectedFile();
            String filePath = chosenFile.getAbsolutePath();
            bi = ImageIO.read(new File(filePath));

            w = bi.getWidth();
            h = bi.getHeight();
            System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_ARGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                big.drawImage(bi2, w, w, this);
                biFiltered = bi = bi2;

            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            e.printStackTrace();

            System.exit(1);
        }

    }

    public Dimension getPreferredSize() {
        return new Dimension(w, h);
    }

    String[] getDescriptions() {
        return descs;
    }

    // Return the formats sorted alphabetically and in lower case
    public String[] getFormats() {
        String[] formats = {"bmp", "gif", "jpeg", "jpg", "png"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    public String[] getPlusMinus() {
        String[] formats = {"+", "-", "scale"};
        TreeSet<String> formatSet = new TreeSet<String>();
        for (String s : formats) {
            formatSet.add(s.toLowerCase());
        }
        return formatSet.toArray(new String[0]);
    }

    void setOpIndex(int i) {
        opIndex = i;
    }

    public void paint(Graphics g) { //  Repaint will call this function so the image will change.
        filterImage();

        g.drawImage(bi, 0, 0, null);
    }

    private static int[][][] convertToArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[][][] result = new int[width][height][4];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);
                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                result[x][y][0] = a;
                result[x][y][1] = r;
                result[x][y][2] = g;
                result[x][y][3] = b;
            }
        }
        return result;
    }

    public BufferedImage convertToBimage(int[][][] TmpArray) {

        int width = TmpArray.length;
        int height = TmpArray[0].length;

        BufferedImage tmpimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = TmpArray[x][y][0];
                int r = TmpArray[x][y][1];
                int g = TmpArray[x][y][2];
                int b = TmpArray[x][y][3];

                //set RGB value
                int p = (a << 24) | (r << 16) | (g << 8) | b;
                tmpimg.setRGB(x, y, p);

            }
        }
        return tmpimg;
    }

    public static int[][][] shiftImage(BufferedImage image) {
        int[][][] ar = convertToArray(image);
        int width = image.getWidth();
        int height = image.getHeight();
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                ar[x + 1][y + 1][0] = ar[x][y][0];  //r
                ar[x + 1][y + 1][1] = ar[x][y][1];  //r
                ar[x + 1][y + 1][2] = ar[x][y][2];  //g
                ar[x + 1][y + 1][3] = ar[x][y][3];  //b
            }
        }
        return ar;
    }

    public BufferedImage ImageNegative(BufferedImage timg) {
        int width = timg.getWidth();
        int height = timg.getHeight();

        int[][][] ImageArray = convertToArray(timg);          //  Convert the image to array

        // Image Negative Operation:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                ImageArray[x][y][1] = 255 - ImageArray[x][y][1];  //r
                ImageArray[x][y][2] = 255 - ImageArray[x][y][2];  //g
                ImageArray[x][y][3] = 255 - ImageArray[x][y][3];  //b
            }
        }

        return convertToBimage(ImageArray);  // Convert the array to BufferedImage
    }

    public BufferedImage logsTrans(BufferedImage img) {

        int width = img.getWidth();
        int height = img.getHeight();
        int newarray[][][] = convertToArray(bi);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int r = newarray[x][y][1];
                int g = newarray[x][y][2];
                int b = newarray[x][y][3];

                newarray[x][y][1] = (int) (Math.log(1 + r) * 255 / Math.log(256));

                newarray[x][y][2] = (int) (Math.log(1 + g) * 255 / Math.log(256));
                newarray[x][y][3] = (int) (Math.log(1 + b) * 255 / Math.log(256));

                //set RGB value
            }
        }
        return convertToBimage(newarray);
    }

    public BufferedImage powerTrans(BufferedImage img) {

        String a = JOptionPane.showInputDialog("add value");
        double p = Double.parseDouble(a);
        if (p > 20) {
            p = 20;
        } else if (p < 0.04) {
            p = 0.04;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        int newarray[][][] = convertToArray(bi);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int r = newarray[x][y][1];
                int g = newarray[x][y][2];
                int b = newarray[x][y][3];

                newarray[x][y][1] = (int) (Math.pow(255, 1 - p) * Math.pow(r, p));;

                newarray[x][y][2] = (int) (Math.pow(255, 1 - p) * Math.pow(r, p));
                newarray[x][y][3] = (int) (Math.pow(255, 1 - p) * Math.pow(r, p));

                //set RGB value
            }
        }
        return convertToBimage(newarray);
    }

    public BufferedImage LUTGen(BufferedImage img) {
        int r = 0;
        int g = 0;
        int b = 0;
        String a = JOptionPane.showInputDialog("add value");
        double p = Double.parseDouble(a);
        if (p > 20) {
            p = 20;
        } else if (p < 0.04) {
            p = 0.04;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        int newarray[][][] = convertToArray(bi);
        int LUT[] = new int[256];
        for (int k = 0; k <= 255; k++) {
            LUT[k] = (int) (Math.pow(255, 1 - p) * Math.pow(k, p));
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = newarray[x][y][1];
                g = newarray[x][y][2];
                b = newarray[x][y][3];
                newarray[x][y][1] = LUT[r];
                newarray[x][y][2] = LUT[g];
                newarray[x][y][3] = LUT[b];
            }
        }
        return convertToBimage(newarray);
    }

    public BufferedImage BitPlaneGen(BufferedImage img) {
        int al = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        String a = JOptionPane.showInputDialog("add value");
        int p = Integer.parseInt(a);
        if (p > 7) {
            p = 7;
        } else if (p < 0) {
            p = 0;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        int newarray[][][] = convertToArray(bi);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                r = newarray[x][y][1];
                g = newarray[x][y][2];
                b = newarray[x][y][3];
                newarray[x][y][1] = (r >> p) & 1;
                newarray[x][y][2] = (g >> p) & 1;
                newarray[x][y][3] = (b >> p) & 1;

                if (newarray[x][y][1] == 1) {
                    newarray[x][y][1] = 255;
                }
                if (newarray[x][y][2] == 1) {
                    newarray[x][y][2] = 255;
                }

                if (newarray[x][y][3] == 1) {
                    newarray[x][y][3] = 255;
                }

            }
        }
        return convertToBimage(newarray);
    }

    public void actionPerformed(ActionEvent e) {

        JComboBox cb;

        switch (e.getActionCommand()) {

            case "SetFilter":
                cb = (JComboBox) e.getSource();
                if (cb.getSelectedItem().equals("addition") || cb.getSelectedItem().equals("subtraction") || cb.getSelectedItem().equals("multiplication") || cb.getSelectedItem().equals("division")) {
                    combineImage(cb.getSelectedItem().toString());
                } else if (cb.getSelectedItem().equals("NOT")) {

                    notImage();
                } else if (cb.getSelectedItem().equals("AND") | cb.getSelectedItem().equals("OR") | cb.getSelectedItem().equals("XOR")) {
                    andImage(cb.getSelectedItem().toString());
                } else {
                    setOpIndex(cb.getSelectedIndex());
                    repaint();
                }
                repaint();
                break;
            case "Formats":
                cb = (JComboBox) e.getSource();

                String format = (String) cb.getSelectedItem();
                File saveFile = new File("savedimage." + format);
                JFileChooser chooser = new JFileChooser();
                chooser.setSelectedFile(saveFile);
                int rval = chooser.showSaveDialog(cb);
                if (rval == JFileChooser.APPROVE_OPTION) {
                    saveFile = chooser.getSelectedFile();
                    try {
                        ImageIO.write(biFiltered, format, saveFile);
                    } catch (IOException ex) {
                    }
                }
                break;
            case "changeColour":
                rescaleandshift();
                repaint();
                break;
            case "Undo":
                undoBtn.setEnabled(false);
                bi = undoImage();
                undoBtn.setEnabled(true);
                repaint();
                break;
            case "newImage":
                changeImg();
                repaint();
                break;
            case "rawImage":
                bi = rawimage();
                repaint();
        }
    }

    public BufferedImage undoImage() {

        if (imageHistory.size() <= 1) {
            System.out.println("Cant undo any further");
            return imageHistory.get(0);
        }

        imageHistory.remove(bi);

        return imageHistory.get(imageHistory.size() - 1);

    }

    public void combineImage(String arithOp) {
        imageHistory.add(bi);
        lab1 de = new lab1();
        int[][][] imagearray1 = convertToArray(de.bi);
        int[][][] imagearray2 = convertToArray(bi);
        int width = bi.getWidth();
        int height = bi.getHeight();

        int[][][] newimagearray = imagearray1;
        int a = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (arithOp) {
                    case "addition":
                        a = imagearray1[x][y][0];
                        r = imagearray1[x][y][1] + imagearray2[x][y][1];
                        g = imagearray1[x][y][2] + imagearray2[x][y][2];
                        b = imagearray1[x][y][3] + imagearray2[x][y][3];
                        break;
                    case "subtraction":
                        a = imagearray1[x][y][0];
                        r = imagearray1[x][y][1] - imagearray2[x][y][1];
                        g = imagearray1[x][y][2] - imagearray2[x][y][2];
                        b = imagearray1[x][y][3] - imagearray2[x][y][3];
                        break;
                    case "multiplication":
                        a = imagearray1[x][y][0];
                        r = imagearray1[x][y][1] * imagearray2[x][y][1];
                        g = imagearray1[x][y][2] * imagearray2[x][y][2];
                        b = imagearray1[x][y][3] * imagearray2[x][y][3];
                        break;
                    case "division":

                        a = imagearray1[x][y][0];
                        r = (int) Math.round((double) imagearray1[x][y][1] / (double) imagearray2[x][y][1]);
                        g = (int) Math.round((double) imagearray1[x][y][2] / (double) imagearray2[x][y][2]);
                        b = (int) Math.round((double) imagearray1[x][y][3] / (double) imagearray2[x][y][3]);
                }

                if (r >= 255) {
                    r = 255;
                } else if (r <= 0) {

                    r = 0;
                }

                if (g >= 255) {
                    g = 255;
                } else if (g <= 0) {
                    g = 0;
                }

                if (b >= 255) {
                    b = 255;
                } else if (b <= 0) {

                    b = 0;
                }
                imagearray2[x][y][0] = a;  //a
                imagearray2[x][y][1] = r;  //r
                imagearray2[x][y][2] = g; //g
                imagearray2[x][y][3] = b; //b
            }

        }
        bi = convertToBimage(imagearray2);
        repaint();

    }

    public void notImage() {
        int width = bi.getWidth();
        int height = bi.getHeight();
        int newarray[][][] = convertToArray(bi);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                int r = newarray[x][y][1];
                int g = newarray[x][y][2];
                int b = newarray[x][y][3];

                newarray[x][y][1] = ~r & 0xff;

                newarray[x][y][2] = ~g & 0xff;
                newarray[x][y][3] = ~b & 0xff;

                //set RGB value
            }
        }
        bi = convertToBimage(newarray);

        repaint();
    }

    public void andImage(String style) {
        lab1 de = new lab1();

        int[][][] imagearray1 = convertToArray(de.bi);
        int[][][] imagearray2 = convertToArray(bi);
        int width = de.bi.getWidth();
        int height = de.bi.getHeight();
        int[][][] newimagearray = imagearray1;
        int a = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                switch (style) {
                    case "AND":

                        r = imagearray1[x][y][1] & 0xff & imagearray2[x][y][1] & 0xff;
                        g = imagearray1[x][y][2] & 0xff & imagearray2[x][y][2] & 0xff;
                        b = imagearray1[x][y][3] & 0xff & imagearray2[x][y][3] & 0xff;
                        break;
                    case "OR":

                        r = imagearray1[x][y][1] & 0xff | imagearray2[x][y][1] & 0xff;
                        g = imagearray1[x][y][2] & 0xff | imagearray2[x][y][2] & 0xff;
                        b = imagearray1[x][y][3] & 0xff | imagearray2[x][y][3] & 0xff;
                        break;
                    case "XOR":

                        r = imagearray1[x][y][1] & 0xff ^ imagearray2[x][y][1] & 0xff;
                        g = imagearray1[x][y][2] & 0xff ^ imagearray2[x][y][2] & 0xff;
                        b = imagearray1[x][y][3] & 0xff ^ imagearray2[x][y][3] & 0xff;
                        break;
                }

                if (r >= 255) {
                    r = 255;
                } else if (r <= 0) {

                    r = 0;
                }

                if (g >= 255) {
                    g = 255;
                } else if (g <= 0) {
                    g = 0;
                }

                if (b >= 255) {
                    b = 255;
                } else if (b <= 0) {

                    b = 0;
                }

                imagearray2[x][y][1] = r;  //r
                imagearray2[x][y][2] = g; //g
                imagearray2[x][y][3] = b; //b
            }

        }

        bi = convertToBimage(imagearray2);
        repaint();
    }

    public void rescaleandshift() {

        int[][][] arrays = convertToArray(bi);
        int width = bi.getWidth();
        int height = bi.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                //maniplate red colors
                if (plusRed.getSelectedItem().equals("+")) {
                    arrays[x][y][1] = arrays[x][y][1] + Integer.parseInt(red.getText());
                    if (arrays[x][y][1] >= 255) {
                        arrays[x][y][1] = 255;
                    }

                } else if (plusRed.getSelectedItem().equals("-")) {
                    arrays[x][y][1] = arrays[x][y][1] - Integer.parseInt(red.getText());
                    if (arrays[x][y][1] <= 0) {
                        arrays[x][y][1] = 0;
                    }

                } else {

                    arrays[x][y][1] = (int) Math.round(arrays[x][y][1] * Double.parseDouble(red.getText()));

                }

                //manipulate green colours
                if (plusGreen.getSelectedItem().equals("+")) {
                    arrays[x][y][2] = arrays[x][y][2] + Integer.parseInt(green.getText());
                    if (arrays[x][y][2] >= 255) {
                        arrays[x][y][2] = 255;
                    }

                } else if (plusGreen.getSelectedItem().equals("-")) {
                    arrays[x][y][2] = arrays[x][y][2] - Integer.parseInt(green.getText());
                    if (arrays[x][y][2] <= 0) {
                        arrays[x][y][2] = 0;
                    }

                } else {

                    arrays[x][y][2] = (int) Math.round(arrays[x][y][2] * Double.parseDouble(green.getText()));

                }

                //manipulate blue colours
                if (plusBlue.getSelectedItem().equals("+")) {
                    arrays[x][y][3] = arrays[x][y][3] + Integer.parseInt(blue.getText());
                    if (arrays[x][y][3] >= 255) {
                        arrays[x][y][3] = 255;
                    }

                } else if (plusBlue.getSelectedItem().equals("-")) {
                    arrays[x][y][3] = arrays[x][y][3] - Integer.parseInt(blue.getText());
                    if (arrays[x][y][3] <= 0) {
                        arrays[x][y][3] = 0;
                    }

                } else {

                    arrays[x][y][3] = (int) Math.round(arrays[x][y][3] * Double.parseDouble(blue.getText()));

                }

            }
        }
        imageHistory.add(convertToBimage(convertToArray(bi)));
        bi = convertToBimage(arrays);

    }

    public BufferedImage getHistogram(BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        int maxPixel = width * height;
        System.out.println("Number of Pixels : " + maxPixel);
        int imgarray[][][] = convertToArray(bi);
        double HGramR[] = new double[256];
        double HGramG[] = new double[256];
        double HGramB[] = new double[256];
        double NormHGramR[] = new double[256];
        double NormHGramG[] = new double[256];
        double NormHGramB[] = new double[256];
        double EqualHGramR[] = new double[256];
        double EqualHGramG[] = new double[256];
        double EqualHGramB[] = new double[256];
        int r = 0;
        int g = 0;
        int b = 0;
        //initialise arrays
        for (int k = 0; k <= 255; k++) {
            HGramR[k] = 0;
            HGramG[k] = 0;
            HGramB[k] = 0;
            NormHGramR[k] = 0;
            NormHGramG[k] = 0;
            NormHGramB[k] = 0;
        }

//create normal histogram
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = imgarray[x][y][1];
                g = imgarray[x][y][2];
                b = imgarray[x][y][3];
                HGramR[r]++;
                HGramG[g]++;
                HGramB[b]++;
            }
        }
        DecimalFormat df2 = new DecimalFormat("0.000");
        double meanRed = 0;
        double meanGreen = 0;
        double meanBlue = 0;
        double valRed = 0;
        double valGreen = 0;
        double valBlue = 0;
        double staDevRed = 0;
        double staDevGreen = 0;
        double staDevBlue = 0;

        //
        for (int k = 0; k <= 255; k++) {

            NormHGramR[k] = HGramR[k] / maxPixel;
            NormHGramG[k] = HGramG[k] / maxPixel;
            NormHGramB[k] = HGramB[k] / maxPixel;
            valRed = valRed + HGramR[k];
            valGreen = valGreen + HGramG[k];
            valBlue = valBlue + HGramB[k];

            meanRed = meanRed + (HGramR[k] * k);
            meanGreen = meanGreen + (HGramG[k] * k);
            meanBlue = meanBlue + (HGramB[k] * k);

            staDevRed = staDevRed + ((k * k) * HGramR[k]);
            staDevGreen = staDevGreen + ((k * k) * HGramG[k]);
            staDevBlue = staDevBlue + ((k * k) * HGramB[k]);
        }
        System.out.println("mean of red histogram :" + df2.format((meanRed / valRed)));
        System.out.println("mean of green histogram :" + df2.format((meanGreen / valGreen)));
        System.out.println("mean of blue histogram :" + df2.format((meanBlue / valBlue)));

        staDevRed = (staDevRed - ((meanRed * meanRed) / valRed)) / (valRed - 1.0);
        staDevGreen = (staDevGreen - ((meanGreen * meanGreen) / valGreen)) / (valGreen - 1.0);
        staDevBlue = (staDevBlue - ((meanBlue * meanBlue) / valBlue)) / (valBlue - 1.0);

        System.out.println("Red Standard Deviation: " + df2.format(Math.sqrt(staDevRed)));
        System.out.println("Green Standard Deviation: " + df2.format(Math.sqrt(staDevGreen)));
        System.out.println("Blue Standard Deviation: " + df2.format(Math.sqrt(staDevBlue)));

        System.out.println(Arrays.toString(HGramR));
        System.out.println(Arrays.toString(HGramG));
        System.out.println(Arrays.toString(HGramB));

        for (int k = 0; k <= 255; k++) {
            System.out.print(df2.format(NormHGramR[k]) + " ");
        }
        System.out.println(" ");
        for (int k = 0; k <= 255; k++) {
            System.out.print(df2.format(NormHGramG[k]) + " ");
        }
        System.out.println(" ");
        for (int k = 0; k <= 255; k++) {

            System.out.print(df2.format(NormHGramB[k]) + " ");
        }

        for (int k = 0; k <= 255; k++) {

            NormHGramR[k] = HGramR[k] / maxPixel;

            NormHGramG[k] = HGramG[k] / maxPixel;
            NormHGramB[k] = HGramB[k] / maxPixel;
        }

        //histogram equalisation
        double cumulativeR = 0;
        double cumulativeG = 0;
        double cumulativeB = 0;
        for (int k = 0; k <= 255; k++) {
            cumulativeR += NormHGramR[k];
            cumulativeG += NormHGramG[k];
            cumulativeB += NormHGramB[k];
            EqualHGramR[k] = cumulativeR * 255;
            EqualHGramG[k] = cumulativeG * 255;
            EqualHGramB[k] = cumulativeB * 255;

        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                imgarray[x][y][1] = (int) EqualHGramR[imgarray[x][y][1]];
                imgarray[x][y][2] = (int) EqualHGramG[imgarray[x][y][2]];
                imgarray[x][y][3] = (int) EqualHGramB[imgarray[x][y][3]];

            }
        }
        return convertToBimage(imgarray);

    }

    public BufferedImage roiImaging(BufferedImage bi) {

        int minY = Integer.parseInt(JOptionPane.showInputDialog("Enter starting Y value from " + bi.getHeight()));
        int rangeY = Integer.parseInt(JOptionPane.showInputDialog("Enter Y range value from " + bi.getHeight()));
        int maxY = minY + rangeY;
        int minX = Integer.parseInt(JOptionPane.showInputDialog("Enter starting x value from " + bi.getWidth()));
        int rangeX = Integer.parseInt(JOptionPane.showInputDialog("Enter  x range value from " + bi.getWidth()));
        int maxX = minX + rangeX;
        int[][][] imagearray2 = convertToArray(bi);
        int width = bi.getWidth();
        int height = bi.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x >= minX && x <= maxX && y >= minY && y <= maxY) {

                } else {
                    imagearray2[x][y][0] = 0;
                }
            }

        }
        return (convertToBimage(imagearray2));

    }

    public BufferedImage imagesmooth(BufferedImage bi, String maskType) {
        int[][][] ImageArray1 = convertToArray(bi);
        int a, r, g, b;
        double o = 0.111111111;
        int width = bi.getWidth();
        int height = bi.getHeight();
        int[][][] ImageArray2 = new int[width][height][4];
        double[][] Mask = new double[3][3];
        for (int y = 0; y < Mask.length; y++) {
            Mask[y][0] = 1.0;
            Mask[y][1] = 1.0;
            Mask[y][2] = 1.0;
        }
        switch (maskType) {
            case ("average"):
//dont change mask
                break;
            case ("WeightedAverage"):
                System.out.println("weight");
                double[][] MaskWeightAvg = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
                Mask = MaskWeightAvg;
                o = 0.0625;
                break;

        }

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                a = 0;
                r = 0;
                g = 0;
                b = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        a = ImageArray1[x + s][y + t][0];
                        r = r + (int) ((o * Mask[1 - s][1 - t]) * ImageArray1[x + s][y + t][1]);
                        g = g + (int) ((o * Mask[1 - s][1 - t]) * ImageArray1[x + s][y + t][2]);
                        b = b + (int) ((o * Mask[1 - s][1 - t]) * ImageArray1[x + s][y + t][3]);;

                    }
                }
                ImageArray2[x][y][0] = a;
                ImageArray2[x][y][1] = r;
                ImageArray2[x][y][2] = g;
                ImageArray2[x][y][3] = b;
            }
        }

        return convertToBimage(ImageArray2);
    }

    public BufferedImage imagesharper(BufferedImage bi, String maskType) {
        int[][][] ImageArray1 = convertToArray(bi);
        int a, r, g, b;
        int width = bi.getWidth();
        int height = bi.getHeight();
        int[][] Mask = new int[3][3];
        switch (maskType) {
            case ("sobelx"):
                int[][] sobelx = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
                Mask = sobelx;
                break;
            case ("sobely"):
                int[][] sobely = {{-1, 0, 0}, {-2, 0, 2}, {-1, 0, 1}};
                Mask = sobely;
                break;
            case ("4Lapi"):
                int[][] fourlapi = {{0, -1, 0}, {-1, 4, -1}, {0, -1, 0}};
                Mask = fourlapi;
                break;
            case ("8Lapi"):
                int[][] eightlapi = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};
                Mask = eightlapi;
                break;
            case ("4LapiEnhance"):
                int[][] fourlapienhance = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
                Mask = fourlapienhance;
                break;
            case ("8LapiEnhance"):
                int[][] eightlapienhance = {{-1, -1, -1}, {-1, 9, -1}, {-1, -1, -1}};
                Mask = eightlapienhance;
                break;
        }

        int[][][] ImageArray2 = new int[width][height][4];

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                a = 0;
                r = 0;
                g = 0;
                b = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        a = ImageArray1[x + s][y + t][0];
                        r = r + (Mask[1 - s][1 - t] * ImageArray1[x + s][y + t][1]);
                        g = g + (Mask[1 - s][1 - t] * ImageArray1[x + s][y + t][2]);
                        b = b + (Mask[1 - s][1 - t] * ImageArray1[x + s][y + t][3]);;

                    }
                }
                if (r >= 255) {
                    r = 255;
                } else if (r <= 0) {

                    r = 0;
                }

                if (g >= 255) {
                    g = 255;
                } else if (g <= 0) {
                    g = 0;
                }

                if (b >= 255) {
                    b = 255;
                } else if (b <= 0) {

                    b = 0;
                }
                ImageArray2[x][y][0] = a;
                ImageArray2[x][y][1] = r;
                ImageArray2[x][y][2] = g;
                ImageArray2[x][y][3] = b;
            }
        }

        return convertToBimage(ImageArray2);
    }

    public BufferedImage imageRobel(BufferedImage bi) {
        int choice = Integer.parseInt(JOptionPane.showInputDialog("Pick 1 or 2 for type of sobel mask"));

        int[][][] ImageArray1 = convertToArray(bi);
        int a, r1, g1, b1, r2, g2, b2;
        int width = bi.getWidth();
        int height = bi.getHeight();
        int[][] Mask;
        int[][] Mask1 = {{0, 0, 0}, {0, 0, 1}, {0, -1, 0}};
        int[][] Mask2 = {{0, 0, 0}, {0, -1, 0}, {0, 0, 1}};

        Mask = Mask1;
        switch (choice) {

            case 1:
                break;
            case 2:
                Mask = Mask2;
                break;
        }

        int[][][] ImageArray2 = new int[width][height][4];

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                a = 0;
                r1 = 0;
                g1 = 0;
                b1 = 0;
                r2 = 0;
                g2 = 0;
                b2 = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        a = ImageArray1[x + s][y + t][0];
                        r1 = r1 + (Mask[1 - s][1 - t] * ImageArray1[x + s][y + t][1]);
                        g1 = g1 + (Mask[1 - s][1 - t] * ImageArray1[x + s][y + t][2]);
                        b1 = b1 + (Mask[1 - s][1 - t] * ImageArray1[x + s][y + t][3]);;

                    }
                }
                if (r1 >= 255) {
                    r1 = 255;
                } else if (r1 <= 0) {

                    r1 = 0;
                }

                if (g1 >= 255) {
                    g1 = 255;
                } else if (g1 <= 0) {
                    g1 = 0;
                }

                if (b1 >= 255) {
                    b1 = 255;
                } else if (b1 <= 0) {

                    b1 = 0;
                }

                ImageArray2[x][y][0] = a;
                ImageArray2[x][y][1] = r1;
                ImageArray2[x][y][2] = g1;
                ImageArray2[x][y][3] = b1;
            }
        }

        return convertToBimage(ImageArray2);
    }

    public BufferedImage statisticFilter(BufferedImage bi, int filter) {

        int k = 0;

        int width = bi.getWidth();
        int height = bi.getHeight();
        int[][][] ImageArray1 = convertToArray(bi);
        int[][][] ImageArray2 = new int[width][height][4];
        int a, r1, g1, b1, r2, g2, b2;

        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                k = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        rWindow[k] = ImageArray1[x + s][y + t][1];
                        gWindow[k] = ImageArray1[x + s][y + t][2];  //g
                        bWindow[k] = ImageArray1[x + s][y + t][3];  //b
                        k++;
                    }
                }

                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                ImageArray2[x][y][0] = ImageArray1[x][y][0];
                ImageArray2[x][y][1] = rWindow[filter];  //r        
                ImageArray2[x][y][2] = gWindow[filter];  //g        
                ImageArray2[x][y][3] = bWindow[filter];  //b    
            }
        }
        return convertToBimage(ImageArray2);
    }

    public BufferedImage statisticFilterMidpoint(BufferedImage bi) {
        int k = 0;

        int width = bi.getWidth();
        int height = bi.getHeight();
        int[][][] ImageArray1 = convertToArray(bi);
        int[][][] ImageArray2 = new int[width][height][4];
        int a, r1, g1, b1, r2, g2, b2;

        int[] rWindow = new int[9];
        int[] gWindow = new int[9];
        int[] bWindow = new int[9];
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                k = 0;
                for (int s = -1; s <= 1; s++) {
                    for (int t = -1; t <= 1; t++) {
                        rWindow[k] = ImageArray1[x + s][y + t][1];
                        gWindow[k] = ImageArray1[x + s][y + t][2];  //g
                        bWindow[k] = ImageArray1[x + s][y + t][3];  //b
                        k++;
                    }
                }
                Arrays.sort(rWindow);
                Arrays.sort(gWindow);
                Arrays.sort(bWindow);
                ImageArray2[x][y][0] = ImageArray1[x][y][0];

                ImageArray2[x][y][1] = (int) ((rWindow[0] + rWindow[8]) * 0.5);  //r        
                ImageArray2[x][y][2] = (int) ((gWindow[0] + gWindow[8]) * 0.5);  //g        
                ImageArray2[x][y][3] = (int) ((bWindow[0] + bWindow[8]) * 0.5);  //b    
            }
            System.out.println(" ");
        }
        return convertToBimage(ImageArray2);
    }

    public BufferedImage addNoise(BufferedImage bi) {
        int width = bi.getWidth();
        int height = bi.getHeight();
        int imgArray[][][] = convertToArray(bi);
        Random heightR = new Random();
        Random widthR = new Random();
        Random noiseTypeR = new Random();
        Random numberOfNoiseR = new Random();
        int max = numberOfNoiseR.nextInt(((width * height) / 2) / 2);
        System.out.println("pixel " + max);
        int h = 0;
        int w = 0;
        int v = 0;

        try {
            for (int i = 0; i < max; i++) {
                h = heightR.nextInt(height - 1);

                w = widthR.nextInt(width - 1);

                v = noiseTypeR.nextInt(1);
                if (v == 1) {
                    v = 255;
                }
                imgArray[w][h][1] = imgArray[w][h][v];
                imgArray[w][h][2] = imgArray[w][h][v];
                imgArray[w][h][3] = imgArray[w][h][v];
                v = 0;
            }
            return convertToBimage(imgArray);
        } catch (IndexOutOfBoundsException e) {

        }
        return bi;
    }

    public BufferedImage simpleThresh(BufferedImage bi) {
        int threshVal = Integer.parseInt(JOptionPane.showInputDialog("Enter threhold value"));
        int width = bi.getWidth();
        int height = bi.getHeight();
        int imgarray[][][] = convertToArray(bi);
        int r = 0;
        int g = 0;
        int b = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = imgarray[x][y][1];
                if (r < threshVal) {
                    r = 0;
                } else {
                    r = 255;
                }
                g = imgarray[x][y][2];
                if (g < threshVal) {
                    g = 0;
                } else {
                    g = 255;
                }
                b = imgarray[x][y][3];
                if (b < threshVal) {
                    b = 0;
                } else {
                    b = 255;
                }
                imgarray[x][y][1] = r;
                imgarray[x][y][2] = g;
                imgarray[x][y][3] = b;

            }
        }
        return convertToBimage(imgarray);
    }

    public BufferedImage complexThresh(BufferedImage bi) {

        int width = bi.getWidth();
        int height = bi.getHeight();
        int maxPixel = width * height;
        int imgarray[][][] = convertToArray(bi);
        //histogram for entire image
        double HGramR[] = new double[256];
        double HGramG[] = new double[256];
        double HGramB[] = new double[256];
        //histogram for background
        double BGramR[] = new double[256];
        double BGramG[] = new double[256];
        double BGramB[] = new double[256];
        //histogram for object
        double OGramR[] = new double[256];
        double OGramG[] = new double[256];
        double OGramB[] = new double[256];
        ArrayList<Integer> Tred = new ArrayList<>();
        ArrayList<Integer> Tblue = new ArrayList<>();
        ArrayList<Integer> Tgreen = new ArrayList<>();

        int r = 0;
        int g = 0;
        int b = 0;

        //get initial histogram
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = imgarray[x][y][1];
                g = imgarray[x][y][2];
                b = imgarray[x][y][3];
                HGramR[r]++;
                HGramG[g]++;
                HGramB[b]++;
            }
        }

        double meanRed = 0;
        double meanGreen = 0;
        double meanBlue = 0;
        //mean for background
        double BmeanRed = 0;
        double BmeanGreen = 0;
        double BmeanBlue = 0;
        //mean for object
        double OmeanRed = 0;
        double OmeanGreen = 0;
        double OmeanBlue = 0;

        //sum of values in object or all image
        double valRed = 0;
        double valGreen = 0;
        double valBlue = 0;
        //sum of values in background
        double BvalRed = 0;
        double BvalGreen = 0;
        double BvalBlue = 0;
        for (int k = 0; k <= 255; k++) {
            valRed = valRed + HGramR[k];
            valGreen = valGreen + HGramG[k];
            valBlue = valBlue + HGramB[k];

            meanRed = meanRed + (HGramR[k] * k);
            meanGreen = meanGreen + (HGramG[k] * k);
            meanBlue = meanBlue + (HGramB[k] * k);
        }
        //inital threshold values
        Tred.add((int) (meanRed / valRed));
        Tblue.add((int) (meanBlue / valBlue));
        Tgreen.add((int) (meanGreen / valGreen));
        boolean flag = true;
        boolean redFound = false;
        boolean greenFound = false;
        boolean blueFound = false;
        int k = 0;
        int tR = 0;
        int tG = 0;
        int tB = 0;
        while (flag) {
            //Reset all variable to zero
            //mean for background
            BmeanRed = 0;
            BmeanGreen = 0;
            BmeanBlue = 0;
            //mean for object
            OmeanRed = 0;
            OmeanGreen = 0;
            OmeanBlue = 0;

            //sum of values in object or all image
            valRed = 0;
            valGreen = 0;
            valBlue = 0;
            //sum of values in background
            BvalRed = 0;
            BvalGreen = 0;
            BvalBlue = 0;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    r = imgarray[x][y][1];
                    g = imgarray[x][y][2];
                    b = imgarray[x][y][3];

                    if (r < Tred.get(k)) {
                        BGramR[r]++;
                    } else {
                        OGramR[r]++;
                    }
                    if (g < Tgreen.get(k)) {
                        BGramG[g]++;
                    } else {
                        OGramG[g]++;
                    }
                    if (b < Tblue.get(k)) {
                        BGramB[b]++;
                    } else {
                        OGramB[b]++;
                    }

                }
            }
            for (int p = 0; p <= 255; p++) {

                //calculate mean of object histogram
                valRed = valRed + OGramR[p];
                valGreen = valGreen + OGramG[p];
                valBlue = valBlue + OGramB[p];

                OmeanRed = OmeanRed + (OGramR[p] * p);
                OmeanGreen = OmeanGreen + (OGramG[p] * p);
                OmeanBlue = OmeanBlue + (OGramB[p] * p);

                //calculate mean of background histogram
                BvalRed = BvalRed + OGramR[p];
                BvalGreen = BvalGreen + OGramG[p];
                BvalBlue = BvalBlue + OGramB[p];

                BmeanRed = BmeanRed + (BGramR[p] * p);
                BmeanGreen = BmeanGreen + (BGramG[p] * p);
                BmeanBlue = BmeanBlue + (BGramB[p] * p);
            }

            Tred.add((int) ((OmeanRed / valRed) + (BmeanRed / BvalRed)) / 2);
            if ((Tred.get(k + 1) - Tred.get(k)) < Tred.get(0)) {
                redFound = true;
                tR = k + 1;
            }
            Tgreen.add((int) ((OmeanGreen / valGreen) + (BmeanGreen / BvalGreen)) / 2);
            if ((Tgreen.get(k + 1) - Tgreen.get(k)) < Tgreen.get(0)) {
                greenFound = true;
                tG = k + 1;
            }
            Tblue.add((int) ((OmeanBlue / valBlue) + (BmeanBlue / BvalBlue)) / 2);
            if ((Tblue.get(k + 1) - Tblue.get(k)) < Tblue.get(0)) {
                blueFound = true;
                tB = k + 1;
            }
            if (redFound && greenFound && blueFound) {
                flag = false;
            } else {
                k++;
            }

        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                r = imgarray[x][y][1];
                if (r < Tred.get(tR)) {
                    r = 0;
                } else {
                    r = 255;
                }
                g = imgarray[x][y][2];
                if (g < Tgreen.get(tG)) {
                    g = 0;
                } else {
                    g = 255;
                }
                b = imgarray[x][y][3];
                if (b < Tblue.get(tB)) {
                    b = 0;
                } else {
                    b = 255;
                }
                imgarray[x][y][1] = r;
                imgarray[x][y][2] = g;
                imgarray[x][y][3] = b;

            }
        }
        return convertToBimage(imgarray);

    }

    public void changeImg() {
        try {
            JFileChooser chooser = new JFileChooser();
            int choice = chooser.showOpenDialog(chooser);
            if (choice != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File chosenFile = chooser.getSelectedFile();
            String filePath = chosenFile.getAbsolutePath();
            bi = ImageIO.read(new File(filePath));

            w = bi.getWidth();
            h = bi.getHeight();
            System.out.println(bi.getType());
            if (bi.getType() != BufferedImage.TYPE_INT_ARGB) {
                BufferedImage bi2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics big = bi2.getGraphics();
                big.drawImage(bi, 0, 0, null);
                big.drawImage(bi2, w, w, this);
                biFiltered = bi = bi2;

            }
        } catch (IOException e) {      // deal with the situation that th image has problem;/
            e.printStackTrace();

            System.exit(1);
        }
    }

    public static void main(String s[]) {
        count = 0;
        f = new JFrame("Image Processing Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        lab1 de = new lab1();

        f.add("Center", de);
        JComboBox choices = new JComboBox(de.getDescriptions());
        choices.setActionCommand("SetFilter");
        choices.addActionListener(de);
        JComboBox formats = new JComboBox(de.getFormats());
        formats.setActionCommand("Formats");
        formats.addActionListener(de);
        JPanel panel = new JPanel();
        panel.add(choices);
        panel.add(new JLabel("Save As"));
        panel.add(formats);
        f.add("North", panel);
        f.pack();
        f.setVisible(true);
        JButton newImg = new JButton("New Image");
        newImg.setActionCommand("newImage");
        newImg.addActionListener(de);
        panel.add(newImg);
        JPanel ba = new JPanel();
        f.add("South", ba);

        //add rgb text box and combobox and button
        JLabel redLabel = new JLabel("red");
        red = new JTextField("00");
        plusRed = new JComboBox(de.getPlusMinus());
        red.setColumns(4);

        JLabel greenLabel = new JLabel("green");
        green = new JTextField("00");
        green.setColumns(4);
        plusGreen = new JComboBox(de.getPlusMinus());

        JLabel blueLabel = new JLabel("blue");
        blue = new JTextField("00");
        blue.setColumns(4);
        plusBlue = new JComboBox(de.getPlusMinus());

        panel.add(redLabel);
        panel.add(red);
        panel.add(plusRed);
        panel.add(greenLabel);
        panel.add(green);
        panel.add(plusGreen);
        panel.add(blueLabel);
        panel.add(blue);
        panel.add(plusBlue);
        JButton cngColor = new JButton("Change Colour");

        cngColor.setActionCommand("changeColour");
        cngColor.addActionListener(de);
        panel.add(cngColor);
        undoBtn = new JButton("Undo");
        undoBtn.setActionCommand("Undo");
        undoBtn.addActionListener(de);
        panel.add(undoBtn);
        rawBtn = new JButton("Read Raw");
        rawBtn.setActionCommand("rawImage");
        rawBtn.addActionListener(de);
        panel.add(rawBtn);

    }
}
