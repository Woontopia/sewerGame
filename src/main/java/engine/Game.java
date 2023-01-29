package engine;

//import sun.awt.windows.ThemeReader;
//import sun.awt.*;

public abstract class Game implements Runnable{

    private final RenderingEngine renderingEngine;
    private GameTime gameTime;
    private boolean playing = true;
    private Thread thread;

    protected abstract void initialize();
    protected abstract void update();
    protected abstract void draw(Buffer buffer);
    protected abstract void conclude();

    public Game() {
        renderingEngine = RenderingEngine.getInstance();
        renderingEngine.setViewport(800, 600);
    }

    public Game(int viewportWidth, int viewportHeight) {
        renderingEngine = RenderingEngine.getInstance();
        renderingEngine.setViewport(viewportWidth, viewportHeight);
    }

    public void start() {
        thread = new Thread(this);
        initialize();
        thread.start();
        play();
        conclude();
    }

    public void stop() {
        playing = false;
    }

    protected int getViewportWidth() {
        return renderingEngine.getViewportWidth();
    }

    protected int getViewportHeight() {
        return renderingEngine.getViewportHeight();
    }

    protected void setTitle(String title) {
        renderingEngine.setTitle(title);
    }

    protected String getTitle() {
        return renderingEngine.getTitle();
    }

    private void play() {
        gameTime = GameTime.getInstance();
        while (playing) {
            update();
            gameTime.synchronize();
        }
    }

    public int getFps() {
        return gameTime.getCurrentFps();
    }

    public void run() {
        renderingEngine.start();
        while (playing) {
            draw(renderingEngine.getRenderingBuffer());
            renderingEngine.renderBufferOnScreen();
        }
        renderingEngine.stop();
        thread.stop();
    }
}