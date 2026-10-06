package com.example.tuwaiqcapstone3.Service;

import com.example.tuwaiqcapstone3.API.ApiException;
import com.example.tuwaiqcapstone3.DTO.AiReviewCheckDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    @Value("${openrouter.api-key}")
    private String apikey;

    private final RestClient restClient= RestClient.builder().baseUrl("https://openrouter.ai/api/v1")
            .build();

    public AiReviewCheckDTO checkInappropriateReview(String comment){

        String prompt = """
        You are a content moderation assistant for a ride-sharing platform.

        Analyze the following passenger review comment and determine whether it is inappropriate.

        The comment can be written in Arabic or English.

        IMPORTANT LANGUAGE RULE:
        - Detect the language of the review comment automatically.
        - If the review is in English, write the reason in English.
        - If the review is in Arabic, write the reason in Arabic.
        - If the review contains both Arabic and English, use the language that is used most in the comment.

        A comment is inappropriate if it contains:
        - Profanity or offensive language
        - Insults or personal attacks
        - Threats or violent language
        - Hate speech or discrimination
        - Harassment or bullying
        - Sexual or explicit content
        - Spam or irrelevant promotional content
        - Personal or sensitive information

        A negative review is NOT automatically inappropriate.

        Users are allowed to honestly complain about:
        - Late arrival
        - Bad driving
        - Unclean car
        - Poor communication
        - Long waiting time
        - Other negative ride experiences

        Return ONLY valid JSON in exactly this format:

        {
          "inappropriate": true,
          "reason": "Offensive language"
        }

        If the comment is appropriate, return:

        {
          "inappropriate": false,
          "reason": "The comment is appropriate"
        }

        For Arabic comments, the reason must also be Arabic.

        Example Arabic inappropriate response:

        {
          "inappropriate": true,
          "reason": "إهانة أو إساءة شخصية"
        }

        Example Arabic appropriate response:

        {
          "inappropriate": false,
          "reason": "التعليق مناسب"
        }

        Review comment:
        """ + comment;

        Map<String, Object> request = Map.of(
                "model", "nvidia/nemotron-3-ultra-550b-a55b:free",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        Map response = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apikey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(Map.class);

        List choices = (List) response.get("choices");
        Map choice = (Map) choices.get(0);
        Map message = (Map) choice.get("message");

        String content = message.get("content").toString();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(content, AiReviewCheckDTO.class);
        } catch (Exception e) {
            throw new ApiException("AI response is invalid");
        }
    }

}
