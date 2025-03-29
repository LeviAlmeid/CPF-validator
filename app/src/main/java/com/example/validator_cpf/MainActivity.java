package com.example.validator_cpf;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCpf;
    private Button btnValidar;
    private TextView textResult;
    private ProgressBar progressBar;

    //18682|c9Wqs5UNaqae2rlezwC6EM0asMTxUHY2
    private static final String BASE_URL = "https://api.invertexto.com/v1/";

    private static final String API_TOKEN = BuildConfig.API_TOKEN;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa os componentes da tela
        editTextCpf = findViewById(R.id.editTextText);
        btnValidar = findViewById(R.id.btnValidar);
        textResult = findViewById(R.id.progress);
        progressBar = findViewById(R.id.progressBar);

        // Configuração do Retrofit
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Evento de clique no botão Validar
        btnValidar.setOnClickListener(v -> {
            String cpf = editTextCpf.getText().toString().trim();
            System.err.println("Erro: ---------------------! " + cpf.length());
            if (cpf.length() == 11) {
                validarCpf(cpf);
            } else {
                textResult.setText("Digite um CPF com 11 digitos!");

            }
        });
    }

    // Interface para comunicação com a API
    public interface ApiService {
        @GET("validator")
        Call<ApiResponse> validarCpf(@Query("token") String token, @Query("value") String cpf);
    }

    // Função para chamar a API e validar o CPF
    private void validarCpf(String cpf) {
        // Mostrar barra de progresso e definir o texto inicial
        progressBar.setVisibility(View.VISIBLE);
        textResult.setText("Validando...");

        Call<ApiResponse> call = apiService.validarCpf(API_TOKEN, cpf);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                // Criar um handler para atrasar a exibição do resultado
                new android.os.Handler().postDelayed(() -> {
                    progressBar.setVisibility(View.GONE); // Esconder barra de progresso

                    if (response.isSuccessful() && response.body() != null) {
                        boolean isValid = response.body().isValid(); // Atualize com o nome correto do campo JSON
                        textResult.setText(isValid ? "✅ CPF Válido" : "❌ CPF Inválido");
                    } else {
                        textResult.setText("Erro ao validar CPF");
                    }
                }, 3000); // Atraso de 3 segundos (3000ms)
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Criar um handler para garantir que a animação aconteça mesmo com erro
                new android.os.Handler().postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    textResult.setText("Erro na conexão");
                    Toast.makeText(MainActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }, 3000); // Atraso de 3 segundos (3000ms)
            }
        });
    }

}
