package gamesystems.architecture;

import core.Uncountable;
import gamesystems.GameSystem;
import gamesystems.architecture.setpieces.BasicColoredQuad;
import gamesystems.rendering.Points;
import joml.AxisAngle4f;
import joml.Quaternionf;
import joml.Vector3f;
import org.json.JSONArray;
import org.json.JSONObject;
import portals.Portal;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArchitectureSystem extends GameSystem {

    private String m_modulesDirectoryName = "resources/modules";
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

    private void importModuleTemplates() {
        try {
            File modulesDirectory = new File(Uncountable.class.getClassLoader().getResource(m_modulesDirectoryName).toURI());
            for(File moduleSpecification : modulesDirectory.listFiles()) {
                if(moduleSpecification.isDirectory()) continue;
                JSONObject json = new JSONObject(Uncountable.stringFromFile(moduleSpecification));
                ModuleTemplate template = new ModuleTemplate(json.getString("name"));
                buildTemplateWalls(template, json.getJSONArray("dimensions"), json.getJSONArray("color"));
                buildTemplatePortals(template, json.getJSONArray("portals"));
                m_moduleTemplates.add(template);
            }
        } catch (URISyntaxException|FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        m_moduleTemplatesByName = m_moduleTemplates.stream()
                .collect(Collectors.toMap(ModuleTemplate::getName, Function.identity()));
    }

    private void buildTemplateWalls(ModuleTemplate template, JSONArray d, JSONArray c) {
        Vector3f dimensions = Points.from3f(d);
        Vector3f color = Points.from3f(c);

        BasicColoredQuad floor = new BasicColoredQuad(Points.WHITE.get().mul(0.5f));
        BasicColoredQuad ceiling = new BasicColoredQuad(Points.WHITE);
        BasicColoredQuad rightWall = new BasicColoredQuad(color.get().mul(0.4f));
        BasicColoredQuad leftWall = new BasicColoredQuad(color.get().mul(0.2f));
        BasicColoredQuad farWall = new BasicColoredQuad(color.get().mul(0.8f));
        BasicColoredQuad nearWall = new BasicColoredQuad(color.get().mul(0.6f));

        floor.scale(1.01f);
        floor.rotate(3 * Points.piOver(2), Points.X__);
        floor.translate(0.5f, 0.0f, 0.5f);
        floor.scale(dimensions.x, 1.0f, dimensions.z);

        ceiling.rotate(Points.piOver(2), Points.X__);
        ceiling.translate(0.5f, dimensions.y, 0.5f);
        ceiling.scale(dimensions.x, 1.0f, dimensions.z);

        leftWall.rotate(Points.piOver(2), Points._Y_);
        leftWall.translate(0.0f, 0.5f, 0.5f);
        leftWall.scale(1.0f, dimensions.y, dimensions.z);

        rightWall.rotate(3 * Points.piOver(2), Points._Y_);
        rightWall.translate(dimensions.x, 0.5f, 0.5f);
        rightWall.scale(1.0f, dimensions.y, dimensions.z);

        farWall.translate(0.5f, 0.5f, 0.0f);
        farWall.scale(dimensions.x, dimensions.y, 1.0f);

        nearWall.rotate(Points.piOver(1), Points._Y_);
        nearWall.translate(0.5f, 0.5f, dimensions.z);
        nearWall.scale(dimensions.x, dimensions.y, 1.0f);

        template.addWalls(floor, ceiling, leftWall, rightWall, farWall, nearWall);
    }

    private void buildTemplatePortals(ModuleTemplate template, JSONArray portals) {
        for(int i = 0; i < portals.length(); ++i) {
            JSONObject portalJson = portals.getJSONObject(i);

            Vector3f position = Points.from3f(portalJson.getJSONArray("position"));
            float angle = (float)portalJson.getDouble("angle") * Points.piOver(180);
            Vector3f normal = new Vector3f(Points.__Z).rotate(new Quaternionf(new AxisAngle4f(angle, Points._Y_)));

            Portal portal = new Portal(portalJson.getString("name"), position, normal, Points._Y_);
            portal.translate(0.0f, 0.0f, 0.001f);
            portal.rotate(angle, Points._Y_);
            portal.translate(position);

            template.addPortal(portal, new HashMap<>());
        }
    }
}
