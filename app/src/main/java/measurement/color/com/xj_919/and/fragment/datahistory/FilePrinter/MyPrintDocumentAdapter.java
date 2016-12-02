package measurement.color.com.xj_919.and.fragment.datahistory.FilePrinter;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wpc on 2016/10/18.
 */

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {


    private Context context;
    PrintedPdfDocument mPdfDocument;
    private String fileAbsPath;


    MyPrintDocumentAdapter(Context context,String pdf_file_pathwithname){
        this.context=context;
        this.fileAbsPath=pdf_file_pathwithname;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
//        // Create a new PdfDocument with the requested page attributes
//        mPdfDocument = new PrintedPdfDocument(context, newAttributes);
//
//        // Respond to cancellation request
//        if (cancellationSignal.isCanceled()) {
//            callback.onLayoutCancelled();
//            return;
//        }
//
//        // Compute the expected number of printed pages
//        int pages = computePageCount(newAttributes);
//
//        if (pages > 0) {
//            // Return print information to print framework
//            PrintDocumentInfo info = new PrintDocumentInfo
//                    .Builder("print_output.pdf")
//                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
//                    .setPageCount(pages);
//            .build();
//            // Content layout reflow is complete
//            callback.onLayoutFinished(info, true);
//        } else {
//            // Otherwise report an error to the print framework
//            callback.onLayoutFailed("Page count calculation failed.");
//        }

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

//        int pages = computePageCount(newAttributes);

        PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Name of file").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

        callback.onLayoutFinished(pdi, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(new File(fileAbsPath));
            output = new FileOutputStream(destination.getFileDescriptor());

            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        } catch (FileNotFoundException ee){
            //Catch exception
        } catch (Exception e) {
            //Catch exception
        } finally {
            try {
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFinish() {
        super.onFinish();
    }

//
//    private int computePageCount(PrintAttributes printAttributes) {
//        int itemsPerPage = 4; // default item count for portrait mode
//
//        PrintAttributes.MediaSize pageSize = printAttributes.getMediaSize();
//        if (!pageSize.isPortrait()) {
//            // Six items per page in landscape orientation
//            itemsPerPage = 6;
//        }
//
//        // Determine number of print items
//        int printItemCount = getPrintItemCount();
//
//        return (int) Math.ceil(printItemCount / itemsPerPage);
//    }

}
