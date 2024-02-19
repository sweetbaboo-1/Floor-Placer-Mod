package net.sweetbaboo.floorplacermod.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.sweetbaboo.floorplacermod.FloorPlacerMod;
import net.sweetbaboo.floorplacermod.SyncmaticaConfigAccess;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SchematicFileArgumentType implements ArgumentType<String> {
    private static final Gson GSON = new Gson();
    private static final CommandSyntaxException READ_ERROR = new SimpleCommandExceptionType(Text.of("Error reading schematic data. Check logs game for details.")).create();
    private static final CommandSyntaxException NO_SCHEMATICS = new SimpleCommandExceptionType(Text.of("No loaded schematics found")).create();
    private static final DynamicCommandExceptionType INVALID_NAME = new DynamicCommandExceptionType(file -> Text.of("Invalid schematic: " + file));

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        Map<String, String> schems = getSchematics();

        String name = reader.readUnquotedString();
        String hash = schems.get(name);
        if (hash == null) throw INVALID_NAME.create(name);
        return hash + ".litematic";
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Collection<String> schems;
        try {
            schems = getSchematics().keySet();
        } catch (CommandSyntaxException e) {
            schems = Collections.emptyList();
        }
        return CommandSource.suggestMatching(schems, builder);
    }

    public Map<String, String> getSchematics() throws CommandSyntaxException {
        File conf = SyncmaticaConfigAccess.getPlacementsConfig();
        if (conf == null || !conf.exists() || !conf.canRead())
            throw NO_SCHEMATICS;
        try (FileReader reader = new FileReader(conf)) {
            Map<String, String> result = new HashMap<>();
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            JsonArray placements = json.getAsJsonArray("placements");

            for (JsonElement element : placements) {
                JsonObject placement = element.getAsJsonObject();
                String hash = placement.get("hash").getAsString();
                String fileName = placement.get("file_name").getAsString();
                result.put(fileName, hash);
            }
            return result;
        } catch (Exception e) {
            FloorPlacerMod.LOGGER.error("Failed to load synced schematics", e);
            throw READ_ERROR;
        }
    }
}