curl -X POST \
  -H "Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsInNpZ25fdHlwZSI6IlNJR04ifQ.eyJhcGlfa2V5IjoiMDc1Yjk4M2YzY2Q1NGM3Njg3OTQ2OGUwMzBiOWJhNzgiLCJleHAiOjE3NjI2NzM5MDQzNTksInRpbWVzdGFtcCI6MTc2MjY3MjEwNDM2MH0.xtYSY_yipbQNTiMxYWNxAJ5F_wPDzcwNtLjOyj2ySyo" \
  -H "Content-Type: application/json" \
  -H "User-Agent: curl/7.88.1" \
  -d '{
    "model":"glm-4",
    "stream": true,
    "messages": [
        {
            "role": "user",
            "content": "1+1"
        }
    ]
  }' \
https://open.bigmodel.cn/api/paas/v4/chat/completions