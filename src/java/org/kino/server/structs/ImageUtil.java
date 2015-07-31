/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.structs;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author kirio
 */
public class ImageUtil {
    
    public static byte[] convertToPng(File file) throws IOException{
         // read a jpeg from a inputFile
        BufferedImage bufferedImage = ImageIO.read(file);
         
        // write the bufferedImage back to outputFile
       // this writes the bufferedImage into a byte array called resultingBytes
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png",  byteArrayOut);
        return byteArrayOut.toByteArray();
  
     }
    
     public static byte[] convertToPng(byte[] data) throws IOException{
         // read a jpeg from a inputFile
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
        // write the bufferedImage back to outputFile
       // this writes the bufferedImage into a byte array called resultingBytes
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png",  byteArrayOut);
        return byteArrayOut.toByteArray();
  
     }
    
        // Для загрузки изображения в базу
        public static byte [] imageToByteArray(Image image) throws IOException
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                BufferedImage buffimg = imageToBufferedImage(image);
                ImageIO.write(buffimg, "PNG", baos);
            } catch (IOException ex) {
                throw new IOException(ex);
             }
            return baos.toByteArray();
        }
        
        
        // Трансформировать размер изображения  
        // keepAspectRatio - учитывать ли пропорции
        // transformMode   - стиль сжатия (Image.SCALE_...)
        public static Image scaledImage(Image img,int w,int h,boolean keepAspectRatio, int transformMode)
        { 
 
             // Трансформирование без учета пропорций
             if(keepAspectRatio==false)
                return img.getScaledInstance(w, h, transformMode);
             // Трансформирование с учетом пропорций
             int imgH = img.getHeight(null);
             int imgW = img.getWidth(null);
             int rw = (h * imgW) / imgH;
             boolean useHeight = (rw <= w);
             int newW,newH;
             if (useHeight) {
                 newW = rw;
                 newH = h;
             } else {
                 newH =  w * imgH / imgW;
                 newW = w;
             }
             return img.getScaledInstance(newW, newH, transformMode);
        }
        
        // Трансформировать размер изображения с учетом пропрорций 
        // transformMode   - стиль сжатия (Image.SCALE_...)
        public static Image scaledImageWithRatio(Image img,int w,int h,int transformMode)
        { 
             return scaledImage(img, w,h,true, transformMode);
        }
        

        // Ужать изображение с учетом пропрорций 
        // Если размеры изображения и так меньше или равны w,h то ничего не делает 
        // (чтобы не растягивать мелкие изображения, сильно теряя качество)
        // transformMode   - стиль сжатия (Image.SCALE_...)
        public static Image squeezedImageWithRatio(Image img,int w,int h, int transformMode)
        {
             if(img.getWidth(null)<=w && img.getHeight(null)<=h)
                return img;
             return scaledImageWithRatio(img,w,h,transformMode);  
        }

        public static  BufferedImage imageToBufferedImage(Image image) {
            if(image instanceof BufferedImage)
                    return (BufferedImage)image;
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            bi.getGraphics().drawImage(image, 0, 0, null);        
            return bi;
        }



 }