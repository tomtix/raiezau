package rz;

import java.util.*;

class StrategyTest implements Strategy {
    private Tracker tracker;

    public StrategyTest(Tracker tracker) {
	this.tracker = tracker;
    }

    private void testGetfile(File file) {
	tracker.doGetfile(file);
	System.out.println("Getfile: " + file);
    }

    private List<File> testLook(LookRequest lr) {
        List<File> results = tracker.doLook(lr);
	System.out.println("Looked: " + results);
	return results;
    }

    private void testTracker() {
	List<File> fileList = FileManager.getFileList();
        
	testGetfile(fileList.get(0));
	testGetfile(fileList.get(1));
	testGetfile(fileList.get(2));

	// exist beacause it's mine
	LookRequest lr = new LookRequest();
	lr.addFilename("file.dat");
	lr.addSizeLT(8000);
        testLook(lr);

	// not exist
	lr = new LookRequest();
	lr.addFilename("file.XXX");
	lr.addSizeLT(1024);
        testLook(lr);

	tracker.doUpdate(fileList);
    }

    private void testPeer() {
	LookRequest lr = new LookRequest();
	lr.addFilename("fifi.dat");
	lr.addSizeLT(2048);
        List<File> files = testLook(lr);

	File file = files.get(0);
	Log.info("File: " + file);
	testGetfile(file);

	List<FilePeer> peers = file.getPeerList();
	FilePeer peer = peers.get(0);
	Log.info("File: " + file);
	peer.doInterested(file);
	Log.info("File: " + file);
	peer.doHave(file);
	peer.doHave(file);
	
	int[] index = new int[1];
	index[0] = 0;
	peer.doGetpieces(file, index);
    }

    @Override
    public void share() {
	try {
	    testTracker();
	    testPeer();
	    Log.info("Test finished :)");
	} catch (IndexOutOfBoundsException e) {
	    Log.warning(e.toString());
	    Log.info("Couldn't finish test!");
	}
    }
}
