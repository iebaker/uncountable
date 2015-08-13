package world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import joml.Vector3f;
import rendering.Points;
import rendering.Renderable;
import world.setpieces.BasicColoredQuad;

public class Module {

    private ModuleTemplate m_template;
    private Map<Portal, Module> m_neighbors = new HashMap<Portal, Module>();
    private String m_name;
    private int m_openPortals;
    private boolean m_isStaged;
    private List<Renderable> m_stagedRenderables = new ArrayList<Renderable>();

    private BasicColoredQuad m_quad;

    private BasicColoredQuad m_X;
    private BasicColoredQuad m_Y;
    private BasicColoredQuad m_Z;
    private BasicColoredQuad m_x;
    private BasicColoredQuad m_y;
    private BasicColoredQuad m_z;

    public Module(ModuleTemplate type, String name) {
        m_template = type;
        m_name = name;
        m_openPortals = m_template.getPortals().size();

        m_X = new BasicColoredQuad(Points.RED);
        m_x = new BasicColoredQuad(Points.BLUE);
        m_Y = new BasicColoredQuad(Points.GREEN);
        m_y = new BasicColoredQuad(Points.MAGENTA);
        m_Z = new BasicColoredQuad(Points.YELLOW);
        m_z = new BasicColoredQuad(Points.CYAN);

        m_Z.translate(Points.__Z);
        m_z.translate(Points.__z);

        m_Y.rotate(Points.piOver(2), Points.X__);
        m_y.rotate(Points.piOver(2), Points.X__);
        m_Y.translate(Points._Y_);
        m_y.translate(Points._y_);

        m_X.rotate(Points.piOver(2), Points._Y_);
        m_x.rotate(Points.piOver(2), Points._Y_);
        m_X.translate(Points.X__);
        m_x.translate(Points.x__);
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
        stage(m_X, m_x, m_Y, m_y, m_Z, m_z);
    }

    public List<Renderable> getStagedRenderables() {
        return m_stagedRenderables;
    }

    public void clearStagedRenderables() {
        m_stagedRenderables.clear();
    }

    private void stage(Renderable... renderables) {
        for(Renderable renderable : renderables) {
            m_stagedRenderables.add(renderable);
        }
    }

    private void stage(Portal portal) {

    }
}
