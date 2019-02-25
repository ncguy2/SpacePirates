package net.game.spacepirates.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import net.game.spacepirates.particles.systems.AbstractParticleSystem;
import net.game.spacepirates.util.DeferredCalls;
import net.game.spacepirates.util.io.Json;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParticleManager {

    private static ParticleManager instance;
    public static ParticleManager get() {
        if(instance == null) {
            instance = new ParticleManager();
        }
        return instance;
    }

    protected List<ParticleBlock> blockRegistry;
    protected Set<ParticleProfile> profiles;
    protected List<Integer> availableIndices;
    protected final List<AbstractParticleSystem> systems;
    public static final int MaxBindingPoints = 96;
    protected List<Integer> takenBindingPoints;

    private ParticleManager() {
        blockRegistry = new CopyOnWriteArrayList<>();
        systems = new CopyOnWriteArrayList<>();
        profiles = new HashSet<>();
        takenBindingPoints = new CopyOnWriteArrayList<>();
        availableIndices = new CopyOnWriteArrayList<>();

        registerDefaultProfiles();
        registerDefaultParticleBlocks();
    }

    protected void registerDefaultParticleBlocks() {
        String path = "particles/compute/blocks/";
        getDefaults(path, s -> s.endsWith(".json")).map(s -> Json.from(s, ParticleBlock.class)).forEach(this::registerParticleBlock);
    }

    public void registerParticleBlock(ParticleBlock block) {
        blockRegistry.add(block);
    }

    public Optional<ParticleBlock> getParticleBlock(String name) {
        return blockRegistry.stream()
                .filter(b -> b.name.equalsIgnoreCase(name))
                .findFirst();
    }

    protected void registerDefaultProfiles() {
        String path = "particles/compute/profiles/";
        getDefaults(path, s -> s.endsWith(".json")).map(s -> Json.from(s, ParticleProfile.class)).forEach(this::registerProfile);
    }

    protected Stream<String> getDefaults(String root, Predicate<String> fileFilter) {
        FileHandle dir = Gdx.files.internal(root);
        String s = dir.readString();

        String[] fileNames = s.split("\n");
        List<FileHandle> handles = new ArrayList<>();
        for (String fileName : fileNames) {
            if(fileFilter.test(fileName)) {
                handles.add(Gdx.files.internal(root + "/" + fileName));
            }
        }

        return handles.stream().map(FileHandle::readString);
    }

    public void registerProfile(ParticleProfile profile) {
        profiles.add(profile);
    }

    public Optional<ParticleProfile> getProfile(final String name) {
        return profiles.stream()
                .filter(p -> p.name.equalsIgnoreCase(name))
                .findFirst();
    }

    public Optional<AbstractParticleSystem> buildSystem(String name) {
        return getProfile(name).flatMap(this::buildSystem);
    }

    public Optional<AbstractParticleSystem> buildSystem(ParticleProfile profile) {
        return Optional.ofNullable(profile.create());
    }

    public int getAvailableBindingPoint() {
        return IntStream.range(1, MaxBindingPoints)
                .filter(i -> !takenBindingPoints.contains(i))
                .findFirst()
                .orElse(-1);
    }

    public void addSystem(AbstractParticleSystem system) {
        system.bufferId = getAvailableBindingPoint();
        synchronized (systems) {
            systems.add(system);
        }
        takenBindingPoints.add(system.bufferId);
    }

    public void removeSystem(AbstractParticleSystem system) {
        removeSystem(system, null);
    }

    public void removeSystem(AbstractParticleSystem system, Runnable callback) {
        final int bufferId = system.bufferId;
        DeferredCalls.get().post(system.duration, () -> {
            synchronized (systems) {
                systems.remove(system);
            }
            takenBindingPoints.remove((Integer) bufferId);
            if(callback != null) {
                callback.run();
            }
        });
    }

    public void systems(Consumer<AbstractParticleSystem> task) {
        synchronized (systems) {
            systems.forEach(task);
        }
    }

    public void blocks(Consumer<ParticleBlock> task) {
        blockRegistry.forEach(task);
    }

    public void profiles(Consumer<ParticleProfile> task) {
        profiles.forEach(task);
    }

    public void update(float delta) {
        AbstractParticleSystem.GlobalLife += delta;
        ArrayList<AbstractParticleSystem> systems;
        synchronized (this.systems) {
            systems = new ArrayList<>(this.systems);
        }
        systems.forEach(sys -> sys.update(delta));
    }

}
