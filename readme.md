# SimpleChatbridge 

This minecraft fabric mod lets you send and receive messages between a MC Server and a Discord channel. This mod only works on the server.

## Configuration

On first startup a config file will be created, which can be found at `.minecraft/config/simple_chatbridge/simple_chatbridge.json`. The server will not launch and you can fill out the values in the JSON file.

Things you will need for this mod to work:

- Discord Bot with Message Content Intent
- Discord Webhook

### How to get a discord bot with the right intents

1. Go to https://discord.com/developers/applications.
1. Click `new Application` and give your bot a name.
1. After you have created your bot, go to the `Bot` Navigation and reset its token.
1. Grab the token and put it in the config file. (**Make sure to not show your token to anyone!**)
1. In the section `Privileged Gateway Intents`, enable the `Message Content Intent`.
1. Go to `OAuth2 -> URL Generator` and click the `bot` scope.
1. Select the `Read Messages/View Channels` permission and copy/paste the url in a new browser tab.
1. Make the bot join your Discord Server and make sure that it can actually see the channel.
1. Turn on the `developer mode` in your Discord Client and copy/paste the chatbridge channel ID into the config file.
1. Create a webhook in your channel (give it a nice name and picture) and paste the URL in the config aswell.

If you are having issue with this mod, please open an issue or reach out to me in my [Discord](https://discord.gg/wmJ3WBYcZF) or send me an [Email](mailto:defnot001@gmail.com).