package world.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import application.Uncountable;
import gamesystems.rendering.Points;
import joml.AxisAngle4f;
import joml.Matrix3f;
import joml.Quaternionf;
import joml.Vector3f;
import world.ModuleTemplate;
import world.setpieces.BasicColoredQuad;
import world.setpieces.Portal;

public class ModuleTemplateDirectoryParser {

    private static final String m_modulesDirectoryName = "resources/modules";
    private static final String m_moduleTemplateFilename = "module.txt";
    private static final String m_modulePortalsFilename = "portals.txt";

    public static Set<ModuleTemplate> getModuleTemplates() {
        Set<ModuleTemplate> result = new HashSet<ModuleTemplate>();
        try {

            URL url = Uncountable.class.getClassLoader().getResource(m_modulesDirectoryName);
            File allModuleTemplates = new File(url.toURI());

            for(File moduleTemplateDirectory : allModuleTemplates.listFiles()) {
                ModuleTemplate template = new ModuleTemplate(moduleTemplateDirectory.getName());
                for(File moduleTemplateSpecifier : moduleTemplateDirectory.listFiles()) {
                    switch(moduleTemplateSpecifier.getName()) {
                    case m_moduleTemplateFilename:
                        parseModuleTemplateFile(template, moduleTemplateSpecifier);
                    break;
                    case m_modulePortalsFilename:
                        parseModulePortalsFile(template, moduleTemplateSpecifier);
                    break;
                    }
                }
                result.add(template);
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return result;
    }

    private static void parseModuleTemplateFile(ModuleTemplate template, File moduleTemplateFile) {
        try(Scanner scanner = new Scanner(moduleTemplateFile)) {

            Vector3f dimensions = new Vector3f(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
            Vector3f color = new Vector3f(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());

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

        } catch (FileNotFoundException e) {

        }
    }

    private static void parseModulePortalsFile(ModuleTemplate template, File modulePortalsFile) {
        try(Scanner scanner = new Scanner(modulePortalsFile)) {
            int i = 0;
            while(scanner.hasNext()) {

                Vector3f basePosition = new Vector3f(scanner.nextFloat(), scanner.nextFloat(), scanner.nextFloat());
                float rotation = scanner.nextFloat() * Points.piOver(180);
                Vector3f normal = new Vector3f(Points.__Z).rotate(new Quaternionf(new AxisAngle4f(rotation, Points._Y_)));

                Portal portal = new Portal(i++ + "", basePosition, normal, Points._Y_);
                portal.translate(0.0f, 0.0f, 0.001f);
                portal.rotate(rotation, Points._Y_);
                portal.translate(basePosition);

                template.addPortal(portal, new HashMap<ModuleTemplate, Integer>());
            }
        } catch (FileNotFoundException e) {

        }
    }
}
