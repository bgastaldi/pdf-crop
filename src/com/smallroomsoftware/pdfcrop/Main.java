package com.smallroomsoftware.pdfcrop;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class Main {



    public static void main(String[] args) throws Exception {
        try {
            String path = args[0];
            float cmToPs = 2.83465f;
            float x = Float.parseFloat(args[1]) * cmToPs;
            float y = Float.parseFloat(args[2]) * cmToPs;
            float width = Float.parseFloat(args[3]) * cmToPs;
            float height = Float.parseFloat(args[4]) * cmToPs;
            int rot = args.length > 5 ? Integer.parseInt(args[5]) : 0;

            PdfRectangle rect = new PdfRectangle(x, y, width + x, height + y);

            String outFile = getUniqueFileName("/tmp", "pdf");

            PdfReader reader = new PdfReader(path);
            int n = reader.getNumberOfPages();

            if (n != 1) {
                System.err.println("File has more than one page (" + n + ")");
            }

            reader.selectPages(String.valueOf(1));

            PdfDictionary pageDict = reader.getPageN(1);
            pageDict.put(PdfName.CROPBOX, rect);
            pageDict.put(PdfName.MEDIABOX, rect);

            PdfNumber rotate = pageDict.getAsNumber(PdfName.ROTATE);
            if (rotate == null) {
                pageDict.put(PdfName.ROTATE, new PdfNumber(rot));
            } else {
                pageDict.put(PdfName.ROTATE, new PdfNumber((rotate.intValue() + rot) % 360));
            }

            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outFile));
            stamper.close();
            reader.close();

            System.out.println(outFile);
        } catch (DocumentException | IOException err) {
            throw err;
        }
    }

    public static String getUniqueFileName(String directory, String extension) {
        String fileName = UUID.randomUUID().toString() + "." + extension.trim();
        return Paths.get(directory, fileName).toString();
    }
}
