package de.goldendeveloper.votemanager.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.votemanager.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class Discord {

    private JDA bot;

    public static String getCmdShutdown = "shutdown";
    public static String getCmdRestart = "restart";
    public static String cmdHelp = "help";

    public static final String cmdSettings = "settings";
    public static final String cmdSettingsSubCmdSetProcessChannel = "set-process-channel";
    public static final String cmdSettingsSubCmdSetClosedChannel = "set-closed-channel";
    public static final String cmdSettingsSubCmdSetOpenChannel = "set-open-channel";
    public static final String cmdSettingsSubCmdOptionChannel = "textchannel";

    public Discord(String Token) {
        try {
            bot = JDABuilder.createDefault(Token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.STICKER, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS, CacheFlag.VOICE_STATE)
                    .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_BANS, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_INVITES, GatewayIntent.DIRECT_MESSAGE_TYPING,
                            GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGE_TYPING)
                    .setAutoReconnect(true)
                    .addEventListeners(new Events())
                    .build().awaitReady();
            registerCommands();
            if (Main.getDeployment()) {
                Online();
            }
            bot.getPresence().setActivity(Activity.playing("/help | " + bot.getGuilds().size() + " Servern"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        bot.upsertCommand(cmdHelp, "Zeigt dir eine Liste möglicher Befehle an!").queue();
        bot.upsertCommand(getCmdShutdown, "Fährt den Discord Bot herunter!").queue();
        bot.upsertCommand(getCmdRestart, "Startet den Discord Bot neu!").queue();
        bot.upsertCommand(cmdSettings, "Stellt den Discord Bot für diesen Server ein!")
                .addSubcommands(
                        new SubcommandData(cmdSettingsSubCmdSetProcessChannel, "Setzt den Todo Process Channel!").addOption(OptionType.CHANNEL, cmdSettingsSubCmdOptionChannel, "Der Channel in den die Aufgaben gesendet werden sollen!", true),
                        new SubcommandData(cmdSettingsSubCmdSetClosedChannel, "Setzt den Todo Closed Channel!").addOption(OptionType.CHANNEL, cmdSettingsSubCmdOptionChannel, "Der Channel in den die Aufgaben gesendet werden sollen!", true),
                        new SubcommandData(cmdSettingsSubCmdSetOpenChannel, "Setzt den Todo Open Channel!").addOption(OptionType.CHANNEL, cmdSettingsSubCmdOptionChannel, "Der Channel in den die Aufgaben gesendet werden sollen!", true)
                ).queue();
    }

    private void Online() {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
        if (Main.getRestart()) {
            embed.setColor(0x33FFFF);
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Neustart erfolgreich"));
        } else {
            embed.setColor(0x00FF00);
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "ONLINE"));
        }
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(getBot().getSelfUser().getName(), getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Gestartet als", bot.getSelfUser().getName()));
        embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(bot.getGuilds().size())));
        embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDFE2 Gestartet"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Version", getProjektVersion()));
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", getBot().getSelfUser().getAvatarUrl()));
        embed.setTimestamp(new Date().toInstant());
        new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build());
    }

    public String getProjektVersion() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("version");
    }

    public String getProjektName() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("name");
    }

    public JDA getBot() {
        return bot;
    }
}

