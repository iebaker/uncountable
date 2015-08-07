package world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rendering.Renderable;

public class Module {

    private ModuleTemplate m_template;
    private Map<Portal, Module> m_neighbors = new HashMap<Portal, Module>();
    private String m_name;
    private int m_openPortals;

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

        // draw self

        for(Module neighbor : getNeighbors()) {
            neighbor.stageScene();
        }
    }

    private void stage(Renderable renderable) {

    }

    private void stage(Portal portal) {

    }
}
