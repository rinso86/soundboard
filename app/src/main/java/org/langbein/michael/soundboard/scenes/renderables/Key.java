package org.langbein.michael.soundboard.scenes.renderables;

import android.graphics.Canvas;
import android.view.MotionEvent;

import org.langbein.michael.soundboard.scenes.renderables.graphicsPrimitives.Hexagon;
import org.langbein.michael.soundboard.scenes.renderables.graphicsPrimitives.Text;
import org.langbein.michael.soundboard.sound.MidiWrapper;


public class Key implements Renderable, Touchable {

    // Graphics
    private Hexagon hex;
    private Text text;
    private int a;
    private int r;
    private int g;
    private int b;

    // Sound
    private float frq;
    private MidiWrapper sound;

    // State

    public Key(int posX, int posY, int len, float freq, int indx, MidiWrapper sw) {

        // Graphics
        hex = new Hexagon(posX, posY, len);
        text = new Text(posX, posY, String.valueOf(indx), 20);
        a = 50;
        r =  (int)(255 * Math.random());
        g =  (int)(255 * Math.random());
        b =  (int)(255 * Math.random());

        // Sound
        sound = sw;
        frq = freq;
        // TODO: what should channel and velocity be?
    }

    @Override
    public void update(long delta) {

    }

    @Override
    public void draw(Canvas canvas) {
        hex.draw(canvas);
        text.drawWithPosInMiddle(canvas);
    }

    @Override
    public void onTouch(MotionEvent event) {
        // TODO: use event.getAction() um bei Ende der Bewegung den sound wieder auszuschalten.
        if(hex.isInsideBounds(event)) {
            switch(event.getFlags()){
                case MotionEvent.ACTION_DOWN:
                    double intensity = event.getPressure();
                    lightsOn(intensity);
                    startPlayingNote();
                    break;
                case MotionEvent.ACTION_UP:
                    lightsOut();
                    stopPlayingNote();
                    break;
            }

        } else {
            lightsOut();
            stopPlayingNote();
        }
    }

    public void setFillColor(int a, int r, int  g, int b){
        this.r = r;
        this.g = g;
        this.b = b;
        hex.setFillColor(a, r, g, b);
    }

    public void lightsOn(){
        setFillColor(255, r, g, b);
    }

    public void lightsOn(double degree){
        setFillColor((int)(degree * 255), r, g, b);
    }

    private void lightsOut(){
        setFillColor(25, r, g, b);
    }

    private void startPlayingNote() {
        // TODO
    }

    private void continuePlayingNote() {
        sound.noteOn(1, frq, 60);
    }

    private void stopPlayingNote() {
        sound.noteOff(1, frq, 60);
    }

    public double getFrequency() {
        return frq;
    }

}
