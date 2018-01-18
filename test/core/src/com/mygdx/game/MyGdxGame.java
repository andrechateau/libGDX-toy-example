package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class MyGdxGame extends ApplicationAdapter implements GestureDetector.GestureListener {
	private SpriteBatch batch;
	private Texture img;
	private Texture playr;
	private Texture playrw;
	private Texture playra;
	private Texture playrs;
	private Texture playrd;

	private int playerX;
	private int playerY;
	private int desiredX;
	private int desiredY;
	private final int SPEED = 5 ;
	//BitmapFont font = new BitmapFont();
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private boolean moving;
	private char dir;


	private int[] background = new int[] {0,1}, foreground = new int[] {2};

	private ShapeRenderer sr;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("bts.png");
		playr = new Texture("char.jpg");

		playrw = new Texture("char - w.png");
		playra = new Texture("char - a.png");
		playrs = new Texture("char - s.png");
		playrd = new Texture("char - d.png");

		playerX=(3191/64)*64;
		playerY=(4617/64)*64;

		desiredX=playerX;
		desiredY=playerY;
		moving=false;
		dir='s';
		map = new TmxMapLoader().load("maps/modernmap.tmx");
		Gdx.input.setInputProcessor(new GestureDetector(this));

		renderer = new OrthogonalTiledMapRenderer(map,2);
		sr = new ShapeRenderer();
		sr.setColor(Color.CYAN);
		Gdx.gl.glLineWidth(3);

		camera = new OrthographicCamera();
		camera.position.x=playerX;
		camera.position.y=playerY;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		renderer.setView(camera);
		renderer.render(background);

		if(!moving) {
			if (Gdx.input.isTouched()) {
				int touchX = Gdx.input.getX();
				int touchY = Gdx.input.getY();

				if (touchY > 850 && touchX > 149 && touchX < 230) {
					if (valid(playerX, playerY - 64)) {
						desiredX=playerX;
						desiredY=playerY-64;
						playerY -= SPEED;
						moving=true;
					}
					dir='s';
				}
				if (touchY < 850 && touchX > 149 && touchX < 230) {
					if (valid(playerX, playerY + 64)) {
						desiredX=playerX;
						desiredY=playerY+64;
						playerY += SPEED;
						moving=true;
					}
					dir='w';
				}
				if (touchX < 150 && touchY > 850 && touchY < 950) {
					if (valid(playerX - 64, playerY)) {
						desiredX=playerX-64;
						desiredY=playerY;
						playerX -= SPEED;
						moving=true;
					}
					dir='a';
				}
				if (touchX > 230 && touchY > 850 && touchY < 950) {
					if (valid(playerX + 64, playerY)) {
						desiredX=playerX+64;
						desiredY=playerY;
						playerX += SPEED;
						moving=true;
					}
					dir='d';
				}

				camera.position.x = playerX;
				camera.position.y = playerY;
			}
		}else{
			switch (dir){
				case 's':
					System.out.println(playerY);
					playerY -= SPEED;
					if(playerY<=desiredY){
						playerY=desiredY-(desiredY%64);
						moving=false;
					}
					break;
				case 'w':
					playerY += SPEED;
					if(playerY>=desiredY){
						playerY=desiredY-(desiredY%64);
						moving=false;
					}
					break;
				case 'a':
					playerX -= SPEED;
					if(playerX<=desiredX){
						playerX=desiredX-(desiredY%64);
						moving=false;
					}
					break;
				case 'd':
					playerX += SPEED;
					if(playerX>=desiredX){
						playerX=desiredX-(desiredY%64);
						moving=false;
					}

					break;
			}
			camera.position.x = playerX;
			camera.position.y = playerY;

		}

		batch.begin();
		switch (dir){
			case 'w':
				batch.draw(playrw,camera.viewportWidth/2 -16,camera.viewportHeight/2,96,96);
				break;
			case 'a':
				batch.draw(playra,camera.viewportWidth/2 - 16,camera.viewportHeight/2,96,96);
				break;
			case 's':
				batch.draw(playrs,camera.viewportWidth/2 - 16,camera.viewportHeight/2,96,96);
				break;
			case 'd':
				batch.draw(playrd,camera.viewportWidth/2 - 16,camera.viewportHeight/2,96,96);
				break;
		}
//		batch.draw(playr,camera.viewportWidth/2,camera.viewportHeight/2,64,64);
		batch.end();
		renderer.render(foreground);

		batch.begin();
		batch.draw(img,50,50,300, 300);
		batch.end();
	}

	private boolean valid(int x, int y){
		boolean b = false;
		for(MapLayer layer:map.getLayers()){
			Cell cell = ((TiledMapTileLayer) layer).getCell(x/64,y/64);
			if(cell!=null) {
				b = b| ((Boolean) cell.getTile().getProperties().get("blocked"));
			}
		}
		return !b;
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		//camera.position.x=width/2;
		//camera.position.y=height/2;


	}

	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		/*if(y>850 && x>149 && x<230){
			playerY-=32;
		}
		if(y<850 && x>149 && x<230){
			playerY+=32;
		}
		if(x<150 && y>850 && y<950){
			playerX-=32;
		}
		if(x>230 && y>850 && y<950){
			playerX+=32;
		}
		camera.position.x=playerX;
		camera.position.y=playerY;
*/
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		Gdx.app.log("touch","X: "+playerX+" Y:"+playerY);

		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void pinchStop() {

	}
}
