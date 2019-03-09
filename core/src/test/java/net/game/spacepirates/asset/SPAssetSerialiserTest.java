package net.game.spacepirates.asset;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class SPAssetSerialiserTest {

    @Test
    void testLoad() {
        InputStream stream = getClass().getResourceAsStream("/samples/test.json");

        SPSprite load = SPAssetSerialiser.load(stream, SPSprite.class);
        System.out.println();
    }
}