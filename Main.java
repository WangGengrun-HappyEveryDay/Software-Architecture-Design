public class Main {
    public static void main(String[] args) {
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        try {
            DirectoryNode root = new DirectoryNode("/");

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] tokens = line.split("\\s+");
                String cmd = tokens[0];
                if ("MKDIR".equals(cmd)) {
                    if (tokens.length < 2) continue;
                    String path = tokens[1];
                    if (!MainHelpers.isValidPath(path)) continue;
                    if ("/".equals(path)) {
                        // root exists, nothing to do
                        continue;
                    }
                    DirectoryNode parent = MainHelpers.getParentDir(root, path);
                    if (parent == null) continue; // parent missing -> ignore
                    String name = MainHelpers.lastSegment(path);
                    // create directory, replacing existing node if any
                    DirectoryNode dir = new DirectoryNode(name);
                    parent.putChild(dir);
                } else if ("TOUCH".equals(cmd)) {
                    if (tokens.length < 3) continue;
                    String path = tokens[1];
                    String sizeStr = tokens[2];
                    if (!MainHelpers.isValidPath(path)) continue;
                    int size;
                    try {
                        size = Integer.parseInt(sizeStr);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    DirectoryNode parent = MainHelpers.getParentDir(root, path);
                    if (parent == null) continue;
                    String name = MainHelpers.lastSegment(path);
                    FileNode file = new FileNode(name, size);
                    parent.putChild(file);
                } else if ("LS".equals(cmd)) {
                    if (tokens.length < 2) continue;
                    String path = tokens[1];
                    if (!MainHelpers.isValidPath(path)) continue;
                    Node node = MainHelpers.getNodeByPath(root, path);
                    if (node == null) continue; // per spec won't happen for LS/INFO in tests
                    if (node instanceof DirectoryNode) {
                        java.util.List<String> names = new java.util.ArrayList<>();
                        for (Node c : ((DirectoryNode) node).getChildren()) names.add(c.getName());
                        java.util.Collections.sort(names);
                        for (String n : names) System.out.println(n);
                    } else {
                        System.out.println(node.getName());
                    }
                } else if ("INFO".equals(cmd)) {
                    if (tokens.length < 2) continue;
                    String path = tokens[1];
                    if (!MainHelpers.isValidPath(path)) continue;
                    Node node = MainHelpers.getNodeByPath(root, path);
                    if (node == null) continue;
                    System.out.println(node.getSize());
                }
            }
        } catch (java.io.IOException e) {
            // ignore
        }
    }
}


