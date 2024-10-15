This is a project for learning.
In short, this service is a telegram (TG) bot which sits in some group and deters users from forwarding anything in a group.
Service uses Spring Boot as a core. YandexGPT is used to imitate human speech and variate messages. TelegramBots lib is
used to poll bot for new messages. I deployed this bot in Yandex Serverless Container with Yandex Trigger to run bot
once in a while to handle new messages and save money by not having a VM running 24/7.

So here are the steps:
1) Register a TG bot using this tutorial https://core.telegram.org/bots/tutorial.
2) Create a new Java/Kotlin project. For serverless environment it is better if you avoid Spring altogether since
it takes too long to start with its injections and context preparations. I am lazy, so, uh, yeah...
3) Just initialize a long polling bot (step 1 link) according to this tutorial. Add some basic logic in ::onUpdateReceived
method and play around with bot in 1:1 chat in TG. 
Realize that bot is not authorized to write to anyone first. Also, bot can't read any group chat messages by default.
https://stackoverflow.com/questions/38565952/how-to-receive-messages-in-group-chats-using-telegram-bot-api
Disable privacy mode to see all the messages with ::onUpdateReceived method.
4) Run app in debug mode to check out the structure of a TG message. It is bothersome at first, so it is best quit
guessing and debug. You have to understand the Message structure to proceed.
5) Now add smart replies with YandexGPT. There are many models out there, but being blocked everywhere in google etc.
it is the least annoying option, perhaps.
For Yandex Cloud you will need a billing account created first. 
https://billing.yandex.cloud/accounts
https://yandex.cloud/en-ru/docs/billing/concepts/billing-account#individual_1
Then create a cloud folder.
6) While in cloud search for 'foundation models', go to 'chat w/ yandexGpt'.
Talk with it for a bit and figure out how it works. Don't forget to copy and save reply/request JSONs for later.
7) Go back to the cloud folder, go to 'access bindings' tab and create a service account, a user w/ limited rights.
Certainly you didn't want to use your main profile... right? Add 'ai.languageModels.user' rights.
https://yandex.cloud/en-ru/docs/iam/concepts/users/service-accounts
Then get an API Key, a token which won't expire and grant us access to API.
https://yandex.cloud/en-ru/docs/iam/operations/api-key/create
https://yandex.cloud/en-ru/docs/iam/concepts/authorization/api-key
8) I would suggest to step up your game and play around with YandexGPT once again.
But this time using POSTMAN or curl. Now that you have an API key you can do it easily.
You will have to use a POST request to https://llm.api.cloud.yandex.net/foundationModels/v1/completion
You will need 2 things: your API Key and your cloud folder ID which is used in a body of a request.
`"modelUri": "gpt://<cloud-folder-id>/yandexgpt-lite"`
https://yandex.cloud/en-ru/docs/foundation-models/concepts/yandexgpt/
9) After playing around add an API Key to web service configs and now make bot to query YandexGPT with WebClient
to get a human-like speech.
10) But as TG tutorial suggests, bot has to run 24/7, and you can't do that with your home PC.
You will have to containerize the service and use some kind of cloud.
I chose Yandex Serverless Container to run bot every 30 mins or so to be cheap and have fun at the same time.
11) First you have to put your container into Yandex Container Registry. It just stores your container, it can't run it.
12) Then go to Serverless Containers and just run it. Supposedly, you won't need any special configurations to
make container reachable, so it should have no problems polling TG. Also, logs should work out of the box too.
If your logger writes to console, you should see logs automatically in Yandex Cloud Logging.
13) Don't forget to use ENVIRONMENT variables to change configs to your need. You can set ENV when in Serverless Container
config

## Container registry
### Setup yandex once
1) run `yc container list`
2) run `yc container registry create --name my-first-registry`
save `registry id`, you will need it to push your containers
3) run `yc container registry configure-docker`
### Do this for each new release of your service
1) see what images you got `docker images`
2) build new one from terminal from the project root 
`docker build -t cr.yandex/<registryid>/telegramapp:0.0.1 -f deploy/backend/Dockerfile .`
replace `<registryid>` with your id
3) push to Yandex `docker push cr.yandex/<registryid>/telegramapp:0.0.1`
replace `<registryid>` with your id