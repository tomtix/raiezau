package rz;

import java.util.*;
import java.io.*;

public class BufferMap implements Serializable{
    private BitSet bitSet;

    public BufferMap(int size) {
	bitSet = new BitSet(size);
    }

    private BufferMap(BitSet bitSet) {
        this.bitSet = bitSet;
    }

    public BufferMap(File file) {
	int size = file.getPieceCount();
	bitSet = new BitSet(size);
        if (file.isSeeded())
            bitSet.setAll();
    }

    public void addCompletedPart(int partNumber) {
        bitSet.set(partNumber);
    }

    public boolean isCompleted(int partNumber){
	return bitSet.test(partNumber);
    }

    public int size() {
        return bitSet.size();
    }
    
    @Override
    public String toString() {
        return bitSet.toString();
    }

    public byte[] toByteArray(){
	return bitSet.toByteArray();
    }

    public static BufferMap fromByteArray(byte[] array) {
        return new BufferMap(BitSet.fromByteArray(array));
    }
}
