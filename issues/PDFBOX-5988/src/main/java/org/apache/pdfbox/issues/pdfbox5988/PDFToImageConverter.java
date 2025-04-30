package org.apache.pdfbox.issues.pdfbox5988;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Minimal example that renders a PDF to an image for PDFBOX-5988 issue.
 */
public class PDFToImageConverter {

    private static final String PDF_RESOURCE_NAME = "sample.pdf";
    private static final String OUTPUT_IMAGE_FORMAT = "PNG";
    private static final String OUTPUT_IMAGE_PREFIX = "rendered-page-";

    public static void main(String[] args) {
        try {
            // Extract the sample PDF from resources to a temporary file if it doesn't exist
            File pdfFile = extractResourceToTempFile();
            
            // If no PDF file was found, print a message and exit
            if (pdfFile == null) {
                System.err.println("No sample PDF file found in resources. Please add a file named '" + 
                                  PDF_RESOURCE_NAME + "' to the resources directory.");
                return;
            }

            boolean benchmark = args.length > 0 && args[0].equals("benchmark");
            
            System.out.println("Converting PDF to images: " + pdfFile.getAbsolutePath());
            
            // Load the PDF document
            try (PDDocument document = Loader.loadPDF(pdfFile)) {
                // Create a renderer for the document
                PDFRenderer renderer = new PDFRenderer(document);
                
                // Get the total number of pages
                int pageCount = document.getNumberOfPages();
                System.out.println("PDF has " + pageCount + " page(s)");

                // Render each page as an image
                for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                    System.out.println("Rendering page " + (pageIndex + 1) + " of " + pageCount);

                    // Render the page to an image
                    BufferedImage image = renderer.renderImageWithDPI(pageIndex, 300);

                    if (!benchmark) {
                        // Create output file name
                        String outputFileName = OUTPUT_IMAGE_PREFIX + (pageIndex + 1) + "." +
                                OUTPUT_IMAGE_FORMAT.toLowerCase();
                        File outputFile = new File(outputFileName);

                        // Write the image to a file
                        ImageIO.write(image, OUTPUT_IMAGE_FORMAT, outputFile);
                        System.out.println("Created image: " + outputFile.getAbsolutePath());
                    }
                }
            }
            
            System.out.println("PDF to image conversion completed successfully.");
            
        } catch (IOException e) {
            System.err.println("Error converting PDF to image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Extracts the sample PDF from resources to a temporary file.
     * 
     * @return The temporary file containing the PDF, or null if the resource doesn't exist
     * @throws IOException If an I/O error occurs
     */
    private static File extractResourceToTempFile() throws IOException {
        // Try to get the resource as a stream
        try (InputStream resourceStream = PDFToImageConverter.class.getClassLoader()
                .getResourceAsStream(PDF_RESOURCE_NAME)) {
            
            // If the resource doesn't exist, return null
            if (resourceStream == null) {
                return null;
            }
            
            // Create a temporary file
            Path tempFile = Files.createTempFile("pdfbox-5988-", ".pdf");
            
            // Copy the resource to the temporary file
            Files.copy(resourceStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            
            // Return the temporary file
            return tempFile.toFile();
        }
    }
}