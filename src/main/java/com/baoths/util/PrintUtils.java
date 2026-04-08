package com.baoths.util;

import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for printing QR code images.
 */
public final class PrintUtils {

    private static final Logger LOGGER = Logger.getLogger(PrintUtils.class.getName());

    private PrintUtils() {
        // Utility class — no instantiation
    }

    /**
     * Prints a JavaFX Image using the system print dialog.
     *
     * @param image the JavaFX Image to print
     * @param owner the owner Stage for the print dialog
     * @return true if printing was initiated successfully
     */
    public static boolean printImage(Image image, Stage owner) {
        if (image == null) {
            LOGGER.warning("Cannot print: image is null.");
            return false;
        }

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            LOGGER.warning("Cannot create printer job. No printer available?");
            return false;
        }

        boolean proceed = job.showPrintDialog(owner);
        if (proceed) {
            ImageView imageView = new ImageView(image);
            PageLayout pageLayout = job.getJobSettings().getPageLayout();

            // Scale image to fit page
            double pageWidth = pageLayout.getPrintableWidth();
            double pageHeight = pageLayout.getPrintableHeight();
            imageView.setFitWidth(Math.min(pageWidth, image.getWidth()));
            imageView.setFitHeight(Math.min(pageHeight, image.getHeight()));
            imageView.setPreserveRatio(true);

            boolean success = job.printPage(imageView);
            if (success) {
                job.endJob();
                LOGGER.info("Print job completed successfully.");
                return true;
            } else {
                LOGGER.log(Level.WARNING, "Print job failed.");
            }
        } else {
            LOGGER.info("Print dialog cancelled by user.");
        }

        return false;
    }
}
