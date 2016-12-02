package measurement.color.com.xj_919.and.fragment.datahistory.FilePrinter;

import android.app.Activity;
import android.content.Context;
import android.print.PrintManager;

import measurement.color.com.xj_919.R;

/**
 * Created by wpc on 2016/10/18.
 */

public class Printer {

    private Activity mContext;

    public Printer(Activity context) {
        mContext = context;
    }

    public void PrintPDFwithAbsPath(String abspath) {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) mContext.getSystemService(Context.PRINT_SERVICE);

        // Set job name, which will be displayed in the print queue
        String jobName = mContext.getString(R.string.app_name) + " Document";

        // Start a print job, passing in a PrintDocumentAdapter implementation
        // to handle the generation of a print document
        printManager.print(jobName, new MyPrintDocumentAdapter( mContext,abspath),
                null); //
    }
}
