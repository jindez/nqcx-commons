package org.nqcx.commons.util.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 二维码操作工具类
 * Created by xiaola on 18/8/1.
 */
public class QRCodeUtil {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(QRCodeUtil.class);

    /**
     * 生成二维码图片
     *
     * @param content 内容
     * @param imgPath 图片路径
     */
    public void encodeQRCode(String content, String imgPath) {
        this.encodeQRCode(content, imgPath, "png", 7);
    }

    /**
     * 生成二维码图片
     *
     * @param content      内容
     * @param outputStream 输出流
     */
    public void encodeQRCode(String content, OutputStream outputStream) {
        this.encodeQRCode(content, outputStream, "png", 7);
    }

    /**
     * 生成二维码图片
     *
     * @param content 内容
     * @param imgPath 图片路径
     * @param imgType 图片类型
     */
    public void encodeQRCode(String content, String imgPath, String imgType) {
        this.encodeQRCode(content, imgPath, imgType, 7);
    }

    /**
     * 生成二维码图片
     *
     * @param content      内容
     * @param outputStream 输出流
     * @param imgType      图片类型
     */
    public void encodQRCode(String content, OutputStream outputStream, String imgType) {
        this.encodeQRCode(content, outputStream, imgType, 7);
    }

    /**
     * 生成二维码图片
     *
     * @param content 内容
     * @param imgPath 输出流
     * @param imgType 图片类型
     * @param size    图片尺寸
     */
    public void encodeQRCode(String content, String imgPath, String imgType, int size) {
        try {
            BufferedImage bufImg = this.encodeCommon(content, size);
            File file = new File(imgPath);
            ImageIO.write(bufImg, imgType, file);
        } catch (Exception e) {
            LOGGER.error("生成二维码异常", e);
        }
    }

    /**
     * 生成二维码
     *
     * @param content      内容
     * @param outputStream 输出流
     * @param imgType      图片类型
     * @param size         图片尺寸
     */
    public void encodeQRCode(String content, OutputStream outputStream, String imgType, int size) {
        try {
            BufferedImage bufImg = this.encodeCommon(content, size);
            ImageIO.write(bufImg, imgType, outputStream);
        } catch (Exception e) {
            LOGGER.error("生成二维码异常", e);
        }

    }

    /**
     * 二维码解析
     *
     * @param imgPath 待解析二维码路径
     * @return
     */
    public String decodeQRCode(String imgPath) {
        String result = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(imgPath));
            result = this.decodeCommon(bufferedImage);
        } catch (Exception e) {
            LOGGER.error("二维码解析错误", e);
        }
        return result;
    }

    /**
     * 二维码解析
     *
     * @param inputStream 待解析输入流
     * @return
     */
    public String decodeQRCode(InputStream inputStream) {
        String result = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(inputStream);
            result = this.decodeCommon(bufferedImage);
        } catch (Exception e) {
            LOGGER.error("二维码解析错误", e);
        }
        return result;
    }

    private String decodeCommon(BufferedImage bufferedImage) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<DecodeHintType, Object>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);// 对图像进行解码
            return result.getText();
        } catch (Exception e) {
            LOGGER.error("二维码解析错误", e);
        }
        return null;
    }

    private BufferedImage encodeCommon(String content, int size) {
        BufferedImage result = null;
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            // 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            // 内容所使用字符集编码
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 1);//设置二维码边的空度，非负数

            BitMatrix bitMatrix = new MultiFormatWriter().encode(content,//要编码的内容
                    BarcodeFormat.QR_CODE,
                    size, //条形码的宽度
                    size, //条形码的高度
                    hints);//生成条形码时的一些配置,此项可选

            // 生成二维码
            result = MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            LOGGER.error("生成二维码异常", e);
        }
        return result;

    }


    public static void main(String[] args) {
//        String imgPath = "/Users/xiaola/Downloads/test.png";
//        String encoderContent = "http://app99.chineseall.cn/DownloadApp/index?id=57&e=15634&shId=1PHKj&aeShId=Nmnrj&&t=1&f=1&cmptCode=BOOK_YUN&account=53&appId=6";
//        QRCodeUtil handler = new QRCodeUtil();
//        handler.encodeQRCode(encoderContent, imgPath, "png");
        String imgPath = "/Users/xiaola/Downloads/test.png";
        QRCodeUtil handler = new QRCodeUtil();
        String result = handler.decodeQRCode(imgPath);
        System.out.print(result);
    }


}
