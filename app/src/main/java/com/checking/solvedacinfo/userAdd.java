
package com.checking.solvedacinfo;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class userAdd extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_add);

        /* 사용할 오브젝트 불러오기 */
        // Header
        SearchView searchView = findViewById(R.id.search_view);
        // Main Content
        View mainContent = findViewById(R.id.mainContent);

        ImageView userIcon = mainContent.findViewById(R.id.userIcon);
        ImageView userTier = mainContent.findViewById(R.id.userTier);
        TextView userNickname = mainContent.findViewById(R.id.userNickname);
        TextView userRating = mainContent.findViewById(R.id.userRating);
        TextView userStreak = mainContent.findViewById(R.id.userStreak);

        ImageButton addButton = mainContent.findViewById(R.id.add_button);
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
                        .url("https://solved.ac/api/v3/user/show?handle=" + newText)
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
                            JSONObject json = new JSONObject(response.body().string());

                            // User Icon
                            String url = json.getString("profileImageUrl");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(userAdd.this)
                                            .load(url)
                                            .placeholder(R.drawable.default_profile)
                                            .error(R.drawable.default_profile)
                                            .fallback(R.drawable.default_profile)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .apply(new RequestOptions().circleCrop())
                                            .into(userIcon);
                                }
                            });

                            // User Tier
                            userTier.setImageResource(getResources().getIdentifier("tier_" + json.getInt("tier"), "drawable", userAdd.this.getPackageName()));

                            // User NicName
                            userNickname.setText(json.getString("handle"));

                            // User Rating
                            int tierTextId = getResources().getIdentifier("tier_" + json.getInt("tier"), "string", userAdd.this.getPackageName());
                            String ratingText = getResources().getString(tierTextId) + " " + json.getInt("rating");
                            userRating.setText(ratingText);

                            if (json.getInt("tier") <= 30) {
                                int tierColorId = getResources().getIdentifier("tier_" + json.getInt("tier"), "color", userAdd.this.getPackageName());
                                userRating.setTextColor(getResources().getColor(tierColorId, null));
                                userRating.getPaint().setShader(null);
                            } else {
                                Shader gradiant = new LinearGradient(
                                        0, 0, 0, userRating.getTextSize(),
                                        new int[] {
                                                getResources().getColor(R.color.tier_31_start, null),
                                                getResources().getColor(R.color.tier_31_center, null),
                                                getResources().getColor(R.color.tier_31_end, null)},
                                        null, Shader.TileMode.CLAMP
                                );

                                userRating.getPaint().setShader(gradiant);
                            }

                            // User Streak
                            userStreak.setText(getString(R.string.maxStreak, json.getInt("maxStreak")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        response.body().close();
                    }
                });

                return true;
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userNickname.getText().toString().indexOf(' ') >= 0) { return; }

                try {
                    FileOutputStream fos = openFileOutput("userData.txt", MODE_PRIVATE);
                    PrintWriter writer = new PrintWriter(fos);

                    writer.println(userNickname.getText().toString());
                    writer.flush();
                    writer.close();

                    Intent intent = new Intent(userAdd.this, userLobby.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    overridePendingTransition(0, 0);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(userAdd.this, userLobby.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
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
            case R.id.second_tab:
                startActivity(new Intent(this, problemSearch.class));
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}