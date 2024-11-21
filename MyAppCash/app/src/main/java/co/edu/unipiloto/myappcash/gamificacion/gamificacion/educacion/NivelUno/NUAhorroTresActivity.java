package co.edu.unipiloto.myappcash.gamificacion.gamificacion.educacion.NivelUno;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import co.edu.unipiloto.myappcash.R;

public class NUAhorroTresActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuahorro_tres); // Asegúrate de que este layout tenga un WebView

        // Inicializar WebView
        webView = findViewById(R.id.webView); // Asegúrate de tener un WebView en tu layout
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Habilitar JavaScript

        // Cargar el video de YouTube
        String videoId = "hZweLjSVoqU"; // ID del video proporcionado
        String videoUrl = "https://www.youtube.com/embed/" + videoId;
        webView.loadUrl(videoUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack(); // Si hay historial, volver atrás
        } else {
            super.onBackPressed(); // De lo contrario, salir de la actividad
        }
    }
}
