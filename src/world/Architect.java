package world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import world.parsing.ModuleTemplateDirectoryParser;

public class Architect {

    private static Set<ModuleTemplate> m_moduleTemplates = new HashSet<ModuleTemplate>();
    private static Map<String, ModuleTemplate> m_moduleTemplatesByName = new HashMap<String, ModuleTemplate>();

    public static void importModuleTemplates() {
        m_moduleTemplates = ModuleTemplateDirectoryParser.getModuleTemplates();
        for(ModuleTemplate template : m_moduleTemplates) {
            m_moduleTemplatesByName.put(template.getName(), template);
        }
    }

    @SuppressWarnings("unused")
    private static void printModuleTypes() {
        for (ModuleTemplate type : m_moduleTemplates) {
            System.out.println(type);
        }
    }

    public static Module getInitialModule() {
        ModuleTemplate leafRoomTemplate = m_moduleTemplatesByName.get("leafroom");
        ModuleTemplate hallwayTemplate = m_moduleTemplatesByName.get("hallway");
        ModuleTemplate cubeRoomTemplate = m_moduleTemplatesByName.get("cuberoom");

        Module leafRoom1 = leafRoomTemplate.getInstance();
        Module hallway = hallwayTemplate.getInstance();
        Module leafRoom2 = leafRoomTemplate.getInstance();

        Module cubeRoom = cubeRoomTemplate.getInstance();

        Portal leafRoomPortal = leafRoomTemplate.getPortal("0");
        Portal hallwayPortal1 = hallwayTemplate.getPortal("0");
        Portal hallwayPortal2 = hallwayTemplate.getPortal("1");
        Portal cubeRoomPortal = cubeRoomTemplate.getPortal("0");

        link(hallway, hallwayPortal1, leafRoomPortal, leafRoom1);
        link(hallway, hallwayPortal2, leafRoomPortal, leafRoom2);
        return hallway;
    }

    public static void buildAround(Module module, Ray lineOfSight) {
        Architect.buildAround(0, null, module, lineOfSight);
    }

    private static void buildAround(int depth, Portal entryPortal, Module module, Ray lineOfSight) {

    }

    private static void link(Module module1, Portal portal1, Portal portal2, Module module2) {
        module1.setNeighbor(portal1, module2);
        module1.linkPortals(portal1, portal2);
        module2.setNeighbor(portal2, module1);
        module2.linkPortals(portal2, portal1);
    }
}
