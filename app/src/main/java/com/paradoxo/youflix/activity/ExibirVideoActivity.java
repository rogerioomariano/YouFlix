package com.paradoxo.youflix.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.paradoxo.youflix.R;
import com.paradoxo.youflix.modelo.Video;
import com.paradoxo.youflix.util.YTinfo;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ExibirVideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    public static final int COD_ERRO = 1000;
    private YouTubePlayerView youTubeView;
    private String idVideo, apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exibir_video);

        configurarToolbar();
        configurarBotaoPlay();
        configurarPlayerYouTube();

        LoadDadosVideo loadDadosVideo = new LoadDadosVideo();
        loadDadosVideo.execute();

    }

    private void configurarToolbar() {
        ((androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void configurarBotaoPlay() {
        findViewById(R.id.botaoPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibirVideo();
            }
        });
    }

    public void exibirVideo() {
        inicializarVideo();
        findViewById(R.id.layoutPreview).setVisibility(View.GONE);
        youTubeView.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        super.onMenuItemSelected(featureId, item);
        return true;
    }

    private void configurarPlayerYouTube() {
        apiKey = getPrefString();
        youTubeView = findViewById(R.id.youtubePlayer);

        Intent intent = getIntent();
        idVideo = intent.getStringExtra("idVideo");
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean foiRestaurado) {
        if (!foiRestaurado) youTubePlayer.loadVideo(idVideo);

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, COD_ERRO).show();
        } else {
            String erro = getString(R.string.erro_carregar_player) + youTubeInitializationResult.toString();
            Toast.makeText(this, erro, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == COD_ERRO) inicializarVideo();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        colocarVideoEmTelaCheia();
    }

    private void colocarVideoEmTelaCheia() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        // Esconde a appBar
    }

    private void inicializarVideo() {
        youTubeView.initialize(apiKey, this);
    }

    private void liberarLayoutLoad() {
        (findViewById(R.id.layoutLoad)).setVisibility(View.VISIBLE);
        (findViewById(R.id.layoutPrincipal)).setVisibility(View.GONE);
        (findViewById(R.id.layoutErro)).setVisibility(View.GONE);
    }

    private void liberarLayoutVideo() {
        (findViewById(R.id.layoutLoad)).setVisibility(View.GONE);
        (findViewById(R.id.layoutPrincipal)).setVisibility(View.VISIBLE);
    }

    private void liberarLayoutErro() {
        (findViewById(R.id.layoutLoad)).setVisibility(View.GONE);
        (findViewById(R.id.layoutErro)).setVisibility(View.VISIBLE);
        (findViewById(R.id.conteudoMensagTextView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDadosVideo loadDadosVideo = new LoadDadosVideo();
                loadDadosVideo.execute();
            }
        });
    }

    private void preencherdadosVideoEmTela(Video video) {
        TextView textViewTitulo = findViewById(R.id.txt_titulo);
        TextView textViewDescricao = findViewById(R.id.txt_descricao);

        textViewTitulo.setText(video.getTitulo());
        textViewDescricao.setText(video.getDescricao());

        TextView visu = findViewById(R.id.numero_visu);
        TextView like = findViewById(R.id.numero_like);
        TextView deslike = findViewById(R.id.numero_deslike);
        TextView tempo_atras = findViewById(R.id.tempo_atras);

        DateTime dtVideo = new DateTime(video.getData().getValue() + video.getData().getTimeZoneShift());
        DateTimeFormatter dtFormatada = DateTimeFormat.forPattern("dd-MM-yyyy  HH:mm");

        visu.setText(video.getQtdVisu());
        like.setText(video.getQtdLike());
        deslike.setText(video.getQtdDeslike());

        Picasso.with(this).load(video.getThumbnail().getUrl()).into((ImageView) findViewById(R.id.thumbnailImageView));

        tempo_atras.setText(dtFormatada.print(dtVideo));
    }

    private String getPrefString() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("apiKey", "");
    }

    @SuppressLint("StaticFieldLeak")
    public class LoadDadosVideo extends AsyncTask<Void, Void, Video> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            liberarLayoutLoad();
        }

        @Override
        protected Video doInBackground(Void... voids) {
            return new YTinfo(getApplicationContext()).carregarTodasInformacoesVideo(idVideo);
        }

        @Override
        protected void onPostExecute(Video video) {
            super.onPostExecute(video);
            if (video.getTitulo() != null) {
                preencherdadosVideoEmTela(video);
                liberarLayoutVideo();
            } else {
                liberarLayoutErro();
            }
        }

    }
}
