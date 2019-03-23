package net.game.spacepirates.input;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import net.game.spacepirates.render.buffer.FBO;
import net.game.spacepirates.render.buffer.FBOFactory;
import net.game.spacepirates.render.post.AbstractPostProcessor;
import net.game.spacepirates.render.post.EmissivePostProcessor;
import net.game.spacepirates.render.post.ParticlePostProcessor;
import net.game.spacepirates.render.post.PostProcessorContext;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.badlogic.gdx.graphics.GL20.GL_ONE;

public class PostProcessingCamera<CAMERA extends Camera> {

    public static List<String> texturesToRender = new ArrayList<>();
    static {
        texturesToRender.add("texture.diffuse");
        texturesToRender.add(ParticlePostProcessor.PARTICLE_TEXTURE_NAME);
        texturesToRender.add(EmissivePostProcessor.EMISSIVE_TEXTURE_NAME);
    }

    public static List<WeakReference<PostProcessingCamera>> cameraRefs = new ArrayList<>();
    private static void _register(PostProcessingCamera camera) {
        cameraRefs.add(new WeakReference<>(camera));
    }
    public static List<PostProcessingCamera> GetCameras() {
        List<WeakReference<PostProcessingCamera>> weakReferences = cameraRefs.stream().filter(e -> e.get() == null).collect(Collectors.toList());
        cameraRefs.removeAll(weakReferences);
        return cameraRefs.stream().map(Reference::get).collect(Collectors.toList());
    }

    public final CAMERA camera;
    public final List<AbstractPostProcessor> processors;
    public FBO fbo;
    public FBO flattenFbo;
    public Texture[] postProcessedTextures;

    public PostProcessingCamera(CAMERA camera, AbstractPostProcessor... processors) {


        for (AbstractPostProcessor processor : processors) {
            processor.init();
        }

        this.camera = camera;
        this.processors = Arrays.asList(processors);
        int width = Math.round(camera.viewportWidth);
        int height = Math.round(camera.viewportHeight);
        this.fbo = FBOFactory.buildDefaultBuffer(width, height);
        this.flattenFbo = new FBO(Pixmap.Format.RGBA8888, width, height, false);
        _register(this);
    }

    public CAMERA getCamera() {
        return camera;
    }

    public void resize(int width, int height) {
        if(fbo != null) {
            fbo.resize(width, height);
        }

        if(flattenFbo != null) {
            flattenFbo.resize(width, height);
        }

        forEach(p -> p.resize(width, height));
    }

    public PostProcessorContext process(SpriteBatch batch, float delta) {
        postProcessedTextures = fbo.getTextures().orElse(new Texture[0]);

        if(batch.isDrawing()) {
            System.err.println("Batch shouldn't be drawing here");
            batch.end();
        }

        String[] names = new String[] {
                "texture.diffuse",
                "texture.normal",
                "texture.emissive",
                "texture.metadata",
        };

        PostProcessorContext ctx = new PostProcessorContext(batch, camera, delta);
        ctx.depthBufferHandle = fbo.getDepthBufferHandle();
        for (int i = 0; i < postProcessedTextures.length; i++) {
            ctx.addTexture(postProcessedTextures[i], names[i]);
        }

        forEach(p -> p.render(ctx));
        return ctx;
    }

