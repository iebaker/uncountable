package world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rendering.Points;
import rendering.Renderable;
import world.setpieces.BasicColoredQuad;
import world.setpieces.Triangle;

public class Module {

    private ModuleTemplate m_template;
    private Map<Portal, Module> m_neighbors = new HashMap<Portal, Module>();
    private String m_name;
    private int m_openPortals;
    private boolean m_isStaged;
    private List<Renderable> m_stagedRenderables = new ArrayList<Renderable>();

    private BasicColoredQuad m_quad;
    private Triangle m_triangle;

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

    public Set<Module> getNeighbors() {
        Set<Module> allNeighbors = new HashSet<Module>();
        for(Portal portal : m_template.getPortals()) {
            if(hasNeighbor(portal)) {
                allNeighbors.add(getNeighbor(portal));
            }
        }
        return allNeighbors;
    }

    public void setNeighbor(Portal portal, Module module) {
        m_neighbors.put(portal, module);
        m_openPortals--;
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

    public void stageScene() {
        if(m_quad == null) m_quad = new BasicColoredQuad(Points.BLUE);
        if(m_triangle == null) m_triangle = new Triangle();
        stage(m_quad);
    }

    public List<Renderable> getStagedRenderables() {
        return m_stagedRenderables;
    }

    public void clearStagedRenderables() {
        m_stagedRenderables.clear();
    }

    private void stage(Renderable renderable) {
        m_stagedRenderables.add(renderable);
    }

    private void stage(Portal portal) {

    }
}
