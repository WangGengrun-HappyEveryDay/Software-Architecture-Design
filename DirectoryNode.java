import java.util.ArrayList;
import java.util.List;

public class DirectoryNode extends Node {
    private List<Node> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public void addChild(Node node) {
        if (node == null) throw new IllegalArgumentException("node is null");
        children.add(node);
    }

    // Put child: replace existing child with same name, otherwise add
    public void putChild(Node node) {
        if (node == null) throw new IllegalArgumentException("node is null");
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getName().equals(node.getName())) {
                children.set(i, node);
                return;
            }
        }
        children.add(node);
    }

    public Node getChild(String name) {
        for (Node n : children) if (n.getName().equals(name)) return n;
        return null;
    }

    public List<Node> getChildren() {
        return new ArrayList<>(children);
    }

    @Override
    public int getSize() {
        int sum = 0;
        for (Node n : children) sum += n.getSize();
        return sum;
    }

    public Node find(String name) {
        for (Node n : children) {
            if (n.getName().equals(name)) return n;
            if (n instanceof DirectoryNode) {
                Node found = ((DirectoryNode) n).find(name);
                if (found != null) return found;
            }
        }
        return null;
    }
}
