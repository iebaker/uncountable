package world;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import world.parsing.ModuleTemplateDirectoryParser;
import world.setpieces.Portal;

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

    public static ModuleTemplate getModuleTemplate(String name) {
        return m_moduleTemplatesByName.get(name);
    }

    public static Module getInitialModule() {
        ModuleTemplate leafRoomTemplate = m_moduleTemplatesByName.get("leafroom");
        ModuleTemplate hallwayTemplate = m_moduleTemplatesByName.get("hallway");
        ModuleTemplate cubeRoomTemplate = m_moduleTemplatesByName.get("cuberoom");
        ModuleTemplate flatRoomTemplate = m_moduleTemplatesByName.get("flatroom");

        Module flatRoom = flatRoomTemplate.getInstance();
        Portal flat0 = flatRoomTemplate.getPortal("0");
        Portal flat1 = flatRoomTemplate.getPortal("1");
        Portal flat2 = flatRoomTemplate.getPortal("2");

        Module leafRoom0 = leafRoomTemplate.getInstance();
        Portal leaf00 = leafRoomTemplate.getPortal("0");

        Module leafRoom1 = leafRoomTemplate.getInstance();
        Portal leaf10 = leafRoomTemplate.getPortal("0");

        Module cubeRoom = cubeRoomTemplate.getInstance();
        Portal cube0 = cubeRoomTemplate.getPortal("0");
        Portal cube1 = cubeRoomTemplate.getPortal("1");
        Portal cube2 = cubeRoomTemplate.getPortal("2");
        Portal cube3 = cubeRoomTemplate.getPortal("3");

        Module hallway = hallwayTemplate.getInstance();
        Module hallway2 = hallwayTemplate.getInstance();
        Portal hall0 = hallwayTemplate.getPortal("0");
        Portal hall1 = hallwayTemplate.getPortal("1");

        link(cubeRoom, cube0, hall0, hallway);
        link(hallway, hall1, cube2, cubeRoom);
        link(cubeRoom, cube1, hall0, hallway2);
        link(hallway2, hall1, cube3, cubeRoom);

        return cubeRoom;
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
