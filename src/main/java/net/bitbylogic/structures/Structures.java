package net.bitbylogic.structures;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.bitbylogic.structures.animation.StructureAnimationManager;
import net.bitbylogic.structures.animation.impl.BlockParticleAnimation;
import net.bitbylogic.structures.command.StructureCommand;
import net.bitbylogic.structures.listener.StructureEditorListener;
import net.bitbylogic.structures.listener.StructureListener;
import net.bitbylogic.structures.manager.StructureManager;
import net.bitbylogic.structures.placeholder.StructurePlaceholderExpansion;
import net.bitbylogic.utils.message.config.MessageProvider;
import net.bitbylogic.utils.message.format.Formatter;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

@Getter
public final class Structures extends JavaPlugin {

    private static final int METRICS_ID = 28974;

    private final File structureFile = new File(getDataFolder(), "structures.yml");

    private YamlConfiguration structureConfig;
    private MessageProvider messageProvider;

    private PaperCommandManager commandManager;

    private StructureAnimationManager animationManager;
    private StructureManager structureManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new Metrics(this, METRICS_ID);

        Formatter.registerConfig(new File(getDataFolder(), "config.yml"));

        if(!structureFile.exists()) {
            try {
                structureFile.createNewFile();
            } catch (IOException e) {
                getLogger().log(Level.SEVERE, "Failed to create structures file!", e);
            }
        }

        this.structureConfig = YamlConfiguration.loadConfiguration(structureFile);
        this.messageProvider = new MessageProvider(getConfig().getConfigurationSection("Messages"));

        messageProvider.getPlaceholders().add(s -> {
            if(!s.contains("%prefix%")) {
                return s;
            }

            return s.replace("%prefix%", messageProvider.getMessage("Prefix"));
        });

        this.commandManager = new PaperCommandManager(this);

        this.animationManager = new StructureAnimationManager();
        animationManager.registerAnimation(new BlockParticleAnimation());

        this.structureManager = new StructureManager(this, animationManager);

        commandManager.getCommandCompletions().registerCompletion("structureIds",
                context -> structureManager.getStructures().keySet());

        commandManager.getCommandCompletions().registerCompletion("animationIds",
                context -> animationManager.getRegisteredAnimations().keySet());

        commandManager.registerCommand(new StructureCommand(messageProvider, animationManager, structureManager));

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new StructureListener(this, structureManager), this);
        pluginManager.registerEvents(new StructureEditorListener(messageProvider, structureManager), this);

        if(getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new StructurePlaceholderExpansion(structureManager).register();
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        if(messageProvider != null) {
            messageProvider.reload(getConfig().getConfigurationSection("Messages"));
        }

        if(structureManager == null) {
            return;
        }

        structureConfig = YamlConfiguration.loadConfiguration(structureFile);

        structureManager.loadStructures();
    }

    public void saveStructureConfig() {
        try {
            structureConfig.save(structureFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to save structures config!", e);
        }
    }

}
