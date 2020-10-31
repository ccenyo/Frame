package com.cenyo.frame.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

public class ImageUtils {

    public static String getImageColors(String imageUrl) throws IOException {
        BufferedImage in = ImageIO.read(new File(imageUrl));
        return ImageDominantColor.getHexColor(in).stream().limit(2).collect(Collectors.joining(","));
    }

    public static String getImageSize(File file) throws IOException {
        BufferedImage bimg = ImageIO.read(file);
        int width          = bimg.getWidth();
        int height         = bimg.getHeight();
        return width+";"+height;
    }
}
