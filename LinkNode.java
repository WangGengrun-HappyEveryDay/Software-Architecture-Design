public class LinkNode extends Node {
    private Node target;

    public LinkNode(String name, Node target) {
        super(name);
        this.target = target;
    }

    public Node getTarget() {
        return target;
    }

    @Override
    public long getSize() {
        return target == null ? 0L : target.getSize();
    }
}
