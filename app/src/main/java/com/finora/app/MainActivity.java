package com.finora.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
  private static final int CREATE_FILE = 4101;
  private static final int OPEN_FILE = 4102;
  private WebView webView;
  private String pendingContent = "";
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    webView = new WebView(this);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDomStorageEnabled(true);
    webView.getSettings().setAllowFileAccess(true);
    webView.getSettings().setAllowContentAccess(false);
    webView.setWebViewClient(new WebViewClient());
    webView.setWebChromeClient(new WebChromeClient());
    webView.addJavascriptInterface(new FinoraBridge(), "AndroidFinora");
    setContentView(webView);
    if (savedInstanceState == null) webView.loadUrl("file:///android_asset/index.html"); else webView.restoreState(savedInstanceState);
  }
  private class FinoraBridge {
    @JavascriptInterface public void saveFile(String content, String filename, String mimeType) {
      runOnUiThread(() -> { pendingContent = content == null ? "" : content; Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType(mimeType == null ? "application/octet-stream" : mimeType); i.putExtra(Intent.EXTRA_TITLE, filename == null ? "finora-export.txt" : filename); startActivityForResult(i, CREATE_FILE); });
    }
    @JavascriptInterface public void pickBackup() {
      runOnUiThread(() -> { Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT); i.addCategory(Intent.CATEGORY_OPENABLE); i.setType("application/json"); startActivityForResult(i, OPEN_FILE); });
    }
  }
  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode != RESULT_OK || data == null || data.getData() == null) return;
    Uri uri = data.getData();
    try {
      if (requestCode == CREATE_FILE) { try (OutputStream out = getContentResolver().openOutputStream(uri)) { if (out != null) out.write(pendingContent.getBytes(StandardCharsets.UTF_8)); } Toast.makeText(this, "File saved", Toast.LENGTH_SHORT).show(); }
      else if (requestCode == OPEN_FILE) { try (java.io.InputStream in = getContentResolver().openInputStream(uri); ByteArrayOutputStream buffer = new ByteArrayOutputStream()) { if (in == null) throw new IllegalStateException("Unable to open file"); byte[] bytes = new byte[4096]; int n; while ((n = in.read(bytes)) != -1) buffer.write(bytes, 0, n); String json = new String(buffer.toByteArray(), StandardCharsets.UTF_8); webView.evaluateJavascript("importBackupText(" + org.json.JSONObject.quote(json) + ")", null); } }
    } catch (Exception ex) { Toast.makeText(this, "File operation failed. Please try again.", Toast.LENGTH_LONG).show(); }
  }
  @Override protected void onSaveInstanceState(Bundle outState) { super.onSaveInstanceState(outState); if (webView != null) webView.saveState(outState); }
  @Override public void onBackPressed() { if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
}
