package xyz.iwolfking.woldsvaults.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import xyz.iwolfking.woldsvaults.api.data.level.PersonalLevelCapData;

public class PersonalLevelCapCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("woldsvaults")
                        .then(Commands.literal("personalCap")
                                .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
                                .then(Commands.literal("resetPersonalCap")
                                        .executes(PersonalLevelCapCommand::resetPersonalCap)
                                )

                                .then(Commands.literal("setPersonalCap")
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 100))
                                                .executes(PersonalLevelCapCommand::setPersonalCap)
                                        )

                                )
                        )
        );
    }

    private static int resetPersonalCap(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        PersonalLevelCapData data = PersonalLevelCapData.get(context.getSource().getLevel());
        data.resetPersonalCap(context.getSource().getPlayerOrException().getUUID());
        return 1;
    }

    private static int setPersonalCap(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        PersonalLevelCapData data = PersonalLevelCapData.get(context.getSource().getLevel());
        data.setPersonalCap(context.getSource().getPlayerOrException().getUUID(), IntegerArgumentType.getInteger(context, "level"));
        return 1;
    }

}
