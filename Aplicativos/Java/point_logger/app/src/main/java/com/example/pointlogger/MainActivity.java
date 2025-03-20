package com.example.pointlogger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list_view);
        setupListView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // Verifica se a opção "Gerenciar Pontos" foi clicada
                    Intent intent = new Intent(MainActivity.this, ManagementActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    // Verifica se a opção "COLETA" foi clicada
                    Intent intent = new Intent(MainActivity.this, CollectActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    // Verifica se a opção "DADOS" foi clicada
                    Intent intent = new Intent(MainActivity.this, ExportActivity.class);
                    startActivity(intent);
                }
                // Adicione outras verificações para os outros itens da lista, se necessário
            }
        });
    }

    private void setupListView(ListView listView) {
        // Criando uma lista de opções com imagens
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.c_gerenciar_ponto);
        images.add(R.drawable.c_gerenciar_ponto);
        images.add(R.drawable.c_gerenciar_ponto);

        List<String> options = new ArrayList<>();
        options.add("GERENCIAR PONTOS");
        options.add("COLETA");
        options.add("DADOS");

        // Criando um adaptador personalizado para a lista de opções
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.text, options) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                ImageView imageView = view.findViewById(R.id.image);
                TextView textView = view.findViewById(R.id.text);

                imageView.setImageResource(images.get(position));
                textView.setText(options.get(position));

                return view;
            }
        };

        listView.setAdapter(adapter);
    }
}
