package com.github.Ramble21.command;

import com.github.Ramble21.commands.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.IOException;
import java.util.*;


public class CommandManager{
    public static final Map<String, Command> commands = new HashMap<>();

    public CommandManager(){
        commands.put("grab-ip", new GrabIp());
        commands.put("rizz-rater", new RizzRater());
        commands.put("state-flags", new StateFlags());
        commands.put("typeracer", new TypeRacerManager());
        commands.put("vocab", new Vocab());
        commands.put("update-logs", new UpdateLogs());
    }

    public Command getCommand(String name){
        return commands.get(name);
    }

    public void executeCommand(String commandName, SlashCommandInteractionEvent event) throws IOException {
        Command command = getCommand(commandName);
        if (command != null) {
            command.execute(event);
        }
        else {
            System.out.println("command is null theres a bug");
        }
    }

}