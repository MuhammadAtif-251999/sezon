package io.sezon.sezon.home.submenu.setting;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebView;

import io.sezon.sezon.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FAQActivity extends AppCompatActivity {
    @BindView(R.id.web_view)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        ButterKnife.bind(this);

        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("https://sezon.io");
    }

}
