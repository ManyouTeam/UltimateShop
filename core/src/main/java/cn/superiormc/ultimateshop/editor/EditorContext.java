package cn.superiormc.ultimateshop.editor;

public class EditorContext {

    private EditorScope scope;

    private EditorTarget target;

    private String path = "";

    private int page = 0;

    public EditorScope getScope() {
        return scope;
    }

    public void setScope(EditorScope scope) {
        this.scope = scope;
    }

    public EditorTarget getTarget() {
        return target;
    }

    public void setTarget(EditorTarget target) {
        this.target = target;
    }

    public String getPath() {
        return path == null ? "" : path;
    }

    public void setPath(String path) {
        this.path = path == null ? "" : path;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(page, 0);
    }
}
