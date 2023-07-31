package com.example.aichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
   List<Message> messageList;
   MessageAdapter messageAdapter;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
OkHttpClient client=new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).build();
   // OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageList=new ArrayList<>();

        recyclerView=findViewById(R.id.recycler_view);
        messageEditText=findViewById(R.id.message_edit_text);
        welcomeTextView=findViewById(R.id.welcome_text);
        sendButton=findViewById(R.id.send_btn);
        //set up recycler view
        messageAdapter =new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm=new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        sendButton.setOnClickListener((v) -> {
          String question = messageEditText.getText().toString().trim();
       //     Toast.makeText(this, question, Toast.LENGTH_LONG).show();
         addToChat(question,Message.SENT_BY_ME);
         messageEditText.setText("");
         callAPI(question);
         welcomeTextView.setVisibility(View.GONE);
        });
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }
    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);

    }

    void callAPI(String question){
//okHttp setUp
        messageList.add(new Message("Typing...",Message.SENT_BY_BOT));
        JSONObject jsonBody=new JSONObject();
        try {
          jsonBody.put("model","gpt-3.5-turbo");
          JSONArray messageArr=new JSONArray();
          JSONObject obj=new JSONObject();
          obj.put("role","user");
          obj.put("content",question);
          messageArr.put(obj);

          jsonBody.put("messages",messageArr);

        } catch (JSONException e) {
        e.printStackTrace();
        }
        RequestBody body=RequestBody.create(jsonBody.toString(),JSON);
        Request request =new Request.Builder()
                .url(" \n" +
                        "https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer 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")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Failed To load responce due to" +e.getMessage());

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
              if(response.isSuccessful()){
                  JSONObject jsonObject = null;
                  try {
                      jsonObject = new JSONObject(response.body().string());
                      JSONArray jsonArray=jsonObject.getJSONArray("choices");
                      String result =jsonArray.getJSONObject(0).getString("text");
                      addResponse(result.trim());
                  } catch (JSONException e) {
                      e.printStackTrace();   //   throw new RuntimeException(e);
                  }


              }else{
                  addResponse("Failed To load responce due to"+ response.body().toString());

              }








            }
        });

    }
}