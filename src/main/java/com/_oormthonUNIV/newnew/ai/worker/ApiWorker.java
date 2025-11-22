package com._oormthonUNIV.newnew.ai.worker;

import com._oormthonUNIV.newnew.global.messageQueue.task.SurveyStatisticsTask;
import com._oormthonUNIV.newnew.global.properties.OpenApiProperties;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;

@Slf4j
@Component
public class ApiWorker {

    private final BlockingQueue<SurveyStatisticsTask> queue;
    private final OpenAIClient client;
    private final OpenApiProperties openApiProperties;

    public ApiWorker(
            BlockingQueue<SurveyStatisticsTask> queue,
            OpenApiProperties properties
    ) {
        this.queue = queue;
        this.openApiProperties = properties;
        if (openApiProperties.getOPENAI_API_KEY() != null
                && !openApiProperties.getOPENAI_API_KEY().isBlank()
        ) {
            this.client = OpenAIOkHttpClient.builder()
                    .apiKey(openApiProperties.getOPENAI_API_KEY())
                    .build();
        } else {
            // 환경변수 OPENAI_API_KEY 사용 (또는 openai.apiKey system property)
            this.client = OpenAIOkHttpClient.fromEnv();
        }
    }

    @SneakyThrows
    @PostConstruct
    public void startWorker() {
        int workerCount = 3; // 필요에 따라 조절
        for (int i = 0; i < workerCount; i++) {
            Thread workerThread = new Thread(this::processQueue, "ApiWorker-" + i);
            workerThread.setDaemon(true);
            workerThread.start();
        }
    }

    private void processQueue() {
        while (true) {
            SurveyStatisticsTask task = null;
            try {
                task = queue.take(); // 이게 InterruptedException 던짐
                String response = chatOnce("");
                // ... 처리 로직

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // 반드시 필요
                return; // 또는 break — 스레드를 종료

            } catch (Exception e) {
                if (task == null || task.getReTryCount() >= 3) {
                    log.error("[ApiWorker] Error while processing task: {}", task, e);
                } else {
                    task.incReTryCount();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    queue.offer(task);
                }
            }
        }
    }


    public String chatOnce(String prompt) {
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .addUserMessage(prompt)
                .model(ChatModel.GPT_4_1_MINI) // 원하는 모델로 변경
                .build();

        ChatCompletion comp = client.chat().completions().create(params);
        // 첫번째 choice의 메시지 텍스트를 리턴

        if (comp == null || comp.choices() == null || comp.choices().isEmpty()) {
            throw new IllegalStateException("OpenAI 응답이 비어있습니다.");
        }
        log.info("[API Call] response -> {}", comp);

        // 보통 첫 번째 선택지를 사용
        String first = String.valueOf(comp.choices().getFirst().message().content());
        return first == null ? "" : first.strip();

    }

}