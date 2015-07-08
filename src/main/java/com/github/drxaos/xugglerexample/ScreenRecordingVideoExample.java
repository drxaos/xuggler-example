package com.github.drxaos.xugglerexample;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.io.XugglerIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class ScreenRecordingVideoExample {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        FileOutputStream f = new FileOutputStream("out.webm");
        ScreenRecordingVideoExample e = new ScreenRecordingVideoExample(f);
        for (int i = 0; i < 100; i++) {
            System.out.println("Frame " + i);
            e.transmitFrame();
            Thread.sleep(10);
        }
        e.close();
    }


    private Dimension screenBounds;

    long startTime;

    IMediaWriter writer;

    public ScreenRecordingVideoExample(OutputStream outputStream) {
        ToolFactory.setTurboCharged(false);

        writer = ToolFactory.makeWriter(XugglerIO.map(outputStream));

        IContainerFormat containerFormat = IContainerFormat.make();
        containerFormat.setOutputFormat("webm", null, "video/webm");
        writer.getContainer().setFormat(containerFormat);

        screenBounds = new Dimension(800, 600);

        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_VP8, screenBounds.width, screenBounds.height);

        startTime = System.nanoTime();
    }

    public void close() {
        writer.close();
    }

    public void transmitFrame() {
        BufferedImage screen = getDesktopScreenshot();

        BufferedImage bgrScreen = convertToType(screen,
                BufferedImage.TYPE_3BYTE_BGR);

        writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    }

    public BufferedImage convertToType(BufferedImage sourceImage, int targetType) {

        BufferedImage image;

        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;

    }

    private BufferedImage getDesktopScreenshot() {
        try {
            Robot robot = new Robot();
            Rectangle captureSize = new Rectangle(screenBounds);
            return robot.createScreenCapture(captureSize);
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }

    }

}