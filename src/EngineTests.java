package byow;

import byow.Core.Engine;
import org.junit.Test;

public class EngineTests {
    @Test
    public void testEngine(){
        Engine engine = new Engine();
        engine.interactWithKeyboard();
    }


    @Test
    public void testStringInput() {
        Engine engine = new Engine();
        engine.interactWithInputString("N999SDDD:Q");
        engine.interactWithInputString("L:Q");
        engine.interactWithInputString("L:Q");
        engine.interactWithInputString("LWWWDDD");
    }
}
