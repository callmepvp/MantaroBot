/*
 * Copyright (C) 2016-2020 David Alejandro Rubio Escares / Kodehawa
 *
 *  Mantaro is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Mantaro is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mantaro.  If not, see http://www.gnu.org/licenses/
 *
 */

package net.kodehawa.mantarobot.commands.utils.reminders;

import io.prometheus.client.Counter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.kodehawa.mantarobot.MantaroBot;
import net.kodehawa.mantarobot.data.MantaroData;
import net.kodehawa.mantarobot.utils.commands.EmoteReference;
import org.json.JSONObject;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;

public class ReminderTask {
    private static final Counter reminderCount = Counter.build()
                                                         .name("reminders_logged").help("Logged reminders")
                                                         .register();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ReminderTask.class);
    
    public static void handle() {
        log.debug("Checking reminder data...");
        try(Jedis j = MantaroData.getDefaultJedisPool().getResource()) {
            Set<String> reminders = j.zrange("zreminder", 0, 14);
            log.debug("Reminder check - remainder is: {}", reminders.size());
            
            for(String rem : reminders) {
                try {
                    JSONObject data = new JSONObject(rem);
                    
                    long fireAt = data.getLong("at");
                    //If the time has passed...
                    //System.out.println("time: " + System.currentTimeMillis() + ", expected: " + fireAt);
                    if(System.currentTimeMillis() >= fireAt) {
                        log.debug("Reminder date has passed, remind accordingly.");
                        String userId = data.getString("user");
                        String fullId = data.getString("id") + ":" + userId;
                        String guildId = data.getString("guild");
                        long scheduledAt = data.getLong("scheduledAt");
                        
                        String reminder = data.getString("reminder"); //The actual reminder data
                        
                        User user = MantaroBot.getInstance().getShardManager().getUserById(userId);
                        Guild guild = MantaroBot.getInstance().getShardManager().getGuildById(guildId);
                        
                        if(user == null) {
                            Reminder.cancel(userId, fullId);
                            return;
                        }
                        
                        reminderCount.inc();
                        Consumer<Throwable> ignore = (t) -> {
                        };
                        user.openPrivateChannel().queue(channel -> channel.sendMessage(
                                EmoteReference.POPPER + "**Reminder!**\n" + "You asked me to remind you of: " + reminder + "\nAt: " + new Date(scheduledAt) +
                                        (guild != null ? "\n*Asked on: " + guild.getName() + "*" : "")
                        ).queue(sc -> {
                            //FYI: This only logs on debug the id data, no personal stuff. We don't see your personal data. I don't wanna see it either, lmao.
                            log.debug("Reminded {}. Removing from remind database", fullId);
                            //Remove reminder from our database.
                            Reminder.cancel(userId, fullId);
                        }, ignore));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
