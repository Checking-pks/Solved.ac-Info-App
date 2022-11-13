package com.checking.solvedacinfo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class userInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");

        ImageView background    = findViewById(R.id.user_info_background);
        ImageView icon          = findViewById(R.id.user_info_icon);
        ImageView tier          = findViewById(R.id.user_info_tier);
        TextView nicknameText   = findViewById(R.id.user_info_nickname);
        TextView bio            = findViewById(R.id.user_info_bio);

        TextView rank           = findViewById(R.id.user_info_rank);
        TextView tierText       = findViewById(R.id.user_info_tier_text);
        TextView ratingByProblemsSumScore = findViewById(R.id.user_info_rating_by_problems_sum_score);
        TextView ratingByClassText        = findViewById(R.id.user_info_rating_by_class_text);
        TextView ratingByClassScore       = findViewById(R.id.user_info_rating_by_class_score);
        TextView ratingBySolvedCountText  = findViewById(R.id.user_info_rating_by_solved_count_text);
        TextView ratingBySolvedCountScore = findViewById(R.id.user_info_rating_by_solved_count_score);
        TextView ratingByVoteCountText    = findViewById(R.id.user_info_rating_by_vote_count_text);
        TextView ratingByVoteCountScore   = findViewById(R.id.user_info_rating_by_vote_count_score);

        TextView maxStreak = findViewById(R.id.user_info_max_streak);

        TextView totalExp    = findViewById(R.id.user_info_total_exp);
        TextView bronzeSolve    = findViewById(R.id.user_info_bronze_solve);
        TextView bronzeSolvePer = findViewById(R.id.user_info_bronze_solve_per);
        TextView bronzeExp      = findViewById(R.id.user_info_bronze_exp);
        TextView bronzeExpPer   = findViewById(R.id.user_info_bronze_exp_per);
        TextView silverSolve    = findViewById(R.id.user_info_silver_solve);
        TextView silverSolvePer = findViewById(R.id.user_info_silver_solve_per);
        TextView silverExp      = findViewById(R.id.user_info_silver_exp);
        TextView silverExpPer   = findViewById(R.id.user_info_silver_exp_per);
        TextView goldSolve    = findViewById(R.id.user_info_gold_solve);
        TextView goldSolvePer = findViewById(R.id.user_info_gold_solve_per);
        TextView goldExp      = findViewById(R.id.user_info_gold_exp);
        TextView goldExpPer   = findViewById(R.id.user_info_gold_exp_per);
        TextView platinumSolve    = findViewById(R.id.user_info_platinum_solve);
        TextView platinumSolvePer = findViewById(R.id.user_info_platinum_solve_per);
        TextView platinumExp      = findViewById(R.id.user_info_platinum_exp);
        TextView platinumExpPer   = findViewById(R.id.user_info_platinum_exp_per);
        TextView diamondSolve    = findViewById(R.id.user_info_diamond_solve);
        TextView diamondSolvePer = findViewById(R.id.user_info_diamond_solve_per);
        TextView diamondExp      = findViewById(R.id.user_info_diamond_exp);
        TextView diamondExpPer   = findViewById(R.id.user_info_diamond_exp_per);
        TextView rubySolve    = findViewById(R.id.user_info_ruby_solve);
        TextView rubySolvePer = findViewById(R.id.user_info_ruby_solve_per);
        TextView rubyExp      = findViewById(R.id.user_info_ruby_exp);
        TextView rubyExpPer   = findViewById(R.id.user_info_ruby_exp_per);

        /* 숫자 가공용 */
        DecimalFormat f = new DecimalFormat("###,###");
        DecimalFormat fp = new DecimalFormat("##0.0");
        fp.setMaximumFractionDigits(1);

        // OkHttp
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request.Builder builder = new Request.Builder();

        Request request1 = builder
                .url("https://solved.ac/api/v3/user/show?handle=" + nickname)
                .get()
                .build();

        client.newCall(request1).enqueue(new Callback() {
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

                    // Background & icon
                    String backgroundUrl = json.getJSONObject("background").getString("backgroundImageUrl");
                    String iconUrl = json.getString("profileImageUrl");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(userInfo.this)
                                    .load(backgroundUrl)
                                    .placeholder(R.drawable.balloon_005)
                                    .error(R.drawable.balloon_005)
                                    .fallback(R.drawable.balloon_005)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(background);

                            Glide.with(userInfo.this)
                                    .load(iconUrl)
                                    .placeholder(R.drawable.default_profile)
                                    .error(R.drawable.default_profile)
                                    .fallback(R.drawable.default_profile)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .apply(new RequestOptions().circleCrop())
                                    .into(icon);

                            // bio
                            try {
                                bio.setText(json.getString("bio"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // Tier
                    tier.setImageResource(getResources().getIdentifier("tier_" + json.getInt("tier"), "drawable", userInfo.this.getPackageName()));

                    // nickname
                    nicknameText.setText(json.getString("handle"));

                    // rank
                    rank.setText("# " + f.format(json.getInt("rank")));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // tierText
                                int tierTextId = getResources().getIdentifier("tier_" + json.getInt("tier"), "string", userInfo.this.getPackageName());
                                String ratingText = getResources().getString(tierTextId) + " " + f.format(json.getInt("rating"));
                                tierText.setText(ratingText);

                                if (json.getInt("tier") <= 30) {
                                    int tierColorId = getResources().getIdentifier("tier_" + json.getInt("tier"), "color", userInfo.this.getPackageName());
                                    tierText.setTextColor(getResources().getColor(tierColorId, null));
                                } else {
                                    Shader gradiant = new LinearGradient(
                                            0, 0, 0, tierText.getTextSize(),
                                            new int[] {
                                                    getResources().getColor(R.color.tier_31_start, null),
                                                    getResources().getColor(R.color.tier_31_center, null),
                                                    getResources().getColor(R.color.tier_31_end, null)},
                                            null, Shader.TileMode.CLAMP
                                    );

                                    tierText.getPaint().setShader(gradiant);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    // ratingByProblemsSumScore
                    ratingByProblemsSumScore.setText("+ " + f.format(json.getInt("ratingByProblemsSum")));

                    // ratingByClassText
                    ratingByClassText.setText("CLASS " + json.getInt("class"));

                    // ratingByClassScore
                    ratingByClassScore.setText("+ " + f.format(json.getInt("ratingByClass")));

                    // ratingBySolvedCountText
                    ratingBySolvedCountText.setText(f.format(json.getInt("solvedCount")) + "문제 해결");

                    // ratingBySolvedCountScore
                    ratingBySolvedCountScore.setText("+ " + f.format(json.getInt("ratingBySolvedCount")));

                    // ratingByVoteCountText
                    ratingByVoteCountText.setText(f.format(json.getInt("voteCount")) + "문제 기여");

                    // ratingByVoteCountScore
                    ratingByVoteCountScore.setText("+ " + f.format(json.getInt("ratingByVoteCount")));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // maxStreak
                            try {
                                maxStreak.setText("최대 " + f.format(json.getInt("maxStreak")) + "일");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // totalExp
                            try {
                                totalExp.setText("경험치 " + f.format(json.getLong("exp")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                response.body().close();
            }
        });

        Request request2 = builder
                .url("https://solved.ac/api/v3/user/problem_stats?handle=" + nickname)
                .get()
                .build();

        client.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { return; }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 404 || response.body() == null) {
                    response.body().close();
                    return;
                }

                try {
                    long totalSolve = 0, totalExp = 0;
                    long[][] result = {{0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}, {0, 0}};

                    JSONArray jsonArray = new JSONArray(response.body().string());

                    for (int i=0; i<6; i++) {
                        result[i][0] = 0L
                                + jsonArray.getJSONObject(i * 5 + 1).getInt("solved")
                                + jsonArray.getJSONObject(i * 5 + 2).getInt("solved")
                                + jsonArray.getJSONObject(i * 5 + 3).getInt("solved")
                                + jsonArray.getJSONObject(i * 5 + 4).getInt("solved")
                                + jsonArray.getJSONObject(i * 5 + 5).getInt("solved");

                        result[i][1] = 0L
                                + jsonArray.getJSONObject(i * 5 + 1).getInt("exp")
                                + jsonArray.getJSONObject(i * 5 + 2).getInt("exp")
                                + jsonArray.getJSONObject(i * 5 + 3).getInt("exp")
                                + jsonArray.getJSONObject(i * 5 + 4).getInt("exp")
                                + jsonArray.getJSONObject(i * 5 + 5).getInt("exp");

                        totalSolve += result[i][0];
                        totalExp += result[i][1];

                        Log.i(TAG, result[i][0] + "\t" + result[i][1]);
                    }

                    long finalTotalSolve = totalSolve;
                    long finalTotalExp = totalExp;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // bronze
                            bronzeSolve.setText(f.format(result[0][0]));
                            bronzeSolvePer.setText(fp.format(100.0f * result[0][0] / finalTotalSolve) + "%");
                            bronzeExp.setText(f.format(result[0][1]));
                            bronzeExpPer.setText(fp.format(100.0f * result[0][1] / finalTotalExp) + "%");

                            // silver
                            silverSolve.setText(f.format(result[1][0]));
                            silverSolvePer.setText(fp.format(100.0f * result[1][0] / finalTotalSolve) + "%");
                            silverExp.setText(f.format(result[1][1]));
                            silverExpPer.setText(fp.format(100.0f * result[1][1] / finalTotalExp) + "%");

                            // gold
                            goldSolve.setText(f.format(result[2][0]));
                            goldSolvePer.setText(fp.format(100.0f * result[2][0] / finalTotalSolve) + "%");
                            goldExp.setText(f.format(result[2][1]));
                            goldExpPer.setText(fp.format(100.0f * result[2][1] / finalTotalExp) + "%");

                            // platinum
                            platinumSolve.setText(f.format(result[3][0]));
                            platinumSolvePer.setText(fp.format(100.0f * result[3][0] / finalTotalSolve) + "%");
                            platinumExp.setText(f.format(result[3][1]));
                            platinumExpPer.setText(fp.format(100.0f * result[3][1] / finalTotalExp) + "%");

                            // diamond
                            diamondSolve.setText(f.format(result[4][0]));
                            diamondSolvePer.setText(fp.format(100.0f * result[4][0] / finalTotalSolve) + "%");
                            diamondExp.setText(f.format(result[4][1]));
                            diamondExpPer.setText(fp.format(100.0f * result[4][1] / finalTotalExp) + "%");

                            // ruby
                            rubySolve.setText(f.format(result[5][0]));
                            rubySolvePer.setText(fp.format(100.0f * result[5][0] / finalTotalSolve) + "%");
                            rubyExp.setText(f.format(result[5][1]));
                            rubyExpPer.setText(fp.format(100.0f * result[5][1] / finalTotalExp) + "%");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                response.body().close();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(userInfo.this, userLobby.class);
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
