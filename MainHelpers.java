public class MainHelpers {
    public static boolean isValidPath(String path) {
        if (path == null) return false;
        if (!path.startsWith("/")) return false;
        if (path.contains("//")) return false;
        if (path.length() > 1 && path.endsWith("/")) return false;
        String[] segs = path.equals("/") ? new String[0] : path.substring(1).split("/");
        for (String s : segs) {
            if (s.equals(".") || s.equals("..")) return false;
            if (s.length() == 0) return false;
        }
        return true;
    }

    public static String lastSegment(String path) {
        if ("/".equals(path)) return "/";
        int idx = path.lastIndexOf('/');
        return path.substring(idx+1);
    }

    public static Node getNodeByPath(DirectoryNode root, String path) {
        if (!isValidPath(path)) return null;
        if ("/".equals(path)) return root;
        String[] segs = path.substring(1).split("/");
        Node cur = root;
        for (String s : segs) {
            if (!(cur instanceof DirectoryNode)) return null;
            DirectoryNode dir = (DirectoryNode) cur;
            Node child = dir.getChild(s);
            if (child == null) return null;
            cur = child;
        }
        return cur;
    }

    public static DirectoryNode getParentDir(DirectoryNode root, String path) {
        if (!isValidPath(path)) return null;
        if ("/".equals(path)) return null;
        int idx = path.lastIndexOf('/');
        String parentPath = (idx == 0) ? "/" : path.substring(0, idx);
        Node p = getNodeByPath(root, parentPath);
        if (p instanceof DirectoryNode) return (DirectoryNode) p;
        return null;
    }
}
