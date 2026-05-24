public class MainHelpers {
    public static String normalizePath(String path) {
        if (path == null || !path.startsWith("/")) return null;

        java.util.ArrayDeque<String> stack = new java.util.ArrayDeque<String>();
        String[] segments = path.split("/");
        for (String segment : segments) {
            if (segment.length() == 0 || ".".equals(segment)) {
                continue;
            }
            if ("..".equals(segment)) {
                if (!stack.isEmpty()) {
                    stack.removeLast();
                }
                continue;
            }
            stack.addLast(segment);
        }

        if (stack.isEmpty()) return "/";

        StringBuilder builder = new StringBuilder();
        for (String segment : stack) {
            builder.append('/').append(segment);
        }
        return builder.toString();
    }

    public static boolean isValidPath(String path) {
        return normalizePath(path) != null;
    }

    public static String parentPath(String normalizedPath) {
        if (normalizedPath == null || "/".equals(normalizedPath)) return null;
        int idx = normalizedPath.lastIndexOf('/');
        return idx <= 0 ? "/" : normalizedPath.substring(0, idx);
    }

    public static String lastSegment(String normalizedPath) {
        if (normalizedPath == null || "/".equals(normalizedPath)) return "/";
        int idx = normalizedPath.lastIndexOf('/');
        return normalizedPath.substring(idx + 1);
    }
}
