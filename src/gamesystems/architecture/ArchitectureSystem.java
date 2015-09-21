package gamesystems.architecture;

import core.Uncountable;
import gamesystems.GameSystem;
import portals.Portal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArchitectureSystem extends GameSystem {

    private Set<ModuleTemplate> m_moduleTemplates = new HashSet<>();
    private Map<String, ModuleTemplate> m_moduleTemplatesByName = new HashMap<>();

    public ArchitectureSystem() {
        super();
    }

    @Override
    public void initialize() {
        importModuleTemplates();

        Module flatRoom = getTemplate("flatroom").getInstance();
        Module leafRoom = getTemplate("leafroom").getInstance();
        Module cubeRoom = getTemplate("cuberoom").getInstance();
        Module hallway1 = getTemplate("hallway").getInstance();
        Module hallway2 = getTemplate("hallway").getInstance();

        link(flatRoom, "2", "0", hallway1);
        link(hallway1, "1", "0", flatRoom);
        link(flatRoom, "1", "0", cubeRoom);
        link(cubeRoom, "1", "0", hallway2);
        link(hallway2, "1", "3", cubeRoom);
        link(cubeRoom, "2", "0", leafRoom);

        Uncountable.game.world.currentModule = hallway1;
    }

    public void importModuleTemplates() {
        m_moduleTemplates = ModuleTemplateDirectoryParser.getModuleTemplates();
        m_moduleTemplatesByName = m_moduleTemplates
                .stream()
                .collect(Collectors.toMap(ModuleTemplate::getName, Function.identity()));
    }

    private ModuleTemplate getTemplate(String name) {
        return m_moduleTemplatesByName.get(name);
    }

    private void link(Module module1, String p1, String p2, Module module2) {
        Portal portal1 = module1.getTemplate().getPortal(p1);
        Portal portal2 = module2.getTemplate().getPortal(p2);
        link(module1, portal1, portal2, module2);
    }

    private void link(Module module1, Portal portal1, Portal portal2, Module module2) {
        module1.setNeighbor(portal1, module2);
        module1.linkPortals(portal1, portal2);
        module2.setNeighbor(portal2, module1);
        module2.linkPortals(portal2, portal1);
    }
}
