package RZ;

import java.util.*;
import java.util.regex.*;
import java.lang.*;

class Tracker {
    private RZSocket socket;

    private final Pattern declarePattern = Pattern.compile(
	"\\s*ok\\s*");
    private final Pattern getfilePattern = Pattern.compile(
	"\\s*peers\\s+([a-f0-9]*)\\s*\\[\\s*(.*)\\s*\\]\\s*");
    private final Pattern lookPattern = Pattern.compile(
	"\\s*list\\s*\\[\\s*(.*)\\s*\\]\\s*");

    Tracker(String ip, int port) throws Exception {
	socket = new RZSocket(ip, port);
	socket.connect();
    }

    void doAnnounce(Map<String, RZFile> files, int port) throws Exception {
	doDeclare("announce " + listenString(port), files);
    }

    void doUpdate(Map<String, RZFile> files) throws Exception {
	doDeclare("update", files);
    }

    void doGetfile(RZFile file) throws Exception {
	socket.send("getfile %s", file.getKey());
	receiveGetfile(file);
    }
    
    List<RZFile> doLook(LookRequest lr) throws Exception {
	socket.send("look [%s]", lr);
	return receiveLook();
    }

    private String listenString(int port) {
	return "listen " + port;
    }

    private String seedString(Map<String, RZFile> files) {
	String leech = "";
	for (Map.Entry<String, RZFile> entry : files.entrySet()) {
	    RZFile file = entry.getValue();
	    if (file.isSeeded())
		leech += file.getKey() + " ";
	}
	return String.format("leech [%s]", leech);
    }

    private String leechString(Map<String, RZFile> files) {
	String seed = "";
	for (Map.Entry<String, RZFile> entry : files.entrySet()) {
	    RZFile file = entry.getValue();
	    if (!file.isSeeded())
		seed += file.announceSeed() + " ";
	}
	return String.format("seed [%s]", seed);
    }

    private void doDeclare(String announcement, Map<String, RZFile> files)
	throws Exception {
	sendDeclare(announcement, files);
	receiveDeclare();
    }

    private void sendDeclare(String announcement, Map<String, RZFile> files)
	throws Exception {
	socket.send("%s %s %s", announcement, 
		    seedString(files), leechString(files));
    }

    private void receiveDeclare() throws Exception {
	socket.receiveMatcher(declarePattern);
    }

    private void receiveGetfile(RZFile file) throws Exception {
	Matcher match = socket.receiveMatcher(getfilePattern);

	String key = match.group(1);
	if (!file.isKey(key))
	    throw new Exception("Wrong key received.");
	
	if (RZPattern.isEmpty(match.group(2)))
	    return;

	String[] peers = match.group(2).split("\\s+");

	for(String peer : peers) {
	    file.addPeer(new Peer(peer));
	}
    }

    private List<RZFile> receiveLook() throws Exception {
	Matcher match = socket.receiveMatcher(lookPattern);
	
	List<RZFile> files = new ArrayList<RZFile>();
	if (RZPattern.isEmpty(match.group(1)))
	    return files;

	String[] filesStr = match.group(1).split("\\s+");
	if (filesStr.length % 4 != 0)
	    throw new Exception("Invalid list size in look response");
	
	for (int i = 0; i < filesStr.length; i += 4) {
	    files.add(new RZFile(filesStr[i],
				 Integer.parseInt(filesStr[i+1]),
				 Integer.parseInt(filesStr[i+2]),
				 filesStr[i+3]));
	}
	return files;
    }
}
