package component;

import java.util.ArrayList;
import java.util.List;

public class Rotor {

    private int id;
    private final int originalNotchIndex;

    private int offset = 0;
    private ArrayList<Integer> alphabetTranslator;
    private int alphabetLength;
    private ArrayList<Integer> forwardMapping;
    private ArrayList<Integer> backwardMapping;

    @Override
    public String toString() {
        return "Rotor{" +
                "id=" + id +
                '}';
    }

    public Rotor(int id, int originalNotchIndex, ArrayList<Integer> alphabetTranslator , int alphabetLength, ArrayList<Integer> forwardMapping, ArrayList<Integer> backwardMapping){
        this.id = id;
        this.originalNotchIndex = originalNotchIndex;
        this.alphabetTranslator = alphabetTranslator;
        this.alphabetLength= alphabetLength;
        this.forwardMapping= forwardMapping;
        this.backwardMapping= backwardMapping;

    }


    public int getMatchForward ( int inputIndex ) {

        int original_inputIndex= (inputIndex + offset + alphabetLength) %  alphabetLength;
        int original_outputIndex= forwardMapping.get(original_inputIndex);
        return (original_outputIndex - offset + alphabetLength) %  alphabetLength;
    }


    public int getMatchBackward ( int inputIndex ) {

        int current_inputIndex= (inputIndex + offset + alphabetLength) % alphabetLength;
        int original_charIndex= backwardMapping.get(current_inputIndex);
        return ((original_charIndex - offset + alphabetLength) % alphabetLength);
    }

    public int getOriginalNotchIndex() {
        return originalNotchIndex;
    }

    public void rotate() {
        offset = (offset+1) % alphabetLength;
    }

    public int getOffset() {
        return offset;
    }
}
