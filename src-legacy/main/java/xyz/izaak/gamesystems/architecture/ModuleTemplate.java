package xyz.izaak.gamesystems.architecture;

import java.util.*;
import java.util.stream.Collectors;

import xyz.izaak.gamesystems.architecture.setpieces.Portal;
import xyz.izaak.gamesystems.rendering.Renderable;

public class ModuleTemplate {

    private String m_name;
    private int m_instanceCount = 0;
    private Map<Portal, Map<String, Integer>> m_rules = new HashMap<>();
    private Map<Portal, Integer> m_ruleSums = new HashMap<>();
    private Map<String, Portal> m_portalsByName = new HashMap<>();
    private List<Renderable> m_walls = new ArrayList<>();

    public ModuleTemplate(String name) {
        m_name = name;
        m_rules = new HashMap<>();
    }

    public void addPortal(Portal portal, Map<String, Integer> rule) {
        m_portalsByName.put(portal.getName(), portal);
        m_rules.put(portal, rule);
        m_ruleSums.put(portal, rule.entrySet().stream().collect(Collectors.summingInt(Map.Entry::getValue)));
    }

    public Portal getPortal(String name) {
        return m_portalsByName.get(name);
    }

    public void addWalls(Renderable... walls) {
        m_walls.addAll(Arrays.asList(walls));
    }

    public List<Renderable> getWalls() {
        return m_walls;
    }

    public String chooseNeighborTemplateName(Portal portal) {
        int choice = new Random().nextInt(m_ruleSums.get(portal));
        int baseline = 0;

        Map<String, Integer> rule = m_rules.get(portal);

        for (String templateName : rule.keySet()) {
            baseline += rule.get(templateName);
            if (choice < baseline) {
                return templateName;
            }
        }

        System.out.println("[ModuleType.chooseNext()] Couldn't choose next module, returning null. Watch out!");
        return null;
    }

    public Module getInstance() {
        return new Module(this, m_name + "#" + (m_instanceCount++));
    }

    public Set<Portal> getPortals() {
        return m_rules.keySet();
    }

    public String getName() {
        return m_name;
    }

    @Override
    public String toString() {
        String result = "ModuleType NAME=" + m_name;
        for (Portal portal : m_rules.keySet()) {
            result += "\n\tPortal NAME=" + portal.getName();
            for(String type : m_rules.get(portal).keySet()) {
                result += "\n\t\tNext NAME=" + type + ", WEIGHT=" + m_rules.get(portal).get(type);
            }
        }
        return result;
    }

}
