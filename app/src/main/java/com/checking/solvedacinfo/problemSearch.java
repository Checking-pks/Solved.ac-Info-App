package com.checking.solvedacinfo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class problemSearch extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_search);

        /* 사용할 오브젝트 불러오기 */
        // Header
        SearchView searchView = findViewById(R.id.problem_search_view);
        // Main Content
        TableLayout tableLayout = findViewById(R.id.problem_table_layout);
        String[] problemCategory = {"#", "제목", "푼 사람 수", "평균 시도"};
        String[] problemOrder = {"problemId", "titleKo", "acceptedUserCount", "averageTries"};

        // Footer
        View footer = findViewById(R.id.footer);

        // OkHttp
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request.Builder builder = new Request.Builder();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Request request = builder
                        .url("https://solved.ac/api/v3/search/problem?query=" + newText)
                        .get()
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) { return; }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.code() == 404 || response.body() == null) {
                            response.body().close();
                            return;
                        }

                        try {
                            tableLayout.removeViewsInLayout(2,tableLayout.getChildCount() - 2);

                            JSONObject json = new JSONObject(response.body().string());
                            JSONArray problemList = json.getJSONArray("items");
                            int problemCount = json.getInt("count");

                            for (int i=0; i<problemCount; i++) {
                                JSONObject nowProblem = problemList.getJSONObject(i);

                                TableRow tableRow = new TableRow(problemSearch.this);
                                tableRow.setLayoutParams(new TableRow.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                ));

                                for (int j=0; j<problemOrder.length; j++) {
                                    TextView textView = new TextView(problemSearch.this);
                                    String str = nowProblem.getString(problemOrder[j]);
                                    if (str.length() > 10) str = str.substring(0, 10) + "...";
                                    textView.setText(str);
                                    textView.setGravity(Gravity.CENTER);


                                    textView.setClickable(true);
                                    textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                                        Uri.parse("https://www.acmicpc.net/problem/" + nowProblem.getString("problemId")));
                                                startActivity(intent);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    tableRow.addView(textView);
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tableLayout.addView(tableRow);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        response.body().close();
                    }
                });

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.first_tab:
                startActivity(new Intent(this, userLobby.class));
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
