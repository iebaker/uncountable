package world;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import rendering.Ray;

public class Treadmill {

    private static final float m_connectivity = 0.1f;
    private static final int m_maxBuildDepth = 5;
    private static Set<ModuleTemplate> m_moduleTemplates = new HashSet<ModuleTemplate>();
    private static Set<Module> m_modulesWithOpenPortals = new HashSet<Module>();
    private static Random m_random = new Random();

    public static void importModuleTemplates(String xmlFilename) {
        m_moduleTemplates = ModuleTemplateFileParser.parseToModuleTemplateSet(xmlFilename);
    }

    @SuppressWarnings("unused")
    private static void printModuleTypes() {
        for (ModuleTemplate type : m_moduleTemplates) {
            System.out.println(type);
        }
    }

    public static Module getInitialModule() {
        return ((ModuleTemplate)(m_moduleTemplates.toArray()[m_random.nextInt(m_moduleTemplates.size())])).getInstance();
    }

    public static void buildAround(Module module, Ray lineOfSight) {
        Treadmill.buildAround(0, null, module, lineOfSight);
    }

    private static void buildAround(int depth, Portal entryPortal, Module module, Ray lineOfSight) {
        if (depth > m_maxBuildDepth) {
            return;
        }

        //System.out.println(String.format("Building around module %s", module.getName()));
        for (Portal portal : module.getTemplate().getPortals()) {
            if(portal == entryPortal) continue;
            //System.out.println(String.format("Attempting link at portal %s", portal.getName()));
            if (portal.visibleFrom(lineOfSight)) {
                Portal neighborPortal = null;
                if (!module.hasNeighbor(portal)) {
                    //System.out.println("No neighbor!");
                    m_modulesWithOpenPortals.remove(module);
                    Module neighbor;
                    if (m_random.nextFloat() < m_connectivity && m_modulesWithOpenPortals.size() > 0) {
                        neighbor = (Module)(m_modulesWithOpenPortals.toArray()[m_random.nextInt(m_modulesWithOpenPortals.size())]);
                        m_modulesWithOpenPortals.remove(neighbor);
                        //System.out.println(String.format("Selected existing module %s with %d open portals", neighbor.getName(), neighbor.getOpenPortalCount()));
                    } else {
                        neighbor = module.getTemplate().chooseNeighborTemplate(portal).getInstance();
                        //System.out.println(String.format("Constructed new module %s with %d open portals", neighbor.getName(), neighbor.getOpenPortalCount()));
                    }
                    neighborPortal = neighbor.selectOpenPortal();
                    Treadmill.link(module, portal, neighborPortal, neighbor);
                    if(module.hasOpenPortals()) m_modulesWithOpenPortals.add(module);
                    if(neighbor.hasOpenPortals()) m_modulesWithOpenPortals.add(neighbor);
                }
                Treadmill.buildAround(depth + 1, neighborPortal, module.getNeighbor(portal), lineOfSight);
            }
        }
    }

    private static void link(Module module1, Portal portal1, Portal portal2, Module module2) {
        module1.setNeighbor(portal1, module2);
        module2.setNeighbor(portal2, module1);
        //System.out.println(String.format("Made link %s -> %s:%s -> %s", module1.getName(), portal1.getName(), portal2.getName(), module2.getName()));
        //if(!module1.hasOpenPortals()) m_modulesWithOpenPortals.remove(module1);
        //if(!module2.hasOpenPortals()) m_modulesWithOpenPortals.remove(module2);
    }
}
