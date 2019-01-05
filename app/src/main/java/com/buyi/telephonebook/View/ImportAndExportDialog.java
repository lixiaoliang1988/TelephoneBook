package com.buyi.telephonebook.View;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.PluralsRes;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.buyi.telephonebook.ContractBean;
import com.buyi.telephonebook.R;

import java.util.List;

public class ImportAndExportDialog extends Dialog {

    private Context context;
    private TextView tv;
    private Button import_btn,export_btn;
    private MyProgressView progressView;
    public ImportAndExportDialog(@NonNull Context context) {
        this(context,android.R.style.Theme_Translucent_NoTitleBar);
    }

    public ImportAndExportDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context  =  context;
        initView(context);
    }
    private void initView(Context context){
        setContentView(R.layout.dialog_import_export);
        setCanceledOnTouchOutside(false);
        setCancelable(true);
        import_btn = findViewById(R.id.import_btn);
        export_btn = findViewById(R.id.export_btn);
        progressView = findViewById(R.id.progress_vw);
        tv = findViewById(R.id.file_path_tv);
    }
  public void importOnClickListener(View.OnClickListener listener){
        import_btn.setOnClickListener(listener);
  }
  public void exportOnClickListener(View.OnClickListener listener){
        export_btn.setOnClickListener(listener);
  }

    public MyProgressView getProgressView() {
        return progressView;
    }

    public TextView getTv() {
        return tv;
    }

    @Override
    public void dismiss() {
        if (progressView!=null)
            progressView.stop();
        super.dismiss();
    }
}
