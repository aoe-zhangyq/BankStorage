package net.natte.bankstorage.world;

import java.util.Map;
import java.util.UUID;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.natte.bankstorage.BankStorage;
import net.natte.bankstorage.container.BankItemStorage;
import net.natte.bankstorage.container.BankType;

public class BankStateSaverAndLoader extends PersistentState {

    private GlobalBankState state;

    public BankStateSaverAndLoader(){
        this.state = new GlobalBankState();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbtCompound) {
        
        BankStorage.LOGGER.info("Saving banks to nbt");

        VersionStateSaverAndLoader.writeNbt(this.state, nbtCompound);
        BankStorage.LOGGER.info("Saving done");


        return nbtCompound;
    }

    public static BankStateSaverAndLoader createFromNbt(NbtCompound nbtCompound) {

        BankStorage.LOGGER.info("Loading banks from nbt");

        BankStateSaverAndLoader stateSaverAndLoader = VersionStateSaverAndLoader.readNbt(nbtCompound);

        BankStorage.LOGGER.info("Loading done");

        return stateSaverAndLoader;
    }

    public static BankStateSaverAndLoader getServerStateSaverAndLoader(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        BankStateSaverAndLoader state = persistentStateManager.getOrCreate(
                new PersistentState.Type<>(
                        BankStateSaverAndLoader::new,
                        BankStateSaverAndLoader::createFromNbt,
                        DataFixTypes.SAVED_DATA_MAP_DATA),
                BankStorage.MOD_ID);

        state.markDirty();

        return state;
    }


    public BankItemStorage getOrCreate(UUID uuid, BankType type) {
        if (this.state.getBankMap().containsKey(uuid)) {
            BankItemStorage old = this.state.getBankMap().get(uuid);//.withDisplayName(name);
            if (old.type != type) {
                BankItemStorage ugpraded = old.asType(type);

                this.state.getBankMap().put(uuid, ugpraded);
                return ugpraded;
            }
            return old;

        } else {
            BankStorage.LOGGER.info("creating new bank with uuid " + uuid);
            BankItemStorage bankItemStorage = new BankItemStorage(type, uuid);
            this.state.getBankMap().put(uuid, bankItemStorage);
            return bankItemStorage;//.withDisplayName(name);
        }
    }

    public BankItemStorage get(UUID uuid) {
        return this.state.get(uuid);
    }

    public Map<UUID, BankItemStorage> getBankMap(){
        return this.state.getBankMap();
    }

    public GlobalBankState getState() {
        return this.state;
    }
}
