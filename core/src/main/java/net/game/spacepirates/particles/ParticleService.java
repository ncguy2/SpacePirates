package net.game.spacepirates.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import net.game.spacepirates.particles.system.AbstractParticleSystem;
import net.game.spacepirates.services.BaseService;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;
import net.game.spacepirates.util.io.Json;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParticleService extends BaseService {

    public static final String PARTICLE_BLOCK_PATH = "particles/compute/blocks/";
    public static final String PARTICLE_PROFILE_PATH = "particles/compute/profiles/";
    public static final String PARTICLE_META_EXTENSION = ".json";
    public static final int MAX_PARTICLE_COUNT = 1_000_000;

    private final List<ParticleBlock> blockRegistry = new ArrayList<>();
    private final Set<ParticleProfile> profiles = new HashSet<>();

    private final Map<AbstractParticleSystem, List<Integer>> allocationMap = new HashMap<>();

    private ParticleBuffer buffer;
    private float globalLife;

    public ParticleService() {
        registerDefaultParticleBlocks();
        registerDefaultProfiles();
        buffer = new ParticleBuffer(MAX_PARTICLE_COUNT);
    }

    public float getGlobalLife() {
        return globalLife;
    }

    public void registerParticleBlock(ParticleBlock block) {
        blockRegistry.add(block);
    }

    public ParticleBuffer getBuffer() {
        return buffer;
    }

    public Optional<ParticleBlock> getParticleBlock(String name) {
        return blockRegistry.stream()
                            .filter(b -> b.name.equalsIgnoreCase(name))
                            .findFirst();
    }

    public void registerProfile(ParticleProfile profile) {
        profiles.add(profile);
    }

    public Optional<ParticleProfile> getProfile(final String name) {
        return profiles.stream()
                       .filter(p -> p.name.equalsIgnoreCase(name))
                       .findFirst();
    }

    public synchronized int[] issueIndices(int amount, AbstractParticleSystem system) {
        int[] ints = IntStream.range(0, Integer.MAX_VALUE)
                              .filter(this::isIndexFree)
                              .limit(amount)
                              .toArray();
        addIssuedIndices(system, ints);
        return ints;
    }

    public synchronized void releaseDeadIndices() {
        int[] deadList = getDeadList();
        if (deadList.length <= 0) {
            return;
        }
        releaseIndices(deadList);
    }

    public int[] getDeadList() {
        int amount = 0;
        byte[] bytes;

        ShaderStorageBufferObject deadBuffer = buffer.getDeadBuffer();
        ByteBuffer map = deadBuffer.getData();
        map.position(0);

        amount = map.getInt(0);

        if (amount == 0) {
            map.position(0);
            return new int[0];
        }

        bytes = new byte[amount * Integer.BYTES];
        map.position(Integer.BYTES);
        map.get(bytes);

        map.putInt(0, 0);
        map.position(0);

        deadBuffer.setData(map);

        int[] indices = new int[amount];

        byte[] tmpArr = new byte[Integer.BYTES];
        for (int i = 0; i < bytes.length; i += Integer.BYTES) {
            System.arraycopy(bytes, i, tmpArr, 0, Integer.BYTES);
            indices[i / Integer.BYTES] = ByteBuffer.wrap(tmpArr).getInt();
        }

        return indices;
    }

    @Override
    public String name() {
        return "Particles";
    }

    @Override
    public Class<? extends BaseService> getServiceClass() {
        return ParticleService.class;
    }

    public Optional<AbstractParticleSystem> buildSystem(ParticleProfile profile) {
        return Optional.ofNullable(profile.create(buffer));
    }

    public void update(float delta) {
        globalLife += delta;
        releaseDeadIndices();
    }

    public void release(AbstractParticleSystem system, int[] indices) {
        Arrays.stream(indices)
              .boxed()
              .forEach(getInts(system)::remove);
    }

    protected void registerDefaultParticleBlocks() {
        getDefaults(PARTICLE_BLOCK_PATH, s -> s.endsWith(PARTICLE_META_EXTENSION))
                .map(s -> Json.from(s, ParticleBlock.class))
                .forEach(this::registerParticleBlock);
    }

    protected void registerDefaultProfiles() {
        getDefaults(PARTICLE_PROFILE_PATH, s -> s.endsWith(PARTICLE_META_EXTENSION))
                .map(s -> Json.from(s, ParticleProfile.class))
                .forEach(this::registerProfile);
    }

    protected Stream<String> getDefaults(String root, Predicate<String> fileFilter) {
        FileHandle dir = Gdx.files.internal(root);
        String s = dir.readString();

        String[] fileNames = s.split("\n");
        List<FileHandle> handles = new ArrayList<>();
        for (String fileName : fileNames) {
            if (fileFilter.test(fileName)) {
                handles.add(Gdx.files.internal(root + "/" + fileName));
            }
        }

        return handles.stream().map(FileHandle::readString);
    }

    private boolean isIndexFree(int idx) {
        return !isIndexIssued(idx);
    }

    private void addIssuedIndices(AbstractParticleSystem sys, int... indices) {
        List<Integer> ints = getInts(sys);
        Arrays.stream(indices).forEach(ints::add);
    }

    public List<Integer> getInts(AbstractParticleSystem sys) {
        if(!allocationMap.containsKey(sys)) {
            allocationMap.put(sys, new ArrayList<>());
        }
        return allocationMap.get(sys);
    }

    public Set<AbstractParticleSystem> getKeys() {
        allocationMap.keySet()
                     .stream()
                     .filter(s -> allocationMap.get(s).isEmpty())
                     .collect(Collectors.toList())
                     .forEach(allocationMap::remove);
        return allocationMap.keySet();
    }

    private boolean isIndexIssued(int idx) {
        for (AbstractParticleSystem key : getKeys()) {
            if(getInts(key).contains(idx)) {
                return true;
            }
        }

        return false;
    }

    private void releaseIndices(int... indices) {

        System.out.println("Releasing dead indices, found " + indices.length);

        List<Integer> indexList = IntStream.of(indices)
                                           .boxed()
                                           .collect(Collectors.toList());
        allocationMap.values().forEach(l -> l.removeAll(indexList));
    }
}
