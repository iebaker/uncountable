package gamesystems.architecture;

import java.util.*;
import java.util.stream.Collectors;

import gamesystems.rendering.Renderable;
import portals.Portal;

public class Module {

    private ModuleTemplate m_template;
    private Map<Portal, Module> m_neighbors = new HashMap<>();
    private Map<Portal, Portal> m_links = new HashMap<>();
    private String m_name;
    private int m_openPortals;
    private boolean m_isStaged;
    private List<Renderable> m_stagedRenderables = new ArrayList<>();

    public Module(ModuleTemplate type, String name) {
        m_template = type;
        m_name = name;
        m_openPortals = m_template.getPortals().size();
    }

    public String getName() {
        return m_name;
    }

    public ModuleTemplate getTemplate() {
        return m_template;
    }

    public Module getNeighbor(Portal portal) {
        return m_neighbors.get(portal);
    }

    public Portal getLinkedPortal(Portal portal) {
        return m_links.get(portal);
    }

    public Set<Module> getNeighbors() {
        return m_template.getPortals()
                .stream()
                .filter(this::hasNeighbor)
                .map(this::getNeighbor)
                .collect(Collectors.toSet());
    }

    public void setNeighbor(Portal portal, Module module) {
        m_neighbors.put(portal, module);
        m_openPortals--;
    }

    public void linkPortals(Portal entry, Portal exit) {
        m_links.put(entry, exit);
    }

    public boolean hasNeighbor(Portal portal) {
        return m_neighbors.containsKey(portal);
    }

    public Portal selectOpenPortal() {
        for (Portal portal : m_template.getPortals()) {
            if (!hasNeighbor(portal)) {
                return portal;
            }
        }
        System.out.println("No more open portals in " + m_name);
        return null;
    }

    public boolean hasOpenPortals() {
        return m_openPortals > 0;
    }

    public int getOpenPortalCount() {
        return m_openPortals;
    }

    public void setShaderForStagedRenderables(String shaderName) {
        m_stagedRenderables.forEach((renderable) -> renderable.setShader(shaderName));
    }

    public void stageScene() {
        stage(m_template.getWalls());
    }

    public List<Renderable> getStagedRenderables() {
        return m_stagedRenderables;
    }

    public void clearScene() {
        m_stagedRenderables.clear();
    }

    private void stage(Renderable... renderables) {
        m_stagedRenderables.addAll(Arrays.asList(renderables));
    }

    private <R extends Renderable> void stage(Collection<R> renderables) {
        m_stagedRenderables.addAll(renderables);
    }
}
