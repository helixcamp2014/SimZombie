/*
 * © 2011 by Matthew Crossley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package simzombie.engine.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.Node;

/**
 *
 * @author      Matthew Crossley <m.crossley@mmu.ac.uk>
 * @version     1.0
 * @since       2011-10-28
 */
public class AnimatedGIFWriter {

  /** See original source: http://forums.sun.com/thread.jspa?messageID=10755673#10755673
  @author Maxideon
  @param delayTime String Frame delay for this frame. */
  public static void configure(IIOMetadata meta,
    String delayTime,
    int imageIndex) {

    String metaFormat = meta.getNativeMetadataFormatName();

    if (!"javax_imageio_gif_image_1.0".equals(metaFormat)) {
      throw new IllegalArgumentException(
        "Unfamiliar gif metadata format: " + metaFormat);
    }

    Node root = meta.getAsTree(metaFormat);

    //find the GraphicControlExtension node
    Node child = root.getFirstChild();
    while (child != null) {
      if ("GraphicControlExtension".equals(child.getNodeName())) {
        break;
      }
      child = child.getNextSibling();
    }

    IIOMetadataNode gce = (IIOMetadataNode) child;
    gce.setAttribute("userDelay", "FALSE");
    gce.setAttribute("delayTime", delayTime);

    //only the first node needs the ApplicationExtensions node
    if (imageIndex == 0) {
      IIOMetadataNode aes =
        new IIOMetadataNode("ApplicationExtensions");
      IIOMetadataNode ae =
        new IIOMetadataNode("ApplicationExtension");
      ae.setAttribute("applicationID", "NETSCAPE");
      ae.setAttribute("authenticationCode", "2.0");
      byte[] uo = new byte[]{
        //last two bytes is an unsigned short (little endian) that
        //indicates the the number of times to loop.
        //0 means loop forever.
        0x1, 0x0, 0x1
      };
      ae.setUserObject(uo);
      aes.appendChild(ae);
      root.appendChild(aes);
    }

    try {
      meta.setFromTree(metaFormat, root);
    } catch (IIOInvalidTreeException e) {
      //shouldn't happen
      throw new Error(e);
    }
  }

  /** See original source: http://forums.sun.com/thread.jspa?messageID=9988198
  @author GeoffTitmus
  @param file File A File in which to store the animation.
  @param frames BufferedImage[] Array of BufferedImages, the frames of the animation.
  @param delayTimes String[] Array of Strings, representing the frame delay times. */
  public static void saveAnimate(
    File file,
    BufferedImage[] frames,
    String[] delayTimes ) throws Exception {

    ImageWriter iw = ImageIO.getImageWritersByFormatName("gif").next();

    ImageOutputStream ios = ImageIO.createImageOutputStream(file);
    iw.setOutput(ios);
    iw.prepareWriteSequence(null);

    for (int i = 0; i < frames.length; i++) {
      BufferedImage src = frames[i];
      ImageWriteParam iwp = iw.getDefaultWriteParam();
      IIOMetadata metadata = iw.getDefaultImageMetadata(
      new ImageTypeSpecifier(src), iwp);

      configure(metadata, delayTimes[i], i);

      IIOImage ii = new IIOImage(src, null, metadata);
      iw.writeToSequence(ii, null);
    }

    iw.endWriteSequence();
    ios.close();
  }
  
}
