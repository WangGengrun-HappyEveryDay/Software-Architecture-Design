import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class FileSystem {
    private final DirectoryNode root = new DirectoryNode("/");

    public void execute(String line, PrintStream out) {
        if (line == null) return;
        line = line.trim();
        if (line.isEmpty()) return;

        String[] tokens = line.split("\\s+");
        String cmd = tokens[0];

        if ("MKDIR".equals(cmd)) {
            if (tokens.length != 2) return;
            handleMkdir(tokens[1]);
        } else if ("TOUCH".equals(cmd)) {
            if (tokens.length != 3) return;
            handleTouch(tokens[1], tokens[2]);
        } else if ("LS".equals(cmd)) {
            if (tokens.length != 2) return;
            handleLs(tokens[1], out);
        } else if ("INFO".equals(cmd)) {
            if (tokens.length != 2) return;
            handleInfo(tokens[1], out);
        } else if ("FIND".equals(cmd)) {
            if (tokens.length != 3) return;
            handleFind(tokens[1], tokens[2], out);
        } else if ("RM".equals(cmd)) {
            if (tokens.length != 2) return;
            handleRm(tokens[1]);
        } else if ("LINK".equals(cmd)) {
            if (tokens.length != 3) return;
            handleLink(tokens[1], tokens[2]);
        }
    }

    private void handleMkdir(String rawPath) {
        String path = MainHelpers.normalizePath(rawPath);
        if (path == null || "/".equals(path)) return;

        DirectoryNode parent = resolveDirectoryNode(MainHelpers.parentPath(path));
        if (parent == null) return;

        String name = MainHelpers.lastSegment(path);
        Node existing = parent.getChild(name);
        if (existing instanceof DirectoryNode) {
            return;
        }
        parent.putChild(new DirectoryNode(name));
    }

    private void handleTouch(String rawPath, String sizeToken) {
        String path = MainHelpers.normalizePath(rawPath);
        if (path == null || "/".equals(path)) return;

        long size;
        try {
            size = Long.parseLong(sizeToken);
        } catch (NumberFormatException e) {
            return;
        }
        if (size < 0) return;

        DirectoryNode parent = resolveDirectoryNode(MainHelpers.parentPath(path));
        if (parent == null) return;

        String name = MainHelpers.lastSegment(path);
        parent.putChild(new FileNode(name, size));
    }

    private void handleLs(String rawPath, PrintStream out) {
        String path = MainHelpers.normalizePath(rawPath);
        if (path == null) return;

        Node node = resolveNode(path);
        if (node == null) return;

        if (node instanceof FileNode) {
            out.println(node.getName());
            return;
        }

        if (node instanceof DirectoryNode) {
            printDirectoryChildren((DirectoryNode) node, out);
            return;
        }

        if (node instanceof LinkNode) {
            Node target = resolveLinkTarget(node);
            if (target == null) return;
            if (target instanceof FileNode) {
                out.println(node.getName());
            } else if (target instanceof DirectoryNode) {
                printDirectoryChildren((DirectoryNode) target, out);
            }
        }
    }

    private void handleInfo(String rawPath, PrintStream out) {
        String path = MainHelpers.normalizePath(rawPath);
        if (path == null) return;

        Node node = resolveNode(path);
        if (node == null) return;

        out.println(computeSize(node));
    }

    private void handleFind(String rawPath, String targetName, PrintStream out) {
        String path = MainHelpers.normalizePath(rawPath);
        if (path == null) return;

        Node node = resolveNode(path);
        if (node == null) return;

        TreeSet<String> results = new TreeSet<String>();
        if (node instanceof FileNode) {
            if (node.getName().equals(targetName)) {
                results.add(node.getPath());
            }
        } else if (node instanceof LinkNode) {
            if (node.getName().equals(targetName)) {
                results.add(node.getPath());
            }
            Node target = resolveLinkTarget(node);
            if (target instanceof DirectoryNode) {
                findInDirectory((DirectoryNode) target, targetName, new HashSet<DirectoryNode>(), results);
            }
        } else if (node instanceof DirectoryNode) {
            findInDirectory((DirectoryNode) node, targetName, new HashSet<DirectoryNode>(), results);
        }

        for (String result : results) {
            out.println(result);
        }
    }

    private void handleRm(String rawPath) {
        String path = MainHelpers.normalizePath(rawPath);
        if (path == null || "/".equals(path)) return;

        DirectoryNode parent = resolveDirectoryNode(MainHelpers.parentPath(path));
        if (parent == null) return;

        String name = MainHelpers.lastSegment(path);
        Node node = parent.getChild(name);
        if (node == null) return;
        if (node instanceof DirectoryNode && !((DirectoryNode) node).isEmpty()) {
            return;
        }
        parent.removeChild(name);
    }

    private void handleLink(String rawSrcPath, String rawDstPath) {
        String srcPath = MainHelpers.normalizePath(rawSrcPath);
        String dstPath = MainHelpers.normalizePath(rawDstPath);
        if (srcPath == null || dstPath == null || "/".equals(dstPath)) return;

        Node srcNode = resolveNode(srcPath);
        if (srcNode == null) return;

        DirectoryNode parent = resolveDirectoryNode(MainHelpers.parentPath(dstPath));
        if (parent == null) return;

        String name = MainHelpers.lastSegment(dstPath);
        parent.putChild(new LinkNode(name, srcNode));
    }

    private void printDirectoryChildren(DirectoryNode directory, PrintStream out) {
        for (Node child : directory.getChildren()) {
            out.println(child.getName());
        }
    }

    private Node resolveNode(String normalizedPath) {
        if (normalizedPath == null) return null;
        if ("/".equals(normalizedPath)) return root;

        String[] segments = normalizedPath.substring(1).split("/");
        Node current = root;
        for (int i = 0; i < segments.length; i++) {
            if (!(current instanceof DirectoryNode)) return null;
            Node child = ((DirectoryNode) current).getChild(segments[i]);
            if (child == null) return null;
            if (i < segments.length - 1) {
                current = resolveLinkTarget(child);
                if (!(current instanceof DirectoryNode)) return null;
            } else {
                current = child;
            }
        }
        return current;
    }

    private DirectoryNode resolveDirectoryNode(String normalizedPath) {
        if (normalizedPath == null) return null;
        Node node = resolveNode(normalizedPath);
        if (node instanceof DirectoryNode) {
            return (DirectoryNode) node;
        }
        Node target = resolveLinkTarget(node);
        if (target instanceof DirectoryNode) {
            return (DirectoryNode) target;
        }
        return null;
    }

    private Node resolveLinkTarget(Node node) {
        Set<Node> visited = new HashSet<Node>();
        Node current = node;
        while (current instanceof LinkNode) {
            if (!visited.add(current)) {
                return null;
            }
            current = ((LinkNode) current).getTarget();
        }
        return current;
    }

    private long computeSize(Node node) {
        Node target = node instanceof LinkNode ? resolveLinkTarget(node) : node;
        if (target == null) return 0L;
        return computeSizeDfs(target, new HashSet<Node>());
    }

    private long computeSizeDfs(Node node, Set<Node> visited) {
        if (node == null) return 0L;
        if (!visited.add(node)) return 0L;

        if (node instanceof FileNode) {
            return ((FileNode) node).getSize();
        }

        long sum = 0L;
        if (node instanceof DirectoryNode) {
            for (Node child : ((DirectoryNode) node).getChildren()) {
                if (child instanceof LinkNode) {
                    sum += computeSizeDfs(resolveLinkTarget(child), visited);
                } else {
                    sum += computeSizeDfs(child, visited);
                }
            }
        }
        return sum;
    }

    private void findInDirectory(DirectoryNode directory, String targetName,
                                 Set<DirectoryNode> visitedDirs, TreeSet<String> results) {
        if (directory == null || !visitedDirs.add(directory)) return;

        if (directory.getName().equals(targetName)) {
            results.add(directory.getPath());
        }

        for (Node child : directory.getChildren()) {
            if (child.getName().equals(targetName)) {
                results.add(child.getPath());
            }

            if (child instanceof DirectoryNode) {
                findInDirectory((DirectoryNode) child, targetName, visitedDirs, results);
            } else if (child instanceof LinkNode) {
                Node target = resolveLinkTarget(child);
                if (target instanceof DirectoryNode) {
                    findInDirectory((DirectoryNode) target, targetName, visitedDirs, results);
                }
            }
        }
    }
}
