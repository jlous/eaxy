package org.eaxy;

public class Namespace {

    public static final Namespace NO_NAMESPACE = new Namespace(null);
    private final String uri;
    private final String prefix;

    public Namespace(String uri, String prefix) {
        this.uri = uri;
        this.prefix = prefix;
    }

    public Namespace(String uri) {
        this.uri = uri;
        this.prefix = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Namespace)) return false;
        Namespace other = ((Namespace) obj);
        return Objects.equals(uri, other.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uri);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{uri=" + uri + ",prefix=" + prefix + "}";
    }

    public Element el(String tagName, Node... content) {
        return Xml.el(tagName, this, content);
    }

    public Element el(String tagName, String stringContent) {
        return Xml.el(tagName, this, Xml.text(stringContent));
    }

    public boolean isNoNamespace() {
        return uri == null;
    }

    public String print() {
        return "xmlns" + (prefix == null ? "" : ":" + prefix) + "=\"" + uri + "\"";
    }

    public String prefix() {
        return prefix == null ? "" : prefix + ":";
    }

    public String getUri() {
        return uri;
    }

    public String getPrefix() {
        return prefix;
    }

    public QualifiedName name(String name) {
        return new QualifiedName(this, name);
    }

    public Attribute attr(String localName, String value) {
        return new Attribute(name(localName), value);
    }

}