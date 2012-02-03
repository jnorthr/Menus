import java.awt.Dimension;
import java.io.*;

public class JPG {

    public static Dimension getImageDimension(String fn) throws IOException 
    {
        File f = new File(fn);
            FileInputStream fis = new FileInputStream(f);
        if (!(f.exists())) System.out.println("JPG Image file " + fn + " missing");
                    //throw new RuntimeException("JPG Image file " + fn + " missing");

            // check for SOI marker
            if (fis.read() != 255 || fis.read() != 216) System.out.println("SOI (Start Of Image) marker 0xff 0xd8 missing");
                    //throw new RuntimeException("SOI (Start Of Image) marker 0xff 0xd8 missing");

            Dimension d = null;

            while (fis.read() == 255) 
        {
                    int marker = fis.read();
                    int len = fis.read() << 8 | fis.read();

                    if (marker == 192) 
            {
                            fis.skip(1);

                            int height = fis.read() << 8 | fis.read();
                            int width = fis.read() << 8 | fis.read();

                            d = new Dimension(width, height);
                            System.out.print("<ok>");
                            break;
                    }

                    fis.skip(len - 2);
            } // end of while

            fis.close();
        return d;
    }; // end of method

    public static void main(String[] args)
    {
        try 
        {
            Dimension d = JPG. getImageDimension("./loading.jpg");
        }
        catch (Exception x) {};
        System.exit(0);
    };


}; // end of class