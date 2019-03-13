package net.game.spacepirates.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import net.game.spacepirates.services.BaseService;
import net.game.spacepirates.util.buffer.ShaderStorageBufferObject;
import net.game.spacepirates.util.io.Json;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ParticleService extends BaseService {

    public static final String PARTICLE_BLOCK_PATH = "particles/compute/blocks/";
    public static final String PARTICLE_PROFILE_PATH = "particles/compute/profiles/";
    public static final String PARTICLE_META_EXTENSION = ".json";
    public static final int MAX_PARTICLE_COUNT = 1_000_000;

    private final List<Integer> issuedIndices = new ArrayList<>();
    private final List<ParticleBlock> blockRegistry = new ArrayList<>();
    private final Set<ParticleProfile> profiles = new HashSet<>();

    private ParticleBuffer buffer;

    public ParticleService() {
        registerDefaultParticleBlocks();
        registerDefaultProfiles();
        buffer = new ParticleBuffer(MAX_PARTICLE_COUNT);
    }

    public void registerParticleBlock(ParticleBlock block) {
        blockRegistry.add(block);
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

    public synchronized int[] issueIndices(int amount) {
        int[] ints = IntStream.range(Integer.MIN_VALUE, Integer.MAX_VALUE)
                              .filter(this::isIndexFree)
                              .limit(amount)
                              .toArray();
        addIssuedIndices(ints);
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
        ByteBuffer map = deadBuffer.map(GL15.GL_READ_WRITE);
        try {
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
        } finally {
            // The buffer must always be unmapped after use
            deadBuffer.unmap();
        }

        int[] indices = new int[amount];
        byte[] tmpArr = new byte[Integer.BYTES];
        for (int i = 0; i < bytes.length; i += Integer.BYTES) {
            System.arraycopy(bytes, i, tmpArr, 0, Integer.BYTES);
            indices[i] = ByteBuffer.wrap(tmpArr).getInt();
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

    protected void registerDefaultParticleBlocks() {
        getDefaults(PARTICLE_BLOCK_PATH, s -> s.endsWith(PARTICLE_META_EXTENSION)).map(s -> Json.from(s, ParticleBlock.class))
                                                                                  .forEach(this::registerParticleBlock);
    }

    protected void registerDefaultProfiles() {
        getDefaults(PARTICLE_PROFILE_PATH, s -> s.endsWith(PARTICLE_META_EXTENSION)).map(s -> Json.from(s, ParticleProfile.class))
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

    private void addIssuedIndices(int... indices) {
        Arrays.stream(indices).forEach(issuedIndices::add);
    }

    private boolean isIndexIssued(int idx) {
        return issuedIndices.contains(idx);
    }

    private void releaseIndices(int... indices) {
        Arrays.stream(indices).forEach(issuedIndices::remove);
    }
}