    public Texture flatten(SpriteBatch batch, PostProcessorContext context) {
        flattenFbo.begin();
        flattenFbo.clear(Color.BLACK, true, true);
        batch.setShader(null);
        batch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, flattenFbo.width(), flattenFbo.height()));
        batch.begin();

        for (Map.Entry<String, Texture> entry : context.namedTextures.entrySet()) {
            if(texturesToRender.contains(entry.getKey()) && !entry.getKey().endsWith(".additive")) {
                batch.draw(entry.getValue(), 0, 0, flattenFbo.width(), flattenFbo.height());
            }
        }

        batch.setBlendFunction(GL_ONE, GL_ONE);

        for (Map.Entry<String, Texture> entry : context.namedTextures.entrySet()) {
            if(texturesToRender.contains(entry.getKey()) && entry.getKey().endsWith(".additive")) {
                batch.draw(entry.getValue(), 0, 0, flattenFbo.width(), flattenFbo.height());
            }
        }

        batch.end();
        flattenFbo.end();

        return flattenFbo.getColorBufferTexture();
    }

    public Texture processAndFlatten(SpriteBatch batch, float delta) {
        return flatten(batch, process(batch, delta));
    }

    public void begin() {
        fbo.begin();
    }

    public void end() {
        fbo.end();
    }

    public void clear(Color colour, boolean clearDepth, boolean clearStencil) {
        fbo.clear(colour, clearDepth, clearStencil);
    }

    public Matrix4 projection() {
        return camera.projection;
    }
    public Matrix4 view() {
        return camera.view;
    }
    public Matrix4 inverseProjectionView() {
        return camera.invProjectionView;
    }
    public Matrix4 combined() {
        return camera.combined;
    }

    // Camera delegate

    public void update() {
        camera.update();
    }

    public void update(boolean updateFrustum) {
        camera.update(updateFrustum);
    }

    public void lookAt(float x, float y, float z) {
        camera.lookAt(x, y, z);
    }

    public void lookAt(Vector3 target) {
        camera.lookAt(target);
    }

    public void normalizeUp() {
        camera.normalizeUp();
    }

    public void rotate(float angle, float axisX, float axisY, float axisZ) {
        camera.rotate(angle, axisX, axisY, axisZ);
    }

    public void rotate(Vector3 axis, float angle) {
        camera.rotate(axis, angle);
    }

    public void rotate(Matrix4 transform) {
        camera.rotate(transform);
    }

    public void rotate(Quaternion quat) {
        camera.rotate(quat);
    }

    public void rotateAround(Vector3 point, Vector3 axis, float angle) {
        camera.rotateAround(point, axis, angle);
    }

    public void transform(Matrix4 transform) {
        camera.transform(transform);
    }

    public void translate(float x, float y, float z) {
        camera.translate(x, y, z);
    }

    public void translate(Vector3 vec) {
        camera.translate(vec);
    }

    public Vector3 unproject(Vector3 screenCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        return camera.unproject(screenCoords, viewportX, viewportY, viewportWidth, viewportHeight);
    }

    public Vector3 unproject(Vector3 screenCoords) {
        return camera.unproject(screenCoords);
    }

    public Vector3 project(Vector3 worldCoords) {
        return camera.project(worldCoords);
    }

    public Vector3 project(Vector3 worldCoords, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        return camera.project(worldCoords, viewportX, viewportY, viewportWidth, viewportHeight);
    }

    public Ray getPickRay(float screenX, float screenY, float viewportX, float viewportY, float viewportWidth, float viewportHeight) {
        return camera.getPickRay(screenX, screenY, viewportX, viewportY, viewportWidth, viewportHeight);
    }

    public Ray getPickRay(float screenX, float screenY) {
        return camera.getPickRay(screenX, screenY);
    }

    // Processors delegate

    public int size() {
        return processors.size();
    }

    public boolean isEmpty() {
        return processors.isEmpty();
    }

    public boolean contains(Object o) {
        return processors.contains(o);
    }

    public Iterator<AbstractPostProcessor> iterator() {
        return processors.iterator();
    }

    public Object[] toArray() {
        return processors.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return processors.toArray(a);
    }

    public boolean add(AbstractPostProcessor abstractPostProcessor) {
        return processors.add(abstractPostProcessor);
    }

    public boolean remove(Object o) {
        return processors.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return processors.containsAll(c);
    }

    public boolean addAll(Collection<? extends AbstractPostProcessor> c) {
        return processors.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends AbstractPostProcessor> c) {
        return processors.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        return processors.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return processors.retainAll(c);
    }

    public void replaceAll(UnaryOperator<AbstractPostProcessor> operator) {
        processors.replaceAll(operator);
    }

    public void sort(Comparator<? super AbstractPostProcessor> c) {
        processors.sort(c);
    }

    public void clear() {
        processors.clear();
    }

    public AbstractPostProcessor get(int index) {
        return processors.get(index);
    }

    public AbstractPostProcessor set(int index, AbstractPostProcessor element) {
        return processors.set(index, element);
    }

    public void add(int index, AbstractPostProcessor element) {
        processors.add(index, element);
    }

    public AbstractPostProcessor remove(int index) {
        return processors.remove(index);
    }

    public int indexOf(Object o) {
        return processors.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return processors.lastIndexOf(o);
    }

    public ListIterator<AbstractPostProcessor> listIterator() {
        return processors.listIterator();
    }

    public ListIterator<AbstractPostProcessor> listIterator(int index) {
        return processors.listIterator(index);
    }

    public List<AbstractPostProcessor> subList(int fromIndex, int toIndex) {
        return processors.subList(fromIndex, toIndex);
    }

    public Spliterator<AbstractPostProcessor> spliterator() {
        return processors.spliterator();
    }

    public boolean removeIf(Predicate<? super AbstractPostProcessor> filter) {
        return processors.removeIf(filter);
    }

    public Stream<AbstractPostProcessor> stream() {
        return processors.stream();
    }

    public Stream<AbstractPostProcessor> parallelStream() {
        return processors.parallelStream();
    }

    public void forEach(Consumer<? super AbstractPostProcessor> action) {
        processors.forEach(action);
    }
}
