package world;

import java.util.*;

import gamesystems.rendering.Renderable;
import world.setpieces.Portal;

public class ModuleTemplate {

    private String m_name;
    private int m_instanceCount = 0;
    private Map<Portal, Map<ModuleTemplate, Integer>> m_rules = new HashMap<>();
    private Map<Portal, Integer> m_ruleSums = new HashMap<>();
    private Map<String, Portal> m_portalsByName = new HashMap<>();
    private List<Renderable> m_walls = new ArrayList<>();

    public ModuleTemplate(String name, Map<Portal, Map<ModuleTemplate, Integer>> rules) {
        m_name = name;
        m_rules = rules;
        computeRuleSums();
    }

    public ModuleTemplate(String name) {
        m_name = name;
        m_rules = new HashMap<>();
    }

    public void addPortal(Portal portal, Map<ModuleTemplate, Integer> rule) {
        m_rules.put(portal, rule);
        m_ruleSums.put(portal, 0);
        m_portalsByName.put(portal.getName(), portal);
        computeRuleSums();
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

    private void computeRuleSums() {
        for (Portal portal : m_ruleSums.keySet()) {

            int sum = 0;

            Map<ModuleTemplate, Integer> rule = m_rules.get(portal);
            for (ModuleTemplate type : rule.keySet()) {
                sum += rule.get(type);
            }

            m_ruleSums.put(portal, sum);
        }
    }

    public ModuleTemplate chooseNeighborTemplate(Portal portal) {
        int choice = new Random().nextInt(m_ruleSums.get(portal));
        int baseline = 0;

        Map<ModuleTemplate, Integer> rule = m_rules.get(portal);

        for (ModuleTemplate type : rule.keySet()) {
            baseline += rule.get(type);
            if (choice < baseline) {
                return type;
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
            for(ModuleTemplate type : m_rules.get(portal).keySet()) {
                result += "\n\t\tNext NAME=" + type.getName() + ", WEIGHT=" + m_rules.get(portal).get(type);
            }
        }
        return result;
    }

}
