package net.game.spacepirates.util.io;

import com.badlogic.gdx.graphics.Color;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ColourTypeAdapter extends TypeAdapter<Color> {

    public static final boolean WRITE_HEX_COLOURS = true;

    @Override
    public void write(JsonWriter out, Color value) throws IOException {

        if(WRITE_HEX_COLOURS) {
            out.value("#" + value.toString());
        } else {
            out.beginArray();

            out.value(value.r);
            out.value(value.g);
            out.value(value.b);
            out.value(value.a);

            out.endArray();
        }
    }

    @Override
    public Color read(JsonReader in) throws IOException {

        Color col;

        if(in.peek() == JsonToken.STRING) {
            String hex = in.nextString();
            col = Color.valueOf(hex);
        } else {
            col = new Color();
            in.beginArray();

            col.r = (float) in.nextDouble();
            col.g = (float) in.nextDouble();
            col.b = (float) in.nextDouble();
            col.a = (float) in.nextDouble();

            in.endArray();
        }

        return col;
    }
}
