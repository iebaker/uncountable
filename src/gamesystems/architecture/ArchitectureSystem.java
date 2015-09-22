package gamesystems.architecture;

import com.sun.corba.se.impl.encoding.OSFCodeSetRegistry;
import com.sun.istack.internal.Nullable;
import core.Uncountable;
import gamesystems.GameSystem;
import gamesystems.architecture.setpieces.BasicColoredQuad;
import gamesystems.rendering.Points;
import joml.AxisAngle4f;
import joml.Quaternionf;
import joml.Vector3f;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lwjgl.opengl.GL11;
import portals.Portal;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ArchitectureSystem extends GameSystem {

    private static String m_modulesDirectoryName = "resources/modules";
    private static String m_rulesDirectoryName = "resources/rules";

    private Set<ModuleTemplate> m_moduleTemplates = new HashSet<>();
    private Map<String, ModuleTemplate> m_moduleTemplatesByName = new HashMap<>();
    private Map<String, Map<String, Integer>> m_rulesByName = new HashMap<>();
    private Map<Portal, Map<String, Integer>> m_unresolvedRules = new HashMap<>();

    private final int m_depth = 4;

    public ArchitectureSystem() {
        super();
    }

    @Override
    public void initialize() {
        importRules();
        importModuleTemplates();
        Uncountable.game.world.currentModule = getTemplate("leafroom").getInstance();
    }

    @Override
    public void tick(float seconds) {
        buildAround(Uncountable.game.world.currentModule);
    }

    private void buildAround(Module currentModule) {
        buildAround(currentModule, 0);
    }

    private void buildAround(Module currentModule, int depth) {
        if(depth > m_depth) return;
        currentModule.getTemplate().getPortals().forEach(
                (portal) -> {
                    if (!currentModule.hasNeighbor(portal)) {
                        String nextTemplateName = currentModule.getTemplate().chooseNeighborTemplateName(portal);
                        ModuleTemplate nextTemplate = m_moduleTemplatesByName.get(nextTemplateName);
                        Module nextModule = nextTemplate.getInstance();
                        Portal remote = nextModule.selectOpenPortal();
                        link(currentModule, portal, remote, nextModule);
                    }
                    buildAround(currentModule.getNeighbor(portal), depth + 1);
                }
        );
    }

    @SuppressWarnings("unused")
    private ModuleTemplate chooseRandomTemplate() {
        return (ModuleTemplate)m_moduleTemplates.toArray()[new Random().nextInt(m_moduleTemplates.size())];
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

    private void importRules() {
        try {
            File rulesDirectory = new File(Uncountable.class.getClassLoader().getResource(m_rulesDirectoryName).toURI());
            for(File ruleSpecification : rulesDirectory.listFiles()) {
                if(ruleSpecification.isDirectory()) continue;
                JSONObject ruleJson = new JSONObject(Uncountable.stringFromFile(ruleSpecification));
                Map<String, Integer> rule = new HashMap<>();
                JSONArray definitionJson = ruleJson.getJSONArray("definition");
                buildRuleDefinition(rule, definitionJson);
                m_rulesByName.put(ruleJson.getString("name"), rule);
                System.out.println(rule);
            }
        } catch (URISyntaxException|FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void importModuleTemplates() {
        try {
            File modulesDirectory = new File(Uncountable.class.getClassLoader().getResource(m_modulesDirectoryName).toURI());
            for(File moduleSpecification : modulesDirectory.listFiles()) {
                if(moduleSpecification.isDirectory()) continue;
                JSONObject moduleJson = new JSONObject(Uncountable.stringFromFile(moduleSpecification));
                ModuleTemplate template = new ModuleTemplate(moduleJson.getString("name"));
                buildTemplateWalls(template, moduleJson.getJSONArray("dimensions"), moduleJson.getJSONArray("color"));
                buildTemplatePortals(template, moduleJson.getJSONArray("portals"));
                m_moduleTemplates.add(template);
            }
        } catch (URISyntaxException|FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        m_moduleTemplatesByName = m_moduleTemplates.stream()
                .collect(Collectors.toMap(ModuleTemplate::getName, Function.identity()));
    }

    private void buildRuleDefinition(Map<String, Integer> rule, JSONArray definition) {
        for(int i = 0; i < definition.length(); ++i) {
            JSONObject transition = definition.getJSONObject(i);
            rule.put(transition.getString("module"), transition.getInt("weight"));
        }
    }

    private void buildTemplateWalls(ModuleTemplate template, JSONArray d, JSONArray c) {
        Vector3f dimensions = Points.from3f(d);
        Vector3f color = Points.from3f(c);

        BasicColoredQuad floor = new BasicColoredQuad(Points.WHITE.get().mul(0.5f));
        BasicColoredQuad ceiling = new BasicColoredQuad(color.get().mul(0.1f));
        BasicColoredQuad rightWall = new BasicColoredQuad(color.get().mul(0.4f));
        BasicColoredQuad leftWall = new BasicColoredQuad(color.get().mul(0.2f));
        BasicColoredQuad farWall = new BasicColoredQuad(color.get().mul(0.8f));
        BasicColoredQuad nearWall = new BasicColoredQuad(color.get().mul(0.6f));

        floor.scale(1.001f);
        floor.rotate(3 * Points.piOver(2), Points.X__);
        floor.translate(0.5f, 0.0f, 0.5f);
        floor.scale(dimensions.x, 1.0f, dimensions.z);

        ceiling.scale(1.001f);
        ceiling.rotate(Points.piOver(2), Points.X__);
        ceiling.translate(0.5f, dimensions.y, 0.5f);
        ceiling.scale(dimensions.x, 1.0f, dimensions.z);

        leftWall.scale(1.001f);
        leftWall.rotate(Points.piOver(2), Points._Y_);
        leftWall.translate(0.0f, 0.5f, 0.5f);
        leftWall.scale(1.0f, dimensions.y, dimensions.z);

        rightWall.scale(1.001f);
        rightWall.rotate(3 * Points.piOver(2), Points._Y_);
        rightWall.translate(dimensions.x, 0.5f, 0.5f);
        rightWall.scale(1.0f, dimensions.y, dimensions.z);

        farWall.scale(1.001f);
        farWall.translate(0.5f, 0.5f, 0.0f);
        farWall.scale(dimensions.x, dimensions.y, 1.0f);

        nearWall.scale(1.001f);
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
            template.addPortal(portal, m_rulesByName.get(portalJson.getString("rule")));
        }
    }
}
