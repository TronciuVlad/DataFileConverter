// Java program to demonstrate 
// creation of random pixel image
  
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;

public class DataToImage
{

    static final int width = 1920;
    static final int height = 2560;

    static final String inputFile = "2015-06-20 11_39_21 +0000.data";

    public static void main(String args[])throws IOException
    {
        BufferedImage img = null;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
  
        File output = null;

        try
        {
            Path path = Paths.get(inputFile);

            byte[] data = Files.readAllBytes(path);

            System.out.println(data.length);

            int r, g, b, a, p, x, y;

            for(int i = 0; i < data.length; i+= 4){
                r = toUnsignedInt(data[i]);
                g = toUnsignedInt(data[i+1]);
                b = toUnsignedInt(data[i+2]);
                a = toUnsignedInt(data[i+3]);
                //System.out.println(i + "::" + r + " " + g + " " + b + " " + a);

                p = (a<<24) | (r<<16) | (g<<8) | b;
                x = (i/4)%(width/5*4);
                y = (i/4)/(width/5*4);

                img.setRGB(x, y, p);
            }

            output = new File("p.png");
            ImageIO.write(img, "png", output);
        }
        catch(IOException e)
        {
            System.out.println("Error: " + e);
        }
    }

    public static int toUnsignedInt(byte x)
    {  
        return ((int) x) & 0xff;  
    }  


}